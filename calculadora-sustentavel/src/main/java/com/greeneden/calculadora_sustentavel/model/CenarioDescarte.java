package com.greeneden.calculadora_sustentavel.model;

/**
 * Cenário de fim de vida do cartão físico.
 * Fator adicional de emissão (kg CO₂e/cartão) no descarte.
 * Fonte: GHG Protocol Scope 3 – Category 12, IPCC AR6 (2021).
 */
public enum CenarioDescarte {

    /** Aterro sanitário – decomposição parcial, emissão de CH₄ */
    ATERRO(0.0085),

    /** Incineração com recuperação de energia – CO₂ de combustão do PVC */
    INCINERACAO(0.0210),

    /** Reciclagem mecânica – crédito de evitação de emissão (valor negativo) */
    RECICLAGEM(-0.0030);

    /** kg CO₂e adicionais por cartão neste cenário de descarte */
    private final double co2PorCartao;

    CenarioDescarte(double co2PorCartao) {
        this.co2PorCartao = co2PorCartao;
    }

    public double getCo2PorCartao() {
        return co2PorCartao;
    }
}
