package biblioteca.ui;

import biblioteca.domain.Livro;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Janela modal de detalhes do livro selecionado.
 *
 * <p>Corrige a {@code TelaLivro} original de 2021, que nunca era acionada ao clicar nas capas
 * e sobrescrevia componentes na região central do {@code JFrame}.
 */
public final class TelaLivroDialog extends JDialog {
    public TelaLivroDialog(Frame owner, Livro livro) {
        super(owner, livro.getTitulo(), true);
        setSize(640, 460);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());

        // Cabeçalho
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AssetLoader.COR_PRIMARIA);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel lblTitulo = new JLabel(livro.getTitulo());
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("%s • %d • %s".formatted(
                livro.getAutor(),
                livro.getAnoLancamento(),
                livro.getGenero().getRotulo()
        ));
        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubtitulo.setForeground(AssetLoader.COR_SECUNDARIA);

        header.add(lblTitulo, BorderLayout.NORTH);
        header.add(lblSubtitulo, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Corpo com capa à esquerda e sinopse à direita
        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblCapa = new JLabel(AssetLoader.loadBookCover(livro.getImagem(), livro.getTitulo(), 170, 250));
        lblCapa.setPreferredSize(new Dimension(170, 250));
        body.add(lblCapa, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new BorderLayout(0, 10));
        infoPanel.setBackground(Color.WHITE);

        JLabel lblSinopseHeader = new JLabel("Sinopse");
        lblSinopseHeader.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblSinopseHeader.setForeground(AssetLoader.COR_PRIMARIA);

        JTextArea txtSinopse = new JTextArea(
                livro.getSinopse().isBlank() ? "Sinopse não informada." : livro.getSinopse()
        );
        txtSinopse.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSinopse.setLineWrap(true);
        txtSinopse.setWrapStyleWord(true);
        txtSinopse.setEditable(false);
        txtSinopse.setBackground(AssetLoader.COR_FUNDO_CLARO);
        txtSinopse.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollSinopse = new JScrollPane(txtSinopse);
        scrollSinopse.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        infoPanel.add(lblSinopseHeader, BorderLayout.NORTH);
        infoPanel.add(scrollSinopse, BorderLayout.CENTER);
        body.add(infoPanel, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        // Rodapé
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 20, 14, 20));
        JButton btnFechar = new JButton("Fechar");
        btnFechar.setBackground(AssetLoader.COR_SECUNDARIA);
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setFocusPainted(false);
        btnFechar.addActionListener(e -> dispose());
        footer.add(btnFechar);
        add(footer, BorderLayout.SOUTH);
    }
}
