package biblioteca.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Carregador seguro de imagens e ícones da interface gráfica.
 *
 * <p>Corrige os erros de {@link NullPointerException} que ocorriam na versão de 2021
 * quando caminhos relativos divergiam entre {@code Library/} e {@code Library/Biblioteca/}.
 */
public final class AssetLoader {
    public static final Color COR_PRIMARIA = Color.decode("#2E3E77");
    public static final Color COR_SECUNDARIA = Color.decode("#3CC3BE");
    public static final Color COR_SECUNDARIA_HOVER = Color.decode("#319592");
    public static final Color COR_FUNDO_CLARO = Color.decode("#F8FAFC");

    private AssetLoader() {
    }

    public static ImageIcon loadIcon(String relativePath, int width, int height) {
        ImageIcon raw = resolveIcon(relativePath);
        if (raw != null && raw.getIconWidth() > 0 && raw.getIconHeight() > 0) {
            Image scaled = raw.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return createPlaceholderIcon(width, height, "Biblioteca");
    }

    public static ImageIcon loadBookCover(String relativePath, String titulo, int width, int height) {
        ImageIcon raw = resolveIcon(relativePath);
        if (raw != null && raw.getIconWidth() > 0 && raw.getIconHeight() > 0) {
            Image scaled = raw.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return createPlaceholderIcon(width, height, titulo);
    }

    private static ImageIcon resolveIcon(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        File direct = new File(path);
        if (direct.isFile()) {
            return new ImageIcon(direct.getAbsolutePath());
        }
        String normalized = path.startsWith("/") ? path.substring(1) : path;
        URL resource = AssetLoader.class.getClassLoader().getResource(normalized);
        if (resource != null) {
            return new ImageIcon(resource);
        }
        return null;
    }

    private static ImageIcon createPlaceholderIcon(int width, int height, String label) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(COR_PRIMARIA);
        g2.fillRoundRect(0, 0, width, height, 12, 12);
        g2.setColor(COR_SECUNDARIA);
        g2.drawRoundRect(2, 2, width - 5, height - 5, 10, 10);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, width / 10)));
        String texto = label == null || label.isBlank() ? "Livro" : label;
        if (texto.length() > 14) {
            texto = texto.substring(0, 12) + "...";
        }
        FontMetrics fm = g2.getFontMetrics();
        int x = Math.max(6, (width - fm.stringWidth(texto)) / 2);
        int y = height / 2;
        g2.drawString(texto, x, y);
        g2.dispose();
        return new ImageIcon(img);
    }
}
