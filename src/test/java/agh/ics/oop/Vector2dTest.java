package agh.ics.oop;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Vector2dTest {
    Vector2d position0 = new Vector2d(1,1);
    Vector2d position1 = new Vector2d(0,0);
    Vector2d position2 = new Vector2d(0,1);
    Vector2d position3 = new Vector2d(0,2);
    Vector2d position4 = new Vector2d(1,0);
    Vector2d position5 = new Vector2d(1,1);
    Vector2d position6 = new Vector2d(1,2);
    Vector2d position7 = new Vector2d(2,0);
    Vector2d position8 = new Vector2d(2,1);
    Vector2d position9 = new Vector2d(2,2);

    @Test
    public void equalsTest(){
        assertFalse(position0.equals(position1));
        assertFalse(position0.equals(position2));
        assertFalse(position0.equals(position3));
        assertFalse(position0.equals(position4));
        assertTrue(position0.equals(position5));
        assertFalse(position0.equals(position6));
        assertFalse(position0.equals(position7));
        assertFalse(position0.equals(position8));
        assertFalse(position0.equals(position9));
    }
    @Test
    public void toStringTest(){
        assertEquals(position0.toString(),"(1,1)");
        assertEquals(position1.toString(),"(0,0)");
        assertEquals(position2.toString(),"(0,1)");
        assertEquals(position3.toString(),"(0,2)");
    }
    @Test
    public void precedesTest(){
        assertFalse(position0.precedes(position1));
        assertFalse(position0.precedes(position2));
        assertFalse(position0.precedes(position3));
        assertFalse(position0.precedes(position4));
        assertTrue(position0.precedes(position5));
        assertTrue(position0.precedes(position6));
        assertFalse(position0.precedes(position7));
        assertTrue(position0.precedes(position8));
        assertTrue(position0.precedes(position9));
    }
    @Test
    public void followsTest(){
        assertTrue(position0.follows(position1));
        assertTrue(position0.follows(position2));
        assertFalse(position0.follows(position3));
        assertTrue(position0.follows(position4));
        assertTrue(position0.follows(position5));
        assertFalse(position0.follows(position6));
        assertFalse(position0.follows(position7));
        assertFalse(position0.follows(position8));
        assertFalse(position0.follows(position9));
    }
    @Test
    public void upperRightTest(){
        assertEquals(position0.upperRight(position1),position0);
        assertEquals(position0.upperRight(position2),position0);
        assertEquals(position0.upperRight(position3),position6);
        assertEquals(position0.upperRight(position4),position0);
        assertEquals(position0.upperRight(position5),position0);
        assertEquals(position0.upperRight(position6),position6);
        assertEquals(position0.upperRight(position7),position8);
        assertEquals(position0.upperRight(position8),position8);
        assertEquals(position0.upperRight(position9),position9);
    }
    @Test
    public void lowerLeftTest(){
        assertEquals(position0.lowerLeft(position1),position1);
        assertEquals(position0.lowerLeft(position2),position2);
        assertEquals(position0.lowerLeft(position3),position2);
        assertEquals(position0.lowerLeft(position4),position4);
        assertEquals(position0.lowerLeft(position5),position5);
        assertEquals(position0.lowerLeft(position6),position5);
        assertEquals(position0.lowerLeft(position7),position4);
        assertEquals(position0.lowerLeft(position8),position5);
        assertEquals(position0.lowerLeft(position9),position5);
    }
    Vector2d positiona = new Vector2d(1,3);
    Vector2d positionb = new Vector2d(2,3);
    Vector2d positionc = new Vector2d(3,3);
    Vector2d positiond = new Vector2d(3,1);
    Vector2d positione = new Vector2d(3,2);
    @Test
    public void addTest(){
        assertEquals(position0.add(position1),position0);
        assertEquals(position0.add(position2),position6);
        assertEquals(position0.add(position3),positiona);
        assertEquals(position0.add(position4),position8);
        assertEquals(position0.add(position5),position9);
        assertEquals(position0.add(position6),positionb);
        assertEquals(position0.add(position7),positiond);
        assertEquals(position0.add(position8),positione);
        assertEquals(position0.add(position9),positionc);
    }
    Vector2d positionf= new Vector2d(1,-1);
    Vector2d positiong = new Vector2d(0,-1);
    Vector2d positionh = new Vector2d(-1,-1);
    Vector2d positioni = new Vector2d(-1,1);
    Vector2d positionj = new Vector2d(-1,0);
    @Test
    public void subtractTest(){
        assertEquals(position0.subtract(position1),position0);
        assertEquals(position0.subtract(position2),position4);
        assertEquals(position0.subtract(position3),positionf);
        assertEquals(position0.subtract(position4),position2);
        assertEquals(position0.subtract(position5),position1);
        assertEquals(position0.subtract(position6),positiong);
        assertEquals(position0.subtract(position7),positioni);
        assertEquals(position0.subtract(position8),positionj);
        assertEquals(position0.subtract(position9),positionh);
    }
    @Test
    public void oppositeTest(){
        assertEquals(position0.opposite(),positionh);
        assertEquals(position1.opposite(),position1);
        assertEquals(position2.opposite(),positiong);
        assertEquals(position4.opposite(),positionj);
    }
}
