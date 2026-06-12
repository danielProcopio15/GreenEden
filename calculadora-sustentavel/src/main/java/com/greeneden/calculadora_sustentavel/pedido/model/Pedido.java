package com.greeneden.calculadora_sustentavel.pedido.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable = true: pedido pode existir sem conta criada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    // Dados de contato direto (preenchidos quando não há conta)
    @Column(length = 255)
    private String nomeContato;

    @Column(length = 255)
    private String emailContato;

    @Column(length = 255)
    private String empresaContato;

    @Column(length = 255)
    private String telefoneContato;

    @Column(length = 255)
    private String cargoContato;

    @Column(length = 20)
    private String cnpjContato;

    // Parâmetros da simulação
    @Column(nullable = false)
    private Integer quantidadeCartoes;

    @Column(nullable = false)
    private Integer remessasPorAno;

    @Column(nullable = false)
    private Integer vidaUtilTransacoesPorCartao;

    @Column(nullable = false)
    private Integer quantidadeTransacoes;

    @Column(length = 100)
    private String origemFabrica;

    @Column(length = 100)
    private String cepDestino;

    @Column(length = 100)
    private String tipoTransporte;

    @Column(length = 100)
    private String cenarioDescarte;

    @Column
    private Double distanciaLogistica;

    // Resultados da simulação
    @Column
    private Double co2Fisico;

    @Column
    private Double co2Digital;

    @Column
    private Double co2Evitado;

    @Column
    private Double arvoresEquivalentes;

    @Column
    private Double precoTotal;

    @Column(length = 50)
    private String tipoPlano; // "Essencial", "Recomendado", "Escala"

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(length = 50)
    private String status = "Ativo"; // "Pendente", "Ativo", "Cancelado"

    @Column(length = 4000)
    private String dadosSalvosJson;

    // Número de protocolo gerado na confirmação (ex: GE-905739)
    @Column(length = 20)
    private String numeroProtocolo;
}