package biblioteca.ui;

import biblioteca.domain.Genero;
import biblioteca.domain.Livro;
import biblioteca.domain.Usuario;
import biblioteca.service.AutenticacaoService;
import biblioteca.service.CatalogoService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Painel administrativo completo para gestão de livros e visualização de usuários.
 *
 * <p>Conclui a implementação da {@code TelaAdmin} de 2021, que possuía apenas uma tabela vazia
 * sem carregamento de dados e sem ações conectadas.
 */
public final class TelaAdminDialog extends JDialog {
    private final CatalogoService catalogoService;
    private final AutenticacaoService autenticacaoService;
    private final Runnable onCatalogChanged;

    private DefaultTableModel livrosModel;
    private JTable tabelaLivros;
    private DefaultTableModel usuariosModel;

    public TelaAdminDialog(
            Frame owner,
            CatalogoService catalogoService,
            AutenticacaoService autenticacaoService,
            Runnable onCatalogChanged
    ) {
        super(owner, "Painel Administrativo — Biblioteca Virtual", true);
        this.catalogoService = catalogoService;
        this.autenticacaoService = autenticacaoService;
        this.onCatalogChanged = onCatalogChanged;

        setSize(940, 540);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Acervo de Livros", criarAbaLivros());
        abas.addTab("Usuários Cadastrados", criarAbaUsuarios());
        add(abas, BorderLayout.CENTER);

        atualizarTabelaLivros();
        atualizarTabelaUsuarios();
    }

    private JPanel criarAbaLivros() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // Formulário de novo livro à esquerda
        JPanel form = new JPanel(new BorderLayout(0, 8));
        form.setPreferredSize(new Dimension(300, 0));
        form.setBorder(BorderFactory.createTitledBorder("Cadastrar Novo Livro"));

        JPanel fields = new JPanel(new GridLayout(10, 1, 0, 4));
        JTextField txtTitulo = new JTextField();
        JTextField txtAutor = new JTextField();
        JComboBox<Genero> cbGenero = new JComboBox<>(Genero.values());
        JTextField txtAno = new JTextField("2026");
        JTextField txtImagem = new JTextField("assets/covers/Neuromancer.png");

        fields.add(new JLabel("Título:"));
        fields.add(txtTitulo);
        fields.add(new JLabel("Autor:"));
        fields.add(txtAutor);
        fields.add(new JLabel("Gênero:"));
        fields.add(cbGenero);
        fields.add(new JLabel("Ano de Lançamento:"));
        fields.add(txtAno);
        fields.add(new JLabel("Caminho da Capa:"));
        fields.add(txtImagem);

        JTextArea txtSinopse = new JTextArea(4, 20);
        txtSinopse.setLineWrap(true);
        txtSinopse.setWrapStyleWord(true);
        JScrollPane scrollSinopse = new JScrollPane(txtSinopse);
        scrollSinopse.setBorder(BorderFactory.createTitledBorder("Sinopse"));

        JButton btnAdicionar = new JButton("Adicionar ao Acervo");
        btnAdicionar.setBackground(AssetLoader.COR_SECUNDARIA);
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAdicionar.addActionListener(e -> {
            try {
                int ano = Integer.parseInt(txtAno.getText().trim());
                catalogoService.cadastrarLivro(
                        txtTitulo.getText(),
                        txtAutor.getText(),
                        (Genero) cbGenero.getSelectedItem(),
                        ano,
                        txtSinopse.getText(),
                        txtImagem.getText()
                );
                txtTitulo.setText("");
                txtAutor.setText("");
                txtSinopse.setText("");
                atualizarTabelaLivros();
                if (onCatalogChanged != null) {
                    onCatalogChanged.run();
                }
                JOptionPane.showMessageDialog(this, "Livro adicionado com sucesso!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Informe um ano numérico válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(fields, BorderLayout.NORTH);
        form.add(scrollSinopse, BorderLayout.CENTER);
        form.add(btnAdicionar, BorderLayout.SOUTH);

        // Tabela de livros à direita
        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        livrosModel = new DefaultTableModel(new Object[]{"ID", "Título", "Autor", "Gênero", "Ano"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaLivros = new JTable(livrosModel);
        rightPanel.add(new JScrollPane(tabelaLivros), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRemover = new JButton("Remover Livro Selecionado");
        btnRemover.addActionListener(e -> {
            int row = tabelaLivros.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecione um livro na tabela para remover.");
                return;
            }
            int id = (int) livrosModel.getValueAt(row, 0);
            catalogoService.removerLivro(id);
            atualizarTabelaLivros();
            if (onCatalogChanged != null) {
                onCatalogChanged.run();
            }
        });
        actions.add(btnRemover);
        rightPanel.add(actions, BorderLayout.SOUTH);

        panel.add(form, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel criarAbaUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        usuariosModel = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Login", "E-mail", "Sexo", "Idade", "Preferências", "Perfil"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabelaUsuarios = new JTable(usuariosModel);
        panel.add(new JScrollPane(tabelaUsuarios), BorderLayout.CENTER);
        return panel;
    }

    private void atualizarTabelaLivros() {
        livrosModel.setRowCount(0);
        for (Livro l : catalogoService.listarTodos()) {
            livrosModel.addRow(new Object[]{
                    l.getId(),
                    l.getTitulo(),
                    l.getAutor(),
                    l.getGenero().getRotulo(),
                    l.getAnoLancamento()
            });
        }
    }

    private void atualizarTabelaUsuarios() {
        usuariosModel.setRowCount(0);
        for (Usuario u : autenticacaoService.listarUsuarios()) {
            String prefs = u.getGenerosPreferidos().stream()
                    .map(Genero::getRotulo)
                    .collect(Collectors.joining(", "));
            usuariosModel.addRow(new Object[]{
                    u.getId(),
                    u.getNome(),
                    u.getLogin(),
                    u.getEmail(),
                    u.getSexo().getRotulo(),
                    u.getIdade(),
                    prefs,
                    u.isAdmin() ? "Admin" : "Leitor"
            });
        }
    }
}
