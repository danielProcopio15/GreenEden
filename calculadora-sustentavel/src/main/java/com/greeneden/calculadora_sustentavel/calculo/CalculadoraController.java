package com.greeneden.calculadora_sustentavel.calculo;

import com.greeneden.calculadora_sustentavel.calculo.model.CenarioDescarte;
import com.greeneden.calculadora_sustentavel.calculo.model.EntradaCalculo;
import com.greeneden.calculadora_sustentavel.calculo.model.ImpactoAmbiental;
import com.greeneden.calculadora_sustentavel.calculo.model.OrigemFabrica;
import com.greeneden.calculadora_sustentavel.calculo.model.TipoMaterial;
import com.greeneden.calculadora_sustentavel.calculo.model.TipoTransacaoDigital;
import com.greeneden.calculadora_sustentavel.calculo.CalculadoraService;
import com.greeneden.calculadora_sustentavel.calculo.GeolocalizacaoService;
import com.greeneden.calculadora_sustentavel.pedido.AutenticacaoService;
import com.greeneden.calculadora_sustentavel.pedido.PedidoService;
import com.greeneden.calculadora_sustentavel.pedido.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;

@Controller
public class CalculadoraController {

    private final CalculadoraService calculadoraService;
    private final GeolocalizacaoService geolocalizacaoService;
    private final AutenticacaoService autenticacaoService;
    private final PedidoService pedidoService;

    public CalculadoraController(CalculadoraService calculadoraService,
                                  GeolocalizacaoService geolocalizacaoService,
                                  AutenticacaoService autenticacaoService,
                                  PedidoService pedidoService) {
        this.calculadoraService    = calculadoraService;
        this.geolocalizacaoService = geolocalizacaoService;
        this.autenticacaoService   = autenticacaoService;
        this.pedidoService         = pedidoService;
    }

    @GetMapping("/")
    public String mostrarHome() {
        return "index";
    }

    @GetMapping("/calculadora")
    public String mostrarCalculadora(Model model) {
        model.addAttribute("cenarioDescarte", CenarioDescarte.values());
        model.addAttribute("origemFabrica", OrigemFabrica.values());
        return "calculadora";
    }

    @PostMapping("/calcular")
    public String calcularImpacto(
            @RequestParam("quantidadeCartoes") Integer quantidadeCartoes,
            @RequestParam("frequenciaRemessasAno") Integer frequenciaRemessasAno,
            @RequestParam("vidaUtilTransacoesPorCartao") Integer vidaUtil,
            @RequestParam("quantidadeTransacoes") Integer quantidadeTransacoes,
            @RequestParam("origemFabrica") String origemFabricaStr,
            @RequestParam("cepDestino") String cepDestino,
            @RequestParam("tipoTransporte") String tipoTransporte,
            @RequestParam("cenarioDescarte") String cenarioDescarteStr,
            HttpSession session,
            Model model) {

        if (quantidadeCartoes == null || quantidadeCartoes < 1) {
            return erro(model, "A quantidade de cartões deve ser maior que zero.");
        }
        if (frequenciaRemessasAno == null || frequenciaRemessasAno < 1) {
            return erro(model, "A frequência de remessas deve ser pelo menos 1 vez por ano.");
        }
        if (vidaUtil == null || vidaUtil < 1) {
            return erro(model, "A vida útil por cartão deve ser pelo menos 1 transação.");
        }
        if (quantidadeTransacoes == null || quantidadeTransacoes < 0) {
            return erro(model, "A quantidade de transações não pode ser negativa.");
        }
        String cepLimpo = cepDestino != null ? cepDestino.replaceAll("[^0-9]", "") : "";
        if (cepLimpo.length() != 8) {
            return erro(model, "CEP inválido. Informe os 8 dígitos do CEP de destino.");
        }

        OrigemFabrica origem;
        try {
            origem = OrigemFabrica.valueOf(origemFabricaStr);
        } catch (Exception e) {
            return erro(model, "Selecione a fábrica de origem.");
        }

        try {
            double distanciaKm = geolocalizacaoService.calcularDistancia(cepLimpo, origem, tipoTransporte);

            EntradaCalculo entrada = new EntradaCalculo();
            entrada.setQuantidadeCartoes(quantidadeCartoes);
            entrada.setFrequenciaRemessasAno(frequenciaRemessasAno);
            entrada.setTipoMaterial(TipoMaterial.PVC_RECICLADO);
            entrada.setVidaUtilTransacoesPorCartao(vidaUtil);
            entrada.setQuantidadeTransacoes(quantidadeTransacoes);
            entrada.setTipoTransacaoDigital(TipoTransacaoDigital.PIX);
            entrada.setOrigemFabrica(origem);
            entrada.setCepDestino(cepLimpo);
            entrada.setDistanciaLogistica(distanciaKm);
            entrada.setTipoTransporte(tipoTransporte);
            entrada.setCenarioDescarte(CenarioDescarte.valueOf(cenarioDescarteStr));

            ImpactoAmbiental impacto = calculadoraService.calcularImpacto(entrada);
            session.setAttribute("ultimoImpacto", impacto);
            model.addAttribute("impacto", impacto);
            return "resultado";

        } catch (Exception e) {
            return erro(model, "Erro ao realizar o cálculo: " + e.getMessage());
        }
    }

