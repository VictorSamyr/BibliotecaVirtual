package biblioteca.repository;

import biblioteca.domain.Genero;
import biblioteca.domain.Sexo;
import biblioteca.domain.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementação JDBC segura de {@link UsuarioRepository}.
 *
 * <p>Substitui o método vulnerável {@code getUsuarios(String sql)} do {@code UsuarioDao} de 2021
 * por consultas parametrizadas com {@link PreparedStatement} e fechamento automático de recursos.
 */
public final class JdbcUsuarioRepository implements UsuarioRepository {
    private final DatabaseConfig dbConfig;

    public JdbcUsuarioRepository(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        String sql = """
                INSERT INTO usuarios (nome, login, sexo, email, idade, generos_preferidos, senha, admin)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, usuario.getSexo().getCodigo());
            stmt.setString(4, usuario.getEmail());
            stmt.setInt(5, usuario.getIdade());
            String preferencias = usuario.getGenerosPreferidos().stream()
                    .map(Genero::getRotulo)
                    .collect(Collectors.joining(","));
            stmt.setString(6, preferencias);
            stmt.setString(7, usuario.getSenhaHash());
            stmt.setBoolean(8, usuario.isAdmin());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return usuario.withId(keys.getInt(1));
                }
            }
            return usuario;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar usuário no banco de dados.", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        String sql = """
                SELECT id, nome, login, sexo, email, idade, generos_preferidos, senha, admin
                FROM usuarios
                WHERE LOWER(login) = LOWER(?)
                """;
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login == null ? "" : login.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar usuário por login.", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = """
                SELECT id, nome, login, sexo, email, idade, generos_preferidos, senha, admin
                FROM usuarios
                WHERE LOWER(email) = LOWER(?)
                """;
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email == null ? "" : email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar usuário por e-mail.", e);
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        String sql = """
                SELECT id, nome, login, sexo, email, idade, generos_preferidos, senha, admin
                FROM usuarios
                ORDER BY id
                """;
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Usuario> usuarios = new ArrayList<>();
            while (rs.next()) {
                usuarios.add(mapRow(rs));
            }
            return usuarios;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar usuários.", e);
        }
    }

    @Override
    public boolean existePorLogin(String login) {
        return buscarPorLogin(login).isPresent();
    }

    @Override
    public boolean removerPorId(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao remover usuário por ID.", e);
        }
    }

    private static Usuario mapRow(ResultSet rs) throws SQLException {
        String prefRaw = rs.getString("generos_preferidos");
        Set<Genero> preferencias = new LinkedHashSet<>();
        if (prefRaw != null && !prefRaw.isBlank()) {
            for (String item : prefRaw.split(",")) {
                Genero.fromString(item).ifPresent(preferencias::add);
            }
        }
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("login"),
                rs.getString("email"),
                Sexo.fromInput(rs.getString("sexo")),
                rs.getInt("idade"),
                preferencias,
                rs.getString("senha"),
                rs.getBoolean("admin")
        );
    }
}
