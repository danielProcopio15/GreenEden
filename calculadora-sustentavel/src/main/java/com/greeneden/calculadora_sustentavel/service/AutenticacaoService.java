package com.greeneden.calculadora_sustentavel.service;

import com.greeneden.calculadora_sustentavel.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AutenticacaoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(String nome, String email, String empresa, String cnpj, String telefone, String cargo, String senha) {
        // Verifica se email já existe
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Cria novo usuário
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setEmpresa(empresa);
        usuario.setCnpj(cnpj);
        usuario.setTelefone(telefone);
        usuario.setCargo(cargo);
        usuario.setSenha(passwordEncoder.encode(senha));

        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent() && passwordEncoder.matches(senha, usuario.get().getSenha())) {
            return usuario;
        }
        return Optional.empty();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario atualizarUsuario(Long id, String nome, String cargo, String empresa, String cnpj, String telefone) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            u.setNome(nome);
            u.setCargo(cargo);
            u.setEmpresa(empresa);
            u.setCnpj(cnpj);
            u.setTelefone(telefone);
            return usuarioRepository.save(u);
        }
        throw new IllegalArgumentException("Usuário não encontrado");
    }

    public void alterarSenha(Long id, String senhaAnterior, String novaSenha) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            if (passwordEncoder.matches(senhaAnterior, u.getSenha())) {
                u.setSenha(passwordEncoder.encode(novaSenha));
                usuarioRepository.save(u);
            } else {
                throw new IllegalArgumentException("Senha atual incorreta");
            }
        } else {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
    }
}
