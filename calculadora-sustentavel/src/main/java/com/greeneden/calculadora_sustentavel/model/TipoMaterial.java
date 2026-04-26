package com.greeneden.calculadora_sustentavel.model;

/**
 * Material do cartão físico.
 * Fatores de emissão de produção (kg CO₂e/cartão) por material,
 * conforme GHG Protocol / Ecoinvent (ano-base 2024).
 */
public enum TipoMaterial {

    /** PVC virgem – padrão de mercado (~5 g/cartão) */
    PVC(0.476),

    /** PVC reciclado pós-consumo – ~40% menos emissão de produção */
    PVC_RECICLADO(0.286),

    /** Metal (aço inoxidável / alumínio) – ~3× mais intenso */
    METAL(1.420);

    /** kg CO₂e emitidos na fabricação de um cartão neste material (Escopos 1-3) */
    private final double co2ProducaoPorCartao;

    TipoMaterial(double co2ProducaoPorCartao) {
        this.co2ProducaoPorCartao = co2ProducaoPorCartao;
    }

    public double getCo2ProducaoPorCartao() {
        return co2ProducaoPorCartao;
    }
}
