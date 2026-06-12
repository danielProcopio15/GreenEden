package com.greeneden.calculadora_sustentavel.calculo.model;

/**
 * Material do cartão físico.
 * Fatores de emissão de produção (kg CO₂e/cartão) por material,
 * conforme GHG Protocol / Ecoinvent (ano-base 2024).
 */
public enum TipoMaterial {

    /** PVC virgem – padrão de mercado (~5 g/cartão)
     *  Fonte: LCA ISO 14040/44 — Visa ESG Report 2021 / Ecoinvent 3.9
     *  Faixa bibliográfica: 0,12–0,22 kg CO₂e/cartão; adotado 0,150 (média central) */
    PVC(0.150),

    /** PVC reciclado pós-consumo – ~40% menos emissão de produção
     *  0,150 × 0,60 = 0,090 kg CO₂e/cartão */
    PVC_RECICLADO(0.090),

    /** Metal (aço inoxidável / alumínio) – ~3× mais intenso que PVC virgem
     *  0,150 × 3,0 = 0,450 kg CO₂e/cartão */
    METAL(0.450);

    /** kg CO₂e emitidos na fabricação de um cartão neste material (Escopos 1-3) */
    private final double co2ProducaoPorCartao;

    TipoMaterial(double co2ProducaoPorCartao) {
        this.co2ProducaoPorCartao = co2ProducaoPorCartao;
    }

    public double getCo2ProducaoPorCartao() {
        return co2ProducaoPorCartao;
    }
}
