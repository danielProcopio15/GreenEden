package com.greeneden.calculadora_sustentavel.service;

import com.greeneden.calculadora_sustentavel.model.ImpactoAmbiental;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraService implements CalculadoraServiceInterface {

    // ─── Constantes derivadas dos dados anuais da Edenred ───────────────────────
    // Base: 5,5 milhões de cartões/ano
    // CO₂e total: Escopo1(103,1) + Escopo2(122,8) + Escopo3(3680,4) = 3.906,3 tCO₂e/ano

    // CO₂ de produção por cartão (Escopos 1+2 + 65% do Escopo3)
    //   = (225.900 + 2.392.260) / 5.500.000 = 0,476 kg CO₂e/cartão
    private static final double CO2_PRODUCAO_POR_CARTAO = 0.476;

    // CO₂ de embalagem por cartão (~15% do Escopo3)
    //   = 552.060 / 5.500.000 = 0,100 kg CO₂e/cartão
    private static final double CO2_EMBALAGEM_POR_CARTAO = 0.100;

    // Fatores de emissão logística por km por cartão (~20% do Escopo3 a 500 km)
    //   Base rodoviário: 736.080 kg CO₂ / (5.500.000 × 500 km) = 0,000268 kg/km/cartão
    private static final double CO2_KM_RODOVIARIO    = 0.000268;  // kg CO₂/km/cartão
    private static final double CO2_KM_AEREO         = 0.001340;  // ~5× rodoviário
    private static final double CO2_KM_FERROVIARIO   = 0.0000536; // ~0,2× rodoviário

    // Recursos por cartão (resíduos anuais / 5.500.000 cartões)
    // Plástico:  310.000 kg / 5.500.000 = 0,05636 kg/cartão
    private static final double PLASTICO_POR_CARTAO  = 0.05636;
    // Papel:     690.000 kg / 5.500.000 = 0,12545 kg/cartão
    private static final double PAPEL_POR_CARTAO     = 0.12545;
    // Água:      9.000.000 L  / 5.500.000 = 1,636 L/cartão
    private static final double AGUA_POR_CARTAO      = 1.636;
    // Energia:   21.183 GJ / 5.500.000 = 3,852 MJ/cartão = 1,070 kWh/cartão
    private static final double ENERGIA_POR_CARTAO   = 1.070;

    // Cenário digital: apenas infraestrutura de TI por transação
    private static final double CO2_DIGITAL_POR_TRANSACAO = 0.0000045; // kg CO₂/transação

    // Equivalências de CO₂
    private static final double CO2_POR_ARVORE_ANO   = 22.0;  // kg CO₂ absorvidos/árvore/ano
    private static final double CO2_POR_KM_CARRO     = 0.21;  // kg CO₂/km de carro

    @Override
    public ImpactoAmbiental calcularImpacto(int quantidadeCartoes, int quantidadeTransacoes,
                                             double distanciaLogistica, String tipoTransporte) {
        if (quantidadeCartoes < 1) {
            throw new IllegalArgumentException("Quantidade de cartões deve ser maior que zero.");
        }
        if (quantidadeTransacoes < 0) {
            throw new IllegalArgumentException("Quantidade de transações não pode ser negativa.");
        }
        if (distanciaLogistica <= 0) {
            throw new IllegalArgumentException("Distância logística deve ser maior que zero.");
        }

        // ── 1. Produção dos cartões ──────────────────────────────────────────────
        double co2Producao = quantidadeCartoes * CO2_PRODUCAO_POR_CARTAO;

        // ── 2. Embalagens ────────────────────────────────────────────────────────
        double co2Embalagem = quantidadeCartoes * CO2_EMBALAGEM_POR_CARTAO;

        // ── 3. Logística e distribuição ──────────────────────────────────────────
        double fatorKm = switch (tipoTransporte.toUpperCase()) {
            case "AEREO"       -> CO2_KM_AEREO;
            case "FERROVIARIO" -> CO2_KM_FERROVIARIO;
            default            -> CO2_KM_RODOVIARIO;
        };
        double co2Logistica = quantidadeCartoes * distanciaLogistica * fatorKm;

        // ── 4. Cenário físico total ──────────────────────────────────────────────
        double co2Total = co2Producao + co2Embalagem + co2Logistica;

        // ── 5. Cenário digital (só infraestrutura de TI) ─────────────────────────
        double co2CenarioDigital = quantidadeTransacoes * CO2_DIGITAL_POR_TRANSACAO;
        double reducaoCO2Digital = co2Total - co2CenarioDigital;

        // ── 6. Recursos consumidos ───────────────────────────────────────────────
        double consumoPlastico = quantidadeCartoes * PLASTICO_POR_CARTAO;
        double consumoPapel    = quantidadeCartoes * PAPEL_POR_CARTAO;
        double consumoAgua     = quantidadeCartoes * AGUA_POR_CARTAO;
        double consumoEnergia  = quantidadeCartoes * ENERGIA_POR_CARTAO;

        // ── 7. Equivalências ─────────────────────────────────────────────────────
        int arvoresSalvas  = (int) (reducaoCO2Digital / CO2_POR_ARVORE_ANO);
        int kmCarroEvitado = (int) (reducaoCO2Digital / CO2_POR_KM_CARRO);

        return new ImpactoAmbiental(
                quantidadeCartoes, quantidadeTransacoes, distanciaLogistica, tipoTransporte,
                co2Producao, co2Embalagem, co2Logistica,
                co2Total, co2CenarioDigital, reducaoCO2Digital,
                consumoPlastico, consumoPapel, consumoAgua, consumoEnergia,
                arvoresSalvas, kmCarroEvitado);
    }
}