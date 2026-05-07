package com.greeneden.calculadora_sustentavel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiosOperacionais {

    private double kmLogisticaEvitados;
    private double reducaoCO2Distribuicao;

    private int cartoesEvitados;
    private double plasticoEvitadoKg;
    private double papelEvitadoKg;
    private double aguaEvitadaL;
    private double energiaEvitadaKwh;

    private int etapasOperacionaisEvitadas;
    private int reposicoesEvitadas;

    private double consumoEnergiaDigitalTotalKwh;
    private double emissaoDigitalExclusivaKg;
    private double gastoInfraDigitalEstimado;

    private int perdasEvitadasEstimadas;
    private int indiceReducaoPerdaRoubo;
    private int indiceMenorRiscoClonagem;
    private int indiceControleUsuario;
    private int indiceAutenticacaoAdicional;
    private String comparativoSeguranca;
    private String destaqueComparativo;
}
