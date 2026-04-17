package com.greeneden.calculadora_sustentavel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntradaCalculo {

    // ── Cartão físico ────────────────────────────────────────────────────────────
    /** Quantidade de cartões emitidos por remessa */
    private int quantidadeCartoes;

    /** Quantas vezes por ano a empresa solicita uma remessa de cartões */
    private int frequenciaRemessasAno;

    /** Material do cartão (PVC, PVC_RECICLADO, METAL) */
    private TipoMaterial tipoMaterial;

    /**
     * Vida útil esperada em número de transações por ciclo de vida do cartão.
     * Usado para calcular emissões por transação = co2Total / (cartoes × vidaUtil).
     * Faixa recomendada: 500–3.000 transações/cartão/ano.
     */
    private int vidaUtilTransacoesPorCartao;

    // ── Transações ───────────────────────────────────────────────────────────────
    /** Total de transações realizadas no período */
    private int quantidadeTransacoes;

    /** Tecnologia digital usada para comparativo */
    private TipoTransacaoDigital tipoTransacaoDigital;

    // ── Logística ────────────────────────────────────────────────────────────────
    /** Fábrica de origem dos cartões (define coordenadas para cálculo de distância) */
    private OrigemFabrica origemFabrica;

    /** CEP do destino final dos cartões (usado para calcular a distância via API de mapas) */
    private String cepDestino;

    /**
     * Distância logística em km (calculada automaticamente via Google Maps / Haversine).
     * Preenchida pelo serviço antes do cálculo de impacto.
     */
    private double distanciaLogistica;

    /** Modal de transporte predominante na distribuição (RODOVIARIO ou AEREO) */
    private String tipoTransporte;

    // ── Fim de vida ───────────────────────────────────────────────────────────────
    /** Cenário de descarte do cartão ao fim da vida útil */
    private CenarioDescarte cenarioDescarte;
}
