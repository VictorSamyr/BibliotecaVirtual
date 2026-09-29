package biblioteca.ui;

import biblioteca.domain.Genero;
import biblioteca.domain.Sexo;
import biblioteca.service.AutenticacaoService;
import biblioteca.service.CatalogoService;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Janela de cadastro de novos leitores na Biblioteca Virtual.
 */
public final class RegistroFrame {
    private final AutenticacaoService autenticacaoService;
    private final CatalogoService catalogoService;

    private JFrame frame;
    private JTextField txtNome;
    private JTextField txtLogin;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmarSenha;
    private JTextField txtIdade;
    private JComboBox<Sexo> cbSexo;
    private final Map<Genero, JCheckBox> checkboxesGeneros = new EnumMap<>(Genero.class);

    public RegistroFrame(AutenticacaoService autenticacaoService, CatalogoService catalogoService) {
        this.autenticacaoService = autenticacaoService;
        this.catalogoService = catalogoService;
        configurarJanela();
    }

    public void exibir() {
        frame.setVisible(true);
    }

    private void configurarJanela() {
        frame = new JFrame("Biblioteca Virtual — Registrar-se");
        frame.setUndecorated(true);
        frame.setShape(new RoundRectangle2D.Double(0, 0, 700, 470, 20, 20));
        frame.setSize(700, 470);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        // Painel lateral esquerdo
        JPanel sidePanel = new JPanel(null);
        sidePanel.setBounds(0, 0, 310, 470);
        sidePanel.setBackground(AssetLoader.COR_SECUNDARIA);

        JLabel lblLogo = new JLabel(
                AssetLoader.loadIcon("assets/icons/BibliotecaIconeRegistro&Login.png", 260, 260)
        );
        lblLogo.setBounds(25, 50, 260, 260);
        sidePanel.add(lblLogo);

        JLabel txtLogo = new JLabel("CRIAR CONTA");
        txtLogo.setBounds(50, 355, 230, 40);
        txtLogo.setFont(new Font("SansSerif", Font.BOLD, 26));
        txtLogo.setForeground(Color.WHITE);
        sidePanel.add(txtLogo);

        JButton btnVoltarLogin = new JButton("← Voltar ao Login");
        btnVoltarLogin.setBounds(75, 405, 160, 30);
        btnVoltarLogin.setBackground(AssetLoader.COR_PRIMARIA);
        btnVoltarLogin.setForeground(Color.WHITE);
        btnVoltarLogin.setFocusPainted(false);
        btnVoltarLogin.addActionListener(e -> {
            frame.dispose();
            new LoginFrame(autenticacaoService, catalogoService).exibir();
        });
        sidePanel.add(btnVoltarLogin);

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

        JLabel txtRegistro = new JLabel("REGISTRO");
        txtRegistro.setBounds(385, 22, 260, 48);
        txtRegistro.setFont(new Font("SansSerif", Font.BOLD, 36));
        txtRegistro.setForeground(AssetLoader.COR_SECUNDARIA);
        panel.add(txtRegistro);

        // Campos de texto
        adicionarLabel(panel, "Nome", 350, 78);
        txtNome = new JTextField();
        txtNome.setBounds(350, 98, 145, 26);
        panel.add(txtNome);

        adicionarLabel(panel, "Login", 510, 78);
        txtLogin = new JTextField();
        txtLogin.setBounds(510, 98, 145, 26);
        panel.add(txtLogin);

        adicionarLabel(panel, "E-mail", 350, 128);
        txtEmail = new JTextField();
        txtEmail.setBounds(350, 148, 305, 26);
        panel.add(txtEmail);

        adicionarLabel(panel, "Senha (mín. 5 caracteres)", 350, 178);
        txtSenha = new JPasswordField();
        txtSenha.setBounds(350, 198, 145, 26);
        panel.add(txtSenha);

        adicionarLabel(panel, "Confirmar Senha", 510, 178);
        txtConfirmarSenha = new JPasswordField();
        txtConfirmarSenha.setBounds(510, 198, 145, 26);
        panel.add(txtConfirmarSenha);

        adicionarLabel(panel, "Idade", 350, 232);
        txtIdade = new JTextField();
        txtIdade.setBounds(350, 252, 80, 26);
        panel.add(txtIdade);

        adicionarLabel(panel, "Sexo", 450, 232);
        cbSexo = new JComboBox<>(Sexo.values());
        cbSexo.setBounds(450, 252, 205, 26);
        panel.add(cbSexo);

        // Gêneros favoritos
        adicionarLabel(panel, "Gêneros Favoritos", 350, 290);
        int gx = 350;
        int gy = 312;
        int col = 0;
        for (Genero genero : Genero.values()) {
            JCheckBox box = new JCheckBox(genero.getRotulo());
            box.setBackground(Color.WHITE);
            box.setForeground(AssetLoader.COR_PRIMARIA);
            box.setBounds(gx + (col * 155), gy, 155, 22);
            checkboxesGeneros.put(genero, box);
            panel.add(box);
            col++;
            if (col == 2) {
                col = 0;
                gy += 24;
            }
        }

        JButton btnRegistrar = new JButton("Registrar →");
        btnRegistrar.setBounds(440, 405, 140, 34);
        btnRegistrar.setBackground(AssetLoader.COR_PRIMARIA);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.addActionListener(e -> registrarUsuario());
        panel.add(btnRegistrar);

        frame.setContentPane(panel);
    }

    private static void adicionarLabel(JPanel panel, String texto, int x, int y) {
        JLabel lbl = new JLabel(texto);
        lbl.setBounds(x, y, 200, 18);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(AssetLoader.COR_PRIMARIA);
        panel.add(lbl);
    }

    private void registrarUsuario() {
        try {
            int idade = Integer.parseInt(txtIdade.getText().trim());
            Set<Genero> preferidos = new LinkedHashSet<>();
            checkboxesGeneros.forEach((genero, box) -> {
                if (box.isSelected()) {
                    preferidos.add(genero);
                }
            });

            autenticacaoService.registrar(
                    txtNome.getText(),
                    txtLogin.getText(),
                    txtEmail.getText(),
                    (Sexo) cbSexo.getSelectedItem(),
                    idade,
                    preferidos,
                    new String(txtSenha.getPassword()),
                    new String(txtConfirmarSenha.getPassword())
            );

            JOptionPane.showMessageDialog(
                    frame,
                    "Cadastro realizado com sucesso! Faça login para acessar o catálogo.",
                    "Registrado",
                    JOptionPane.INFORMATION_MESSAGE
            );
            frame.dispose();
            new LoginFrame(autenticacaoService, catalogoService).exibir();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Informe uma idade numérica válida.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    e.getMessage(),
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
