package com.greeneden.calculadora_sustentavel.service;

import com.greeneden.calculadora_sustentavel.model.CenarioDescarte;
import com.greeneden.calculadora_sustentavel.model.EmissoesCO2;
import com.greeneden.calculadora_sustentavel.model.EntradaCalculo;
import com.greeneden.calculadora_sustentavel.model.EquivalenciasAmbientais;
import com.greeneden.calculadora_sustentavel.model.ImpactoAmbiental;
import com.greeneden.calculadora_sustentavel.model.MetadadosCalculo;
import com.greeneden.calculadora_sustentavel.model.OrigemFabrica;
import com.greeneden.calculadora_sustentavel.model.RecursosConsumidos;
import com.greeneden.calculadora_sustentavel.model.TipoMaterial;
import com.greeneden.calculadora_sustentavel.model.TipoTransacaoDigital;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraService implements CalculadoraServiceInterface {

    // â”€â”€â”€ Fatores de emissÃ£o â€” Embalagem â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // Fonte: GHG Protocol Scope 3 Cat.1 / Ecoinvent 3.9 (ano-base 2024)
    // ~15% do Escopo3 Edenred / 5.500.000 cartÃµes = 0,100 kg COâ‚‚e/cartÃ£o
    private static final double CO2_EMBALAGEM_POR_CARTAO = 0.100;

    // â”€â”€â”€ Fatores logÃ­sticos por km/cartÃ£o â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // Fonte: DEFRA UK (2023) convertido para tonelada-km modal
    // Base rodoviÃ¡rio: 736.080 kg / (5.500.000 Ã— 500 km) = 0,000268 kg/km/cartÃ£o
    private static final double CO2_KM_RODOVIARIO  = 0.000268;
    private static final double CO2_KM_AEREO       = 0.001340;  // ~5Ã— rodoviÃ¡rio

    // â”€â”€â”€ Recursos por cartÃ£o PVC padrÃ£o â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // Fonte: relatÃ³rio ambiental Edenred 2023 / 5.500.000 cartÃµes
    private static final double PLASTICO_POR_CARTAO = 0.05636;  // kg
    private static final double PAPEL_POR_CARTAO    = 0.12545;  // kg
    private static final double AGUA_POR_CARTAO     = 1.636;    // L
    private static final double ENERGIA_POR_CARTAO  = 1.070;    // kWh

    // â”€â”€â”€ Digital â€” fatores detalhados por transaÃ§Ã£o â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // Fonte: IEA 2023, Freitag et al. (2021), benchmarks de instant payment EBA
    // Fator emissÃ£o rede elÃ©trica Brasil: 0,0839 kg COâ‚‚e/kWh (MCTI 2023)
    // Servidor/data center: 0,000040 kWh/transaÃ§Ã£o Ã— 0,0839 = 0,00000336 kg COâ‚‚e
    private static final double CO2_SERVIDOR_POR_TRANSACAO      = 0.00000336; // kg COâ‚‚e
    // Telecom: 0,000008 kWh/transaÃ§Ã£o Ã— 0,0839
    private static final double CO2_TELECOM_POR_TRANSACAO       = 0.00000067; // kg COâ‚‚e
    // Dispositivo do usuÃ¡rio: 0,000011 kWh/transaÃ§Ã£o Ã— 0,0839
    private static final double CO2_DISPOSITIVO_POR_TRANSACAO   = 0.00000092; // kg COâ‚‚e

    // â”€â”€â”€ EquivalÃªncias â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private static final double CO2_POR_ARVORE_ANO = 22.0;  // kg COâ‚‚/Ã¡rvore/ano
    private static final double CO2_POR_KM_CARRO   = 0.21;  // kg COâ‚‚/km (carro mÃ©dio)

    // â”€â”€â”€ Metadados fixos desta versÃ£o do serviÃ§o â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private static final MetadadosCalculo METADADOS = new MetadadosCalculo(
            "2.0.0",
            2024,
            "GHG Protocol Corporate Standard; IPCC AR6; MCTI 2023; IEA 2023; DEFRA 2023; Ecoinvent 3.9",
            2025,
            "Anual",
            "Cradle-to-Grave (fÃ­sico) / Cradle-to-Gate (digital)",
            "Por produto (cartÃ£o fÃ­sico vs. transaÃ§Ã£o digital)"
    );

    @Override
    public ImpactoAmbiental calcularImpacto(EntradaCalculo entrada) {

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
        double fatorKm = "AEREO".equals(modal) ? CO2_KM_AEREO : CO2_KM_RODOVIARIO;
        // Cada remessa Ã© uma viagem completa: custo logÃ­stico Ã© frequencia Ã— cartÃµes/remessa Ã— distÃ¢ncia
        double co2Logistica = qtdCartoesPorRemessa * distancia * fatorKm * frequencia;

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
        EmissoesCO2 emissoes = new EmissoesCO2(
                entrada,
                co2Producao, co2Embalagem, co2Logistica, co2FimDeVida, co2Total,
                co2PorTransacaoFisico,
                co2Servidor, co2Telecom, co2Dispositivo, co2Digital,
                co2PorTransacaoDigital, reducaoCO2Digital);

        return new ImpactoAmbiental(emissoes, recursos, equivalencias, METADADOS);
    }
}

