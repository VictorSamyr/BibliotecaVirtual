package biblioteca.repository;

import biblioteca.domain.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistência para usuários da Biblioteca Virtual.
 */
public interface UsuarioRepository {
    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorLogin(String login);

    Optional<Usuario> buscarPorEmail(String email);

    List<Usuario> listarTodos();

    boolean existePorLogin(String login);

    boolean removerPorId(int id);
}
