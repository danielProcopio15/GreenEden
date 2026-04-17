package com.greeneden.calculadora_sustentavel.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmissoesCO2 extends EntradaCalculo {

    // ── Cartão físico — Cradle-to-Grave (kg CO₂e) ────────────────────────────
    private double co2Producao;
    private double co2Embalagem;
    private double co2Logistica;
    private double co2FimDeVida;
    /** Soma de todas as etapas do ciclo de vida físico */
    private double co2Total;

    // ── Unidade funcional (kg CO₂e / transação) ──────────────────────────────
    /** Emissão por transação com cartão físico = co2Total / (cartões × vidaUtil) */
    private double co2PorTransacaoFisico;

    // ── Cenário digital — Cradle-to-Gate (kg CO₂e) ───────────────────────────
    /** Emissões de servidor/data center por transação digital */
    private double co2ServidorDigital;
    /** Emissões de rede de telecomunicações por transação digital */
    private double co2Telecomunicacoes;
    /** Fração proporcional do dispositivo do usuário por transação */
    private double co2DispositivoUsuario;
    /** Total digital = (servidor + telecom + dispositivo) × quantidade transações */
    private double co2CenarioDigital;
    /** Emissão por transação digital (unidade funcional comparável) */
    private double co2PorTransacaoDigital;

    // ── Comparativo ──────────────────────────────────────────────────────────
    /** Diferença absoluta (co2Total físico - co2CenarioDigital) */
    private double reducaoCO2Digital;

    public EmissoesCO2(EntradaCalculo entrada,
                       double co2Producao, double co2Embalagem,
                       double co2Logistica, double co2FimDeVida, double co2Total,
                       double co2PorTransacaoFisico,
                       double co2ServidorDigital, double co2Telecomunicacoes,
                       double co2DispositivoUsuario, double co2CenarioDigital,
                       double co2PorTransacaoDigital, double reducaoCO2Digital) {
        // propagate all EntradaCalculo fields
        setQuantidadeCartoes(entrada.getQuantidadeCartoes());
        setFrequenciaRemessasAno(entrada.getFrequenciaRemessasAno());
        setTipoMaterial(entrada.getTipoMaterial());
        setVidaUtilTransacoesPorCartao(entrada.getVidaUtilTransacoesPorCartao());
        setQuantidadeTransacoes(entrada.getQuantidadeTransacoes());
        setTipoTransacaoDigital(entrada.getTipoTransacaoDigital());
        setOrigemFabrica(entrada.getOrigemFabrica());
        setCepDestino(entrada.getCepDestino());
        setDistanciaLogistica(entrada.getDistanciaLogistica());
        setTipoTransporte(entrada.getTipoTransporte());
        setCenarioDescarte(entrada.getCenarioDescarte());

        this.co2Producao = co2Producao;
        this.co2Embalagem = co2Embalagem;
        this.co2Logistica = co2Logistica;
        this.co2FimDeVida = co2FimDeVida;
        this.co2Total = co2Total;
        this.co2PorTransacaoFisico = co2PorTransacaoFisico;
        this.co2ServidorDigital = co2ServidorDigital;
        this.co2Telecomunicacoes = co2Telecomunicacoes;
        this.co2DispositivoUsuario = co2DispositivoUsuario;
        this.co2CenarioDigital = co2CenarioDigital;
        this.co2PorTransacaoDigital = co2PorTransacaoDigital;
        this.reducaoCO2Digital = reducaoCO2Digital;
    }
}
