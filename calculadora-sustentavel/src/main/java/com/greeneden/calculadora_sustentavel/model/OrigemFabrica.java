package com.greeneden.calculadora_sustentavel.model;

/**
 * Fábricas de cartões da Edenred.
 * Distância aproximada até o centro de distribuição nacional (Osasco/SP),
 * usada como referência de distância logística padrão quando não informada manualmente.
 * Fonte: endereços oficiais + cálculo rodoviário via ANTT.
 */
public enum OrigemFabrica {

    /**
     * Fábrica 1 – R. Amália Strapasson de Souza, 398 - Mauá, Colombo/PR, 83413-560
     * Distância rodoviária até Osasco/SP ≈ 410 km.
     */
    COLOMBO_PR("Colombo/PR", 410.0),

    /**
     * Fábrica 2 – Rua General Bertoldo Klinger, 69/89/131 - São Bernardo do Campo/SP - 09688-000
     * Distância rodoviária até Osasco/SP ≈ 35 km.
     */
    SAO_BERNARDO_SP("São Bernardo do Campo/SP", 35.0),

    /**
     * Fábrica 3 – Rua Soluções do Lar, 407 – Cotia/SP - 06716-020
     * Distância rodoviária até Osasco/SP ≈ 28 km.
     */
    COTIA_SP("Cotia/SP", 28.0);

    private final String descricao;
    private final double distanciaKmPadrao;

    OrigemFabrica(String descricao, double distanciaKmPadrao) {
        this.descricao = descricao;
        this.distanciaKmPadrao = distanciaKmPadrao;
    }

    public String getDescricao() { return descricao; }
    public double getDistanciaKmPadrao() { return distanciaKmPadrao; }
}
