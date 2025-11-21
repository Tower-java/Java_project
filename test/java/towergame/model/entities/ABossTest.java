package towergame.model.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import towergame.model.actions.AAction;
import towergame.model.actions.BossAttackAction;
import towergame.model.actions.Element;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ABossTest {

    private FireElementalBoss boss;

    @BeforeEach
    void setUp() {
        boss = new FireElementalBoss();
    }

    @Test
    void testTakeDamage() {
        int initialHp = boss.getHp();
        boss.setInvulnerable(false);
        boss.takeDamage(10);
        assertEquals(initialHp - 10, boss.getHp(), "Les PV devraient diminuer après avoir subi des dégâts.");
    }

    @Test
    void testTakeDamageWhenInvulnerable() {
        int initialHp = boss.getHp();
        boss.setInvulnerable(true);
        boss.takeDamage(10);
        assertEquals(initialHp, boss.getHp(), "Les PV ne devraient pas changer quand le boss est invulnérable.");
    }

    @Test
    void testSetAndGetActionScript() {
        List<AAction> script = new ArrayList<>();
        script.add(new BossAttackAction("Action 1", Element.NEUTRAL, 0, 1.0));
        script.add(new BossAttackAction("Action 2", Element.FIRE, 0, 1.0));
        boss.setActionScript(script);
        assertEquals(script, boss.getActionScript(), "Le script d'action retourné doit être celui qui a été défini.");
    }

    @Test
    void testGetNextAction() {
        List<AAction> script = new ArrayList<>();
        AAction action1 = new BossAttackAction("Action 1", Element.NEUTRAL, 0, 1.0);
        AAction action2 = new BossAttackAction("Action 2", Element.FIRE, 0, 1.0);
        script.add(action1);
        script.add(action2);
        boss.setActionScript(script);

        assertEquals(action1, boss.getNextAction(), "La première action récupérée doit être la première du script.");
        assertEquals(action2, boss.getNextAction(), "La deuxième action récupérée doit être la deuxième du script.");
        assertEquals(action1, boss.getNextAction(), "Le script doit revenir au début après avoir atteint la fin."); // Test looping
    }

    @Test
    void testGetters() {
        assertEquals(10, boss.getAttackPoints());
        assertEquals(10, boss.getHealPoints());
        assertFalse(boss.isEnraged());
        assertTrue(boss.isInvulnerable());
    }

    @Test
    void testSetInvulnerable() {
        boss.setInvulnerable(false);
        assertFalse(boss.isInvulnerable());
        boss.setInvulnerable(true);
        assertTrue(boss.isInvulnerable());
    }
}
