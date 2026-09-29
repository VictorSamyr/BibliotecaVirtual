package biblioteca.domain;

import java.util.Arrays;
import java.util.Optional;

/**
 * Gêneros literários suportados pelo catálogo da Biblioteca Virtual.
 */
public enum Genero {
    FICCAO_CIENTIFICA("Ficção científica"),
    ROMANCE("Romance"),
    AUTO_AJUDA("Auto-Ajuda"),
    HISTORIA_EM_QUADRINHOS("História em quadrinhos"),
    SUSPENSE("Suspense");

    private final String rotulo;

    Genero(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    public static Optional<Genero> fromString(String texto) {
        if (texto == null || texto.isBlank()) {
            return Optional.empty();
        }
        String normalizado = texto.trim();
        return Arrays.stream(values())
                .filter(g -> g.rotulo.equalsIgnoreCase(normalizado) || g.name().equalsIgnoreCase(normalizado))
                .findFirst();
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
