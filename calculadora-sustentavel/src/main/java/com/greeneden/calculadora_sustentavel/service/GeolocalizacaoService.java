package com.greeneden.calculadora_sustentavel.service;

import com.greeneden.calculadora_sustentavel.model.OrigemFabrica;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * Serviço de geolocalização — sem necessidade de chave de API.
 *
 * Fluxo:
 *  1. ViaCEP (gratuito, sem chave) — valida o CEP e obtém cidade/UF.
 *  2. Nominatim / OpenStreetMap (gratuito, sem cadastro) — converte endereço em lat/lng.
 *  3. Modal RODOVIARIO: OSRM public API (gratuito, sem cadastro) — distância real por estrada.
 *     Modal AEREO: Fórmula de Haversine — linha reta (padrão ICAO para carga aérea).
 */
@Service
public class GeolocalizacaoService {

    private static final String VIACEP_URL    = "https://viacep.com.br/ws/{cep}/json/";
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    private static final String OSRM_URL      = "http://router.project-osrm.org/route/v1/driving/";

    // Nominatim exige um User-Agent identificando a aplicação
    private static final String USER_AGENT = "GreenEden-Calculadora/1.0";

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
     * @param cepDestino CEP do destinatário (somente números)
     * @param origem     Fábrica de origem
     * @param modal      "RODOVIARIO" ou "AEREO"
     * @return distância em km
     */
    public double calcularDistancia(String cepDestino, OrigemFabrica origem, String modal) {
        String cepLimpo = cepDestino.replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException("CEP inválido: deve conter 8 dígitos.");
        }

        String enderecoDestino = buscarEnderecoViaCep(cepLimpo);
        double[] coordsDestino = geocodificar(enderecoDestino, cepLimpo);
        double[] coordsOrigem  = COORDS_FABRICA.get(origem);
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
            return (!logradouro.isBlank() ? logradouro + ", " : "")
                 + (!bairro.isBlank()     ? bairro + ", "     : "")
                 + localidade + " - " + uf + ", Brasil";
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar ViaCEP: " + e.getMessage(), e);
        }
    }

    /**
     * Converte endereço em [lat, lng] via Nominatim (OpenStreetMap).
     * Tenta primeiro com o endereço completo; fallback com CEP direto.
     */
    @SuppressWarnings("unchecked")
    private double[] geocodificar(String endereco, String cepFallback) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", USER_AGENT);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Tentativa 1: endereço completo vindo do ViaCEP
        String url = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
                .queryParam("q", endereco)
                .queryParam("countrycodes", "br")
                .queryParam("format", "json")
                .queryParam("limit", "1")
                .toUriString();
        try {
            List<Map<String, Object>> results = restTemplate
                    .exchange(url, HttpMethod.GET, entity, List.class).getBody();

            // Tentativa 2: apenas pelo CEP (fallback)
            if (results == null || results.isEmpty()) {
                url = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
                        .queryParam("postalcode", cepFallback)
                        .queryParam("countrycodes", "br")
                        .queryParam("format", "json")
                        .queryParam("limit", "1")
                        .toUriString();
                results = restTemplate.exchange(url, HttpMethod.GET, entity, List.class).getBody();
            }

            if (results == null || results.isEmpty()) {
                throw new RuntimeException(
                    "Não foi possível localizar as coordenadas para o CEP " + cepFallback
                    + ". Verifique se o CEP está correto.");
            }
            double lat = Double.parseDouble((String) results.get(0).get("lat"));
            double lon = Double.parseDouble((String) results.get(0).get("lon"));
            return new double[]{lat, lon};
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao geocodificar endereço: " + e.getMessage(), e);
        }
    }

    /**
     * Calcula distância rodoviária real via OSRM (Open Source Routing Machine).
     * OSRM usa coordenadas na ordem lon,lat (inversa à convencional).
     */
    @SuppressWarnings("unchecked")
    private double distanciaRodoviaria(double[] origem, double[] destino) {
        // OSRM: longitude primeiro, latitude depois
        String coords = origem[1] + "," + origem[0] + ";" + destino[1] + "," + destino[0];
        String url = UriComponentsBuilder.fromUriString(OSRM_URL + coords)
                .queryParam("overview", "false")
                .toUriString();
        try {
            Map<String, Object> resp = restTemplate.getForObject(url, Map.class);
            if (resp == null || !"Ok".equals(resp.get("code"))) {
                throw new RuntimeException("OSRM falhou — code: "
                        + (resp != null ? resp.get("code") : "null"));
            }
            var routes = (List<Map<String, Object>>) resp.get("routes");
            Number metros = (Number) routes.get(0).get("distance");
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
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 10.0) / 10.0;
    }
}