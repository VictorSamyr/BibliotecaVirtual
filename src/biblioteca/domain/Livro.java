package biblioteca.domain;

import java.util.Objects;

/**
 * Entidade de domínio que representa um livro do acervo.
 */
public final class Livro {
    private final int id;
    private final String titulo;
    private final String autor;
    private final Genero genero;
    private final int anoLancamento;
    private final String sinopse;
    private final String imagem;

    public Livro(int id, String titulo, String autor, Genero genero, int anoLancamento, String sinopse, String imagem) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título do livro é obrigatório.");
        }
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("O autor do livro é obrigatório.");
        }
        Objects.requireNonNull(genero, "O gênero do livro é obrigatório.");
        if (anoLancamento <= 0) {
            throw new IllegalArgumentException("O ano de lançamento deve ser positivo.");
        }
        this.id = id;
        this.titulo = titulo.trim();
        this.autor = autor.trim();
        this.genero = genero;
        this.anoLancamento = anoLancamento;
        this.sinopse = sinopse == null ? "" : sinopse.trim();
        this.imagem = imagem == null ? "" : imagem.trim();
    }

    public Livro withId(int novoId) {
        return new Livro(novoId, titulo, autor, genero, anoLancamento, sinopse, imagem);
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public Genero getGenero() {
        return genero;
    }

    public int getAnoLancamento() {
        return anoLancamento;
    }

    public String getSinopse() {
        return sinopse;
    }

    public String getImagem() {
        return imagem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Livro livro)) return false;
        return id == livro.id && titulo.equalsIgnoreCase(livro.titulo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titulo.toLowerCase());
    }

    @Override
    public String toString() {
        return "%s (%d) - %s [%s]".formatted(titulo, anoLancamento, autor, genero.getRotulo());
    }
}
