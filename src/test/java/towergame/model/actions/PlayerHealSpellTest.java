package towergame.model.actions;

import org.junit.jupiter.api.Test;
import towergame.model.entities.Player;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerHealSpellTest {

    @Test
    public void testHealAndCooldown() {
        // Arrange
        Player player = new Player("Test Player");
        player.takeDamage(50); // Reduce HP to 50

        PlayerHealSpell healSpell = new PlayerHealSpell("Minor Heal", Element.NEUTRAL, 20, 3);

        // Act
        healSpell.execute(player, player);

        // Assert
        assertEquals(70, player.getHp(), "Player should be healed by 20 HP.");
        assertFalse(healSpell.isReady(), "Spell should be on cooldown after execution.");
        assertEquals(3, healSpell.getCurrentCooldown(), "Cooldown should be set to its duration.");
    }

    @Test
    public void testHealingCappedAtMaxHp() {
        // Arrange
        Player player = new Player("Test Player");
        player.takeDamage(10); // HP is 90

        PlayerHealSpell healSpell = new PlayerHealSpell("Major Heal", Element.NEUTRAL, 20, 3);

        // Act
        healSpell.execute(player, player);

        // Assert
        assertEquals(100, player.getHp(), "Player HP should not exceed max HP.");
    }

    @Test
    public void testSpellDoesNotExecuteWhenOnCooldown() {
        // Arrange
        Player player = new Player("Test Player");
        player.takeDamage(50); // HP is 50

        PlayerHealSpell healSpell = new PlayerHealSpell("Minor Heal", Element.NEUTRAL, 20, 3);

        // Act
        healSpell.execute(player, player); // First execution, HP becomes 70, cooldown starts

        // Try to execute again immediately
        healSpell.execute(player, player);

        // Assert
        assertEquals(70, player.getHp(), "HP should not change when spell is on cooldown.");
    }
}
