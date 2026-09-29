package biblioteca.domain;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidade de domínio que representa um usuário cadastrado na Biblioteca Virtual.
 */
public final class Usuario {
    private final int id;
    private final String nome;
    private final String login;
    private final String email;
    private final Sexo sexo;
    private final int idade;
    private final Set<Genero> generosPreferidos;
    private final String senhaHash;
    private final boolean admin;

    public Usuario(
            int id,
            String nome,
            String login,
            String email,
            Sexo sexo,
            int idade,
            Set<Genero> generosPreferidos,
            String senhaHash,
            boolean admin
    ) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do usuário é obrigatório.");
        }
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("O login do usuário é obrigatório.");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Informe um endereço de e-mail válido.");
        }
        if (idade <= 0 || idade > 130) {
            throw new IllegalArgumentException("A idade deve estar entre 1 e 130 anos.");
        }
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new IllegalArgumentException("O hash de senha é obrigatório.");
        }
        this.id = id;
        this.nome = nome.trim();
        this.login = login.trim();
        this.email = email.trim();
        this.sexo = Objects.requireNonNullElse(sexo, Sexo.OUTRO);
        this.idade = idade;
        this.generosPreferidos = generosPreferidos == null
                ? Set.of()
                : Collections.unmodifiableSet(new LinkedHashSet<>(generosPreferidos));
        this.senhaHash = senhaHash;
        this.admin = admin;
    }

    public Usuario withId(int novoId) {
        return new Usuario(novoId, nome, login, email, sexo, idade, generosPreferidos, senhaHash, admin);
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public int getIdade() {
        return idade;
    }

    public Set<Genero> getGenerosPreferidos() {
        return generosPreferidos;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public boolean isAdmin() {
        return admin;
    }
}
