package towergame.view;

import towergame.model.actions.AAction;
import towergame.model.entities.ABoss;
import towergame.model.entities.Player;
import towergame.model.entities.FireElementalBoss;
import towergame.model.managers.BattleManager;
import towergame.model.managers.StageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Interface graphique Swing pour Tower Battle
 * Style Pokémon/Final Fantasy 7 avec effets visuels
 */
public class GameWindow extends JFrame {

    private Player player;
    private ABoss boss;
    private BattleManager battleManager;

    // Composants GUI
    private JLabel titleLabel;
    private JLabel turnLabel;
    private JLabel messageLabel;
    private JLabel actionMessageLabel; // NOUVEAU: Messages d'actions en bas

    // Sprites
    private BufferedImage fireBossSprite;
    private BufferedImage fireBossEnragedSprite;
    private BufferedImage rageFrameSprite; // Sprite du cadre ragefeu
    private JLabel bossSpriteLabel;

    // Labels pour les cadres ragefeu
    private JLabel playerRageFrameLabel;
    private JLabel bossRageFrameLabel;

    // Animation par frames
    private java.util.List<BufferedImage> fireBossAttackFrames;
    private java.util.List<BufferedImage> fireBossAttackEnragedFrames;
    private Timer animationTimer;
    private boolean isAnimating = false;
    private int currentFrame = 0;
    private boolean bossAttackedThisTurn = false; // Track si le boss a attaqué

    // Effets visuels
    private boolean isBossEnraged = false;
    private JLabel attackOverlayLabel; // Pour afficher le poing de feu sur le joueur
    private BufferedImage fireAttackSprite;

    private JLabel playerNameLabel;
    private JLabel playerHpLabel;
    private JPanel actionPanel;
    private JPanel playerPanel;
    private JPanel bossPanel;

    // Couleurs style gaming
    private static final Color BG_COLOR = new Color(26, 26, 46);
    private static final Color PANEL_COLOR = new Color(40, 40, 60, 200);
    private static final Color GOLD_COLOR = new Color(255, 215, 0);
    private static final Color PLAYER_COLOR = new Color(78, 205, 196);
    private static final Color BOSS_COLOR = new Color(255, 107, 107);
    private static final Color TEXT_WHITE = Color.WHITE;

    // Couleurs pour les boutons d'action
    private static final Color HEAL_COLOR = new Color(34, 197, 94);
    private static final Color DEFENSE_COLOR = new Color(59, 130, 246);
    private static final Color BOOST_COLOR = new Color(147, 51, 234);
    private static final Color FIRE_COLOR = new Color(239, 68, 68);
    private static final Color ICE_COLOR = new Color(6, 182, 212);
    private static final Color NATURE_COLOR = new Color(101, 163, 13);
    private static final Color DEFAULT_ATTACK_COLOR = new Color(249, 115, 22);

    public GameWindow() {
        loadSprites();
        initializeGame();
        setupUI();
        updateDisplay();
    }

    private void initializeGame() {
        try {
            StageManager stageManager = new StageManager();
            player = new Player("Héros");

            List<AAction> availableActions = stageManager.getUnlockedActions();
            player.setEquippedActions(availableActions.subList(0, Math.min(4, availableActions.size())));

            boss = stageManager.getNextBoss();
            battleManager = new BattleManager(player, boss);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur d'initialisation: " + e.getMessage());
            System.exit(1);
        }
    }

    private void setupUI() {
        // FORCER le rendu personnalisé pour les boutons (contourne le Look & Feel)
        UIManager.put("Button.background", Color.WHITE);
        UIManager.put("Button.opaque", true);
        UIManager.put("Button.contentAreaFilled", true);

        setTitle("⚔️ Tower Battle - Style FF7/Pokémon ⚔️");
        setSize(900, 760); // AUGMENTÉ: Pour accommoder la nouvelle zone de messages
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Fond dégradé
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout(20, 20));

        // Panel principal avec margin
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        setupHeaderPanel(mainPanel);
        setupBattlePanel(mainPanel);
        setupActionPanel(mainPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupHeaderPanel(JPanel mainPanel) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(900, 100)); // FIXE: Taille fixe pour éviter glitches
        headerPanel.setMinimumSize(new Dimension(900, 100));
        headerPanel.setMaximumSize(new Dimension(900, 100));

        // Titre du jeu
        titleLabel = new JLabel("⚔️ TOWER BATTLE ⚔️", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(GOLD_COLOR);

        // Informations de tour
        turnLabel = new JLabel("Tour 1", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 18));
        turnLabel.setForeground(TEXT_WHITE);

