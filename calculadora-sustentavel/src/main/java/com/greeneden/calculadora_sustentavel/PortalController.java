package com.greeneden.calculadora_sustentavel;

import com.greeneden.calculadora_sustentavel.model.Pedido;
import com.greeneden.calculadora_sustentavel.model.Usuario;
import com.greeneden.calculadora_sustentavel.service.AutenticacaoService;
import com.greeneden.calculadora_sustentavel.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/portal")
public class PortalController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private PedidoService pedidoService;

    private String verificarAutenticacao(HttpSession session, Model model) {
        if (session.getAttribute("usuarioId") == null) {
            return "redirect:/auth/login";
        }
        return null;
    }

    @GetMapping("")
    public String dashboard(HttpSession session, Model model) {
        String redirectCheck = verificarAutenticacao(session, model);
        if (redirectCheck != null) return redirectCheck;

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioNome = (String) session.getAttribute("usuarioNome");

        List<Pedido> pedidos = pedidoService.listarPedidosDoUsuario(usuarioId);

        double totalCO2 = pedidos.stream().filter(p -> p.getCo2Evitado() != null)
                .mapToDouble(Pedido::getCo2Evitado).sum();
        double totalArvores = pedidos.stream().filter(p -> p.getArvoresEquivalentes() != null)
                .mapToDouble(Pedido::getArvoresEquivalentes).sum();
        double totalInvestimento = pedidos.stream().filter(p -> p.getPrecoTotal() != null)
                .mapToDouble(Pedido::getPrecoTotal).sum();

        // Dashboard mostra só os 5 pedidos ATIVOS mais recentes
        List<Pedido> pedidosAtivos = pedidos.stream()
                .filter(p -> "Ativo".equals(p.getStatus()))
                .limit(5).toList();

        model.addAttribute("usuarioNome", usuarioNome);
        model.addAttribute("totalCO2Evitado", String.format("%.2f", totalCO2));
        model.addAttribute("totalArvoresEquivalentes", (long) totalArvores);
        model.addAttribute("totalPedidos", pedidos.size());
        model.addAttribute("pedidosAtivos", pedidos.stream().filter(p -> "Ativo".equals(p.getStatus())).count());
        model.addAttribute("ultimosPedidos", pedidosAtivos);
        model.addAttribute("totalInvestimento", String.format("%.2f", totalInvestimento));

        return "portal";
    }

    @GetMapping("/pedidos")
    public String listarPedidos(HttpSession session, Model model) {
        String redirectCheck = verificarAutenticacao(session, model);
        if (redirectCheck != null) return redirectCheck;

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioNome = (String) session.getAttribute("usuarioNome");

        List<Pedido> ativos = pedidoService.listarPedidosAtivos(usuarioId);
        List<Pedido> historico = pedidoService.listarHistorico(usuarioId);
        List<Pedido> todos = pedidoService.listarPedidosDoUsuario(usuarioId);

        double totalPreco = ativos.stream().filter(p -> p.getPrecoTotal() != null)
                .mapToDouble(Pedido::getPrecoTotal).sum();
        double totalCO2 = ativos.stream().filter(p -> p.getCo2Evitado() != null)
                .mapToDouble(Pedido::getCo2Evitado).sum();
        double totalArvores = ativos.stream().filter(p -> p.getArvoresEquivalentes() != null)
                .mapToDouble(Pedido::getArvoresEquivalentes).sum();

        model.addAttribute("usuarioNome", usuarioNome);
        model.addAttribute("pedidos", ativos);
        model.addAttribute("historico", historico);
        model.addAttribute("totalPedidos", todos.size());
        model.addAttribute("totalPreco", String.format("%.2f", totalPreco));
        model.addAttribute("totalCO2Pedidos", String.format("%.2f", totalCO2));
        model.addAttribute("totalArvoresPedidos", (long) totalArvores);

        return "portal-pedidos";
    }

    @GetMapping("/pedido/{id}")
    public String verPedido(@PathVariable Long id, HttpSession session, Model model) {
        String redirectCheck = verificarAutenticacao(session, model);
        if (redirectCheck != null) return redirectCheck;

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioNome = (String) session.getAttribute("usuarioNome");

        Optional<Pedido> pedidoOpt = pedidoService.obterPorId(id);
        if (pedidoOpt.isEmpty() || pedidoOpt.get().getUsuario() == null
                || !pedidoOpt.get().getUsuario().getId().equals(usuarioId)) {
            return "redirect:/portal/pedidos";
        }

        Pedido pedido = pedidoOpt.get();
        String tipoPlano = pedido.getTipoPlano() != null ? pedido.getTipoPlano().toLowerCase() : "";
        boolean isDigital = !tipoPlano.contains("fisico")
                && !tipoPlano.contains("físico")
                && !tipoPlano.contains("personalizado");

        double reducaoPercentual = 0;
        if (isDigital && pedido.getCo2Fisico() != null && pedido.getCo2Fisico() > 0) {
            reducaoPercentual = (pedido.getCo2Evitado() / pedido.getCo2Fisico()) * 100;
        }

        model.addAttribute("usuarioNome", usuarioNome);
        model.addAttribute("pedido", pedido);
        model.addAttribute("isDigital", isDigital);
        model.addAttribute("reducaoPercentual", String.format("%.1f", reducaoPercentual));

        return "portal-pedido-detail";
    }

    @PostMapping("/pedido/{id}/cancelar")
    public String cancelarPedido(@PathVariable Long id, HttpSession session, Model model) {
        String redirectCheck = verificarAutenticacao(session, model);
        if (redirectCheck != null) return redirectCheck;

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        Optional<Pedido> pedidoOpt = pedidoService.obterPorId(id);

        if (pedidoOpt.isEmpty() || pedidoOpt.get().getUsuario() == null
                || !pedidoOpt.get().getUsuario().getId().equals(usuarioId)) {
            return "redirect:/portal/pedidos";
        }

        pedidoService.cancelarPedido(id);
        return "redirect:/portal/pedidos?cancelado=true";
    }

    @GetMapping("/configuracoes")
    public String configuracoes(HttpSession session, Model model) {
        String redirectCheck = verificarAutenticacao(session, model);
        if (redirectCheck != null) return redirectCheck;

        Optional<Usuario> usuarioOpt = autenticacaoService.buscarPorEmail(
                (String) session.getAttribute("usuarioEmail"));
        usuarioOpt.ifPresent(u -> model.addAttribute("usuario", u));
        model.addAttribute("usuarioNome", session.getAttribute("usuarioNome"));

        return "portal-configuracoes";
    }

    @PostMapping("/configuracoes")
    public String atualizarConfiguracoes(
            @RequestParam String nome,
            @RequestParam String cargo,
            @RequestParam String empresa,
            @RequestParam(required = false) String cnpj,
            @RequestParam(required = false) String telefone,
            HttpSession session, Model model) {

        String redirectCheck = verificarAutenticacao(session, model);
        if (redirectCheck != null) return redirectCheck;

        try {
            Long usuarioId = (Long) session.getAttribute("usuarioId");
            autenticacaoService.atualizarUsuario(usuarioId, nome, cargo, empresa, cnpj, telefone);
            session.setAttribute("usuarioNome", nome);
            return "redirect:/portal/configuracoes?sucesso=true";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            return "portal-configuracoes";
        }
    }

    @PostMapping("/alterar-senha")
    public String alterarSenha(
            @RequestParam String senhaAnterior,
            @RequestParam String novaSenha,
            @RequestParam String novasSenhaConfirm,
            HttpSession session, Model model) {

        String redirectCheck = verificarAutenticacao(session, model);
        if (redirectCheck != null) return redirectCheck;

        try {
            Long usuarioId = (Long) session.getAttribute("usuarioId");
            if (!novaSenha.equals(novasSenhaConfirm)) {
                model.addAttribute("erro", "As senhas não coincidem");
                return "portal-configuracoes";
            }
            autenticacaoService.alterarSenha(usuarioId, senhaAnterior, novaSenha);
            return "redirect:/portal/configuracoes?sucesso=true";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            return "portal-configuracoes";
        }
    }
}