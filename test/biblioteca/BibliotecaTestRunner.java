package biblioteca;

import biblioteca.domain.Genero;
import biblioteca.domain.Livro;
import biblioteca.domain.Sexo;
import biblioteca.domain.Usuario;
import biblioteca.repository.InMemoryLivroRepository;
import biblioteca.repository.InMemoryUsuarioRepository;
import biblioteca.security.PasswordHasher;
import biblioteca.service.AutenticacaoService;
import biblioteca.service.CatalogoService;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Suíte de testes automatizados da Biblioteca Virtual (executável diretamente com o JDK).
 */
public final class BibliotecaTestRunner {
    private static int executados = 0;

    private BibliotecaTestRunner() {
    }

    public static void main(String[] args) {
        testarHashPBKDF2ESaltAleatorio();
        testarCorrecaoBugZerosAEsquerdaSHA256Legado();
        testarRegistroEAutenticacaoDeUsuario();
        testarRejeicaoDeLoginDuplicadoESenhaInvalida();
        testarMitigacaoPayloadSqlInjectionNoLogin();
        testarIntegridadeDasCapasDoAcervoPadrao();
        testarBuscaFiltroERecomendacaoPorGenerosPreferidos();
        testarCadastroERemocaoDinamicaDeLivros();

        System.out.println("OK: " + executados + " testes executados com sucesso.");
    }

    private static void testarHashPBKDF2ESaltAleatorio() {
        String hash1 = PasswordHasher.hash("SenhaForte123");
        String hash2 = PasswordHasher.hash("SenhaForte123");

        assertTrue(!hash1.equals(hash2), "Hashes PBKDF2 da mesma senha devem diferir pelo salt aleatório.");
        assertTrue(PasswordHasher.verify("SenhaForte123", hash1), "Deve validar senha correta no hash1.");
        assertTrue(PasswordHasher.verify("SenhaForte123", hash2), "Deve validar senha correta no hash2.");
        assertTrue(!PasswordHasher.verify("SenhaErrada", hash1), "Deve rejeitar senha incorreta.");
        executados++;
    }

    private static void testarCorrecaoBugZerosAEsquerdaSHA256Legado() {
        // O SHA-256 da string "senha7" inicia com o nibble '0' ("0a1252..."), expondo o truncamento de BigInteger.toString(16).
        String entradaComZeroInicialNoHash = "senha2";
        for (int i = 0; i < 100; i++) {
            String candidata = "senha" + i;
            if (PasswordHasher.sha256Hex(candidata).startsWith("0")) {
                entradaComZeroInicialNoHash = candidata;
                break;
            }
        }
        String legadoTruncado = PasswordHasher.legacyBuggySha256Hex(entradaComZeroInicialNoHash);
        String corrigido64Chars = PasswordHasher.sha256Hex(entradaComZeroInicialNoHash);

        assertTrue(legadoTruncado.length() < 64, "O algoritmo legado de 2021 perdia zeros iniciais (< 64 chars).");
        assertEquals(64, corrigido64Chars.length(), "A implementação corrigida deve preservar 64 caracteres hex.");
        assertTrue(corrigido64Chars.startsWith("0"), "O hash corrigido deve iniciar com '0'.");
        assertTrue(PasswordHasher.verify(entradaComZeroInicialNoHash, corrigido64Chars), "Deve aceitar hash SHA-256 padrão.");
        assertTrue(PasswordHasher.verify(entradaComZeroInicialNoHash, legadoTruncado), "Deve manter compatibilidade com hash legado.");
        executados++;
    }

    private static void testarRegistroEAutenticacaoDeUsuario() {
        AutenticacaoService auth = new AutenticacaoService(new InMemoryUsuarioRepository(false));
        Usuario registrado = auth.registrar(
                "Victor Samyr",
                "victorsamyr",
                "victor@ufal.br",
                Sexo.MASCULINO,
                21,
                Set.of(Genero.FICCAO_CIENTIFICA, Genero.SUSPENSE),
                "segredo123",
                "segredo123"
        );

        assertTrue(registrado.getId() > 0, "Usuário registrado deve receber um ID positivo.");
        assertTrue(registrado.getSenhaHash().startsWith("pbkdf2$"), "Senha deve ser armazenada com PBKDF2.");

        Optional<Usuario> autenticado = auth.autenticar("victorsamyr", "segredo123");
        assertTrue(autenticado.isPresent(), "Deve autenticar com credenciais válidas.");
        assertEquals("Victor Samyr", autenticado.get().getNome(), "Nome do usuário autenticado deve corresponder.");
        executados++;
    }