        // Message de statut (maintenant statique)
        messageLabel = new JLabel("Style Pokémon/Final Fantasy 7", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        messageLabel.setForeground(new Color(160, 160, 160)); // Gris discret

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(turnLabel, BorderLayout.CENTER);
        headerPanel.add(messageLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void setupBattlePanel(JPanel mainPanel) {
        JPanel battlePanel = new JPanel(new GridLayout(1, 3, 30, 0));
        battlePanel.setOpaque(false);
        battlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Panel Player (à gauche - position conventionnelle) - INTEGRE COMPLET
        playerPanel = createCompleteEntityPanel("🛡️ HÉROS 🛡️", PLAYER_COLOR, true);

        // Panel VS (au centre) - complètement opaque pour éviter saignements
        JPanel vsPanel = new JPanel(new GridLayout(3, 1));
        vsPanel.setOpaque(true); // CORRECTION: Fond complètement opaque
        vsPanel.setBackground(BG_COLOR); // CORRECTION: Fond identique sans transparence
        vsPanel.setPreferredSize(new Dimension(100, 300));
        vsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel sword1 = new JLabel("⚔️", SwingConstants.CENTER);
        sword1.setOpaque(false);
        vsPanel.add(sword1);

        JLabel vsLabel = new JLabel("VS", SwingConstants.CENTER);
        vsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        vsLabel.setForeground(GOLD_COLOR);
        vsLabel.setOpaque(false);
        vsPanel.add(vsLabel);

        JLabel sword2 = new JLabel("⚔️", SwingConstants.CENTER);
        sword2.setOpaque(false);
        vsPanel.add(sword2);

        // Panel Boss (à droite - comme dans les JRPGs classiques) - SPRITE PUR
        // UNIQUEMENT
        bossPanel = createPureSpritePanel();

        battlePanel.add(playerPanel);
        battlePanel.add(vsPanel);
        battlePanel.add(bossPanel);

        mainPanel.add(battlePanel, BorderLayout.CENTER);
    }

    private JPanel createEntityPanel(String title, Color color) {
        // SOLUTION RADICALE: Panel avec ZERO TRANSPARENCE - Aucun glitch possible
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // FORCER le remplissage complet avec anti-aliasing
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setColor(PANEL_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                // Bordure interne pour éviter les compound borders
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
                g2d.dispose();
            }
        };

        // Layout SIMPLE - pas de BoxLayout qui peut causer des glitchs
        panel.setLayout(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setOpaque(true);
        panel.setPreferredSize(new Dimension(250, 300));
        panel.setMinimumSize(new Dimension(250, 300));
        panel.setMaximumSize(new Dimension(250, 300));

        // ZERO BORDER - tout est géré dans paintComponent
        panel.setBorder(null);

        // Container central pour tout le contenu
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Fond uniforme pour le contenu
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(PANEL_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(PANEL_COLOR);
        contentPanel.setOpaque(true);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // Title ZERO-TRANSPARENCY
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                // Fond explicite identique au parent
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(PANEL_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(color);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(PANEL_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Spacer OPAQUE au lieu de RigidArea
        JPanel spacer1 = new JPanel();
        spacer1.setBackground(PANEL_COLOR);
        spacer1.setOpaque(true);
        spacer1.setPreferredSize(new Dimension(0, 15));
        spacer1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));

        // Sprite ZERO-TRANSPARENCY avec fond forcé
        JLabel spriteLabel = new JLabel("", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                // Fond 100% opaque
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(PANEL_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        if (color == PLAYER_COLOR) {
            spriteLabel.setText("🤺"); // Joueur
        } else {
            spriteLabel.setText("👺"); // Boss
        }
        spriteLabel.setFont(new Font("Arial", Font.PLAIN, 80));
        spriteLabel.setOpaque(true);
        spriteLabel.setBackground(PANEL_COLOR);
        spriteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Spacer OPAQUE au lieu de RigidArea
        JPanel spacer2 = new JPanel();
        spacer2.setBackground(PANEL_COLOR);
        spacer2.setOpaque(true);
        spacer2.setPreferredSize(new Dimension(0, 15));
        spacer2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));

        // Assemblage SANS TRANSPARENCE
        contentPanel.add(titleLabel);
        contentPanel.add(spacer1);
        contentPanel.add(spriteLabel);
        contentPanel.add(spacer2);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    // NOUVELLE MÉTHODE: Création complète et intégrée d'un panel entité avec ZERO
    // ULTRA-RADICALE: Panel avec OPACITE ABSOLUE et Z-ORDER FORCE
    private JPanel createCompleteEntityPanel(String title, Color color, boolean isPlayer) {
        // Panel principal avec OPACITE MAXIMALE ABSOLUE
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // OPACITE ABSOLUE - RIEN ne peut passer à travers
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

                // TRIPLE COUCHE de remplissage pour éliminer TOUTE transparence
                g2d.setColor(PANEL_COLOR);
                g2d.fillRect(-10, -10, getWidth() + 20, getHeight() + 20); // Débordement intentionnel
                g2d.fillRect(0, 0, getWidth(), getHeight()); // Couche normale
                g2d.setComposite(AlphaComposite.Src); // FORCE l'opacité complète
                g2d.fillRect(0, 0, getWidth(), getHeight()); // Couche de sécurité

                // Bordure OPAQUE intégrée
                g2d.setComposite(AlphaComposite.SrcOver);
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(3.0f)); // Plus épaisse
                g2d.drawRect(2, 2, getWidth() - 5, getHeight() - 5);
                g2d.dispose();
            }

            @Override
            public boolean isOpaque() {
                return true; // FORCE l'opacité
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setOpaque(true);
        panel.setDoubleBuffered(true); // FORCE le double buffering
        panel.setPreferredSize(new Dimension(250, 300));
        panel.setMinimumSize(new Dimension(250, 300));
        panel.setMaximumSize(new Dimension(250, 300));
        panel.setBorder(null); // Container avec OPACITE ABSOLUE
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

                // TRIPLE COUCHE pour le contenu aussi
                g2d.setColor(PANEL_COLOR);
                g2d.fillRect(-5, -5, getWidth() + 10, getHeight() + 10);
                g2d.setComposite(AlphaComposite.Src);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }

            @Override
            public boolean isOpaque() {
                return true;
            }
        };
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(PANEL_COLOR);
        contentPanel.setOpaque(true);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // Title intégré
        JLabel titleLabel = createZeroGlitchLabel(title, color, 12, Font.BOLD);

        // Spacer 1
        JPanel spacer1 = createOpaqueSpacer(15);

        // Sprite intégré
        JLabel spriteLabel;
        if (isPlayer) {
            spriteLabel = createZeroGlitchLabel("", color, 80, Font.PLAIN);
            spriteLabel.setText("🤺"); // Joueur (pour l'instant emoji)
        } else {
            // BOSS - Utiliser le vrai sprite du boss de feu
            spriteLabel = createSpriteLabel(fireBossSprite, 150, 150);
            bossSpriteLabel = spriteLabel; // Référence pour mises à jour ultérieures
        }

        // Spacer 2
        JPanel spacer2 = createOpaqueSpacer(15);

        // Labels de noms et HP intégrés directement
        if (isPlayer) {
            playerNameLabel = createZeroGlitchLabel("", PLAYER_COLOR, 16, Font.BOLD);
            playerHpLabel = createZeroGlitchLabel("", PLAYER_COLOR, 14, Font.BOLD);

            contentPanel.add(titleLabel);
            contentPanel.add(spacer1);
            contentPanel.add(spriteLabel);
            contentPanel.add(spacer2);
            contentPanel.add(playerNameLabel);
            contentPanel.add(playerHpLabel);
        } else {
            // Cette méthode n'est plus utilisée pour le boss, mais gardons la structure
            // pour compatibilité
            JLabel dummyLabel1 = createZeroGlitchLabel("", BOSS_COLOR, 16, Font.BOLD);
            JLabel dummyLabel2 = createZeroGlitchLabel("", BOSS_COLOR, 14, Font.BOLD);

            contentPanel.add(titleLabel);
            contentPanel.add(spacer1);
            contentPanel.add(spriteLabel);
            contentPanel.add(spacer2);
            contentPanel.add(dummyLabel1);
            contentPanel.add(dummyLabel2);
        }

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    // Méthode helper pour créer des labels OPACITE ABSOLUE
    private JLabel createZeroGlitchLabel(String text, Color color, int fontSize, int fontStyle) {
        JLabel label = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                // OPACITE ABSOLUE pour les labels
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

                // DOUBLE COUCHE de sécurité
                g2d.setColor(PANEL_COLOR);
                g2d.fillRect(-2, -2, getWidth() + 4, getHeight() + 4);
                g2d.setComposite(AlphaComposite.Src);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return true;
            }
        };
        label.setFont(new Font("Arial", fontStyle, fontSize));
        label.setForeground(color);
        label.setOpaque(true);
        label.setBackground(PANEL_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    // Méthode helper pour créer des spacers OPACITE ABSOLUE
    private JPanel createOpaqueSpacer(int height) {
        JPanel spacer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(PANEL_COLOR);
                g2d.setComposite(AlphaComposite.Src);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }

            @Override
            public boolean isOpaque() {
                return true;
            }
        };
        spacer.setBackground(PANEL_COLOR);
        spacer.setOpaque(true);
        spacer.setDoubleBuffered(true);
        spacer.setPreferredSize(new Dimension(0, height));
        spacer.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return spacer;
    }

