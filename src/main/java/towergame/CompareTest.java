package towergame;

import towergame.view.GameEndScreen;
import javax.swing.*;

/**
 * Test comparatif des écrans
 */
public class CompareTest {

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            System.out.println("📊 COMPARAISON DES ÉCRANS DE FIN :");
            System.out.println("- Les statistiques '⚔️ Légende écrite en X actes' ont été SUPPRIMÉES");
            System.out.println("- Plus d'espace entre le message narratif et les boutons");
            System.out.println("- Interface plus épurée");
            System.out.println();

            // Boutons pour tester les deux écrans
            JFrame controlFrame = new JFrame("🎮 Test Controller");
            controlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            controlFrame.setSize(400, 200);
            controlFrame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel titleLabel = new JLabel("🔍 Comparaison - Statistiques supprimées", SwingConstants.CENTER);
            titleLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            titleLabel.setFont(titleLabel.getFont().deriveFont(16f));

            JButton victoryBtn = new JButton("🏆 Tester VICTOIRE (sans stats)");
            victoryBtn.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            victoryBtn.addActionListener(e -> {
                GameEndScreen.showGameEnd(true, "Héros", "Boss", 15,
                        () -> System.out.println("Redémarrer"),
                        () -> {
                        });
            });

            JButton defeatBtn = new JButton("💀 Tester DÉFAITE (sans stats)");
            defeatBtn.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            defeatBtn.addActionListener(e -> {
                GameEndScreen.showGameEnd(false, "Héros", "Boss", 27,
                        () -> System.out.println("Redémarrer"),
                        () -> {
                        });
            });

            JButton exitBtn = new JButton("❌ Fermer");
            exitBtn.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            exitBtn.addActionListener(e -> System.exit(0));

            panel.add(Box.createVerticalGlue());
            panel.add(titleLabel);
            panel.add(Box.createRigidArea(new java.awt.Dimension(0, 20)));
            panel.add(victoryBtn);
            panel.add(Box.createRigidArea(new java.awt.Dimension(0, 10)));
            panel.add(defeatBtn);
            panel.add(Box.createRigidArea(new java.awt.Dimension(0, 20)));
            panel.add(exitBtn);
            panel.add(Box.createVerticalGlue());

            controlFrame.add(panel);
            controlFrame.setVisible(true);
        });
    }
}