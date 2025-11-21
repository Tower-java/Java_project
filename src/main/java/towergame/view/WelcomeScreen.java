package towergame.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

/**
 * Écran d'accueil pour Tower Battle
 * Affiche le titre du jeu et "Press any key to start"
 */
public class WelcomeScreen extends JFrame implements KeyListener {

    private static final Color BG_COLOR = new Color(20, 20, 35);
    private static final Color GOLD_COLOR = new Color(255, 215, 0);
    private static final Color TEXT_WHITE = new Color(245, 245, 245);

    private JLabel pressKeyLabel;
    private Timer blinkTimer;
    private boolean textVisible = true;
    private Runnable onStartCallback;

    public WelcomeScreen(Runnable onStartCallback) {
        this.onStartCallback = onStartCallback;
        initializeUI();
        setupBlinkingText();

        // Important: Ajouter le KeyListener AVANT setVisible
        addKeyListener(this);
        setFocusable(true);

        // Ajouter un WindowListener pour s'assurer du focus
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // Forcer le focus sur la fenêtre principale
                SwingUtilities.invokeLater(() -> {
                    requestFocus();
                    requestFocusInWindow();
                });
            }

            @Override
            public void windowActivated(WindowEvent e) {
                // Lorsque la fenêtre devient active, forcer le focus
                SwingUtilities.invokeLater(() -> {
                    requestFocus();
                    requestFocusInWindow();
                });
            }
        });
    }

    private void initializeUI() {
        setTitle("Tower Battle - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal avec fond sombre
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                // Gradient de fond
                GradientPaint gradient = new GradientPaint(
                        0, 0, BG_COLOR,
                        0, getHeight(), BG_COLOR.darker());
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Effet d'étoiles
                g2d.setColor(new Color(255, 255, 255, 80));
                for (int i = 0; i < 50; i++) {
                    int x = (int) (Math.random() * getWidth());
                    int y = (int) (Math.random() * getHeight());
                    g2d.fillOval(x, y, 2, 2);
                }

                g2d.dispose();
            }
        };

        // Panel central pour le contenu
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));

        // Titre principal
        JLabel titleLabel = new JLabel("⚔️ TOWER BATTLE ⚔️", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(GOLD_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Sous-titre
        JLabel subtitleLabel = new JLabel("Epic RPG Adventure", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        subtitleLabel.setForeground(TEXT_WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Espacement
        Component verticalSpace1 = Box.createRigidArea(new Dimension(0, 80));

        // Description du jeu
        JLabel descLabel = new JLabel(
                "<html><div style='text-align: center;'>Affrontez des boss légendaires<br>dans des combats au tour par tour</div></html>",
                SwingConstants.CENTER);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Espacement
        Component verticalSpace2 = Box.createRigidArea(new Dimension(0, 100));

        // Texte "Press any key"
        pressKeyLabel = new JLabel("APPUYEZ SUR N'IMPORTE QUELLE TOUCHE POUR COMMENCER", SwingConstants.CENTER);
        pressKeyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        pressKeyLabel.setForeground(GOLD_COLOR);
        pressKeyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        pressKeyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Ajout des composants
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(subtitleLabel);
        contentPanel.add(verticalSpace1);
        contentPanel.add(descLabel);
        contentPanel.add(verticalSpace2);
        contentPanel.add(pressKeyLabel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Footer avec crédits
        JLabel creditLabel = new JLabel("Created with ❤️ for Epic Gaming", SwingConstants.CENTER);
        creditLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        creditLabel.setForeground(new Color(150, 150, 150));
        creditLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(creditLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupBlinkingText() {
        // Timer pour faire clignoter le texte "Press any key"
        blinkTimer = new Timer(800, e -> {
            textVisible = !textVisible;
            pressKeyLabel.setVisible(textVisible);
        });
        blinkTimer.start();
    }

    private void startGame() {
        // Arrêter le timer de clignotement
        if (blinkTimer != null) {
            blinkTimer.stop();
        }

        // Fermer cet écran
        dispose();

        // Lancer le jeu
        if (onStartCallback != null) {
            SwingUtilities.invokeLater(onStartCallback);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        startGame();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Non utilisé
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Non utilisé
    }

    /**
     * Point d'entrée pour tester l'écran d'accueil
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WelcomeScreen welcome = new WelcomeScreen(() -> {
                System.out.println("Démarrage du jeu...");
                // Ici on lancerait le vrai jeu
                new GameWindow().setVisible(true);
            });
            welcome.setVisible(true);
        });
    }
}