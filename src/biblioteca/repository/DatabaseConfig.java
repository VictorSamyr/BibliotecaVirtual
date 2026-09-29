package biblioteca.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica de conexões JDBC configurável via variáveis de ambiente ou propriedades de sistema.
 *
 * <p>Substitui a {@code ConnectionFactory} original de 2021 que mantinha credenciais
 * fixas ({@code postgres/password}) diretamente no código-fonte.
 */
public final class DatabaseConfig {
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/biblioteca";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "";

    private final String url;
    private final String user;
    private final String password;

    public DatabaseConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfig fromEnvironment() {
        String url = readSetting("DB_URL", "biblioteca.db.url", DEFAULT_URL);
        String user = readSetting("DB_USER", "biblioteca.db.user", DEFAULT_USER);
        String password = readSetting("DB_PASSWORD", "biblioteca.db.password", DEFAULT_PASSWORD);
        return new DatabaseConfig(url, user, password);
    }

    public static boolean isJdbcConfigured() {
        String mode = readSetting("BIBLIOTECA_STORAGE", "biblioteca.storage", "memory");
        return "jdbc".equalsIgnoreCase(mode) || "postgres".equalsIgnoreCase(mode);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private static String readSetting(String envKey, String propKey, String fallback) {
        String prop = System.getProperty(propKey);
        if (prop != null && !prop.isBlank()) {
            return prop.trim();
        }
        String env = System.getenv(envKey);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        return fallback;
    }
}
