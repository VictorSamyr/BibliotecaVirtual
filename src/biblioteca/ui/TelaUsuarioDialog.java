package biblioteca.ui;

import biblioteca.domain.Genero;
import biblioteca.domain.Usuario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Janela de perfil do usuário autenticado.
 */
public final class TelaUsuarioDialog extends JDialog {
    public TelaUsuarioDialog(Frame owner, Usuario usuario) {
        super(owner, "Minha Conta — " + usuario.getNome(), true);
        setSize(420, 480);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());

        // Topo com ícone e nome
        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setBackground(AssetLoader.COR_PRIMARIA);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblIcone = new JLabel(AssetLoader.loadIcon("assets/icons/userIcon.jpg", 96, 96), SwingConstants.CENTER);
        JLabel lblNome = new JLabel(usuario.getNome(), SwingConstants.CENTER);
        lblNome.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblNome.setForeground(Color.WHITE);

        JLabel lblPerfil = new JLabel(usuario.isAdmin() ? "Perfil: Administrador" : "Perfil: Leitor", SwingConstants.CENTER);
        lblPerfil.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblPerfil.setForeground(AssetLoader.COR_SECUNDARIA);

        topPanel.add(lblIcone, BorderLayout.NORTH);
        topPanel.add(lblNome, BorderLayout.CENTER);
        topPanel.add(lblPerfil, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Dados cadastrais
        JPanel centerPanel = new JPanel(new BorderLayout(0, 14));
        centerPanel.setBackground(AssetLoader.COR_SECUNDARIA);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JPanel dadosGrid = new JPanel(new GridLayout(4, 1, 0, 6));
        dadosGrid.setOpaque(false);
        dadosGrid.add(criarLabelDado("Login: " + usuario.getLogin()));
        dadosGrid.add(criarLabelDado("E-mail: " + usuario.getEmail()));
        dadosGrid.add(criarLabelDado("Sexo: " + usuario.getSexo().getRotulo()));
        dadosGrid.add(criarLabelDado("Idade: " + usuario.getIdade() + " anos"));

        JPanel generosWrapper = new JPanel(new BorderLayout(0, 6));
        generosWrapper.setOpaque(false);
        JLabel lblTituloGeneros = criarLabelDado("Gêneros Preferidos:");
        generosWrapper.add(lblTituloGeneros, BorderLayout.NORTH);

        JPanel generosList = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        generosList.setOpaque(false);
        if (usuario.getGenerosPreferidos().isEmpty()) {
            generosList.add(criarBadgeGenero("Nenhum gênero selecionado"));
        } else {
            for (Genero genero : usuario.getGenerosPreferidos()) {
                generosList.add(criarBadgeGenero(genero.getRotulo()));
            }
        }
        generosWrapper.add(generosList, BorderLayout.CENTER);

        centerPanel.add(dadosGrid, BorderLayout.NORTH);
        centerPanel.add(generosWrapper, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(AssetLoader.COR_SECUNDARIA);
        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        footer.add(btnFechar);
        add(footer, BorderLayout.SOUTH);
    }

    private static JLabel criarLabelDado(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private static JLabel criarBadgeGenero(String texto) {
        JLabel badge = new JLabel(texto);
        badge.setOpaque(true);
        badge.setBackground(AssetLoader.COR_PRIMARIA);
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("SansSerif", Font.BOLD, 12));
        badge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        return badge;
    }
}
