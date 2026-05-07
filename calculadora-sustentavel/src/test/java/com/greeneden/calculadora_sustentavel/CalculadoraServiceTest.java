package com.greeneden.calculadora_sustentavel;

import com.greeneden.calculadora_sustentavel.model.CenarioDescarte;
import com.greeneden.calculadora_sustentavel.model.EntradaCalculo;
import com.greeneden.calculadora_sustentavel.model.OrigemFabrica;
import com.greeneden.calculadora_sustentavel.model.TipoTransacaoDigital;
import com.greeneden.calculadora_sustentavel.service.CalculadoraService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraServiceTest {

    @Test
    void deveGerarBeneficiosOperacionaisEComparativoDigital() {
        CalculadoraService service = new CalculadoraService();

        EntradaCalculo entrada = new EntradaCalculo();
        entrada.setQuantidadeCartoes(1000);
        entrada.setFrequenciaRemessasAno(2);
        entrada.setVidaUtilTransacoesPorCartao(1500);
        entrada.setQuantidadeTransacoes(300000);
        entrada.setTipoTransacaoDigital(TipoTransacaoDigital.PIX);
        entrada.setOrigemFabrica(OrigemFabrica.COTIA_SP);
        entrada.setCepDestino("06700-000");
        entrada.setDistanciaLogistica(28.0);
        entrada.setTipoTransporte("RODOVIARIO");
        entrada.setCenarioDescarte(CenarioDescarte.RECICLAGEM);

        var impacto = service.calcularImpacto(entrada);

        assertNotNull(impacto);
        assertNotNull(impacto.getBeneficiosOperacionais());
        assertTrue(impacto.getBeneficiosOperacionais().getCartoesEvitados() > 0);
        assertTrue(impacto.getBeneficiosOperacionais().getKmLogisticaEvitados() > 0);
        assertTrue(impacto.getBeneficiosOperacionais().getReducaoCO2Distribuicao() > 0);
        assertTrue(impacto.getBeneficiosOperacionais().getPerdasEvitadasEstimadas() > 0);
        assertTrue(impacto.getBeneficiosOperacionais().getIndiceReducaoPerdaRoubo() > 0);
        assertTrue(impacto.getBeneficiosOperacionais().getIndiceMenorRiscoClonagem() > 0);
        assertTrue(impacto.getBeneficiosOperacionais().getIndiceControleUsuario() > 0);
        assertTrue(impacto.getBeneficiosOperacionais().getIndiceAutenticacaoAdicional() > 0);
        assertNotNull(impacto.getBeneficiosOperacionais().getComparativoSeguranca());
        assertEquals(300000, impacto.getQuantidadeTransacoes());
        assertTrue(impacto.getCo2PorTransacaoDigital() > 0);
        assertTrue(impacto.getCo2PorTransacaoFisico() > impacto.getCo2PorTransacaoDigital());
    }
}
