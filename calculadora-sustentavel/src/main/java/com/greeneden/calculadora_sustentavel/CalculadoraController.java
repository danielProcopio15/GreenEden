package com.greeneden.calculadora_sustentavel;

import com.greeneden.calculadora_sustentavel.model.ImpactoAmbiental;
import com.greeneden.calculadora_sustentavel.service.CalculadoraServiceInterface;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalculadoraController {

    
    private final CalculadoraServiceInterface calculadoraService;

    public CalculadoraController(CalculadoraServiceInterface calculadoraService) {
        this.calculadoraService = calculadoraService;
    }

    @GetMapping("/")
    public String mostrarHome() {
        return "index";
    }

    @GetMapping("/calculadora")
    public String mostrarCalculadora() {
        return "calculadora";
    }



    @PostMapping("/calcular")
    public String calcularImpacto(
            @RequestParam("quantidadeCartoes") Integer quantidadeCartoes,
            @RequestParam("quantidadeTransacoes") Integer quantidadeTransacoes,
            @RequestParam("distanciaLogistica") Double distanciaLogistica,
            @RequestParam("tipoTransporte") String tipoTransporte,
            Model model) {

        if (quantidadeCartoes == null || quantidadeCartoes < 1) {
            model.addAttribute("erro", "A quantidade de cartões deve ser um número maior que zero.");
            return "calculadora";
        }
        if (quantidadeTransacoes == null || quantidadeTransacoes < 0) {
            model.addAttribute("erro", "A quantidade de transações não pode ser negativa.");
            return "calculadora";
        }
        if (distanciaLogistica == null || distanciaLogistica <= 0) {
            model.addAttribute("erro", "A distância logística deve ser maior que zero.");
            return "calculadora";
        }

        try {
            ImpactoAmbiental impacto = calculadoraService.calcularImpacto(
                    quantidadeCartoes, quantidadeTransacoes, distanciaLogistica, tipoTransporte);
            model.addAttribute("impacto", impacto);
            return "resultado";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao realizar o cálculo. Tente novamente.");
            return "calculadora";
        }
    }
}