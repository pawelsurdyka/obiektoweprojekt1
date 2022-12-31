package agh.ics.oop;

import agh.ics.oop.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Vector2dTest {
    Vector2d a = new Vector2d(-1,-2);
    Vector2d b = new Vector2d(1,2);
    Vector2d c = new Vector2d(2,7);
    Vector2d d = new Vector2d(9,1);
    @Test
    public void equalsTest(){
        assertEquals(a, a);
        Assertions.assertNotEquals(a, b);
    }
    @Test
    public void toStringTest(){
        assertEquals("(-1,-2)", a.toString());
    }
    @Test
    public void precedesTest(){
        Assertions.assertFalse(b.precedes(a));
        Assertions.assertFalse(c.precedes(a));
    }
    @Test
    public void followsTest(){
        Assertions.assertFalse(a.follows(b));
        Assertions.assertTrue(c.follows(a));
    }
    @Test
    public void addTest(){
        assertEquals( a.add(c),new Vector2d(1,5));
        assertEquals( c.add(a),new Vector2d(1,5));
    }
    @Test
    public void subtractTest(){
        assertEquals( d.subtract(c),new Vector2d(7,-6));
        assertEquals( d.subtract(a),new Vector2d(10,3));
    }
}
