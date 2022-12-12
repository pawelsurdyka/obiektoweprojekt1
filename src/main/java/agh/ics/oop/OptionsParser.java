package agh.ics.oop;
import java.util.ArrayList;
import java.util.List;

public class OptionsParser {
    public static ArrayList<MoveDirection> parse(String args){
        ArrayList<MoveDirection>A=new ArrayList<>();
        ArrayList<String> args2 = new ArrayList<>();
        String s = "";
        for(int i=0;i<args.length();i++){
            if(args.charAt(i)!=' '){
                s+=args.charAt(i);
            }
            else{
                args2.add(s);
                s = "";
            }
        }
        if(s.length()>0)
            args2.add(s);
        for(String arg:args2){
            switch (arg) {
                case "f", "forward":
                    A.add(MoveDirection.FORWARD);
                    break;
                case "b", "backward":
                    A.add(MoveDirection.BACKWARD);
                    break;
                case "r", "right":
                    A.add(MoveDirection.RIGHT);
                    break;
                case "l", "left":
                    A.add(MoveDirection.LEFT);
                    break;
                default:
                    throw new IllegalArgumentException(arg + " is not legal move specification");
            }
        }
        return A;
    }
}