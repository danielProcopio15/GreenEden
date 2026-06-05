package com.greeneden.calculadora_sustentavel;

import com.greeneden.calculadora_sustentavel.model.Usuario;
import com.greeneden.calculadora_sustentavel.service.AutenticacaoService;
import com.greeneden.calculadora_sustentavel.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AutenticacaoController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String fazerLogin(
            @RequestParam String email,
            @RequestParam String senha,
            HttpSession session,
            Model model) {

        Optional<Usuario> usuario = autenticacaoService.autenticar(email, senha);
        if (usuario.isPresent()) {
            session.setAttribute("usuarioId", usuario.get().getId());
            session.setAttribute("usuarioNome", usuario.get().getNome());
            session.setAttribute("usuarioEmail", usuario.get().getEmail());

            // Vincula pedidos órfãos feitos com esse email antes de ter conta
            pedidoService.vincularPedidosOrfaos(usuario.get().getId(), email);

            return "redirect:/portal";
        } else {
            model.addAttribute("erro", "Email ou senha incorretos");
            return "login";
        }
    }

    @GetMapping("/cadastro")
    public String mostrarCadastro(
            @RequestParam(required = false) String email,
            Model model) {
        if (email != null && !email.isBlank()) {
            model.addAttribute("emailPreenchido", email);
        }
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String fazerCadastro(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam(defaultValue = "") String empresa,
            @RequestParam(required = false) String cnpj,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String cargo,
            @RequestParam String senha,
            @RequestParam String senhaConfirm,
            HttpSession session,
            Model model) {

        if (!senha.equals(senhaConfirm)) {
            model.addAttribute("erro", "As senhas não coincidem");
            model.addAttribute("emailPreenchido", email);
            return "cadastro";
        }

        if (senha.length() < 8) {
            model.addAttribute("erro", "A senha deve ter pelo menos 8 caracteres");
            model.addAttribute("emailPreenchido", email);
            return "cadastro";
        }

        if (!validarSenhaForte(senha)) {
            model.addAttribute("erro", "A senha deve conter: maiúsculas, minúsculas, números e caracteres especiais (!@#$%^&*)");
            model.addAttribute("emailPreenchido", email);
            return "cadastro";
        }

        try {
            Usuario usuario = autenticacaoService.registrarUsuario(
                    nome, email, empresa, cnpj, telefone, cargo, senha);

            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNome", usuario.getNome());
            session.setAttribute("usuarioEmail", usuario.getEmail());

            // Vincula pedidos órfãos feitos com esse email antes de ter conta
            pedidoService.vincularPedidosOrfaos(usuario.getId(), email);

            return "redirect:/portal";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("emailPreenchido", email);
            return "cadastro";
        }
    }

    private boolean validarSenhaForte(String senha) {
        return senha.matches(".*[A-Z].*")
            && senha.matches(".*[a-z].*")
            && senha.matches(".*[0-9].*")
            && senha.matches(".*[!@#$%^&*].*");
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}