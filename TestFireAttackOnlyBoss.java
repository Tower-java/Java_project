/**
 * Test final pour valider que fire_attack.png apparaît UNIQUEMENT
 * lorsque le boss attaque le héros, pas à chaque tour du boss
 */
public class TestFireAttackOnlyBoss {

    public static void main(String[] args) {
        try {
            System.out.println("🔥 === Test FINAL: fire_attack UNIQUEMENT quand boss attaque ===");
            System.out.println();
            System.out.println("📋 Comportement attendu :");
            System.out.println("   ✅ Boss attaque héros → PNG fire_attack sur cadre HÉROS");
            System.out.println("   ❌ Boss se défend/soigne → AUCUN PNG fire_attack");
            System.out.println("   ❌ Héros attaque boss → AUCUN PNG fire_attack");
            System.out.println("   🎯 fire_attack est EXCLUSIF aux attaques du boss sur le héros !");
            System.out.println();
            System.out.println("🚀 Lancement du jeu pour test manuel...");
            System.out.println("👀 Vérifiez que fire_attack n'apparaît QUE quand le boss attaque !");
            System.out.println();

            // Démarrer le jeu GUI
            towergame.view.GameWindow.main(args);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du test : " + e.getMessage());
            e.printStackTrace();
        }
    }
}