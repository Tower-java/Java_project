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
    private JLabel playerNameLabel;
    private JLabel playerHpLabel;
    private JLabel bossNameLabel;
    private JLabel bossHpLabel;
    private JPanel actionPanel;
    private JPanel effectPanel;
    private JPanel playerPanel;
    private JPanel bossPanel;

    // Couleurs style gaming
    private static final Color BG_COLOR = new Color(26, 26, 46);
    private static final Color PANEL_COLOR = new Color(40, 40, 60, 200);
    private static final Color GOLD_COLOR = new Color(255, 215, 0);
    private static final Color PLAYER_COLOR = new Color(78, 205, 196);
    private static final Color BOSS_COLOR = new Color(255, 107, 107);
    private static final Color TEXT_WHITE = Color.WHITE;

    public GameWindow() {
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
        setTitle("⚔️ Tower Battle - Style FF7/Pokémon ⚔️");
        setSize(900, 700);
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

        // Titre du jeu
        titleLabel = new JLabel("⚔️ TOWER BATTLE ⚔️", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(GOLD_COLOR);

        // Informations de tour
        turnLabel = new JLabel("Tour 1", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 18));
        turnLabel.setForeground(TEXT_WHITE);

        // Message de statut
        messageLabel = new JLabel("Le combat commence !", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setForeground(PLAYER_COLOR);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(turnLabel, BorderLayout.CENTER);
        headerPanel.add(messageLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void setupBattlePanel(JPanel mainPanel) {
        JPanel battlePanel = new JPanel(new GridLayout(1, 3, 30, 0));
        battlePanel.setOpaque(false);
        battlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Panel Player (à gauche - position conventionnelle)
        playerPanel = createEntityPanel("🛡️ HÉROS 🛡️", PLAYER_COLOR);
        playerNameLabel = new JLabel("", SwingConstants.CENTER);
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerNameLabel.setForeground(PLAYER_COLOR);

        playerHpLabel = new JLabel("", SwingConstants.CENTER);
        playerHpLabel.setFont(new Font("Arial", Font.BOLD, 14));
        playerHpLabel.setForeground(PLAYER_COLOR);

        playerPanel.add(playerNameLabel);
        playerPanel.add(playerHpLabel);

        // Panel VS (au centre)
        JPanel vsPanel = new JPanel(new GridLayout(3, 1));
        vsPanel.setOpaque(false);
        vsPanel.add(new JLabel("⚔️", SwingConstants.CENTER));

        JLabel vsLabel = new JLabel("VS", SwingConstants.CENTER);
        vsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        vsLabel.setForeground(GOLD_COLOR);
        vsPanel.add(vsLabel);
        vsPanel.add(new JLabel("⚔️", SwingConstants.CENTER));

        // Panel Boss (à droite - comme dans les JRPGs classiques)
        bossPanel = createEntityPanel("🔥 BOSS 🔥", BOSS_COLOR);
        bossNameLabel = new JLabel("", SwingConstants.CENTER);
        bossNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bossNameLabel.setForeground(BOSS_COLOR);

        bossHpLabel = new JLabel("", SwingConstants.CENTER);
        bossHpLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bossHpLabel.setForeground(BOSS_COLOR);

        bossPanel.add(bossNameLabel);
        bossPanel.add(bossHpLabel);

        battlePanel.add(playerPanel);
        battlePanel.add(vsPanel);
        battlePanel.add(bossPanel);

        mainPanel.add(battlePanel, BorderLayout.CENTER);
    }

    private JPanel createEntityPanel(String title, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(color);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Sprite placeholder avec orientation
        JLabel spriteLabel = new JLabel("👹", SwingConstants.CENTER);
        if (color == PLAYER_COLOR) {
            spriteLabel.setText("🤺"); // Joueur orienté vers la droite
        } else {
            spriteLabel.setText("👺"); // Boss orienté vers la gauche
        }
        spriteLabel.setFont(new Font("Arial", Font.PLAIN, 80));
        spriteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(spriteLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        return panel;
    }

    private void setupActionPanel(JPanel mainPanel) {
        JPanel actionContainer = new JPanel(new BorderLayout());
        actionContainer.setOpaque(false);

        JLabel actionTitle = new JLabel("⚡ ACTIONS DISPONIBLES ⚡", SwingConstants.CENTER);
        actionTitle.setFont(new Font("Arial", Font.BOLD, 16));
        actionTitle.setForeground(GOLD_COLOR);
        actionContainer.add(actionTitle, BorderLayout.NORTH);

        actionPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        setupActionButtons();

        actionContainer.add(actionPanel, BorderLayout.CENTER);
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

        JButton button = new JButton(buttonText);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(160, 50));

        // Style des boutons selon le type d'action
        if (action.getName().toLowerCase().contains("soin")) {
            button.setBackground(new Color(46, 213, 115));
        } else if (action.getName().toLowerCase().contains("barrière")) {
            button.setBackground(new Color(112, 161, 255));
        } else {
            button.setBackground(new Color(255, 71, 87));
        }

        button.setForeground(Color.WHITE);
        button.setEnabled(action.isReady() && !battleManager.isBattleOver());

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

        // Vérifier les résistances/faiblesses
        if (boss instanceof FireElementalBoss) {
            FireElementalBoss fireBoss = (FireElementalBoss) boss;

            if (fireBoss.isResistant(action.getElement())) {
                showEffect("🛡️ " + boss.getName() + " résiste à " + action.getName()
                        + " !\\nL'attaque n'est pas très efficace...", BOSS_COLOR);
            } else if (fireBoss.isWeak(action.getElement())) {
                showEffect("⚡ " + boss.getName() + " craint " + action.getName() + " !\\nC'est super efficace !",
                        GOLD_COLOR);
            }
        }

        // Animer l'action du joueur
        showPlayerAction(action.getName());

        // Exécuter le tour après un délai
        Timer delay = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean wasEnraged = boss.isEnraged();
                battleManager.executeTurn(action);

                // Vérifier si le boss vient d'entrer en rage
                if (!wasEnraged && boss.isEnraged()) {
                    showBossEnrage();
                }

                updateDisplay();
                setupActionButtons();

                if (battleManager.isBattleOver()) {
                    endBattle();
                }

                ((Timer) e.getSource()).stop();
            }
        });
        delay.start();
    }

    private void showEffect(String message, Color color) {
        messageLabel.setText("<html><center>" + message.replace("\\n", "<br>") + "</center></html>");
        messageLabel.setForeground(color);

        // Animation de clignotement
        Timer blink = new Timer(300, null);
        final boolean[] visible = { true };

        blink.addActionListener(new ActionListener() {
            int count = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                messageLabel.setVisible(visible[0]);
                visible[0] = !visible[0];
                count++;

                if (count >= 6) {
                    messageLabel.setVisible(true);
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        blink.start();
    }

    private void showPlayerAction(String actionName) {
        messageLabel.setText("🎯 " + player.getName() + " utilise " + actionName + " !");
        messageLabel.setForeground(PLAYER_COLOR);
    }

    private void showBossEnrage() {
        showEffect("🔥 " + boss.getName() + " ENTRE EN RAGE ! 🔥\\nSes attaques deviennent plus puissantes !",
                Color.RED);

        // Animation du panel boss
        Timer pulse = new Timer(200, null);
        final Color originalColor = bossPanel.getBackground();
        final boolean[] red = { false };

        pulse.addActionListener(new ActionListener() {
            int count = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                bossPanel.setBackground(red[0] ? Color.RED : originalColor);
                red[0] = !red[0];
                count++;

                if (count >= 8) {
                    bossPanel.setBackground(originalColor);
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        pulse.start();
    }

    private void updateDisplay() {
        turnLabel.setText("Tour " + battleManager.getTurnNumber());

        playerNameLabel.setText(player.getName());
        playerHpLabel.setText("HP: " + player.getHp() + "/" + player.getMaxHp());

        bossNameLabel.setText(boss.getName());
        bossHpLabel.setText("HP: " + boss.getHp() + "/" + boss.getMaxHp());

        String status = "";
        if (boss.isInvulnerable())
            status += " (Invulnérable)";
        if (boss.isEnraged())
            status += " (Enragé)";

        if (!status.isEmpty()) {
            bossNameLabel.setText(boss.getName() + status);
        }
    }

    private void endBattle() {
        String message = player.isAlive()
                ? "🎉 Victoire ! " + boss.getName() + " a été vaincu !"
                : "💀 Défaite... " + player.getName() + " est tombé au combat.";

        message += "\\n\\nCombat terminé en " + battleManager.getTurnNumber() + " tours.";

        JOptionPane.showMessageDialog(this,
                message.replace("\\n", "\n"),
                "Combat terminé",
                JOptionPane.INFORMATION_MESSAGE);

        System.exit(0);
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