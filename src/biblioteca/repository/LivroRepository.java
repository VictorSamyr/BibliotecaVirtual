package biblioteca.repository;

import biblioteca.domain.Genero;
import biblioteca.domain.Livro;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistência para o catálogo de livros.
 */
public interface LivroRepository {
    Livro salvar(Livro livro);

    List<Livro> listarTodos();

    Optional<Livro> buscarPorId(int id);

    List<Livro> buscarPorGenero(Genero genero);

    List<Livro> buscarPorTermo(String termo);

    boolean removerPorId(int id);
}
