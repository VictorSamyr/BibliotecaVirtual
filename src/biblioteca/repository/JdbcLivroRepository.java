package biblioteca.repository;

import biblioteca.domain.Genero;
import biblioteca.domain.Livro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação JDBC segura de {@link LivroRepository}.
 *
 * <p>Corrige dois problemas críticos do {@code LivroDao} original de 2021:
 * <ol>
 *   <li>Elimina o método {@code getLivros(String sql)} que executava SQL arbitrário via {@link Statement}.</li>
 *   <li>Gerencia o ciclo de vida de {@link Connection}, {@link PreparedStatement} e {@link ResultSet}
 *       com blocos {@code try-with-resources}, evitando vazamento de conexões.</li>
 * </ol>
 */
public final class JdbcLivroRepository implements LivroRepository {
    private final DatabaseConfig dbConfig;

    public JdbcLivroRepository(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public Livro salvar(Livro livro) {
        String sql = """
                INSERT INTO livros (titulo, genero, autor, ano_lancamento, sinopse, imagem)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getGenero().getRotulo());
            stmt.setString(3, livro.getAutor());
            stmt.setInt(4, livro.getAnoLancamento());
            stmt.setString(5, livro.getSinopse());
            stmt.setString(6, livro.getImagem());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return livro.withId(keys.getInt(1));
                }
            }
            return livro;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar livro no banco de dados.", e);
        }
    }

    @Override
    public List<Livro> listarTodos() {
        String sql = "SELECT id, titulo, genero, autor, ano_lancamento, sinopse, imagem FROM livros ORDER BY id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Livro> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(mapRow(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar livros do banco de dados.", e);
        }
    }

    @Override
    public Optional<Livro> buscarPorId(int id) {
        String sql = "SELECT id, titulo, genero, autor, ano_lancamento, sinopse, imagem FROM livros WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar livro por ID.", e);
        }
    }

    @Override
    public List<Livro> buscarPorGenero(Genero genero) {
        String sql = """
                SELECT id, titulo, genero, autor, ano_lancamento, sinopse, imagem
                FROM livros
                WHERE LOWER(genero) = LOWER(?)
                ORDER BY titulo
                """;
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, genero.getRotulo());
            try (ResultSet rs = stmt.executeQuery()) {
                List<Livro> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(mapRow(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao filtrar livros por gênero.", e);
        }
    }

    @Override
    public List<Livro> buscarPorTermo(String termo) {
        String filtro = "%" + (termo == null ? "" : termo.trim().toLowerCase()) + "%";
        String sql = """
                SELECT id, titulo, genero, autor, ano_lancamento, sinopse, imagem
                FROM livros
                WHERE LOWER(titulo) LIKE ? OR LOWER(autor) LIKE ?
                ORDER BY titulo
                """;
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, filtro);
            stmt.setString(2, filtro);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Livro> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(mapRow(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao pesquisar livros por termo.", e);
        }
    }

    @Override
    public boolean removerPorId(int id) {
        String sql = "DELETE FROM livros WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao remover livro por ID.", e);
        }
    }

    private static Livro mapRow(ResultSet rs) throws SQLException {
        Genero genero = Genero.fromString(rs.getString("genero")).orElse(Genero.FICCAO_CIENTIFICA);
        return new Livro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                genero,
                rs.getInt("ano_lancamento"),
                rs.getString("sinopse"),
                rs.getString("imagem")
        );
    }
}
