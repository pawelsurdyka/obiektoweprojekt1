package agh.ics.oop;
import java.lang.Math;

public class Vector2d{
    public final int x;
    public final int y;
    public Vector2d(int x, int y){
        this.x=x;
        this.y=y;
    }
    public String toString(){
        return "(" + this.x + "," + this.y + ")";
    }

    public boolean precedes(Vector2d other){
        if(this.x <= other.x && this.y <= other.y)
            return true;
        return false;
    }
    public boolean follows(Vector2d other){
        if(this.x >= other.x && this.y >= other.y)
            return true;
        return false;
    }
    public Vector2d upperRight(Vector2d other){
        Vector2d tmp = new Vector2d(Math.max(this.x, other.x), Math.max(this.y, other.y));
        return tmp;
    }
    public Vector2d lowerLeft(Vector2d other){
        Vector2d tmp = new Vector2d(Math.min(this.x, other.x), Math.min(this.y, other.y));
        return tmp;
    }
    public Vector2d add(Vector2d other){
        Vector2d tmp = new Vector2d(this.x + other.x, this.y + other.y);
        return tmp;
    }
    public Vector2d subtract(Vector2d other){
        Vector2d tmp = new Vector2d(this.x - other.x, this.y - other.y);
        return tmp;
    }
    public boolean equals(Object other){
        if(this == other)
            return true;
        else if(!(other instanceof Vector2d))
            return false;
        else {
            Vector2d that = (Vector2d) other;
            return (this.x == that.x && this.y == that.y);
        }
    }
    @Override
    public int hashCode() {
        int tmp = (this.y+((this.x+1)/2));
        return this.x + (tmp * tmp);
    }
    public Vector2d opposite(){
        Vector2d tmp = new Vector2d(-this.x, -this.y);
        return tmp;
    }
}