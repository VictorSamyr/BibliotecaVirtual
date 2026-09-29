package biblioteca.ui;

import biblioteca.domain.Genero;
import biblioteca.domain.Livro;
import biblioteca.domain.Usuario;
import biblioteca.service.AutenticacaoService;
import biblioteca.service.CatalogoService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Tela principal do catálogo da Biblioteca Virtual.
 *
 * <p>Evoluções em relação à classe {@code Principal} original de 2021:
 * <ul>
 *   <li>Substitui os dois laços fixos de 5 livros ({@code paths.get(i+5)}) por uma grade dinâmica
 *       com barra de rolagem ({@link JScrollPane}), suportando qualquer quantidade de livros.</li>
 *   <li>Conecta o clique em cada livro à abertura de {@link TelaLivroDialog} com capa e sinopse.</li>
 *   <li>Adiciona busca por título/autor, filtro por gênero e filtro de recomendações personalizadas
 *       com base nos gêneros favoritos escolhidos pelo usuário no cadastro.</li>
 * </ul>
 */
public final class PrincipalFrame {
    private final Usuario usuario;
    private final CatalogoService catalogoService;
    private final AutenticacaoService autenticacaoService;

    private JFrame frame;
    private JPanel gridLivros;
    private JLabel lblContador;
    private JTextField txtBusca;
    private JComboBox<Object> cbGenero;
    private JCheckBox chkRecomendados;

    public PrincipalFrame(
            Usuario usuario,
            CatalogoService catalogoService,
            AutenticacaoService autenticacaoService
    ) {
        this.usuario = usuario;
        this.catalogoService = catalogoService;
        this.autenticacaoService = autenticacaoService;
        configurarJanela();
    }

    public void exibir() {
        frame.setVisible(true);
    }

