package biblioteca.service;

import biblioteca.domain.Genero;
import biblioteca.domain.Sexo;
import biblioteca.domain.Usuario;
import biblioteca.repository.UsuarioRepository;
import biblioteca.security.PasswordHasher;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Regras de negócio de autenticação e registro de usuários, desacopladas da interface gráfica.
 */
public final class AutenticacaoService {
    private static final int TAMANHO_MINIMO_SENHA = 5;

    private final UsuarioRepository usuarioRepository;

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository, "usuarioRepository não pode ser nulo.");
    }

    public Optional<Usuario> autenticar(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isEmpty()) {
            return Optional.empty();
        }
        return usuarioRepository.buscarPorLogin(login.trim())
                .filter(u -> PasswordHasher.verify(senha, u.getSenhaHash()));
    }

    public Usuario registrar(
            String nome,
            String login,
            String email,
            Sexo sexo,
            int idade,
            Set<Genero> generosPreferidos,
            String senha,
            String confirmacaoSenha
    ) {
        if (nome == null || nome.isBlank()
                || login == null || login.isBlank()
                || email == null || email.isBlank()
                || senha == null || senha.isEmpty()
                || confirmacaoSenha == null || confirmacaoSenha.isEmpty()) {
            throw new IllegalArgumentException("Preencha todos os campos obrigatórios.");
        }
        if (!senha.equals(confirmacaoSenha)) {
            throw new IllegalArgumentException("A senha e a confirmação de senha não coincidem.");
        }
        if (senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new IllegalArgumentException("A senha deve ter no mínimo " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
        String loginNormalizado = login.trim();
        if (usuarioRepository.existePorLogin(loginNormalizado)) {
            throw new IllegalArgumentException("Este login já está registrado.");
        }
        String emailNormalizado = email.trim();
        if (usuarioRepository.buscarPorEmail(emailNormalizado).isPresent()) {
            throw new IllegalArgumentException("Este endereço de e-mail já está registrado.");
        }

        String hash = PasswordHasher.hash(senha);
        Usuario novoUsuario = new Usuario(
                0,
                nome.trim(),
                loginNormalizado,
                emailNormalizado,
                sexo,
                idade,
                generosPreferidos,
                hash,
                false
        );
        return usuarioRepository.salvar(novoUsuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.listarTodos();
    }

    public boolean removerUsuario(int id) {
        return usuarioRepository.removerPorId(id);
    }
}
