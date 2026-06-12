package com.greeneden.calculadora_sustentavel.pedido;

import com.greeneden.calculadora_sustentavel.pedido.model.Pedido;
import com.greeneden.calculadora_sustentavel.pedido.model.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Pedido criarPedido(Long usuarioId,
                              Integer quantidadeCartoes,
                              Integer remessasPorAno,
                              Integer vidaUtilTransacoesPorCartao,
                              Integer quantidadeTransacoes,
                              String origemFabrica,
                              String cepDestino,
                              String tipoTransporte,
                              String cenarioDescarte,
                              Double distanciaLogistica,
                              Double co2Fisico,
                              Double co2Digital,
                              Double co2Evitado,
                              Double arvoresEquivalentes,
                              Double precoTotal,
                              String tipoPlano,
                              String protocolo,
                              String nomeContato,
                              String emailContato,
                              String empresaContato,
                              String cnpjContato,
                              String telefoneContato,
                              String cargoContato) {

        Pedido pedido = new Pedido();

        if (usuarioId != null) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            usuarioOpt.ifPresent(pedido::setUsuario);
        }

        pedido.setNomeContato(nomeContato);
        pedido.setEmailContato(emailContato);
        pedido.setEmpresaContato(empresaContato);
        pedido.setCnpjContato(cnpjContato);
        pedido.setTelefoneContato(telefoneContato);
        pedido.setCargoContato(cargoContato);
        pedido.setNumeroProtocolo(protocolo);
        pedido.setQuantidadeCartoes(quantidadeCartoes);
        pedido.setRemessasPorAno(remessasPorAno);
        pedido.setVidaUtilTransacoesPorCartao(vidaUtilTransacoesPorCartao);
        pedido.setQuantidadeTransacoes(quantidadeTransacoes);
        pedido.setOrigemFabrica(origemFabrica);
        pedido.setCepDestino(cepDestino);
        pedido.setTipoTransporte(tipoTransporte);
        pedido.setCenarioDescarte(cenarioDescarte);
        pedido.setDistanciaLogistica(distanciaLogistica);
        pedido.setCo2Fisico(co2Fisico);
        pedido.setCo2Digital(co2Digital);
        pedido.setCo2Evitado(co2Evitado);
        pedido.setArvoresEquivalentes(arvoresEquivalentes);
        pedido.setPrecoTotal(precoTotal);
        pedido.setTipoPlano(tipoPlano);
        pedido.setStatus("Ativo");

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidosDoUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId);
    }

    public List<Pedido> listarPedidosAtivos(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdAndStatusOrderByCriadoEmDesc(usuarioId, "Ativo");
    }

    public List<Pedido> listarHistorico(Long usuarioId) {
        return pedidoRepository.findHistoricoPorUsuario(usuarioId);
    }

    public Optional<Pedido> obterPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId);
    }

    public void cancelarPedido(Long pedidoId) {
        Optional<Pedido> pedido = pedidoRepository.findById(pedidoId);
        if (pedido.isPresent()) {
            pedido.get().setStatus("Cancelado");
            pedidoRepository.save(pedido.get());
        }
    }

    // Vincula pedidos feitos sem conta ao usuário após ele criar/logar
    @Transactional
    public int vincularPedidosOrfaos(Long usuarioId, String email) {
        return pedidoRepository.vincularPedidosOrfaos(usuarioId, email);
    }

    /**
     * Atualiza quantidade e remessas do pedido, escalando todos os valores de CO₂
     * proporcionalmente ao novo volume total (novaQtd × novasRemessas).
     */
    @Transactional
    public void atualizarQuantidades(Long pedidoId, int novaQtd, int novasRemessas) {
        Optional<Pedido> opt = pedidoRepository.findById(pedidoId);
        if (opt.isEmpty()) return;
        Pedido p = opt.get();

        int oldTotal = (p.getQuantidadeCartoes() != null ? p.getQuantidadeCartoes() : 1)
                     * (p.getRemessasPorAno()     != null ? p.getRemessasPorAno()     : 1);
        int newTotal = novaQtd * novasRemessas;
        if (oldTotal <= 0 || newTotal <= 0) return;

        double scale = (double) newTotal / oldTotal;

        p.setQuantidadeCartoes(novaQtd);
        p.setRemessasPorAno(novasRemessas);
        if (p.getQuantidadeTransacoes() != null)
            p.setQuantidadeTransacoes((int) Math.round(p.getQuantidadeTransacoes() * scale));
        if (p.getCo2Fisico() != null)       p.setCo2Fisico(p.getCo2Fisico() * scale);
        if (p.getCo2Digital() != null)      p.setCo2Digital(p.getCo2Digital() * scale);
        if (p.getCo2Evitado() != null)      p.setCo2Evitado(p.getCo2Evitado() * scale);
        if (p.getArvoresEquivalentes() != null) p.setArvoresEquivalentes(p.getArvoresEquivalentes() * scale);

        pedidoRepository.save(p);
    }
}