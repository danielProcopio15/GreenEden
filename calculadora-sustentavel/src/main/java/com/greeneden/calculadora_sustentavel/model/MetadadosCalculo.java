package com.greeneden.calculadora_sustentavel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Rastreabilidade e governança do cálculo.
 * Garante auditabilidade conforme ISO 14064 e GHG Protocol Corporate Standard.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetadadosCalculo {

    /** Versão do conjunto de fatores de emissão utilizado */
    private String versaoFatores;

    /** Ano de referência dos fatores de emissão */
    private int anoReferenciaFatores;

    /** Fontes dos fatores de emissão aplicados */
    private String fontesFatores;

    /** Ano-base do inventário (exercício simulado) */
    private int anoBaseInventario;

    /** Horizonte temporal considerado (fronteira de sistema) */
    private String horizonteTemporal;

    /** Abordagem de ciclo de vida: Cradle-to-Grave (físico) ou Cradle-to-Gate (digital) */
    private String abordagemCicloDeVida;

    /** Escopo de aplicação: por produto, por instituição, por tipo de transação */
    private String escopoAplicacao;
}
