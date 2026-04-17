package com.greeneden.calculadora_sustentavel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecursosConsumidos {

    private double consumoPlastico;   // kg
    private double consumoPapel;      // kg
    private double consumoAgua;       // litros
    private double consumoEnergia;    // kWh
}
