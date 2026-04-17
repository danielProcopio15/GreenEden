package com.greeneden.calculadora_sustentavel.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImpactoAmbiental extends EmissoesCO2 {

    private RecursosConsumidos recursos;
    private EquivalenciasAmbientais equivalencias;
    private MetadadosCalculo metadados;

    public ImpactoAmbiental(EmissoesCO2 emissoes,
                             RecursosConsumidos recursos,
                             EquivalenciasAmbientais equivalencias,
                             MetadadosCalculo metadados) {
        // propagate EmissoesCO2 (which already carries EntradaCalculo fields)
        setQuantidadeCartoes(emissoes.getQuantidadeCartoes());
        setFrequenciaRemessasAno(emissoes.getFrequenciaRemessasAno());
        setTipoMaterial(emissoes.getTipoMaterial());
        setVidaUtilTransacoesPorCartao(emissoes.getVidaUtilTransacoesPorCartao());
        setQuantidadeTransacoes(emissoes.getQuantidadeTransacoes());
        setTipoTransacaoDigital(emissoes.getTipoTransacaoDigital());
        setOrigemFabrica(emissoes.getOrigemFabrica());
        setCepDestino(emissoes.getCepDestino());
        setDistanciaLogistica(emissoes.getDistanciaLogistica());
        setTipoTransporte(emissoes.getTipoTransporte());
        setCenarioDescarte(emissoes.getCenarioDescarte());

        setCo2Producao(emissoes.getCo2Producao());
        setCo2Embalagem(emissoes.getCo2Embalagem());
        setCo2Logistica(emissoes.getCo2Logistica());
        setCo2FimDeVida(emissoes.getCo2FimDeVida());
        setCo2Total(emissoes.getCo2Total());
        setCo2PorTransacaoFisico(emissoes.getCo2PorTransacaoFisico());
        setCo2ServidorDigital(emissoes.getCo2ServidorDigital());
        setCo2Telecomunicacoes(emissoes.getCo2Telecomunicacoes());
        setCo2DispositivoUsuario(emissoes.getCo2DispositivoUsuario());
        setCo2CenarioDigital(emissoes.getCo2CenarioDigital());
        setCo2PorTransacaoDigital(emissoes.getCo2PorTransacaoDigital());
        setReducaoCO2Digital(emissoes.getReducaoCO2Digital());

        this.recursos = recursos;
        this.equivalencias = equivalencias;
        this.metadados = metadados;
    }
}