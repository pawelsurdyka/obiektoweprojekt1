import agh.ics.oop.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class OptionParserTest {
    @Test
    public void parseTest(){
        String S = "f r r l backward";
        ArrayList<MoveDirection> OKdirections = new ArrayList<>();
        OKdirections.add(MoveDirection.FORWARD);
        OKdirections.add(MoveDirection.RIGHT);
        OKdirections.add(MoveDirection.RIGHT);
        OKdirections.add(MoveDirection.LEFT);
        OKdirections.add(MoveDirection.BACKWARD);
        new OptionsParser();
        ArrayList<MoveDirection> directions = OptionsParser.parse(S);
        assertEquals(OKdirections, directions);
    }
    @Test
    public void parseTestThrow(){
        boolean thrown = false;
        try{
            String S = "f r r X backward";
            new OptionsParser();
            ArrayList<MoveDirection> directions = OptionsParser.parse(S);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
    }
}