    private String erro(Model model, String mensagem) {
        model.addAttribute("erro", mensagem);
        model.addAttribute("cenarioDescarte", CenarioDescarte.values());
        model.addAttribute("origemFabrica", OrigemFabrica.values());
        return "calculadora";
    }

    @GetMapping("/resultado")
    public String mostrarResultado(HttpSession session, Model model) {
        ImpactoAmbiental impacto = (ImpactoAmbiental) session.getAttribute("ultimoImpacto");
        if (impacto == null) {
            return "redirect:/calculadora";
        }
        model.addAttribute("impacto", impacto);
        return "resultado";
    }

    @GetMapping("/beneficios")
    public String mostrarBeneficios(HttpSession session, Model model) {
        ImpactoAmbiental impacto = (ImpactoAmbiental) session.getAttribute("ultimoImpacto");
        if (impacto == null) {
            return "redirect:/calculadora";
        }
        model.addAttribute("impacto", impacto);
        return "beneficios";
    }

    @GetMapping("/seguranca")
    public String mostrarSeguranca(HttpSession session, Model model) {
        ImpactoAmbiental impacto = (ImpactoAmbiental) session.getAttribute("ultimoImpacto");
        if (impacto == null) {
            return "redirect:/calculadora";
        }
        model.addAttribute("impacto", impacto);
        return "seguranca";
    }

    @GetMapping("/compra")
    public String mostrarCompra(
            @RequestParam(defaultValue = "resultado") String from,
            HttpSession session, Model model) {
        ImpactoAmbiental impacto = (ImpactoAmbiental) session.getAttribute("ultimoImpacto");
        model.addAttribute("impacto", impacto);
        model.addAttribute("temImpacto", impacto != null);
        model.addAttribute("from", from);
        model.addAttribute("fromHome", Boolean.valueOf("home".equals(from)));
        return "compra";
    }

    @GetMapping("/contato")
    public String mostrarContato(
            @RequestParam(defaultValue = "") String plano,
            @RequestParam(defaultValue = "0") int quantidade,
            @RequestParam(defaultValue = "digital") String tipo,
            @RequestParam(defaultValue = "0") double precoTotal,
            @RequestParam(defaultValue = "0") double co2Evitado,
            @RequestParam(defaultValue = "0") double co2Fisico,
            @RequestParam(defaultValue = "0") double arvores,
            @RequestParam(defaultValue = "false") boolean fromHome,
            HttpSession session, Model model) {
        ImpactoAmbiental impacto = (ImpactoAmbiental) session.getAttribute("ultimoImpacto");
        model.addAttribute("impacto", impacto);
        model.addAttribute("plano", plano);
        model.addAttribute("quantidade", quantidade);
        model.addAttribute("tipo", tipo);
        model.addAttribute("precoTotal", precoTotal);
        model.addAttribute("co2Evitado", co2Evitado);
        model.addAttribute("co2Fisico", co2Fisico);
        model.addAttribute("arvores", arvores);
        model.addAttribute("fromHome", fromHome);
        return "contato";
    }

