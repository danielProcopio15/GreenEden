package com.greeneden.calculadora_sustentavel;

import com.greeneden.calculadora_sustentavel.model.CenarioDescarte;
import com.greeneden.calculadora_sustentavel.model.EntradaCalculo;
import com.greeneden.calculadora_sustentavel.model.ImpactoAmbiental;
import com.greeneden.calculadora_sustentavel.model.OrigemFabrica;
import com.greeneden.calculadora_sustentavel.model.TipoMaterial;
import com.greeneden.calculadora_sustentavel.model.TipoTransacaoDigital;
import com.greeneden.calculadora_sustentavel.service.CalculadoraServiceInterface;
import com.greeneden.calculadora_sustentavel.service.GeolocalizacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalculadoraController {

    private final CalculadoraServiceInterface calculadoraService;
    private final GeolocalizacaoService geolocalizacaoService;

    public CalculadoraController(CalculadoraServiceInterface calculadoraService,
                                  GeolocalizacaoService geolocalizacaoService) {
        this.calculadoraService   = calculadoraService;
        this.geolocalizacaoService = geolocalizacaoService;
    }

    @GetMapping("/")
    public String mostrarHome() {
        return "index";
    }

    @GetMapping("/calculadora")
    public String mostrarCalculadora(Model model) {
        model.addAttribute("cenarioDescarte", CenarioDescarte.values());
        model.addAttribute("origemFabrica", OrigemFabrica.values());
        return "calculadora";
    }

    @PostMapping("/calcular")
    public String calcularImpacto(
            @RequestParam("quantidadeCartoes") Integer quantidadeCartoes,
            @RequestParam("frequenciaRemessasAno") Integer frequenciaRemessasAno,
            @RequestParam("vidaUtilTransacoesPorCartao") Integer vidaUtil,
            @RequestParam("quantidadeTransacoes") Integer quantidadeTransacoes,
            @RequestParam("origemFabrica") String origemFabricaStr,
            @RequestParam("cepDestino") String cepDestino,
            @RequestParam("tipoTransporte") String tipoTransporte,
            @RequestParam("cenarioDescarte") String cenarioDescarteStr,
            Model model) {

        // Validações de entrada
        if (quantidadeCartoes == null || quantidadeCartoes < 1) {
            return erro(model, "A quantidade de cartões deve ser maior que zero.");
        }
        if (frequenciaRemessasAno == null || frequenciaRemessasAno < 1) {
            return erro(model, "A frequência de remessas deve ser pelo menos 1 vez por ano.");
        }
        if (vidaUtil == null || vidaUtil < 1) {
            return erro(model, "A vida útil por cartão deve ser pelo menos 1 transação.");
        }
        if (quantidadeTransacoes == null || quantidadeTransacoes < 0) {
            return erro(model, "A quantidade de transações não pode ser negativa.");
        }
        String cepLimpo = cepDestino != null ? cepDestino.replaceAll("[^0-9]", "") : "";
        if (cepLimpo.length() != 8) {
            return erro(model, "CEP inválido. Informe os 8 dígitos do CEP de destino.");
        }

        OrigemFabrica origem;
        try {
            origem = OrigemFabrica.valueOf(origemFabricaStr);
        } catch (Exception e) {
            return erro(model, "Selecione a fábrica de origem.");
        }

        try {
            // Calcula a distância via API de mapas antes do cálculo de impacto
            double distanciaKm = geolocalizacaoService.calcularDistancia(cepLimpo, origem, tipoTransporte);

            EntradaCalculo entrada = new EntradaCalculo();
            entrada.setQuantidadeCartoes(quantidadeCartoes);
            entrada.setFrequenciaRemessasAno(frequenciaRemessasAno);
            // Material fixo: PVC Reciclado (composição padrão de todos os cartões)
            entrada.setTipoMaterial(TipoMaterial.PVC_RECICLADO);
            entrada.setVidaUtilTransacoesPorCartao(vidaUtil);
            entrada.setQuantidadeTransacoes(quantidadeTransacoes);
            // Comparativo digital sempre via PIX (menor emissão — caso mais favorável ao digital)
            entrada.setTipoTransacaoDigital(TipoTransacaoDigital.PIX);
            entrada.setOrigemFabrica(origem);
            entrada.setCepDestino(cepLimpo);
            entrada.setDistanciaLogistica(distanciaKm);
            entrada.setTipoTransporte(tipoTransporte);
            entrada.setCenarioDescarte(CenarioDescarte.valueOf(cenarioDescarteStr));

            ImpactoAmbiental impacto = calculadoraService.calcularImpacto(entrada);
            model.addAttribute("impacto", impacto);
            return "resultado";

        } catch (Exception e) {
            return erro(model, "Erro ao realizar o cálculo: " + e.getMessage());
        }
    }

    private String erro(Model model, String mensagem) {
        model.addAttribute("erro", mensagem);
        model.addAttribute("cenarioDescarte", CenarioDescarte.values());
        model.addAttribute("origemFabrica", OrigemFabrica.values());
        return "calculadora";
    }
}