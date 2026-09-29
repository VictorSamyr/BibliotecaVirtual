package biblioteca;

import biblioteca.domain.Livro;
import biblioteca.domain.Usuario;
import biblioteca.repository.DatabaseConfig;
import biblioteca.repository.InMemoryLivroRepository;
import biblioteca.repository.InMemoryUsuarioRepository;
import biblioteca.repository.JdbcLivroRepository;
import biblioteca.repository.JdbcUsuarioRepository;
import biblioteca.repository.LivroRepository;
import biblioteca.repository.UsuarioRepository;
import biblioteca.service.AutenticacaoService;
import biblioteca.service.CatalogoService;
import biblioteca.ui.LoginFrame;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Ponto de entrada da Biblioteca Virtual.
 *
 * <p>Por padrão, inicia com repositórios em memória pré-carregados com o acervo clássico
 * de 10 livros e contas de demonstração ({@code admin/admin123} e {@code leitor/leitor123}),
 * permitindo execução imediata sem necessidade de servidor PostgreSQL local.
 *
 * <p>Caso {@code BIBLIOTECA_STORAGE=jdbc} esteja definido no ambiente, utiliza os
 * repositórios JDBC parametrizados ({@link JdbcLivroRepository} e {@link JdbcUsuarioRepository}).
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        LivroRepository livroRepository;
        UsuarioRepository usuarioRepository;

        if (DatabaseConfig.isJdbcConfigured()) {
            DatabaseConfig dbConfig = DatabaseConfig.fromEnvironment();
            livroRepository = new JdbcLivroRepository(dbConfig);
            usuarioRepository = new JdbcUsuarioRepository(dbConfig);
        } else {
            livroRepository = new InMemoryLivroRepository();
            usuarioRepository = new InMemoryUsuarioRepository();
        }

        AutenticacaoService autenticacaoService = new AutenticacaoService(usuarioRepository);
        CatalogoService catalogoService = new CatalogoService(livroRepository);

        boolean cliDemo = Arrays.asList(args).contains("--cli-demo");
        if (cliDemo || GraphicsEnvironment.isHeadless()) {
            executarDemonstracaoTerminal(autenticacaoService, catalogoService);
            return;
        }

        SwingUtilities.invokeLater(() -> new LoginFrame(autenticacaoService, catalogoService).exibir());
    }

    private static void executarDemonstracaoTerminal(
            AutenticacaoService autenticacaoService,
            CatalogoService catalogoService
    ) {
        System.out.println("=== Biblioteca Virtual (Modo Demonstração CLI) ===");
        List<Livro> acervo = catalogoService.listarTodos();
        System.out.println("Total de livros no acervo: " + acervo.size());
        for (Livro livro : acervo) {
            System.out.println("  - " + livro);
        }

        Usuario leitor = autenticacaoService.autenticar("leitor", "leitor123").orElseThrow();
        List<Livro> recomendados = catalogoService.recomendarParaUsuario(leitor);
        System.out.println("\nRecomendações para '" + leitor.getNome() + "' " + leitor.getGenerosPreferidos() + ":");
        for (Livro livro : recomendados) {
            System.out.println("  * " + livro);
        }
    }
}