    private static void testarRejeicaoDeLoginDuplicadoESenhaInvalida() {
        AutenticacaoService auth = new AutenticacaoService(new InMemoryUsuarioRepository(true));

        // Login 'admin' já existe na carga de demonstração
        expectIllegalArgument(() -> auth.registrar(
                "Outro Admin",
                "admin",
                "outro@biblioteca.local",
                Sexo.MASCULINO,
                25,
                Set.of(),
                "12345",
                "12345"
        ), "Deve rejeitar login duplicado.");

        // Confirmação de senha divergente
        expectIllegalArgument(() -> auth.registrar(
                "Novo Leitor",
                "novoleitor",
                "novo@biblioteca.local",
                Sexo.FEMININO,
                22,
                Set.of(),
                "senha123",
                "outra123"
        ), "Deve rejeitar confirmação de senha divergente.");

        executados++;
    }

    private static void testarMitigacaoPayloadSqlInjectionNoLogin() {
        AutenticacaoService auth = new AutenticacaoService(new InMemoryUsuarioRepository(true));
        Optional<Usuario> tentativaInjecao = auth.autenticar("' OR '1'='1", "qualquer");
        assertTrue(tentativaInjecao.isEmpty(), "Payloads de SQL injection não devem autenticar usuários.");
        executados++;
    }

    private static void testarIntegridadeDasCapasDoAcervoPadrao() {
        CatalogoService catalogo = new CatalogoService(new InMemoryLivroRepository(true));
        List<Livro> livros = catalogo.listarTodos();
        assertEquals(10, livros.size(), "O acervo inicial deve conter os 10 livros clássicos.");

        for (Livro livro : livros) {
            File arquivoCapa = new File(livro.getImagem());
            assertTrue(
                    arquivoCapa.isFile(),
                    "Arquivo de capa deve existir no disco para '" + livro.getTitulo() + "': " + livro.getImagem()
            );
        }
        executados++;
    }

    private static void testarBuscaFiltroERecomendacaoPorGenerosPreferidos() {
        CatalogoService catalogo = new CatalogoService(new InMemoryLivroRepository(true));

        List<Livro> ficcao = catalogo.buscarPorGenero(Genero.FICCAO_CIENTIFICA);
        assertEquals(2, ficcao.size(), "Deve encontrar 2 livros de Ficção científica (Blade Runner e Neuromancer).");

        List<Livro> buscaLovecraft = catalogo.buscarPorTermo("Lovecraft");
        assertEquals(1, buscaLovecraft.size(), "Deve encontrar 'O Horror de Dunwich' ao buscar por 'Lovecraft'.");

        Usuario leitorSuspense = new Usuario(
                99,
                "Leitor Suspense",
                "suspense",
                "suspense@local.test",
                Sexo.OUTRO,
                20,
                Set.of(Genero.SUSPENSE),
                PasswordHasher.hash("12345"),
                false
        );
        List<Livro> recomendados = catalogo.recomendarParaUsuario(leitorSuspense);
        assertEquals(2, recomendados.size(), "Deve recomendar apenas os 2 livros de Suspense (It e O Horror de Dunwich).");
        executados++;
    }

    private static void testarCadastroERemocaoDinamicaDeLivros() {
        CatalogoService catalogo = new CatalogoService(new InMemoryLivroRepository(true));
        Livro decimoPrimeiro = catalogo.cadastrarLivro(
                "Duna",
                "Frank Herbert",
                Genero.FICCAO_CIENTIFICA,
                1965,
                "A disputa pelo controle de Arrakis e da especiaria melange.",
                ""
        );

        assertEquals(11, catalogo.listarTodos().size(), "Acervo deve suportar mais de 10 livros dinamicamente.");
        assertTrue(catalogo.removerLivro(decimoPrimeiro.getId()), "Deve remover livro pelo ID.");
        assertEquals(10, catalogo.listarTodos().size(), "Acervo deve voltar a 10 livros após remoção.");
        executados++;
    }

    private static void expectIllegalArgument(Runnable action, String message) {
        try {
            action.run();
            throw new AssertionError(message + " (nenhuma exceção lançada)");
        } catch (IllegalArgumentException expected) {
            // Comportamento esperado
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Falha na asserção: " + message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError("%s | Esperado: <%s>, Obtido: <%s>".formatted(message, expected, actual));
        }
    }
}
