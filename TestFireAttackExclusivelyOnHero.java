/**
 * TEST CRUCIAL: Vérifier que fire_attack s'affiche UNIQUEMENT sur le héros
 */
public class TestFireAttackExclusivelyOnHero {

    public static void main(String[] args) {
        try {
            System.out.println("🎯 === TEST: fire_attack EXCLUSIVEMENT sur HÉROS ===");

            // Créer une instance de GameWindow
            towergame.view.GameWindow gameWindow = new towergame.view.GameWindow();
            gameWindow.setVisible(true);

            // Attendre que l'interface soit chargée
            Thread.sleep(2000);

            System.out.println("✅ Interface chargée, test de l'overlay...");

            // Utiliser la réflexion pour forcer l'appel direct
            java.lang.reflect.Method showOverlayMethod = towergame.view.GameWindow.class
                    .getDeclaredMethod("showAttackOverlayOnPlayer");
            showOverlayMethod.setAccessible(true);

            System.out.println("🔥 FORCE: Appel de showAttackOverlayOnPlayer()...");
            showOverlayMethod.invoke(gameWindow);

            System.out.println("⏱️ Attente 3 secondes pour observer l'effet...");
            Thread.sleep(3000);

            System.out.println("✅ Test terminé ! L'overlay devrait être apparu UNIQUEMENT sur le HÉROS !");

        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}