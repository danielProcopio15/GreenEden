package com.greeneden.calculadora_sustentavel.service;



import com.greeneden.calculadora_sustentavel.model.ImpactoAmbiental;
import org.springframework.stereotype.Service;



@Service
public class CalculadoraService implements CalculadoraServiceInterface {

    @Override
    public ImpactoAmbiental calcularImpacto(int quantidade) {

        if (quantidade < 1) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        // Valores por transação 
        double co2PorTransacao = 0.014;        // kg de CO₂ 
        double plasticoPorTransacao = 0.0052;  // kg de plástico 
        double papelPorTransacao = 0.0075;     // kg de papel 


        double emissaoCO2 = quantidade * co2PorTransacao;
        double consumoPlastico = quantidade * plasticoPorTransacao;
        double consumoPapel = quantidade * papelPorTransacao;
        double reducaoCO2Digital = emissaoCO2 * 0.85; // 85% de redução com a transação digital




        int arvoresSalvas = (int) (reducaoCO2Digital / 22);
        int kmCarroEvitado = (int) (reducaoCO2Digital * 0.28);



        return new ImpactoAmbiental(quantidade, emissaoCO2, consumoPlastico, consumoPapel,
                reducaoCO2Digital, arvoresSalvas, kmCarroEvitado);
    }
}