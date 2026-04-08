package com.greeneden.calculadora_sustentavel.model;

public class ImpactoAmbiental {

    // Entradas
    private int quantidadeCartoes;
    private int quantidadeTransacoes;
    private double distanciaLogistica;
    private String tipoTransporte;

    // CO₂ por etapa (kg CO₂e)
    private double co2Producao;
    private double co2Embalagem;
    private double co2Logistica;
    private double co2Total;

    // Cenário digital
    private double co2CenarioDigital;
    private double reducaoCO2Digital;

    // Recursos consumidos
    private double consumoPlastico;   // kg
    private double consumoPapel;      // kg
    private double consumoAgua;       // litros
    private double consumoEnergia;    // kWh

    // Equivalências
    private int arvoresSalvas;
    private int kmCarroEvitado;

    public ImpactoAmbiental(int quantidadeCartoes, int quantidadeTransacoes,
                            double distanciaLogistica, String tipoTransporte,
                            double co2Producao, double co2Embalagem, double co2Logistica,
                            double co2Total, double co2CenarioDigital, double reducaoCO2Digital,
                            double consumoPlastico, double consumoPapel,
                            double consumoAgua, double consumoEnergia,
                            int arvoresSalvas, int kmCarroEvitado) {
        this.quantidadeCartoes = quantidadeCartoes;
        this.quantidadeTransacoes = quantidadeTransacoes;
        this.distanciaLogistica = distanciaLogistica;
        this.tipoTransporte = tipoTransporte;
        this.co2Producao = co2Producao;
        this.co2Embalagem = co2Embalagem;
        this.co2Logistica = co2Logistica;
        this.co2Total = co2Total;
        this.co2CenarioDigital = co2CenarioDigital;
        this.reducaoCO2Digital = reducaoCO2Digital;
        this.consumoPlastico = consumoPlastico;
        this.consumoPapel = consumoPapel;
        this.consumoAgua = consumoAgua;
        this.consumoEnergia = consumoEnergia;
        this.arvoresSalvas = arvoresSalvas;
        this.kmCarroEvitado = kmCarroEvitado;
    }

    public int getQuantidadeCartoes() { return quantidadeCartoes; }
    public int getQuantidadeTransacoes() { return quantidadeTransacoes; }
    public double getDistanciaLogistica() { return distanciaLogistica; }
    public String getTipoTransporte() { return tipoTransporte; }
    public double getCo2Producao() { return co2Producao; }
    public double getCo2Embalagem() { return co2Embalagem; }
    public double getCo2Logistica() { return co2Logistica; }
    public double getCo2Total() { return co2Total; }
    public double getCo2CenarioDigital() { return co2CenarioDigital; }
    public double getReducaoCO2Digital() { return reducaoCO2Digital; }
    public double getConsumoPlastico() { return consumoPlastico; }
    public double getConsumoPapel() { return consumoPapel; }
    public double getConsumoAgua() { return consumoAgua; }
    public double getConsumoEnergia() { return consumoEnergia; }
    public int getArvoresSalvas() { return arvoresSalvas; }
    public int getKmCarroEvitado() { return kmCarroEvitado; }

    public void setQuantidadeCartoes(int quantidadeCartoes) { this.quantidadeCartoes = quantidadeCartoes; }
    public void setQuantidadeTransacoes(int quantidadeTransacoes) { this.quantidadeTransacoes = quantidadeTransacoes; }
    public void setDistanciaLogistica(double distanciaLogistica) { this.distanciaLogistica = distanciaLogistica; }
    public void setTipoTransporte(String tipoTransporte) { this.tipoTransporte = tipoTransporte; }
    public void setCo2Producao(double co2Producao) { this.co2Producao = co2Producao; }
    public void setCo2Embalagem(double co2Embalagem) { this.co2Embalagem = co2Embalagem; }
    public void setCo2Logistica(double co2Logistica) { this.co2Logistica = co2Logistica; }
    public void setCo2Total(double co2Total) { this.co2Total = co2Total; }
    public void setCo2CenarioDigital(double co2CenarioDigital) { this.co2CenarioDigital = co2CenarioDigital; }
    public void setReducaoCO2Digital(double reducaoCO2Digital) { this.reducaoCO2Digital = reducaoCO2Digital; }
    public void setConsumoPlastico(double consumoPlastico) { this.consumoPlastico = consumoPlastico; }
    public void setConsumoPapel(double consumoPapel) { this.consumoPapel = consumoPapel; }
    public void setConsumoAgua(double consumoAgua) { this.consumoAgua = consumoAgua; }
    public void setConsumoEnergia(double consumoEnergia) { this.consumoEnergia = consumoEnergia; }
    public void setArvoresSalvas(int arvoresSalvas) { this.arvoresSalvas = arvoresSalvas; }
    public void setKmCarroEvitado(int kmCarroEvitado) { this.kmCarroEvitado = kmCarroEvitado; }
}