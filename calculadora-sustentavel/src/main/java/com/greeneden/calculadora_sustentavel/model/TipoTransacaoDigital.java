package com.greeneden.calculadora_sustentavel.model;

/**
 * Tecnologia utilizada na transação digital.
 * Fator de emissão (kg CO₂e/transação) inclui: servidor/data center,
 * rede de telecomunicações e proporção do dispositivo do usuário.
 * Fonte: literatura acadêmica + benchmarks de setor (IEA, GHG Protocol, DEFRA – 2024).
 * Fator de emissão da matriz elétrica brasileira: 0,0839 kg CO₂e/kWh (MCTI 2023).
 */
public enum TipoTransacaoDigital {

    /**
     * NFC (contactless) – tap-to-pay via terminal POS.
     * kWh/transação ≈ 0,00005 (servidor) + 0,000003 (rede) + 0,000002 (dispositivo)
     */
    NFC(0.0000045),

    /**
     * Carteira digital (wallet) – app + tokenização + servidor.
     * kWh ligeiramente maior pelo processamento de token.
     */
    WALLET(0.0000052),

    /**
     * QR Code – geração/leitura de imagem + validação servidor.
     */
    QR_CODE(0.0000048),

    /**
     * PIX – infraestrutura BACEN + instituições participantes + rede IP.
     * Fonte: estimativa baseada em benchmarks de instant payment europeus (EBA).
     */
    PIX(0.0000038);

    /** kg CO₂e por transação digital nesta tecnologia */
    private final double co2PorTransacao;

    TipoTransacaoDigital(double co2PorTransacao) {
        this.co2PorTransacao = co2PorTransacao;
    }

    public double getCo2PorTransacao() {
        return co2PorTransacao;
    }
}
