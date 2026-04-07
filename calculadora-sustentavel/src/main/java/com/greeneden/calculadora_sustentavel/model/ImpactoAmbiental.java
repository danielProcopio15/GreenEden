package com.greeneden.calculadora_sustentavel.model;

public class ImpactoAmbiental {

    private int quantidadeTransacoes;
    private double emissaoCO2;
    private double consumoPlastico;
    private double consumoPapel;
    private double reducaoCO2Digital;
    private int arvoresSalvas;
    private int kmCarroEvitado;


    
    public ImpactoAmbiental(int quantidadeTransacoes, double emissaoCO2, double consumoPlastico,
                            double consumoPapel, double reducaoCO2Digital, int arvoresSalvas, int kmCarroEvitado) {
        this.quantidadeTransacoes = quantidadeTransacoes;
        this.emissaoCO2 = emissaoCO2;
        this.consumoPlastico = consumoPlastico;
        this.consumoPapel = consumoPapel;
        this.reducaoCO2Digital = reducaoCO2Digital;
        this.arvoresSalvas = arvoresSalvas;
        this.kmCarroEvitado = kmCarroEvitado;
    }

    
    public int getQuantidadeTransacoes() { return quantidadeTransacoes; }
    public double getEmissaoCO2() { return emissaoCO2; }
    public double getConsumoPlastico() { return consumoPlastico; }
    public double getConsumoPapel() { return consumoPapel; }
    public double getReducaoCO2Digital() { return reducaoCO2Digital; }
    public int getArvoresSalvas() { return arvoresSalvas; }
    public int getKmCarroEvitado() { return kmCarroEvitado; }

    

    public void setQuantidadeTransacoes(int quantidadeTransacoes) { this.quantidadeTransacoes = quantidadeTransacoes; }
    public void setEmissaoCO2(double emissaoCO2) { this.emissaoCO2 = emissaoCO2; }
    public void setConsumoPlastico(double consumoPlastico) { this.consumoPlastico = consumoPlastico; }
    public void setConsumoPapel(double consumoPapel) { this.consumoPapel = consumoPapel; }
    public void setReducaoCO2Digital(double reducaoCO2Digital) { this.reducaoCO2Digital = reducaoCO2Digital; }
    public void setArvoresSalvas(int arvoresSalvas) { this.arvoresSalvas = arvoresSalvas; }
    public void setKmCarroEvitado(int kmCarroEvitado) { this.kmCarroEvitado = kmCarroEvitado; }
}