package biblioteca.repository;

import biblioteca.domain.Genero;
import biblioteca.domain.Sexo;
import biblioteca.domain.Usuario;
import biblioteca.security.PasswordHasher;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repositório de usuários em memória com contas de demonstração pré-cadastradas.
 */
public final class InMemoryUsuarioRepository implements UsuarioRepository {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final AtomicInteger sequence = new AtomicInteger(0);

    public InMemoryUsuarioRepository() {
        this(true);
    }

    public InMemoryUsuarioRepository(boolean carregarContasDemo) {
        if (carregarContasDemo) {
            salvar(new Usuario(
                    0,
                    "Administrador da Biblioteca",
                    "admin",
                    "admin@biblioteca.local",
                    Sexo.MASCULINO,
                    22,
                    Set.of(Genero.FICCAO_CIENTIFICA, Genero.SUSPENSE, Genero.HISTORIA_EM_QUADRINHOS),
                    PasswordHasher.hash("admin123"),
                    true
            ));
            salvar(new Usuario(
                    0,
                    "Leitor Demonstração",
                    "leitor",
                    "leitor@biblioteca.local",
                    Sexo.FEMININO,
                    20,
                    Set.of(Genero.ROMANCE, Genero.FICCAO_CIENTIFICA),
                    PasswordHasher.hash("leitor123"),
                    false
            ));
        }
    }

    @Override
    public synchronized Usuario salvar(Usuario usuario) {
        int id = usuario.getId() > 0 ? usuario.getId() : sequence.incrementAndGet();
        sequence.updateAndGet(atual -> Math.max(atual, id));
        Usuario persistido = usuario.withId(id);
        usuarios.removeIf(u -> u.getId() == id);
        usuarios.add(persistido);
        return persistido;
    }

    @Override
    public synchronized Optional<Usuario> buscarPorLogin(String login) {
        if (login == null || login.isBlank()) {
            return Optional.empty();
        }
        String normalizado = login.trim();
        return usuarios.stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(normalizado))
                .findFirst();
    }

    @Override
    public synchronized Optional<Usuario> buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        String normalizado = email.trim();
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(normalizado))
                .findFirst();
    }

    @Override
    public synchronized List<Usuario> listarTodos() {
        return List.copyOf(usuarios);
    }

    @Override
    public synchronized boolean existePorLogin(String login) {
        return buscarPorLogin(login).isPresent();
    }

    @Override
    public synchronized boolean removerPorId(int id) {
        return usuarios.removeIf(u -> u.getId() == id);
    }
}
