package towergame.model.actions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ElementTest {

    @Test
    void getMultiplierAgainst_shouldReturnCorrectMultipliersForStandardTriangle() {
        // Avantages (x2.0)
        assertEquals(2.0, Element.FIRE.getMultiplierAgainst(Element.PLANT));
        assertEquals(2.0, Element.WATER.getMultiplierAgainst(Element.FIRE));
        assertEquals(2.0, Element.PLANT.getMultiplierAgainst(Element.WATER));

        // Faiblesses (x0.5)
        assertEquals(0.5, Element.FIRE.getMultiplierAgainst(Element.WATER));
        assertEquals(0.5, Element.WATER.getMultiplierAgainst(Element.PLANT));
        assertEquals(0.5, Element.PLANT.getMultiplierAgainst(Element.FIRE));

        // Neutre (même élément) (x1.0)
        assertEquals(1.0, Element.FIRE.getMultiplierAgainst(Element.FIRE));
        assertEquals(1.0, Element.WATER.getMultiplierAgainst(Element.WATER));
        assertEquals(1.0, Element.PLANT.getMultiplierAgainst(Element.PLANT));
    }

    @Test
    void getMultiplierAgainst_shouldReturnOne_forNeutralInteractions() {
        // Attaques de type NEUTRE
        assertEquals(1.0, Element.NEUTRAL.getMultiplierAgainst(Element.FIRE));
        assertEquals(1.0, Element.NEUTRAL.getMultiplierAgainst(Element.WATER));
        assertEquals(1.0, Element.NEUTRAL.getMultiplierAgainst(Element.PLANT));
        assertEquals(1.0, Element.NEUTRAL.getMultiplierAgainst(Element.NEUTRAL));

        // Cibles de type NEUTRE
        assertEquals(1.0, Element.FIRE.getMultiplierAgainst(Element.NEUTRAL));
        assertEquals(1.0, Element.WATER.getMultiplierAgainst(Element.NEUTRAL));
        assertEquals(1.0, Element.PLANT.getMultiplierAgainst(Element.NEUTRAL));
    }

    @Test
    void getMultiplierAgainst_shouldReturnOne_whenTargetIsNull() {
        assertEquals(1.0, Element.FIRE.getMultiplierAgainst(null));
        assertEquals(1.0, Element.NEUTRAL.getMultiplierAgainst(null));
    }
}