    @PostMapping("/contato")
    public String enviarContato(
            @RequestParam String nome,
            @RequestParam String cargo,
            @RequestParam String empresa,
            @RequestParam String cnpj,
            @RequestParam String email,
            @RequestParam String telefone,
            @RequestParam(defaultValue = "") String observacoes,
            @RequestParam(defaultValue = "") String plano,
            @RequestParam(defaultValue = "0") int quantidade,
            @RequestParam(defaultValue = "digital") String tipo,
            @RequestParam(defaultValue = "0") double precoTotal,
            @RequestParam(defaultValue = "0") double co2Evitado,
            @RequestParam(defaultValue = "0") double co2Fisico,
            @RequestParam(defaultValue = "0") double arvores,
            HttpSession session,
            Model model) {

        try {
            String protocolo = "GE-" + (100000 + (int)(Math.random() * 899999));

            Optional<Usuario> usuarioOpt = autenticacaoService.buscarPorEmail(email);
            Long usuarioId = null;

            if (usuarioOpt.isPresent()) {
                usuarioId = usuarioOpt.get().getId();
                session.setAttribute("usuarioId", usuarioId);
                session.setAttribute("usuarioNome", usuarioOpt.get().getNome());
                session.setAttribute("usuarioEmail", email);
            }

            ImpactoAmbiental impacto = (ImpactoAmbiental) session.getAttribute("ultimoImpacto");

            if (impacto != null) {
                pedidoService.criarPedido(
                        usuarioId,
                        impacto.getQuantidadeCartoes(),
                        impacto.getFrequenciaRemessasAno(),
                        impacto.getVidaUtilTransacoesPorCartao(),
                        impacto.getQuantidadeTransacoes(),
                        impacto.getOrigemFabrica().toString(),
                        impacto.getCepDestino(),
                        impacto.getTipoTransporte(),
                        impacto.getCenarioDescarte().toString(),
                        impacto.getDistanciaLogistica(),
                        impacto.getCo2Total(),
                        impacto.getCo2CenarioDigital(),
                        impacto.getReducaoCO2Digital(),
                        (double) impacto.getEquivalencias().getArvoresSalvas(),
                        precoTotal,
                        plano.isEmpty() ? "Padrão" : plano,
                        protocolo,
                        nome, email, empresa, cnpj, telefone, cargo
                );
            } else {
                // Compra direta da homepage: usa valores calculados pelo JS da página de compra
                double co2FisicoSalvo  = co2Fisico > 0 ? co2Fisico : (quantidade > 0 ? quantidade * 0.13 : 130.0);
                double co2DigitalSalvo = Math.max(0, co2FisicoSalvo - co2Evitado);
                pedidoService.criarPedido(
                        usuarioId,
                        quantidade > 0 ? quantidade : 1000,
                        12,
                        50,
                        150000,
                        "BRASIL",
                        "00000000",
                        "RODOVIARIO",
                        "ATERRO",
                        0.0,
                        co2FisicoSalvo,
                        co2DigitalSalvo,
                        co2Evitado,
                        arvores,
                        precoTotal,
                        plano.isEmpty() ? "Consulta Especialista" : plano,
                        protocolo,
                        nome, email, empresa, cnpj, telefone, cargo
                );
            }

            model.addAttribute("protocolo", protocolo);
            model.addAttribute("nome", nome);
            model.addAttribute("empresa", empresa);
            model.addAttribute("plano", plano);
            model.addAttribute("quantidade", quantidade);
            model.addAttribute("tipo", tipo);
            model.addAttribute("precoTotal", precoTotal);
            model.addAttribute("emailContato", email);
            model.addAttribute("mostrarModalConta", !usuarioOpt.isPresent());

            return "confirmacao";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao processar: " + e.getMessage());
            return "contato";
        }
    }
}