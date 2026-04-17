package com.greeneden.calculadora_sustentavel.service;

import com.greeneden.calculadora_sustentavel.model.OrigemFabrica;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Serviço de geolocalização.
 *
 * Fluxo:
 *  1. ViaCEP (gratuito, sem chave) — valida o CEP e obtém cidade/UF.
 *  2. Google Maps Geocoding API — converte o endereço ViaCEP em lat/lng.
 *  3. Para modal RODOVIARIO: Google Maps Distance Matrix API — distância real por estrada.
 *     Para modal AEREO: Fórmula de Haversine — linha reta entre fábrica e destino
 *                        (padrão ICAO para cálculo de emissões de carga aérea).
 */
@Service
public class GeolocalizacaoService {

    @Value("${google.maps.api-key}")
    private String apiKey;

    private static final String VIACEP_URL       = "https://viacep.com.br/ws/{cep}/json/";
    private static final String GEOCODING_URL     = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

    // Coordenadas fixas das fábricas parceiras
    private static final Map<OrigemFabrica, double[]> COORDS_FABRICA = Map.of(
        OrigemFabrica.COLOMBO_PR,       new double[]{-25.2968, -49.2239},
        OrigemFabrica.SAO_BERNARDO_SP,  new double[]{-23.6939, -46.5650},
        OrigemFabrica.COTIA_SP,         new double[]{-23.6031, -46.9192}
    );

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Calcula a distância em km entre a fábrica de origem e o CEP de destino.
     *
     * @param cepDestino CEP do destinatário (somente números ou com hífen)
     * @param origem     Fábrica de origem
     * @param modal      "RODOVIARIO" ou "AEREO"
     * @return distância em km
     */
    public double calcularDistancia(String cepDestino, OrigemFabrica origem, String modal) {
        String cepLimpo = cepDestino.replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException("CEP inválido: deve conter 8 dígitos.");
        }

        // 1. Valida CEP e obtém endereço via ViaCEP
        String enderecoDestino = buscarEnderecoViaCep(cepLimpo);

        // 2. Obtém coordenadas do destino via Geocoding
        double[] coordsDestino = geocodificar(enderecoDestino);

        // 3. Calcula distância conforme modal
        double[] coordsOrigem = COORDS_FABRICA.get(origem);
        if (coordsOrigem == null) {
            throw new IllegalArgumentException("Fábrica de origem não reconhecida.");
        }

        if ("AEREO".equalsIgnoreCase(modal)) {
            return haversineKm(coordsOrigem[0], coordsOrigem[1], coordsDestino[0], coordsDestino[1]);
        } else {
            return distanciaRodoviaria(coordsOrigem, coordsDestino);
        }
    }

    /** Busca endereço textual no ViaCEP. */
    @SuppressWarnings("unchecked")
    private String buscarEnderecoViaCep(String cep) {
        try {
            Map<String, Object> resposta = restTemplate.getForObject(VIACEP_URL, Map.class, cep);
            if (resposta == null || resposta.containsKey("erro")) {
                throw new IllegalArgumentException("CEP não encontrado: " + cep);
            }
            String logradouro = (String) resposta.getOrDefault("logradouro", "");
            String bairro     = (String) resposta.getOrDefault("bairro", "");
            String localidade = (String) resposta.getOrDefault("localidade", "");
            String uf         = (String) resposta.getOrDefault("uf", "");
            // Monta endereço para geocodificação
            String base = (!logradouro.isBlank() ? logradouro + ", " : "")
                        + (!bairro.isBlank()     ? bairro + ", "     : "")
                        + localidade + " - " + uf + ", Brasil";
            return base;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar ViaCEP: " + e.getMessage(), e);
        }
    }

    /** Converte um endereço textual em [lat, lng] via Google Geocoding. */
    @SuppressWarnings("unchecked")
    private double[] geocodificar(String endereco) {
        String url = UriComponentsBuilder.fromUriString(GEOCODING_URL)
                .queryParam("address", endereco)
                .queryParam("key", apiKey)
                .toUriString();
        try {
            Map<String, Object> resp = restTemplate.getForObject(url, Map.class);
            if (resp == null || !"OK".equals(resp.get("status"))) {
                throw new RuntimeException("Geocoding falhou para: " + endereco
                        + " — status: " + (resp != null ? resp.get("status") : "null"));
            }
            var results  = (java.util.List<Map<String, Object>>) resp.get("results");
            var geometry = (Map<String, Object>) results.get(0).get("geometry");
            var location = (Map<String, Double>)  geometry.get("location");
            return new double[]{location.get("lat"), location.get("lng")};
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao geocodificar endereço: " + e.getMessage(), e);
        }
    }

    /**
     * Calcula distância rodoviária real via Google Distance Matrix API.
     * Retorna km arredondado.
     */
    @SuppressWarnings("unchecked")
    private double distanciaRodoviaria(double[] origem, double[] destino) {
        String origemStr  = origem[0]  + "," + origem[1];
        String destinoStr = destino[0] + "," + destino[1];
        String url = UriComponentsBuilder.fromUriString(DISTANCE_MATRIX_URL)
                .queryParam("origins", origemStr)
                .queryParam("destinations", destinoStr)
                .queryParam("mode", "driving")
                .queryParam("key", apiKey)
                .toUriString();
        try {
            Map<String, Object> resp = restTemplate.getForObject(url, Map.class);
            if (resp == null || !"OK".equals(resp.get("status"))) {
                throw new RuntimeException("Distance Matrix falhou — status: "
                        + (resp != null ? resp.get("status") : "null"));
            }
            var rows     = (java.util.List<Map<String, Object>>) resp.get("rows");
            var elements = (java.util.List<Map<String, Object>>) rows.get(0).get("elements");
            var element  = elements.get(0);
            if (!"OK".equals(element.get("status"))) {
                throw new RuntimeException("Rota não encontrada para o destino informado.");
            }
            var distance = (Map<String, Object>) element.get("distance");
            Number metros = (Number) distance.get("value");
            return Math.round(metros.doubleValue() / 100.0) / 10.0; // metros → km (1 decimal)
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular distância rodoviária: " + e.getMessage(), e);
        }
    }

    /**
     * Fórmula de Haversine — distância em linha reta entre dois pontos na Terra.
     * Padrão utilizado pelo ICAO Carbon Calculator para emissões de carga aérea.
     */
    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // raio médio da Terra em km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 10.0) / 10.0; // km com 1 decimal
    }
}
