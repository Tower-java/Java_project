package towergame.view;

import javax.swing.*;
import java.awt.*;

/**
 * Écran de fin pour Tower Battle
 * Affiche les résultats et propose de rejouer ou quitter
 */
public class GameEndScreen extends JFrame {

    private static final Color BG_COLOR = new Color(20, 20, 35);
    private static final Color GOLD_COLOR = new Color(255, 215, 0);
    private static final Color TEXT_WHITE = new Color(245, 245, 245);
    private static final Color WIN_COLOR = new Color(34, 197, 94);
    private static final Color LOSE_COLOR = new Color(239, 68, 68);

    private boolean playerWon;
    private String playerName;
    private String bossName;
    private int turnCount;
    private Runnable onRestartCallback;
    private Runnable onExitCallback;

    public GameEndScreen(boolean playerWon, String playerName, String bossName, int turnCount,
            Runnable onRestartCallback, Runnable onExitCallback) {
        this.playerWon = playerWon;
        this.playerName = playerName;
        this.bossName = bossName;
        this.turnCount = turnCount;
        this.onRestartCallback = onRestartCallback;
        this.onExitCallback = onExitCallback;

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Tower Battle - Fin de partie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal avec fond animé
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                // Gradient de fond selon victoire/défaite
                Color topColor = playerWon ? WIN_COLOR.darker().darker() : LOSE_COLOR.darker().darker();
                Color bottomColor = BG_COLOR;

                GradientPaint gradient = new GradientPaint(
                        0, 0, topColor,
                        0, getHeight(), bottomColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Particules d'ambiance
                g2d.setColor(new Color(255, 255, 255, playerWon ? 120 : 60));
                for (int i = 0; i < (playerWon ? 80 : 30); i++) {
                    int x = (int) (Math.random() * getWidth());
                    int y = (int) (Math.random() * getHeight());
                    int size = playerWon ? 3 : 1;
                    g2d.fillOval(x, y, size, size);
                }

                g2d.dispose();
            }
        };

        // Panel central pour le contenu
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Résultat principal
        String resultEmoji = playerWon ? "🎆" : "🌑";
        String resultText = playerWon ? "VICTOIRE !" : "DÉFAITE";
        JLabel resultLabel = new JLabel(resultEmoji + " " + resultText + " " + resultEmoji, SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 42));
        resultLabel.setForeground(playerWon ? WIN_COLOR : LOSE_COLOR);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Message narratif
        String narrativeText;
        if (playerWon) {
            narrativeText = "<html><center>Félicitation tu es le héros de cette histoire,<br>tu es devenu la légende de cette histoire en 3 actes.</center></html>";
        } else {
            narrativeText = "<html><center>Défaite, un autre héros viendra prendre ta place,<br>ton échec n'est qu'un contretemps</center></html>";
        }

        JLabel narrativeLabel = new JLabel(narrativeText, SwingConstants.CENTER);
        narrativeLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        narrativeLabel.setForeground(TEXT_WHITE);
        narrativeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setOpaque(false);

        // Bouton Recommencer
        JButton restartButton = createStyledButton("recommencer", WIN_COLOR, () -> {
            dispose();
            if (onRestartCallback != null) {
                SwingUtilities.invokeLater(onRestartCallback);
            }
        });

        // Bouton Quitter
        JButton exitButton = createStyledButton("quitter", LOSE_COLOR, () -> {
            dispose();
            if (onExitCallback != null) {
                SwingUtilities.invokeLater(onExitCallback);
            } else {
                System.exit(0);
            }
        });

        buttonPanel.add(restartButton);
        buttonPanel.add(exitButton);

        // Assemblage
        contentPanel.add(resultLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(narrativeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        contentPanel.add(buttonPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Citation inspirante en bas
        String quote = playerWon ? "\"La vraie victoire est celle qui élève l'âme.\" - Platon"
                : "\"Il n'y a pas d'échec, seulement des leçons.\" - Nelson Mandela";

        JLabel quoteLabel = new JLabel(quote, SwingConstants.CENTER);
        quoteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        quoteLabel.setForeground(new Color(150, 150, 150));
        quoteLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(quoteLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color baseColor, Runnable action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond avec gradient
                GradientPaint gradient = new GradientPaint(
                        0, 0, baseColor,
                        0, getHeight(), baseColor.darker());
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Bordure
                g2d.setColor(baseColor.darker().darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);

                // Texte
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(200, 60));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        // Effet de survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        button.addActionListener(e -> action.run());

        return button;
    }

    /**
     * Méthode statique pour afficher l'écran de fin
     */
    public static void showGameEnd(boolean playerWon, String playerName, String bossName, int turnCount,
            Runnable onRestart, Runnable onExit) {
        SwingUtilities.invokeLater(() -> {
            GameEndScreen endScreen = new GameEndScreen(playerWon, playerName, bossName, turnCount, onRestart, onExit);
            endScreen.setVisible(true);
        });
    }
}