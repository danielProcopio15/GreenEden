package com.greeneden.calculadora_sustentavel.service;

import com.greeneden.calculadora_sustentavel.model.EntradaCalculo;
import com.greeneden.calculadora_sustentavel.model.ImpactoAmbiental;

public interface CalculadoraServiceInterface {
    ImpactoAmbiental calcularImpacto(EntradaCalculo entrada);
}