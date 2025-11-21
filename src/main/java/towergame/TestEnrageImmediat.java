package towergame;

import towergame.model.entities.FireElementalBoss;
import towergame.model.entities.Player;
import towergame.model.managers.BattleManager;
import towergame.view.GameWindow;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Test pour forcer le boss à entrer en rage immédiatement
 */
public class TestEnrageImmediat {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("🔥 TEST: Boss enragé immédiatement 🔥");

            try {
                // Créer la fenêtre de jeu
                GameWindow window = new GameWindow();

                // Utiliser la réflection pour accéder aux champs privés
                Field playerField = GameWindow.class.getDeclaredField("player");
                playerField.setAccessible(true);
                Player player = (Player) playerField.get(window);

                Field bossField = GameWindow.class.getDeclaredField("boss");
                bossField.setAccessible(true);
                FireElementalBoss boss = (FireElementalBoss) bossField.get(window);

                Field battleManagerField = GameWindow.class.getDeclaredField("battleManager");
                battleManagerField.setAccessible(true);
                BattleManager battleManager = (BattleManager) battleManagerField.get(window);

                System.out.println("✅ Accès aux objets du jeu réussi");

                // Forcer le boss à avoir des HP faibles (pour déclencher l'enrage)
                System.out.println("📊 HP du boss AVANT: " + boss.getHp() + "/" + boss.getMaxHp());

                // Méthode 1: Réduire directement les HP via réflection
                Field hpField = boss.getClass().getSuperclass().getSuperclass().getDeclaredField("hp");
                hpField.setAccessible(true);
                hpField.set(boss, 50); // Mettre à 50 HP (50% de 100)

                System.out.println("🔻 HP du boss APRÈS modification: " + boss.getHp() + "/" + boss.getMaxHp());

                // Forcer la mise à jour de l'affichage
                Method updateDisplayMethod = GameWindow.class.getDeclaredMethod("updateDisplay");
                updateDisplayMethod.setAccessible(true);
                updateDisplayMethod.invoke(window);

                System.out.println("🎯 Mise à jour de l'affichage forcée");

                // Afficher la fenêtre
                window.setVisible(true);

                // Attendre un peu puis forcer l'enrage
                Timer enrageTimer = new Timer(2000, e -> {
                    try {
                        System.out.println("\n🔥 DÉCLENCHEMENT DE L'ENRAGE 🔥");

                        // Réduire encore plus les HP pour déclencher l'enrage (moins de 60%)
                        hpField.set(boss, 55); // 55 HP = 55% donc devrait déclencher l'enrage

                        // Forcer la vérification de l'état d'enrage
                        Field isBossEnragedField = GameWindow.class.getDeclaredField("isBossEnraged");
                        isBossEnragedField.setAccessible(true);
                        isBossEnragedField.set(window, true);

                        // Forcer la mise à jour du sprite
                        Method updateBossSpriteMethod = GameWindow.class.getDeclaredMethod("updateBossSprite");
                        updateBossSpriteMethod.setAccessible(true);
                        updateBossSpriteMethod.invoke(window);

                        System.out.println("✅ Sprite du boss mis à jour pour l'enrage");

                        // Déclencher l'effet d'enrage visuel
                        Method showEnrageEffectMethod = GameWindow.class.getDeclaredMethod("showEnrageEffect");
                        showEnrageEffectMethod.setAccessible(true);
                        showEnrageEffectMethod.invoke(window);
                        System.out.println("🔥 Effets visuels d'enrage déclenchés !");

                    } catch (Exception ex) {
                        System.err.println("❌ Erreur lors du déclenchement de l'enrage: " + ex.getMessage());
                        ex.printStackTrace();
                    }

                    ((Timer) e.getSource()).stop();
                });

                enrageTimer.setRepeats(false);
                enrageTimer.start();

                // Test encore plus agressif après 5 secondes
                Timer testAgressif = new Timer(5000, e -> {
                    try {
                        System.out.println("\n🚨 TEST AGRESSIF: HP CRITIQUES 🚨");

                        // HP vraiment critiques (20%)
                        hpField.set(boss, 20);
                        System.out.println("💀 HP du boss: " + boss.getHp() + "/" + boss.getMaxHp() + " ("
                                + (boss.getHp() * 100 / boss.getMaxHp()) + "%)");

                        // Forcer l'enrage dans le boss lui-même
                        Field isEnragedField = boss.getClass().getSuperclass().getDeclaredField("isEnraged");
                        isEnragedField.setAccessible(true);
                        isEnragedField.set(boss, true);

                        // Double vérification: forcer aussi dans GameWindow
                        Field isBossEnragedField = GameWindow.class.getDeclaredField("isBossEnraged");
                        isBossEnragedField.setAccessible(true);
                        isBossEnragedField.set(window, true);

                        // Triple update
                        Method updateBossSprite2 = GameWindow.class.getDeclaredMethod("updateBossSprite");
                        updateBossSprite2.setAccessible(true);
                        updateBossSprite2.invoke(window);

                        Method showEnrageEffect2 = GameWindow.class.getDeclaredMethod("showEnrageEffect");
                        showEnrageEffect2.setAccessible(true);
                        showEnrageEffect2.invoke(window);

                        Method updateDisplay2 = GameWindow.class.getDeclaredMethod("updateDisplay");
                        updateDisplay2.setAccessible(true);
                        updateDisplay2.invoke(window);
                        System.out.println("🔥🔥🔥 ENRAGE FORCÉ AVEC SUCCÈS ! 🔥🔥🔥");

                    } catch (Exception ex) {
                        System.err.println("❌ Erreur test agressif: " + ex.getMessage());
                        ex.printStackTrace();
                    }

                    ((Timer) e.getSource()).stop();
                });

                testAgressif.setRepeats(false);
                testAgressif.start();

            } catch (Exception e) {
                System.err.println("❌ Erreur lors de l'initialisation du test: " + e.getMessage());
                e.printStackTrace();

                // Fallback: lancer le jeu normal
                System.out.println("📄 Fallback: Lancement du jeu normal...");
                new GameWindow().setVisible(true);
            }
        });
    }
}