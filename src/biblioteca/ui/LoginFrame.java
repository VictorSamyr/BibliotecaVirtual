package biblioteca.ui;

import biblioteca.domain.Usuario;
import biblioteca.service.AutenticacaoService;
import biblioteca.service.CatalogoService;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Optional;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Janela de autenticação da Biblioteca Virtual.
 *
 * <p>Mantém a identidade visual original de 2021, mas delega toda a validação e criptografia
 * para {@link AutenticacaoService}, eliminando consultas SQL concatenadas na camada de interface.
 */
public final class LoginFrame {
    private final AutenticacaoService autenticacaoService;
    private final CatalogoService catalogoService;

    private JFrame frame;
    private JTextField txtLogin;
    private JPasswordField txtSenha;

    public LoginFrame(AutenticacaoService autenticacaoService, CatalogoService catalogoService) {
        this.autenticacaoService = autenticacaoService;
        this.catalogoService = catalogoService;
        configurarJanela();
    }

    public void exibir() {
        frame.setVisible(true);
    }

    private void configurarJanela() {
        frame = new JFrame("Biblioteca Virtual — Entrar");
        frame.setUndecorated(true);
        frame.setShape(new RoundRectangle2D.Double(0, 0, 700, 450, 20, 20));
        frame.setSize(700, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        // Painel lateral esquerdo
        JPanel sidePanel = new JPanel(null);
        sidePanel.setBounds(0, 0, 325, 450);
        sidePanel.setBackground(AssetLoader.COR_SECUNDARIA);

        JLabel labelLogo = new JLabel(
                AssetLoader.loadIcon("assets/icons/BibliotecaIconeRegistro&Login.png", 280, 280)
        );
        labelLogo.setBounds(22, 50, 280, 280);
        sidePanel.add(labelLogo);

        JLabel txtLogo = new JLabel("BEM-VINDO!");
        txtLogo.setBounds(65, 360, 250, 45);
        txtLogo.setFont(new Font("SansSerif", Font.BOLD, 28));
        txtLogo.setForeground(Color.WHITE);
        sidePanel.add(txtLogo);

        panel.add(sidePanel);

        // Botão fechar
        JLabel lblFechar = new JLabel(AssetLoader.loadIcon("assets/icons/BotaoFechar.png", 20, 20));
        lblFechar.setBounds(668, 12, 20, 20);
        lblFechar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblFechar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
            }
        });
        panel.add(lblFechar);

        // Título e formulário
        JLabel tituloLogin = new JLabel("LOGIN");
        tituloLogin.setBounds(435, 38, 220, 55);
        tituloLogin.setFont(new Font("SansSerif", Font.BOLD, 42));
        tituloLogin.setForeground(AssetLoader.COR_SECUNDARIA);
        panel.add(tituloLogin);

        JLabel lblLogin = new JLabel("Login");
        lblLogin.setBounds(412, 122, 120, 22);
        lblLogin.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblLogin.setForeground(AssetLoader.COR_PRIMARIA);
        panel.add(lblLogin);

        txtLogin = new JTextField();
        txtLogin.setBounds(412, 145, 200, 32);
        panel.add(txtLogin);

        JLabel iconLogin = new JLabel(AssetLoader.loadIcon("assets/icons/LoginIcone.png", 26, 26));
        iconLogin.setBounds(618, 148, 26, 26);
        panel.add(iconLogin);

        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setBounds(412, 185, 120, 22);
        lblSenha.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSenha.setForeground(AssetLoader.COR_PRIMARIA);
        panel.add(lblSenha);

        txtSenha = new JPasswordField();
        txtSenha.setBounds(412, 208, 200, 32);
        txtSenha.addActionListener(e -> autenticarUsuario());
        panel.add(txtSenha);

        JLabel iconSenha = new JLabel(AssetLoader.loadIcon("assets/icons/SenhaIcone.png", 26, 26));
        iconSenha.setBounds(618, 211, 26, 26);
        panel.add(iconSenha);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(452, 262, 120, 34);
        btnEntrar.setBackground(AssetLoader.COR_PRIMARIA);
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnEntrar.setFocusPainted(false);
        btnEntrar.addActionListener(e -> autenticarUsuario());
        panel.add(btnEntrar);

        JLabel lblRegistrar = new JLabel("Não possui uma conta? Clique aqui!");
        lblRegistrar.setBounds(408, 312, 240, 30);
        lblRegistrar.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblRegistrar.setForeground(AssetLoader.COR_SECUNDARIA);
        lblRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblRegistrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                new RegistroFrame(autenticacaoService, catalogoService).exibir();
            }
        });
        panel.add(lblRegistrar);

        JLabel lblDemo = new JLabel("Contas demo: admin / admin123 | leitor / leitor123");
        lblDemo.setBounds(375, 410, 310, 20);
        lblDemo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblDemo.setForeground(Color.GRAY);
        panel.add(lblDemo);

        frame.setContentPane(panel);
    }

    private void autenticarUsuario() {
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());
        Optional<Usuario> autenticado = autenticacaoService.autenticar(login, senha);
        if (autenticado.isEmpty()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Login ou senha inválidos.",
                    "Erro de Autenticação",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        frame.dispose();
        new PrincipalFrame(autenticado.get(), catalogoService, autenticacaoService).exibir();
    }
}