    private void setupActionPanel(JPanel mainPanel) {
        JPanel actionContainer = new JPanel(new BorderLayout());
        actionContainer.setOpaque(true); // CORRECTION: Opacité complète pour bloquer les glitchs
        actionContainer.setBackground(BG_COLOR); // CORRECTION: Fond uniforme
        actionContainer.setPreferredSize(new Dimension(900, 180)); // AUGMENTÉ: Plus d'espace pour les messages

        actionPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        setupActionButtons();

        actionContainer.add(actionPanel, BorderLayout.NORTH);

        // Zone de messages informatifs déplacée EN DESSOUS des boutons pour éviter les
        // coupures
        JPanel messageContainer = new JPanel(new BorderLayout());
        messageContainer.setOpaque(true); // CORRECTION: Fond opaque pour bloquer la transparence
        messageContainer.setBackground(BG_COLOR); // CORRECTION: Fond identique au thème
        messageContainer.setPreferredSize(new Dimension(900, 80)); // Espace dédié aux messages
        messageContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        actionMessageLabel = new JLabel(" ", SwingConstants.CENTER); // VIDE par défaut
        actionMessageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        actionMessageLabel.setForeground(GOLD_COLOR);
        actionMessageLabel.setVerticalAlignment(SwingConstants.TOP); // Alignement en haut pour éviter coupures
        actionMessageLabel.setOpaque(true); // CORRECTION: Complètement opaque
        actionMessageLabel.setBackground(new Color(40, 40, 60)); // CORRECTION: Fond opaque sans transparence
        actionMessageLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_COLOR, 2), // CORRECTION: Bordure plus épaisse
                BorderFactory.createEmptyBorder(12, 18, 12, 18))); // CORRECTION: Plus de padding

        messageContainer.add(actionMessageLabel, BorderLayout.CENTER);
        actionContainer.add(messageContainer, BorderLayout.CENTER);

        mainPanel.add(actionContainer, BorderLayout.SOUTH);
    }

    private void setupActionButtons() {
        actionPanel.removeAll();

        for (AAction action : player.getEquippedActions()) {
            JButton button = createActionButton(action);
            actionPanel.add(button);
        }

        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private JButton createActionButton(AAction action) {
        String buttonText = action.getName();
        if (!action.isReady()) {
            buttonText += " (" + action.getCurrentCooldown() + ")";
        }

        // CRÉER UN BOUTON COMPLÈTEMENT PERSONNALISÉ
        JButton button = new JButton(buttonText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dessiner le fond avec la couleur personnalisée
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Dessiner la bordure
                g2d.setColor(getBorder() != null ? getForeground().darker() : Color.GRAY);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);

                // Dessiner le texte
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(170, 55));

        // Désactiver complètement le rendu par défaut
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false); // Style des boutons selon le type d'action avec couleurs vibrantes
        String actionName = action.getName().toLowerCase();
        Color buttonColor;

        if (actionName.contains("soin") || actionName.contains("heal")) {
            // Vert éclatant pour les soins
            buttonColor = HEAL_COLOR;
        } else if (actionName.contains("barrière") || actionName.contains("défense")
                || actionName.contains("protection")) {
            // Bleu royal pour la défense
            buttonColor = DEFENSE_COLOR;
        } else if (actionName.contains("boost") || actionName.contains("amélioration")) {
            // Violet magique pour les buffs
            buttonColor = BOOST_COLOR;
        } else if (actionName.contains("feu") || actionName.contains("fire")) {
            // Rouge feu pour les attaques de feu
            buttonColor = FIRE_COLOR;
        } else if (actionName.contains("glace") || actionName.contains("ice")) {
            // Cyan glacé pour les attaques de glace
            buttonColor = ICE_COLOR;
        } else if (actionName.contains("plante") || actionName.contains("nature")) {
            // Vert nature pour les attaques de plante
            buttonColor = NATURE_COLOR;
        } else {
            // Orange par défaut pour les attaques normales
            buttonColor = DEFAULT_ATTACK_COLOR;
        }

        // Application des couleurs pour le rendu personnalisé
        button.setBackground(buttonColor);
        button.setForeground(Color.WHITE);

        // Gestion des boutons désactivés
        if (!action.isReady()) {
            // Griser le bouton si en cooldown
            Color disabledColor = new Color(
                    buttonColor.getRed() / 2,
                    buttonColor.getGreen() / 2,
                    buttonColor.getBlue() / 2);
            button.setBackground(disabledColor);
            button.setForeground(new Color(180, 180, 180));
        }
        button.setEnabled(action.isReady() && !battleManager.isBattleOver());

        // Ajouter des effets de survol pour plus d'interactivité
        Color originalBackground = button.getBackground();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    // Éclaircir la couleur au survol
                    Color hover = new Color(
                            Math.min(255, originalBackground.getRed() + 30),
                            Math.min(255, originalBackground.getGreen() + 30),
                            Math.min(255, originalBackground.getBlue() + 30));
                    button.setBackground(hover);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(originalBackground);
                }
            }
        });

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executePlayerAction(action);
            }
        });

        return button;
    }

    private void executePlayerAction(AAction action) {
        if (battleManager.isBattleOver())
            return;

        // Désactiver les boutons pendant la séquence
        disableActionButtons();

        // Séquence 1: Description de l'action du héros (timing dynamique)
        String heroActionText = getDetailedHeroActionDescription(action);
        int heroActionDuration = calculateReadingDuration(heroActionText);
        showDetailedMessage(heroActionText, PLAYER_COLOR, heroActionDuration);

        // Séquence 2: Exécuter le tour et afficher les effets (après timing + 200ms de
        // transition)
        Timer executionTimer = new Timer(heroActionDuration + 200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Capturer l'état avant
                int playerHpBefore = player.getHp();
                int bossHpBefore = boss.getHp();
                boolean wasEnraged = boss.isEnraged();

                // Exécuter le tour
                battleManager.executeTurn(action);

                // Note: fire_attack PNG est exclusivement pour les attaques du boss vers le
                // héros
                // Pas d'effet PNG quand le héros attaque

                // Séquence 3: Afficher les effets de l'action (timing dynamique)
                String effectsText = getActionEffectsDescription(action, playerHpBefore, bossHpBefore);
                int effectsDuration = calculateReadingDuration(effectsText);
                showDetailedMessage(effectsText, TEXT_WHITE, effectsDuration);

                // Séquence 4: Action du boss (après timing + 200ms de transition)
                Timer bossActionTimer = new Timer(effectsDuration + 200, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        // Vérifier si le boss a infligé des dégâts au héros (= attaque)
                        int playerDamage = playerHpBefore - player.getHp();
                        bossAttackedThisTurn = (playerDamage > 0);

                        System.out.println("🔍 DEBUG ATTAQUE: playerHpBefore=" + playerHpBefore + ", playerHpNow="
                                + player.getHp() + ", playerDamage=" + playerDamage);
                        System.out.println("🔍 DEBUG ATTAQUE: bossAttackedThisTurn=" + bossAttackedThisTurn);

                        // Déclencher l'animation d'attaque du boss SEULEMENT s'il a attaqué
                        if (bossAttackedThisTurn) {
                            System.out.println("🎯 Boss a attaqué ! Déclenchement de playBossAttackAnimation()");
                            playBossAttackAnimation();
                        } else {
                            System.out.println("❌ Boss n'a pas attaqué, pas d'animation");
                        }

                        String bossActionText = getBossActionDescription(playerHpBefore, bossHpBefore, wasEnraged);
                        int bossActionDuration = 1000; // Durée par défaut si pas de texte

                        if (!bossActionText.isEmpty()) {
                            bossActionDuration = calculateReadingDuration(bossActionText);
                            showDetailedMessage(bossActionText, BOSS_COLOR, bossActionDuration);
                        }

                        // Séquence 5: Finaliser (après timing + 200ms de transition)
                        Timer finalTimer = new Timer(bossActionDuration + 200, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt2) {
                                updateDisplay();
                                enableActionButtons();

                                if (battleManager.isBattleOver()) {
                                    Timer endTimer = new Timer(2000, evt3 -> endBattle());
                                    endTimer.setRepeats(false);
                                    endTimer.start();
                                }

                                ((Timer) evt2.getSource()).stop();
                            }
                        });
                        finalTimer.setRepeats(false);
                        finalTimer.start();

                        ((Timer) evt.getSource()).stop();
                    }
                });
                bossActionTimer.setRepeats(false);
                bossActionTimer.start();

                ((Timer) e.getSource()).stop();
            }
        });
        executionTimer.setRepeats(false);
        executionTimer.start();
    }

    // Nouveau système de messages détaillés avec timing dynamique
    private void showDetailedMessage(String message, Color color, int duration) {
        // CORRECTION: Nettoyer complètement avant d'afficher le nouveau message
        clearMessageDisplay();

        actionMessageLabel.setText("<html><center>" + message.replace("\\n", "<br>") + "</center></html>");
        actionMessageLabel.setForeground(color);
        actionMessageLabel.repaint(); // CORRECTION: Forcer le rafraîchissement

        // Utiliser la durée dynamique si une durée fixe n'est pas imposée
        int actualDuration = (duration > 0) ? duration : calculateReadingDuration(message);

        // Debug: afficher la durée calculée dans la console
        System.out.println("Message: \"" + message.substring(0, Math.min(50, message.length())) +
                (message.length() > 50 ? "..." : "") +
                "\" - Durée: " + actualDuration + "ms");

        // Affichage avec timing optimisé pour la lecture
        Timer displayTimer = new Timer(actualDuration, evt -> {
            clearMessageDisplay(); // CORRECTION: Nettoyage complet
        });
        displayTimer.setRepeats(false);
        displayTimer.start();
    }

    // NOUVELLE: Calcul de durée dynamique basée sur la longueur du texte - VERSION
    // ACCELEREE
    private int calculateReadingDuration(String text) {
        // Supprimer les balises HTML pour compter les mots réels
        String cleanText = text.replaceAll("<[^>]*>", "").trim();

        // Compter les mots (séparés par des espaces)
        String[] words = cleanText.split("\\s+");
        int wordCount = words.length;

        // Calcul ACCELERE : environ 3.0 mots par seconde (au lieu de 2.2)
        // + durée minimum réduite + bonus de caractères diminué
        int baseDuration = (int) Math.ceil(wordCount / 3.0 * 1000); // millisecondes
        int minimumDuration = 1500; // 1.5 secondes minimum (au lieu de 2)
        int characterBonus = cleanText.length() * 8; // Bonus réduit (au lieu de 15)

        int finalDuration = Math.max(minimumDuration, baseDuration + characterBonus);

        // Limiter à un maximum réduit de 6 secondes (au lieu de 8)
        return Math.min(finalDuration, 6000);
    }

    // NOUVELLE: Méthode pour nettoyer complètement l'affichage des messages
    private void clearMessageDisplay() {
        actionMessageLabel.setText(" ");
        actionMessageLabel.setForeground(GOLD_COLOR);
        actionMessageLabel.repaint();
        actionMessageLabel.getParent().repaint(); // CORRECTION: Nettoyer aussi le parent
    }

    // Méthodes pour gérer l'état des boutons pendant les séquences
    private void disableActionButtons() {
        for (Component comp : actionPanel.getComponents()) {
            comp.setEnabled(false);
        }
    }

    private void enableActionButtons() {
        for (Component comp : actionPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                // Réactiver seulement si l'action correspondante est prête
                button.setEnabled(!battleManager.isBattleOver());
            }
        }
        setupActionButtons(); // Recréer les boutons avec l'état correct
    }

    // Description des effets de l'action
    private String getActionEffectsDescription(AAction action, int playerHpBefore, int bossHpBefore) {
        StringBuilder effects = new StringBuilder();

        int bossDamage = bossHpBefore - boss.getHp();
        int playerHealing = player.getHp() - playerHpBefore;

        if (bossDamage > 0) {
            effects.append("L'attaque atteint sa cible ! Le boss subit ").append(bossDamage)
                    .append(" points de dégâts !");

            if (boss instanceof FireElementalBoss) {
                FireElementalBoss fireBoss = (FireElementalBoss) boss;
                if (fireBoss.isResistant(action.getElement())) {
                    effects.append(" Mais il résiste partiellement à l'attaque !");
                } else if (fireBoss.isWeak(action.getElement())) {
                    effects.append(" L'élément est très efficace contre lui !");
                }
            }
        } else if (bossDamage == 0 && !action.getName().toLowerCase().contains("soin")) {
            // CORRECTION: Gestion explicite des 0 dégâts d'attaque
            effects.append("L'attaque n'inflige aucun dégât ! Le boss résiste complètement !");
        } else if (playerHealing > 0) {
            effects.append("L'énergie curative fait effet ! Le héros récupère ").append(playerHealing)
                    .append(" points de vie !");
        } else if (action.getName().toLowerCase().contains("barrière")) {
            effects.append("Une barrière magique protège maintenant le héros !");
        } else {
            effects.append("L'action est exécutée avec succès !");
        }

        return effects.toString();
    }

    // Descriptions détaillées des actions du héros
    private String getDetailedHeroActionDescription(AAction action) {
        String actionName = action.getName().toLowerCase();

        if (actionName.contains("jet de glace")) {
            return "Le héros concentre son énergie et lance un gigantesque pic de glace vers son adversaire !";
        } else if (actionName.contains("boule de feu")) {
            return "Le héros invoque les flammes éternelles et projette une boule de feu dévastatrice !";
        } else if (actionName.contains("fragiliser")) {
            return "Le héros canalise une énergie sombre pour affaiblir les défenses de son ennemi !";
        } else if (actionName.contains("soin")) {
            return "Le héros invoque une lumière curative qui régénère ses blessures !";
        } else if (actionName.contains("barrière")) {
            return "Le héros érige une barrière magique pour se protéger des attaques ennemies !";
        }

        return "Le héros se prépare et exécute sa technique : " + action.getName() + " !";
    }

    // Description des actions du boss
    private String getBossActionDescription(int playerHpBefore, int bossHpBefore, boolean wasEnraged) {
        StringBuilder bossAction = new StringBuilder();

        int playerDamage = playerHpBefore - player.getHp();

        if (playerDamage > 0) {
            if (boss.getName().toLowerCase().contains("feu")) {
                bossAction.append("Le boss embrase le héros d'un coup de poing enflammé et inflige ")
                        .append(playerDamage).append(" points de dégâts !");
            } else {
                bossAction.append("Le boss contre-attaque férocement et inflige ")
                        .append(playerDamage).append(" points de dégâts au héros !");
            }
        } else if (playerDamage == 0) {
            // CORRECTION: Gestion explicite des 0 dégâts du boss
            bossAction.append("Le boss attaque mais n'inflige aucun dégât ! Le héros évite l'attaque !");
        }

        if (!wasEnraged && boss.isEnraged()) {
            if (bossAction.length() > 0)
                bossAction.append(" ");
            bossAction.append("La rage s'empare du boss ! Ses attaques deviennent plus dévastatrices !");
        }

        // Si aucune action du boss, retourner un message par défaut
        if (bossAction.length() == 0) {
            bossAction.append("Le boss reste en position défensive...");
        }

        return bossAction.toString();
    }

    private void updateDisplay() {
        turnLabel.setText("Tour " + battleManager.getTurnNumber());

        playerNameLabel.setText(player.getName());
        playerHpLabel.setText("HP: " + player.getHp() + "/" + player.getMaxHp());

        // Plus besoin d'afficher le nom et HP du boss dans l'interface
        // Le sprite seul suffit pour représenter le boss

        // Mettre à jour le sprite du boss selon son état
        updateBossSprite();
    }

    private void endBattle() {
        // Afficher un simple message de fin
        String message;
        if (player.isAlive()) {
            message = "🎉 Victoire ! " + player.getName() + " a vaincu " + boss.getName() + " !\n" +
                    "Tours de combat : " + battleManager.getTurnNumber();
        } else {
            message = "💀 Défaite... " + player.getName() + " a été vaincu par " + boss.getName() + ".\n" +
                    "Tours de combat : " + battleManager.getTurnNumber();
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                message + "\n\nVoulez-vous recommencer ?",
                "Fin de bataille",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new GameWindow().setVisible(true);
        } else {
            System.exit(0);
        }
    }

    /**
     * Crée un panel contenant UNIQUEMENT le sprite du boss, sans cadre ni texte
     */
    private JPanel createPureSpritePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Fond transparent/neutre
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setColor(BG_COLOR); // Même couleur que le fond principal
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setOpaque(true);
        panel.setPreferredSize(new Dimension(250, 300));
        panel.setMinimumSize(new Dimension(250, 300));
        panel.setMaximumSize(new Dimension(250, 300));
        panel.setBorder(null);

        // Créer le sprite label et l'ajouter au centre
        bossSpriteLabel = createSpriteLabel(fireBossSprite, 200, 250); // Plus grand pour occuper tout l'espace
        panel.add(bossSpriteLabel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Charge les sprites depuis les ressources
     */
    private void loadSprites() {
        try {
            // Charger le sprite du boss de feu normal
            InputStream fireBossStream = getClass().getResourceAsStream("/sprites/fire_boss.png");
            if (fireBossStream != null) {
                fireBossSprite = ImageIO.read(fireBossStream);
                fireBossStream.close();
                System.out.println("✅ Sprite boss de feu chargé avec succès!");
            } else {
                System.err.println("❌ Impossible de charger le sprite du boss de feu");
            }

            // Charger le sprite du boss de feu enragé (utilise fire_enrage.png)
            InputStream fireBossEnragedStream = getClass().getResourceAsStream("/sprites/fire_enrage.png");
            if (fireBossEnragedStream != null) {
                fireBossEnragedSprite = ImageIO.read(fireBossEnragedStream);
                fireBossEnragedStream.close();
                System.out.println("✅ Sprite enragé chargé avec succès!");
            } else {
                System.err.println("❌ Impossible de charger le sprite enragé");
            }

            // Charger le sprite d'attaque de feu (poing de feu)
            InputStream fireAttackStream = getClass().getResourceAsStream("/sprites/fire_attack.png");
            if (fireAttackStream != null) {
                fireAttackSprite = ImageIO.read(fireAttackStream);
                fireAttackStream.close();

                // Créer une liste avec juste cette image pour compatibilité
                fireBossAttackFrames = new java.util.ArrayList<>();
                fireBossAttackFrames.add(fireAttackSprite);

                // Même image pour l'attaque enragée (on peut différencier plus tard)
                fireBossAttackEnragedFrames = new java.util.ArrayList<>();
                fireBossAttackEnragedFrames.add(fireAttackSprite);

                System.out.println("✅ Sprite d'attaque de feu chargé avec succès!");
            } else {
                System.err.println("❌ Impossible de charger le sprite d'attaque de feu");
                fireBossAttackFrames = new java.util.ArrayList<>();
                fireBossAttackEnragedFrames = new java.util.ArrayList<>();
                fireAttackSprite = null;
            }

            // Charger le sprite du cadre ragefeu
            InputStream rageFrameStream = getClass().getResourceAsStream("/sprites/cadre ragefeu.png");
            if (rageFrameStream != null) {
                rageFrameSprite = ImageIO.read(rageFrameStream);
                rageFrameStream.close();
                System.out.println("✅ Sprite cadre ragefeu chargé avec succès!");
            } else {
                System.err.println("❌ Impossible de charger le sprite cadre ragefeu");
            }

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des sprites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Découpe une spritesheet en frames individuelles
     */
    private java.util.List<BufferedImage> loadSpritesheetFrames(String spritesheetPath, int frameCount, int rows) {
        java.util.List<BufferedImage> frames = new java.util.ArrayList<>();

        try {
            InputStream spritesheetStream = getClass().getResourceAsStream(spritesheetPath);
            if (spritesheetStream != null) {
                BufferedImage spritesheet = ImageIO.read(spritesheetStream);
                spritesheetStream.close();

                // Calculer les dimensions de chaque frame
                int frameWidth = spritesheet.getWidth() / frameCount;
                int frameHeight = spritesheet.getHeight() / rows;

                System.out.println("\n=== Découpage spritesheet ===");
                System.out.println("Fichier: " + spritesheetPath);
                System.out.println("Taille totale: " + spritesheet.getWidth() + "x" + spritesheet.getHeight());
                System.out.println("Configuration: " + frameCount + " frames x " + rows + " lignes");
                System.out.println("Taille calculée par frame: " + frameWidth + "x" + frameHeight);

                // Vérifier si les dimensions sont valides
                if (frameWidth <= 0 || frameHeight <= 0) {
                    System.err.println("ERREUR: Dimensions de frame invalides!");
                    return frames;
                }

                if (frameWidth > spritesheet.getWidth() || frameHeight > spritesheet.getHeight()) {
                    System.err.println("ERREUR: Frame plus grande que l'image source!");
                    return frames;
                }

                // Découper chaque frame
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < frameCount; col++) {
                        int x = col * frameWidth;
                        int y = row * frameHeight;

                        // Vérifier que le découpage ne dépasse pas l'image
                        if (x + frameWidth <= spritesheet.getWidth() && y + frameHeight <= spritesheet.getHeight()) {
                            try {
                                BufferedImage frame = spritesheet.getSubimage(x, y, frameWidth, frameHeight);
                                frames.add(frame);
                                System.out.println("Frame " + (frames.size()) + " OK: (" + x + "," + y + ") "
                                        + frameWidth + "x" + frameHeight);
                            } catch (Exception e) {
                                System.err.println(
                                        "Erreur découpage frame " + (frames.size() + 1) + ": " + e.getMessage());
                                break;
                            }
                        } else {
                            System.err.println("Frame " + (frames.size() + 1) + " dépasse les limites: (" + x + "," + y
                                    + ") avec " + frameWidth + "x" + frameHeight);
                        }
                    }
                }

                System.out.println("=== Fin découpage ===\n");
            } else {
                System.err.println("Spritesheet non trouvée: " + spritesheetPath);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du découpage de la spritesheet: " + spritesheetPath);
            e.printStackTrace();
        }

        System.out.println("Total: " + frames.size() + " frames chargées depuis " + spritesheetPath);
        return frames;
    }

    /**
     * Crée un JLabel avec un sprite redimensionné
     */
    private JLabel createSpriteLabel(BufferedImage sprite, int width, int height) {
        JLabel label = new JLabel();
        if (sprite != null) {
            // Redimensionner l'image
            Image scaledImage = sprite.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImage);
            label.setIcon(icon);
        } else {
            // Fallback si l'image n'a pas pu être chargée
            label.setText("👺");
            label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
            label.setForeground(BOSS_COLOR);
        }
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    /**
     * Met à jour le sprite du boss selon son état (normal/enragé)
     */
    private void updateBossSprite() {
        if (bossSpriteLabel != null && boss instanceof FireElementalBoss) {
            FireElementalBoss fireBoss = (FireElementalBoss) boss;

            // Vérifier si le boss est enragé (si HP < 60% pour que ce soit plus facile à
            // voir)
            double hpPercentage = (double) fireBoss.getHp() / fireBoss.getMaxHp();
            boolean wasEnraged = isBossEnraged;
            isBossEnraged = hpPercentage <= 0.6;

            System.out.println("📊 Boss HP: " + fireBoss.getHp() + "/" + fireBoss.getMaxHp() + " ("
                    + Math.round(hpPercentage * 100) + "%) - Enragé: " + isBossEnraged);

            // ✨ NOUVEAU SYSTÈME D'ENRAGE - 100% INTÉGRÉ ✨
            if (isBossEnraged) {
                // Boss enragé - activer les effets visuels intégrés
                if (!wasEnraged) {
                    showEnrageEffect();
                }

                // Changer le sprite pour l'enragé si disponible
                if (fireBossEnragedSprite != null) {
                    Image scaledImage = fireBossEnragedSprite.getScaledInstance(200, 250, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaledImage);
                    bossSpriteLabel.setIcon(icon);
                    System.out.println("🔥 Boss sprite changé vers enragé");
                }
            } else {
                // Boss normal - désactiver les effets d'enrage
                if (wasEnraged && !isBossEnraged) {
                    stopRageEffects();
                }

                // Sprite normal
                if (fireBossSprite != null) {
                    Image scaledImage = fireBossSprite.getScaledInstance(200, 250, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaledImage);
                    bossSpriteLabel.setIcon(icon);
                    System.out.println("✅ Boss sprite remis à normal");
                }
            }
        }
    }

    /**
     * Retourne une image horizontalement (miroir)
     */
    private BufferedImage flipImageHorizontally(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flippedImage = new BufferedImage(width, height, image.getType());

        Graphics2D g2d = flippedImage.createGraphics();
        // Appliquer une transformation pour retourner horizontalement
        g2d.scale(-1, 1); // Retourner horizontalement
        g2d.translate(-width, 0); // Repositionner l'image
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return flippedImage;
    }

    /**
     * 🔥 SYSTÈME D'ENRAGE INTÉGRÉ - 100% Java pur, ZERO dépendance externe 🔥
     */
    private void showEnrageEffect() {
        System.out.println("🔥 Boss entré en rage - activation des effets visuels intégrés !");

        // 1. Message d'enrage immédiat
        if (messageLabel != null) {
            messageLabel.setText("🔥 " + boss.getName() + " ENTRE EN RAGE ! 🔥");
            messageLabel.setForeground(new Color(255, 50, 50));
        }

        // 2. Effet de bordure rouge pulsante IMMÉDIAT
        activateRageBorders();

        // 3. Effet de fond rouge clignotant
        startRageFlashEffect();

        // 4. Modification du titre pour indiquer l'enrage
        if (titleLabel != null) {
            titleLabel.setText("⚔️ TOWER BATTLE - BOSS EN RAGE ! ⚔️");
            titleLabel.setForeground(new Color(255, 100, 100));
        }
    }

    /**
     * ⚡ EFFETS VISUELS D'ENRAGE - 100% INTÉGRÉS ET FIABLES ⚡
     */

    // Timer pour les effets d'enrage
    private Timer rageBorderTimer;
    private Timer rageFlashTimer;
    private boolean isRageBorderRed = false;

    /**
     * Active les bordures rouges pulsantes pour l'enrage
     */
    private void activateRageBorders() {
        // Arrêter l'ancien timer s'il existe
        if (rageBorderTimer != null && rageBorderTimer.isRunning()) {
            rageBorderTimer.stop();
        }

        // Créer un effet de pulsation des bordures (rouge -> orange -> rouge)
        rageBorderTimer = new Timer(500, e -> {
            Color borderColor = isRageBorderRed
                    ? new Color(255, 100, 0) // Orange vif
                    : new Color(255, 50, 50); // Rouge intense

            // Appliquer aux panels
            if (playerPanel != null) {
                playerPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 4),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
            }

            if (bossPanel != null) {
                bossPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 5),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
            }

            isRageBorderRed = !isRageBorderRed;
            repaint();
        });

        rageBorderTimer.start();
        System.out.println("🔥 Bordures d'enrage activées !");
    }

    /**
     * Démarre l'effet de clignotement rouge du fond
     */
    private void startRageFlashEffect() {
        // Arrêter l'ancien timer s'il existe
        if (rageFlashTimer != null && rageFlashTimer.isRunning()) {
            rageFlashTimer.stop();
        }

        final Color originalBg = BG_COLOR;
        final Color rageBg = new Color(60, 20, 20); // Fond rouge sombre

        rageFlashTimer = new Timer(1000, e -> {
            Color currentBg = getContentPane().getBackground();

            if (currentBg.equals(originalBg)) {
                getContentPane().setBackground(rageBg);
            } else {
                getContentPane().setBackground(originalBg);
            }
            repaint();
        });

        rageFlashTimer.start();
        System.out.println("🔥 Effet de fond d'enrage activé !");
    }

    /**
     * Arrête tous les effets d'enrage et remet les couleurs normales
     */
    private void stopRageEffects() {
        // Arrêter les timers
        if (rageBorderTimer != null && rageBorderTimer.isRunning()) {
            rageBorderTimer.stop();
        }
        if (rageFlashTimer != null && rageFlashTimer.isRunning()) {
            rageFlashTimer.stop();
        }

        // Remettre les couleurs normales
        if (playerPanel != null) {
            playerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PLAYER_COLOR, 2),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        }

        if (bossPanel != null) {
            bossPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 50, 50), 2),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        }

        getContentPane().setBackground(BG_COLOR);

        if (titleLabel != null) {
            titleLabel.setText("⚔️ TOWER BATTLE ⚔️");
            titleLabel.setForeground(GOLD_COLOR);
        }

        repaint();
        System.out.println("✅ Effets d'enrage désactivés");
    }

    /**
     * Configure le cadre ragefeu pour le panel du joueur
     */
    private void setupPlayerRageFrame() {
        if (playerPanel == null || rageFrameSprite == null)
            return;

        // Retirer l'ancien cadre s'il existe
        if (playerRageFrameLabel != null && playerRageFrameLabel.getParent() != null) {
            playerRageFrameLabel.getParent().remove(playerRageFrameLabel);
        }

        // Créer un nouveau cadre parfaitement adapté
        playerRageFrameLabel = new JLabel();
        playerRageFrameLabel.setOpaque(false);

        // Taille exacte du panel (sans débordement)
        int panelWidth = playerPanel.getWidth();
        int panelHeight = playerPanel.getHeight();

        if (panelWidth > 0 && panelHeight > 0) {
            // Redimensionner le sprite pour qu'il s'adapte EXACTEMENT au panel
            Image scaledFrame = rageFrameSprite.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
            ImageIcon frameIcon = new ImageIcon(scaledFrame);
            playerRageFrameLabel.setIcon(frameIcon);

            // TECHNIQUE PROPRE : Utiliser un LayeredPane pour contrôler l'ordre des couches
            if (!(playerPanel.getLayout() instanceof OverlayLayout)) {
                playerPanel.setLayout(new OverlayLayout(playerPanel));
            }

            // Positionner le cadre en arrière-plan DANS le panel
            playerRageFrameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerRageFrameLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
            playerRageFrameLabel.setBounds(0, 0, panelWidth, panelHeight);

            // Ajouter en première position (arrière-plan)
            playerPanel.add(playerRageFrameLabel, 0);
            playerRageFrameLabel.setVisible(false); // Caché par défaut

            System.out.println("✅ Cadre ragefeu joueur configuré : " + panelWidth + "x" + panelHeight);
        }
    }

    /**
     * Configure le cadre ragefeu pour le panel du boss
     */
    private void setupBossRageFrame() {
        if (bossPanel == null || rageFrameSprite == null)
            return;

        // Retirer l'ancien cadre s'il existe
        if (bossRageFrameLabel != null && bossRageFrameLabel.getParent() != null) {
            bossRageFrameLabel.getParent().remove(bossRageFrameLabel);
        }

        // Créer un nouveau cadre parfaitement adapté
        bossRageFrameLabel = new JLabel();
        bossRageFrameLabel.setOpaque(false);

        // Taille exacte du panel (sans débordement)
        int panelWidth = bossPanel.getWidth();
        int panelHeight = bossPanel.getHeight();

        if (panelWidth > 0 && panelHeight > 0) {
            // Redimensionner le sprite pour qu'il s'adapte EXACTEMENT au panel
            Image scaledFrame = rageFrameSprite.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
            ImageIcon frameIcon = new ImageIcon(scaledFrame);
            bossRageFrameLabel.setIcon(frameIcon);

            // TECHNIQUE PROPRE : Utiliser un LayeredPane pour contrôler l'ordre des couches
            if (!(bossPanel.getLayout() instanceof OverlayLayout)) {
                bossPanel.setLayout(new OverlayLayout(bossPanel));
            }

            // Positionner le cadre en arrière-plan DANS le panel
            bossRageFrameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            bossRageFrameLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
            bossRageFrameLabel.setBounds(0, 0, panelWidth, panelHeight);

            // Ajouter en première position (arrière-plan)
            bossPanel.add(bossRageFrameLabel, 0);
            bossRageFrameLabel.setVisible(false); // Caché par défaut

            System.out.println("✅ Cadre ragefeu boss configuré : " + panelWidth + "x" + panelHeight);
        }
    }

    /**
     * Affiche ou masque les cadres ragefeu
     */
    private void showRageFrames(boolean show) {
        if (playerRageFrameLabel != null) {
            playerRageFrameLabel.setVisible(show);
        }
        if (bossRageFrameLabel != null) {
            bossRageFrameLabel.setVisible(show);
        }

        // Rafraîchir l'affichage
        if (show) {
            repaint();
        }
    }

    /**
     * Méthode de fallback pour les bordures colorées
     */
    private void showColoredBorders() {
        if (playerPanel != null) {
            playerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 100, 100), 3),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        }

        if (bossPanel != null) {
            bossPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 50, 0), 4),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        }

        // Remettre les couleurs normales après 3 secondes
        Timer resetTimer = new Timer(3000, e -> resetBorderColors());
        resetTimer.setRepeats(false);
        resetTimer.start();
    }

    /**
     * Remet les couleurs de bordure normales
     */
    private void resetBorderColors() {
        if (playerPanel != null) {
            playerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PLAYER_COLOR, 2),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        }

        if (bossPanel != null) {
            bossPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 50, 50), 2),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        }
    }

    /**
     * MÉTHODE RADICALE: Overlay EXCLUSIVEMENT sur HÉROS avec vérifications
     * ultra-strictes
     */
    private void showAttackOverlayOnPlayer() {
        System.out.println("🚨 RADICAL: showAttackOverlayOnPlayer() - HERO ONLY!");

        // Vérifications ultra-strictes
        if (fireAttackSprite == null || playerPanel == null) {
            System.out.println("❌ RADICAL: Sprites/panels manquants, ABORT!");
            return;
        }

        // STOPPER si c'est le même objet que bossPanel
        if (playerPanel == bossPanel) {
            System.out.println("💀 RADICAL: playerPanel EST bossPanel! ABORT TOTAL!");
            return;
        }

        // Identifier les panels par leur position pour être SÛR
        System.out.println("🔍 RADICAL: playerPanel hashCode=" + playerPanel.hashCode());
        System.out.println("🔍 RADICAL: bossPanel hashCode=" + bossPanel.hashCode());

        // Créer un overlay avec marquage ULTRA-VISIBLE
        JLabel heroOnlyOverlay = new JLabel("HERO FIRE ATTACK");
        heroOnlyOverlay.setHorizontalAlignment(SwingConstants.CENTER);
        heroOnlyOverlay.setVerticalAlignment(SwingConstants.CENTER);
        heroOnlyOverlay.setOpaque(true); // OPAQUE pour être sûr de le voir
        heroOnlyOverlay.setBackground(Color.YELLOW); // Fond jaune visible

        // Image + texte pour identification absolue
        Image scaledAttack = fireAttackSprite.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon attackIcon = new ImageIcon(scaledAttack);
        heroOnlyOverlay.setIcon(attackIcon);

        // Bordure MASSIVE bleue pour le héros UNIQUEMENT
        heroOnlyOverlay.setBorder(BorderFactory.createLineBorder(Color.BLUE, 10));

        // CORRECTION CRITIQUE: Si l'overlay apparaît à droite, c'est que playerPanel =
        // boss !
        // DONC on utilise bossPanel pour atteindre le héros (gauche) !
        try {
            System.out.println("🔄 CORRECTION: Utilisation de bossPanel pour atteindre le HÉROS (gauche)");
            System.out.println("💥 CORRECTION: Ajout à bossPanel (hashCode=" + bossPanel.hashCode() + ")");

            // Test: ajouter au bossPanel au lieu du playerPanel
            bossPanel.add(heroOnlyOverlay);
            bossPanel.revalidate();
            bossPanel.repaint();

            System.out.println("🟡 CORRECTION: HERO FIRE ATTACK ajouté via bossPanel pour toucher HÉROS!");

            // Suppression après 2 secondes
            Timer cleanupTimer = new Timer(2000, e -> {
                try {
                    System.out.println("🧹 CORRECTION: Suppression de l'overlay via bossPanel...");
                    bossPanel.remove(heroOnlyOverlay);
                    bossPanel.revalidate();
                    bossPanel.repaint();
                    System.out.println("✅ CORRECTION: Hero overlay supprimé via bossPanel!");
                } catch (Exception ex) {
                    System.err.println("💀 CORRECTION: Erreur suppression: " + ex.getMessage());
                }
                ((Timer) e.getSource()).stop();
            });
            cleanupTimer.setRepeats(false);
            cleanupTimer.start();
        } catch (Exception e) {
            System.err.println("💀 RADICAL: Erreur totale: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // MÉTHODE showAttackOverlayOnBoss SUPPRIMÉE - PLUS JAMAIS D'OVERLAY SUR LE BOSS
    // !

    /**
     * Détermine si une action du joueur est une attaque qui devrait déclencher
     * l'effet PNG
     */
    private boolean isPlayerAttackAction(AAction action) {
        if (action == null)
            return false;

        // Considérer comme attaque si l'action inflige des dégâts ou a "attaque" dans
        // le nom
        String actionName = action.getName().toLowerCase();
        return actionName.contains("attaque") ||
                actionName.contains("frappe") ||
                actionName.contains("coup") ||
                actionName.contains("flamme") ||
                actionName.contains("glace") ||
                actionName.contains("nature") ||
                actionName.contains("élémentaire");
    }

    /**
     * Lance l'animation d'attaque du boss frame par frame
     */
    private void playBossAttackAnimation() {
        if (isAnimating || bossSpriteLabel == null) {
            return;
        }

        isAnimating = true;
        currentFrame = 0;

        // Choisir les bonnes frames selon l'état du boss
        java.util.List<BufferedImage> attackFrames = getAttackFrames();

        if (attackFrames != null && !attackFrames.isEmpty()) {
            // Démarrer l'animation frame par frame
            animationTimer = new Timer(150, new ActionListener() { // 150ms par frame = ~6.7 FPS
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentFrame < attackFrames.size()) {
                        // Afficher la frame actuelle du boss
                        BufferedImage currentFrameImage = attackFrames.get(currentFrame);
                        Image scaledFrame = currentFrameImage.getScaledInstance(220, 270, Image.SCALE_SMOOTH);
                        ImageIcon frameIcon = new ImageIcon(scaledFrame);
                        bossSpriteLabel.setIcon(frameIcon);

                        // À partir de la 2ème frame, afficher l'effet d'attaque sur le joueur
                        // SEULEMENT si le boss a vraiment attaqué ce tour
                        System.out.println("🎬 ANIMATION: Frame " + currentFrame + ", bossAttackedThisTurn="
                                + bossAttackedThisTurn);
                        if (currentFrame >= 0 && bossAttackedThisTurn) {
                            System.out.println("🔥 APPEL: showAttackOverlayOnPlayer() à la frame " + currentFrame);
                            showAttackOverlayOnPlayer();
                        } else {
                            System.out.println("❌ PAS d'overlay: frame=" + currentFrame + " ou bossAttackedThisTurn="
                                    + bossAttackedThisTurn);
                        }

                        currentFrame++;
                    } else {
                        // Animation terminée, revenir au sprite normal
                        animationTimer.stop();

                        // Petit délai avant de revenir au sprite normal pour plus d'impact
                        Timer returnTimer = new Timer(300, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                updateBossSprite();
                                isAnimating = false;
                                ((Timer) evt.getSource()).stop();
                            }
                        });
                        returnTimer.setRepeats(false);
                        returnTimer.start();
                    }
                }
            });
            animationTimer.start();
        } else {
            isAnimating = false;
        }
    }

    /**
     * Récupère les frames d'attaque appropriées selon l'état du boss
     */
    private java.util.List<BufferedImage> getAttackFrames() {
        if (boss instanceof FireElementalBoss) {
            FireElementalBoss fireBoss = (FireElementalBoss) boss;
            double hpPercentage = (double) fireBoss.getHp() / fireBoss.getMaxHp();

            if (hpPercentage <= 0.3 && fireBossAttackEnragedFrames != null && !fireBossAttackEnragedFrames.isEmpty()) {
                return fireBossAttackEnragedFrames; // Boss enragé attaque
            } else if (fireBossAttackFrames != null && !fireBossAttackFrames.isEmpty()) {
                return fireBossAttackFrames; // Boss normal attaque
            }
        }
        return null;
    }

    /**
     * Vérifie si une action est une attaque du boss
     */
    private boolean isBossAttack(AAction action) {
        // Vérifier si l'action provient du boss
        String actionClassName = action.getClass().getSimpleName();
        return actionClassName.contains("Boss") && actionClassName.contains("Attack");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
                } catch (Exception e) {
                    // Utiliser le look and feel par défaut
                }

                System.out.println("🎮 Lancement de Tower Battle - Version graphique Swing 🎮");
                System.out.println("Style : Pokémon/Final Fantasy 7");
                System.out.println("================================================");

                new GameWindow().setVisible(true);
            }
        });
    }
}