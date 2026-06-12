package com.greeneden.calculadora_sustentavel.calculo;

import com.greeneden.calculadora_sustentavel.calculo.model.BeneficiosOperacionais;
import com.greeneden.calculadora_sustentavel.calculo.model.CenarioDescarte;
import com.greeneden.calculadora_sustentavel.calculo.model.EmissoesCO2;
import com.greeneden.calculadora_sustentavel.calculo.model.EntradaCalculo;
import com.greeneden.calculadora_sustentavel.calculo.model.EquivalenciasAmbientais;
import com.greeneden.calculadora_sustentavel.calculo.model.ImpactoAmbiental;
import com.greeneden.calculadora_sustentavel.calculo.model.MetadadosCalculo;
import com.greeneden.calculadora_sustentavel.calculo.model.RecursosConsumidos;
import com.greeneden.calculadora_sustentavel.calculo.model.TipoMaterial;
import com.greeneden.calculadora_sustentavel.calculo.model.TipoTransacaoDigital;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraService {

    // â”€â”€â”€ Fatores de emissÃ£o â€” Embalagem â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // Fonte: LCA ISO 14040/44 — envelope kraft (~30 g) + protetor plastico (~2 g) + cartao-guia (~15 g)
    // Faixa bibliografica: 0,015-0,035 kg CO2e/cartao; adotado 0,020 (media central, Ecoinvent 3.9)
    private static final double CO2_EMBALAGEM_POR_CARTAO = 0.020;  // kg CO2e/cartao

    // â”€â”€â”€ Fatores logÃ­sticos por km/cartÃ£o â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // Fonte: DEFRA UK (2023) convertido para tonelada-km modal
    // Base rodoviÃ¡rio: 736.080 kg / (5.500.000 Ã— 500 km) = 0,000268 kg/km/cartÃ£o
    // Modelo: 1 veiculo por remessa, ida e volta (2x distancia), independente do numero de cartoes.
    // Fonte: DEFRA UK (2023) - emissoes por veiculo-km
    // Logistica por tonelada-km (DEFRA 2023) - peso real da carga, ida e volta
    // Cartao fisico: ~5 g = 0,005 kg/cartao (ISO 7810 ID-1)
    // HGV carregado medio: 0,107 kg CO2e/tonelada-km | Aviao cargueiro: 0,599 kg CO2e/tonelada-km
    private static final double PESO_CARTAO_KG           = 0.005;   // kg por cartao (ISO 7810 ID-1)
    private static final double CO2_TONELADA_KM_CAMINHAO = 0.107;   // kg CO2e/tonelada-km (DEFRA 2023 HGV)
    private static final double CO2_TONELADA_KM_AVIAO    = 0.599;   // kg CO2e/tonelada-km (DEFRA 2023 air freight)

    // â”€â”€â”€ Recursos por cartÃ£o PVC padrÃ£o â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // Fonte: relatÃ³rio ambiental Edenred 2023 / 5.500.000 cartÃµes
    private static final double PLASTICO_POR_CARTAO = 0.05636;  // kg
    private static final double PAPEL_POR_CARTAO    = 0.12545;  // kg
    private static final double AGUA_POR_CARTAO     = 1.636;    // L
    private static final double ENERGIA_POR_CARTAO  = 1.070;    // kWh

    // â”€â”€â”€ Digital â€” fatores detalhados por transaÃ§Ã£o â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // Fontes: Freitag et al. (2021) Patterns, Cell Press; EBA (2023) instant payments carbon benchmark;
    // Malmodin & Lunden (2018) J. Industrial Ecology; IEA (2023) Data Centres and Transmission Networks;
    // Falk & Obwegeser (2020) J. Cleaner Production.
    // Inclui carbono operacional + carbono incorporado (embodied carbon) da infraestrutura — omitir
    // o embodied carbon subestima 40-70% do footprint digital (Freitag et al. 2021).
    // PIX baseline total: ~0,0002 kg CO2e/transacao (EBA 2023 instant payment, limite inferior conservador).
    // Servidor/DC: operacional + embodied carbon de servidores e data centers
    private static final double CO2_SERVIDOR_POR_TRANSACAO    = 0.000080; // kg CO2e
    // Telecom: infraestrutura fixa + movel amortizada — Malmodin & Lunden (2018)
    private static final double CO2_TELECOM_POR_TRANSACAO     = 0.000070; // kg CO2e
    // Dispositivo: amortizacao de fabricacao (~70 kg CO2e / 3 anos / 1095 tx/ano) — Falk & Obwegeser (2020)
    private static final double CO2_DISPOSITIVO_POR_TRANSACAO = 0.000050; // kg CO2e

    // â”€â”€â”€ EquivalÃªncias â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private static final double CO2_POR_ARVORE_ANO = 22.0;  // kg COâ‚‚/Ã¡rvore/ano
    private static final double CO2_POR_KM_CARRO   = 0.170; // kg CO2/km (frota brasileira flex - ANFAVEA/SEEG 2023)

    // â”€â”€â”€ Metadados fixos desta versÃ£o do serviÃ§o â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private static final MetadadosCalculo METADADOS = new MetadadosCalculo(
            "2.0.0",
            2024,
            "GHG Protocol Corporate Standard; IPCC AR6; MCTI 2023; IEA 2023; DEFRA 2023; Ecoinvent 3.9",
            2025,
            "Anual",
            "Cradle-to-Grave (fÃ­sico) / Cradle-to-Gate (digital)",
            "Por produto (cartÃ£o fÃ­sico vs. transaÃ§Ã£o digital)"
    );    public ImpactoAmbiental calcularImpacto(EntradaCalculo entrada) {

        int qtdCartoesPorRemessa = entrada.getQuantidadeCartoes();
        int frequencia    = entrada.getFrequenciaRemessasAno() > 0 ? entrada.getFrequenciaRemessasAno() : 1;
        int qtdCartoes    = qtdCartoesPorRemessa * frequencia;  // total anual
        int qtdTransacoes = entrada.getQuantidadeTransacoes();
        int vidaUtil      = entrada.getVidaUtilTransacoesPorCartao();

        if (qtdCartoesPorRemessa < 1)
            throw new IllegalArgumentException("Quantidade de cartÃµes deve ser maior que zero.");
        if (frequencia < 1)
            throw new IllegalArgumentException("FrequÃªncia de remessas deve ser pelo menos 1 por ano.");
        if (qtdTransacoes < 0)
            throw new IllegalArgumentException("Quantidade de transaÃ§Ãµes nÃ£o pode ser negativa.");
        if (vidaUtil < 1)
            throw new IllegalArgumentException("Vida Ãºtil deve ser pelo menos 1 transaÃ§Ã£o por cartÃ£o.");

        // â”€â”€ DistÃ¢ncia: usa padrÃ£o da fÃ¡brica se nÃ£o informada â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        double distancia = entrada.getDistanciaLogistica();
        if (distancia <= 0 && entrada.getOrigemFabrica() != null) {
            distancia = entrada.getOrigemFabrica().getDistanciaKmPadrao();
            entrada.setDistanciaLogistica(distancia);
        }
        if (distancia <= 0)
            throw new IllegalArgumentException("DistÃ¢ncia logÃ­stica deve ser maior que zero.");

        // â”€â”€ 1. ProduÃ§Ã£o â€” fator varia conforme material â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        TipoMaterial material = entrada.getTipoMaterial() != null
                ? entrada.getTipoMaterial() : TipoMaterial.PVC;
        double co2Producao = qtdCartoes * material.getCo2ProducaoPorCartao();

        // â”€â”€ 2. Embalagem â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        double co2Embalagem = qtdCartoes * CO2_EMBALAGEM_POR_CARTAO;

        // â”€â”€ 3. LogÃ­stica â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        String modal = entrada.getTipoTransporte() != null
                ? entrada.getTipoTransporte().toUpperCase() : "RODOVIARIO";
        double fatorTonKm = "AEREO".equals(modal) ? CO2_TONELADA_KM_AVIAO : CO2_TONELADA_KM_CAMINHAO;
        // Cada remessa Ã© uma viagem completa: custo logÃ­stico Ã© frequencia Ã— cartÃµes/remessa Ã— distÃ¢ncia
        double pesoRemessaTon = qtdCartoesPorRemessa * PESO_CARTAO_KG / 1000.0;
        double co2Logistica = pesoRemessaTon * distancia * fatorTonKm * 2.0 * frequencia;

        // â”€â”€ 4. Fim de vida â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        CenarioDescarte descarte = entrada.getCenarioDescarte() != null
                ? entrada.getCenarioDescarte() : CenarioDescarte.ATERRO;
        double co2FimDeVida = qtdCartoes * descarte.getCo2PorCartao();

        // â”€â”€ 5. Total ciclo de vida fÃ­sico â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        double co2Total = co2Producao + co2Embalagem + co2Logistica + co2FimDeVida;

        // â”€â”€ 6. Unidade funcional fÃ­sica â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        // kg COâ‚‚e / transaÃ§Ã£o = co2Total / (cartÃµes Ã— vida Ãºtil)
        double co2PorTransacaoFisico = co2Total / ((double) qtdCartoes * vidaUtil);

        // â”€â”€ 7. CenÃ¡rio digital detalhado â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        TipoTransacaoDigital tecDigital = entrada.getTipoTransacaoDigital() != null
                ? entrada.getTipoTransacaoDigital() : TipoTransacaoDigital.PIX;

        // Fator de tecnologia ajusta apenas o componente servidor (restante Ã© fixo de infra)
        double fatorTec = tecDigital.getCo2PorTransacao()
                / TipoTransacaoDigital.PIX.getCo2PorTransacao();

        double co2Servidor    = qtdTransacoes * CO2_SERVIDOR_POR_TRANSACAO * fatorTec;
        double co2Telecom     = qtdTransacoes * CO2_TELECOM_POR_TRANSACAO;
        double co2Dispositivo = qtdTransacoes * CO2_DISPOSITIVO_POR_TRANSACAO;
        double co2Digital     = co2Servidor + co2Telecom + co2Dispositivo;
        double co2PorTransacaoDigital = qtdTransacoes > 0 ? co2Digital / qtdTransacoes : 0.0;

        // â”€â”€ 8. ReduÃ§Ã£o â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        double reducaoCO2Digital = co2Total - co2Digital;

        // â”€â”€ 9. Recursos consumidos â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        RecursosConsumidos recursos = new RecursosConsumidos(
                qtdCartoes * PLASTICO_POR_CARTAO,
                qtdCartoes * PAPEL_POR_CARTAO,
                qtdCartoes * AGUA_POR_CARTAO,
                qtdCartoes * ENERGIA_POR_CARTAO);

        // â”€â”€ 10. EquivalÃªncias â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        EquivalenciasAmbientais equivalencias = new EquivalenciasAmbientais(
                (int) (reducaoCO2Digital / CO2_POR_ARVORE_ANO),
                (int) (reducaoCO2Digital / CO2_POR_KM_CARRO));

        // â”€â”€ 11. Montar EmissoesCO2 e ImpactoAmbiental â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        // Beneficios operacionais
        double fatorEmissaoBrasil    = 0.0839; // kg CO2e/kWh (MCTI 2023)
        double tarifaKwhBrasil       = 0.75;   // R$/kWh estimado
        double consumoEnergiaDigital = co2Digital / fatorEmissaoBrasil;

        BeneficiosOperacionais beneficios = new BeneficiosOperacionais(
                distancia * frequencia,
                co2Logistica,
                qtdCartoes,
                (int) Math.round(qtdCartoes * 0.20),
                recursos.getConsumoPlastico(),
                recursos.getConsumoPapel(),
                recursos.getConsumoAgua(),
                recursos.getConsumoEnergia(),
                6,
                consumoEnergiaDigital,
                co2Digital,
                consumoEnergiaDigital * tarifaKwhBrasil,
                90, 95, 98, 85,
                (int) Math.round(qtdCartoes * 0.02),
                "Transacoes digitais via PIX eliminam a necessidade de cartao fisico, "
                + "reduzindo em ate 95% o risco de clonagem e fraude por extravio."
        );

        EmissoesCO2 emissoes = new EmissoesCO2(
                entrada,
                co2Producao, co2Embalagem, co2Logistica, co2FimDeVida, co2Total,
                co2PorTransacaoFisico,
                co2Servidor, co2Telecom, co2Dispositivo, co2Digital,
                co2PorTransacaoDigital, reducaoCO2Digital);

        return new ImpactoAmbiental(emissoes, recursos, equivalencias, METADADOS, beneficios);
    }
}

