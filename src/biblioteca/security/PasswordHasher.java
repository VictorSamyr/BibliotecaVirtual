package biblioteca.security;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utilitário criptográfico para derivação e verificação segura de senhas.
 *
 * <p>Evolução em relação ao {@code GeradorSHA256} original de 2021:
 * <ul>
 *   <li>Substitui o hash SHA-256 simples sem salt por <b>PBKDF2WithHmacSHA256</b>
 *       com salt aleatório de 128 bits ({@link SecureRandom}) e 65.536 iterações.</li>
 *   <li>Utiliza comparação em tempo constante ({@link MessageDigest#isEqual}) para
 *       mitigar ataques de temporização.</li>
 *   <li>Corrige o bug de truncamento de zeros à esquerda que ocorria no código legado
 *       ao converter bytes via {@code new BigInteger(1, shaByte).toString(16)}.</li>
 * </ul>
 */
public final class PasswordHasher {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String PREFIX = "pbkdf2";
    private static final int ITERATIONS = 65_536;
    private static final int SALT_BYTES = 16;
    private static final int KEY_LENGTH_BITS = 256;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    /**
     * Gera um hash seguro no formato {@code pbkdf2$iteracoes$saltBase64$hashBase64}.
     */
    public static String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        }
        byte[] salt = new byte[SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        byte[] derived = pbkdf2(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
        Base64.Encoder encoder = Base64.getEncoder().withoutPadding();
        return "%s$%d$%s$%s".formatted(
                PREFIX,
                ITERATIONS,
                encoder.encodeToString(salt),
                encoder.encodeToString(derived)
        );
    }

    /**
     * Verifica se a senha em texto plano corresponde ao hash armazenado.
     * Suporta tanto o formato PBKDF2 atual quanto hashes SHA-256 legados de 2021.
     */
    public static boolean verify(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null || storedHash.isBlank()) {
            return false;
        }
        if (storedHash.startsWith(PREFIX + "$")) {
            String[] parts = storedHash.split("\\$");
            if (parts.length != 4) {
                return false;
            }
            try {
                int iterations = Integer.parseInt(parts[1]);
                Base64.Decoder decoder = Base64.getDecoder();
                byte[] salt = decoder.decode(parts[2]);
                byte[] expectedHash = decoder.decode(parts[3]);
                byte[] actualHash = pbkdf2(rawPassword.toCharArray(), salt, iterations, expectedHash.length * 8);
                return MessageDigest.isEqual(expectedHash, actualHash);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        // Compatibilidade retroativa com registros legados em SHA-256 (64 hex chars ou truncados)
        String sha256Padded = sha256Hex(rawPassword);
        byte[] expectedBytes = storedHash.getBytes(StandardCharsets.UTF_8);
        if (MessageDigest.isEqual(sha256Padded.getBytes(StandardCharsets.UTF_8), expectedBytes)) {
            return true;
        }
        String legacyUnpadded = legacyBuggySha256Hex(rawPassword);
        return MessageDigest.isEqual(legacyUnpadded.getBytes(StandardCharsets.UTF_8), expectedBytes);
    }

    /**
     * Calcula SHA-256 hexadecimal com 64 caracteres exatos (preservando zeros à esquerda).
     */
    public static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível na JVM.", e);
        }
    }

    /**
     * Reproduz o comportamento exato do {@code GeradorSHA256} de 2021 para fins de teste
     * e compatibilidade retroativa (onde zeros à esquerda eram omitidos por {@link BigInteger#toString(int)}).
     */
    public static String legacyBuggySha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return new BigInteger(1, digest).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível na JVM.", e);
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Falha ao derivar chave PBKDF2.", e);
        }
    }
}
