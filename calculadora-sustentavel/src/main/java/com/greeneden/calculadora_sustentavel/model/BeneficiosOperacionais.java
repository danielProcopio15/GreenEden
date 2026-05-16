package com.greeneden.calculadora_sustentavel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiosOperacionais {

    // ── Logística evitada ────────────────────────────────────────────────────
    private double kmLogisticaEvitados;
    private double reducaoCO2Distribuicao;

    // ── Produção física evitada ──────────────────────────────────────────────
    private int    cartoesEvitados;
    private int    reposicoesEvitadas;
    private double plasticoEvitadoKg;
    private double papelEvitadoKg;
    private double aguaEvitadaL;
    private double energiaEvitadaKwh;

    // ── Eficiência operacional ───────────────────────────────────────────────
    private int etapasOperacionaisEvitadas;

    // ── Custos exclusivos do digital ─────────────────────────────────────────
    private double consumoEnergiaDigitalTotalKwh;
    private double emissaoDigitalExclusivaKg;
    private double gastoInfraDigitalEstimado;

    // ── Segurança ────────────────────────────────────────────────────────────
    private int    indiceReducaoPerdaRoubo;
    private int    indiceMenorRiscoClonagem;
    private int    indiceControleUsuario;
    private int    indiceAutenticacaoAdicional;
    private int    perdasEvitadasEstimadas;
    private String comparativoSeguranca;
}
