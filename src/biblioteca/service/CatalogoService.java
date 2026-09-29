package biblioteca.service;

import biblioteca.domain.Genero;
import biblioteca.domain.Livro;
import biblioteca.domain.Usuario;
import biblioteca.repository.LivroRepository;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Serviço de aplicação responsável pela consulta, filtragem, recomendação e gestão do acervo.
 */
public final class CatalogoService {
    private final LivroRepository livroRepository;

    public CatalogoService(LivroRepository livroRepository) {
        this.livroRepository = Objects.requireNonNull(livroRepository, "livroRepository não pode ser nulo.");
    }

    public List<Livro> listarTodos() {
        return livroRepository.listarTodos();
    }

    public List<Livro> buscarPorGenero(Genero genero) {
        return livroRepository.buscarPorGenero(genero);
    }

    public List<Livro> buscarPorTermo(String termo) {
        return livroRepository.buscarPorTermo(termo);
    }

    /**
     * Recomenda livros cujo gênero pertença às preferências do usuário.
     * Caso o usuário não tenha gêneros selecionados, retorna o acervo completo.
     */
    public List<Livro> recomendarParaUsuario(Usuario usuario) {
        if (usuario == null || usuario.getGenerosPreferidos().isEmpty()) {
            return listarTodos();
        }
        Set<Genero> preferidos = usuario.getGenerosPreferidos();
        return livroRepository.listarTodos().stream()
                .filter(l -> preferidos.contains(l.getGenero()))
                .toList();
    }

    /**
     * Filtra o acervo combinando gênero, busca textual e filtro de recomendações do usuário.
     */
    public List<Livro> filtrar(Genero genero, String termo, boolean apenasRecomendados, Usuario usuario) {
        List<Livro> base = apenasRecomendados ? recomendarParaUsuario(usuario) : listarTodos();
        String q = termo == null ? "" : termo.trim().toLowerCase(Locale.ROOT);

        return base.stream()
                .filter(l -> genero == null || l.getGenero() == genero)
                .filter(l -> q.isEmpty()
                        || l.getTitulo().toLowerCase(Locale.ROOT).contains(q)
                        || l.getAutor().toLowerCase(Locale.ROOT).contains(q))
                .toList();
    }

    public Livro cadastrarLivro(
            String titulo,
            String autor,
            Genero genero,
            int anoLancamento,
            String sinopse,
            String imagem
    ) {
        String caminhoImagem = (imagem == null || imagem.isBlank())
                ? "assets/icons/BibliotecaIconePrincipal.png"
                : imagem.trim();
        Livro novo = new Livro(0, titulo, autor, genero, anoLancamento, sinopse, caminhoImagem);
        return livroRepository.salvar(novo);
    }

    public boolean removerLivro(int id) {
        return livroRepository.removerPorId(id);
    }
}