    private void configurarJanela() {
        frame = new JFrame("Biblioteca Virtual — Catálogo (" + usuario.getNome() + ")");
        frame.setSize(1060, 660);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.add(criarSidebar(), BorderLayout.WEST);
        frame.add(criarAreaConteudo(), BorderLayout.CENTER);

        atualizarGradeLivros();
    }

    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(215, 0));
        sidebar.setBackground(AssetLoader.COR_PRIMARIA);

        // Logo superior
        JPanel topLogo = new JPanel(new BorderLayout());
        topLogo.setOpaque(false);
        topLogo.setBorder(BorderFactory.createEmptyBorder(14, 10, 10, 10));
        JLabel iconLogo = new JLabel(
                AssetLoader.loadIcon("assets/icons/BibliotecaIconePrincipal.png", 170, 170),
                SwingConstants.CENTER
        );
        JLabel lblBoasVindas = new JLabel("Olá, " + usuario.getNome(), SwingConstants.CENTER);
        lblBoasVindas.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblBoasVindas.setForeground(Color.WHITE);
        lblBoasVindas.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));
        topLogo.add(iconLogo, BorderLayout.CENTER);
        topLogo.add(lblBoasVindas, BorderLayout.SOUTH);
        sidebar.add(topLogo, BorderLayout.NORTH);

        // Botões de navegação na base da barra lateral
        JPanel menuInferior = new JPanel(new GridLayout(4, 1, 0, 6));
        menuInferior.setOpaque(false);
        menuInferior.setBorder(BorderFactory.createEmptyBorder(10, 12, 16, 12));

        JButton btnTodos = criarBotaoMenu("Todos os Livros");
        btnTodos.addActionListener(e -> {
            cbGenero.setSelectedIndex(0);
            chkRecomendados.setSelected(false);
            txtBusca.setText("");
            atualizarGradeLivros();
        });

        JButton btnConta = criarBotaoMenu("Minha Conta");
        btnConta.addActionListener(e -> new TelaUsuarioDialog(frame, usuario).setVisible(true));

        JButton btnAdmin = criarBotaoMenu("Painel Admin");
        btnAdmin.addActionListener(
                e -> new TelaAdminDialog(frame, catalogoService, autenticacaoService, this::atualizarGradeLivros)
                        .setVisible(true)
        );

        JButton btnSair = criarBotaoMenu("Sair");
        btnSair.addActionListener(e -> {
            frame.dispose();
            new LoginFrame(autenticacaoService, catalogoService).exibir();
        });

        menuInferior.add(btnTodos);
        menuInferior.add(btnConta);
        menuInferior.add(btnAdmin);
        menuInferior.add(btnSair);

        sidebar.add(menuInferior, BorderLayout.SOUTH);
        return sidebar;
    }

    private static JButton criarBotaoMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(180, 44));
        btn.setBackground(AssetLoader.COR_SECUNDARIA);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(AssetLoader.COR_SECUNDARIA_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(AssetLoader.COR_SECUNDARIA);
            }
        });
        return btn;
    }

    private JPanel criarAreaConteudo() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(AssetLoader.COR_PRIMARIA);

        // Cabeçalho com título e filtros
        JPanel header = new JPanel(new BorderLayout(0, 12));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 12, 22));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JLabel lblTitulo = new JLabel("Acervo da Biblioteca");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTitulo.setForeground(Color.WHITE);

        lblContador = new JLabel();
        lblContador.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblContador.setForeground(AssetLoader.COR_SECUNDARIA);

        titleRow.add(lblTitulo, BorderLayout.WEST);
        titleRow.add(lblContador, BorderLayout.EAST);

        // Barra de busca e filtros
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterBar.setOpaque(false);

        JLabel lblBusca = new JLabel("Buscar:");
        lblBusca.setForeground(Color.WHITE);
        lblBusca.setFont(new Font("SansSerif", Font.BOLD, 13));

        txtBusca = new JTextField(18);
        txtBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarGradeLivros();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarGradeLivros();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarGradeLivros();
            }
        });

        JLabel lblGenero = new JLabel("Gênero:");
        lblGenero.setForeground(Color.WHITE);
        lblGenero.setFont(new Font("SansSerif", Font.BOLD, 13));

        cbGenero = new JComboBox<>();
        cbGenero.addItem("Todos os gêneros");
        for (Genero g : Genero.values()) {
            cbGenero.addItem(g);
        }
        cbGenero.addActionListener(e -> atualizarGradeLivros());

        chkRecomendados = new JCheckBox("Recomendados para meus gêneros favoritos");
        chkRecomendados.setOpaque(false);
        chkRecomendados.setForeground(Color.WHITE);
        chkRecomendados.setFont(new Font("SansSerif", Font.BOLD, 13));
        chkRecomendados.addActionListener(e -> atualizarGradeLivros());

        filterBar.add(lblBusca);
        filterBar.add(txtBusca);
        filterBar.add(lblGenero);
        filterBar.add(cbGenero);
        filterBar.add(chkRecomendados);

        header.add(titleRow, BorderLayout.NORTH);
        header.add(filterBar, BorderLayout.SOUTH);
        content.add(header, BorderLayout.NORTH);

        // Grade rolável de livros
        gridLivros = new JPanel(new GridLayout(0, 5, 16, 16));
        gridLivros.setBackground(AssetLoader.COR_PRIMARIA);
        gridLivros.setBorder(BorderFactory.createEmptyBorder(10, 22, 22, 22));

        JScrollPane scrollPane = new JScrollPane(gridLivros);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        content.add(scrollPane, BorderLayout.CENTER);

        // Faixa inferior decorativa mantendo o visual de 2021
        JPanel tarjaInferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 8));
        tarjaInferior.setBackground(AssetLoader.COR_SECUNDARIA);
        JLabel lblDica = new JLabel("Dica: clique sobre a capa de qualquer livro para visualizar a sinopse completa.");
        lblDica.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblDica.setForeground(Color.WHITE);
        tarjaInferior.add(lblDica);
        content.add(tarjaInferior, BorderLayout.SOUTH);

        return content;
    }

    private void atualizarGradeLivros() {
        Object selecionado = cbGenero.getSelectedItem();
        Genero generoFiltro = (selecionado instanceof Genero g) ? g : null;
        String termo = txtBusca.getText();
        boolean apenasRecomendados = chkRecomendados.isSelected();

        List<Livro> livrosFiltrados = catalogoService.filtrar(generoFiltro, termo, apenasRecomendados, usuario);
        lblContador.setText(livrosFiltrados.size() + " livro(s) exibido(s)");

        gridLivros.removeAll();
        for (Livro livro : livrosFiltrados) {
            gridLivros.add(criarCardLivro(livro));
        }
        gridLivros.revalidate();
        gridLivros.repaint();
    }

    private JPanel criarCardLivro(Livro livro) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(new Color(34, 48, 96));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AssetLoader.COR_SECUNDARIA, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblCapa = new JLabel(
                AssetLoader.loadBookCover(livro.getImagem(), livro.getTitulo(), 130, 190),
                SwingConstants.CENTER
        );

        JLabel lblTitulo = new JLabel(livro.getTitulo(), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setToolTipText(livro.getTitulo() + " (" + livro.getAutor() + ")");

        JLabel lblGenero = new JLabel(livro.getGenero().getRotulo(), SwingConstants.CENTER);
        lblGenero.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblGenero.setForeground(AssetLoader.COR_SECUNDARIA);

        JPanel rodape = new JPanel(new GridLayout(2, 1));
        rodape.setOpaque(false);
        rodape.add(lblTitulo);
        rodape.add(lblGenero);

        card.add(lblCapa, BorderLayout.CENTER);
        card.add(rodape, BorderLayout.SOUTH);

        MouseAdapter abrirDetalhes = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new TelaLivroDialog(frame, livro).setVisible(true);
            }
        };
        card.addMouseListener(abrirDetalhes);
        lblCapa.addMouseListener(abrirDetalhes);
        return card;
    }
}
