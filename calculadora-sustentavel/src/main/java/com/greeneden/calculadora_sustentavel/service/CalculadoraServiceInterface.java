package com.greeneden.calculadora_sustentavel.service;

import com.greeneden.calculadora_sustentavel.model.ImpactoAmbiental;

public interface CalculadoraServiceInterface {
    ImpactoAmbiental calcularImpacto(int quantidade);
}