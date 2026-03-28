package com.greeneden.calculadora_sustentavel;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalculadoraController {

    @GetMapping("/")
    public String mostrarHome() {
        return "index";
    }

    @GetMapping("/calculadora")
    public String mostrarCalculadora() {
        return "calculadora";
    }

    @PostMapping("/calcular")
    public String calcularImpacto(@RequestParam("quantidade") Integer quantidade, Model model) {

        if (quantidade == null || quantidade < 1) {
            model.addAttribute("erro", "A quantidade deve ser um número maior que zero.");
            return "calculadora";
        }

        // === CÁLCULOS AMBIENTAIS (premissas realistas) ===
        double quantidadeTransacoes = quantidade;

        // Impacto por transação física (valores aproximados baseados em estudos)
        double co2PorTransacaoFisica = 0.012;     // kg de CO₂ por cartão/transação
        double plasticoPorTransacao = 0.005;      // kg de plástico (cartão)
        double papelPorTransacao = 0.008;         // kg de papel (embalagem + guia)
        double logisticaPorTransacao = 0.003;     // kg CO₂ de transporte

        // Cálculos
        double emissaoCO2 = quantidadeTransacoes * (co2PorTransacaoFisica + logisticaPorTransacao);
        double consumoPlastico = quantidadeTransacoes * plasticoPorTransacao;
        double consumoPapel = quantidadeTransacoes * papelPorTransacao;
        double economiaCO2Digital = emissaoCO2 * 0.85; // 85% de redução com digital

        // Adiciona os resultados no Model
        model.addAttribute("quantidade", quantidade);
        model.addAttribute("emissaoCO2", String.format("%.2f", emissaoCO2));
        model.addAttribute("consumoPlastico", String.format("%.3f", consumoPlastico));
        model.addAttribute("consumoPapel", String.format("%.3f", consumoPapel));
        model.addAttribute("economiaCO2Digital", String.format("%.2f", economiaCO2Digital));

        // Equivalências visuais (para deixar mais interessante)
        model.addAttribute("arvoresSalvas", (int) (economiaCO2Digital / 20)); // ~20kg CO₂ por árvore/ano
        model.addAttribute("kmCarro", (int) (economiaCO2Digital * 0.25));     // km rodados por carro

        return "resultado";   // Redireciona para a tela de resultados
    }
}