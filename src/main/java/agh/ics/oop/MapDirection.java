package agh.ics.oop;

public enum MapDirection {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    public String toString() {
        switch (this) {
            case NORTH:
                return "Północ";
            case SOUTH:
                return "Południe";
            case EAST:
                return "Wschód";
            case WEST:
                return "Zachód";
            default:
                return "zła dana";
        }
        //return "true";
    }
    public MapDirection next(){
        switch (this){
            case NORTH:
                return EAST;
            case SOUTH:
                return WEST;
            case EAST:
                return SOUTH;
            case WEST:
                return NORTH;
            default:
                return this;
        }
    }

    public MapDirection previous(){
        switch (this){
            case NORTH:
                return WEST;
            case SOUTH:
                return EAST;
            case EAST:
                return NORTH;
            case WEST:
                return SOUTH;
            default:
                return this;
        }
    }

    public Vector2d toUnitVector(){
        Vector2d VectorNORTH = new Vector2d(0,1);
        Vector2d VectorEAST= new Vector2d(1,0);
        Vector2d VectorSOUTH = new Vector2d(0,-1);
        Vector2d VectorWEST= new Vector2d(-1,0);
        Vector2d VectorZERO= new Vector2d(0,0);
        switch (this){
            case NORTH:
                return VectorNORTH;
            case SOUTH:
                return VectorSOUTH;
            case EAST:
                return VectorEAST;
            case WEST:
                return VectorWEST;
            default:
                return VectorZERO;
        }
    }
}
