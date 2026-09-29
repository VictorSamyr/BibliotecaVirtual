package biblioteca.domain;

/**
 * Representação padronizada do campo sexo no cadastro de usuário.
 */
public enum Sexo {
    MASCULINO("M", "Masculino"),
    FEMININO("F", "Feminino"),
    OUTRO("X", "Não binário / Outro");

    private final String codigo;
    private final String rotulo;

    Sexo(String codigo, String rotulo) {
        this.codigo = codigo;
        this.rotulo = rotulo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getRotulo() {
        return rotulo;
    }

    public static Sexo fromInput(String valor) {
        if (valor == null || valor.isBlank()) {
            return OUTRO;
        }
        String v = valor.trim();
        for (Sexo s : values()) {
            if (s.codigo.equalsIgnoreCase(v) || s.rotulo.equalsIgnoreCase(v) || s.name().equalsIgnoreCase(v)) {
                return s;
            }
        }
        if (v.equalsIgnoreCase("Outro")) {
            return OUTRO;
        }
        return OUTRO;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
