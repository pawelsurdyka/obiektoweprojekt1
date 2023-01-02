package agh.ics.oop;

public enum MapDirection {
    NORTH, SOUTH, WEST, EAST, NWEST, NEAST, SWEST, SEAST;

    public MapDirection next(){
        return switch (this) {
            case NORTH -> NEAST;
            case NEAST -> EAST;
            case EAST -> SEAST;
            case SEAST -> SOUTH;
            case SOUTH -> SWEST;
            case SWEST -> WEST;
            case WEST -> NWEST;
            case NWEST -> NORTH;
        };
    }

    public Vector2d toUnitVector(){
        return switch(this){
            case NORTH -> new Vector2d(0,-1);
            case NEAST -> new Vector2d(1,-1);
            case EAST -> new Vector2d(1,0);
            case SEAST -> new Vector2d(1,1);
            case SOUTH -> new Vector2d(0,1);
            case SWEST -> new Vector2d(-1,1);
            case WEST -> new Vector2d(-1,0);
            case NWEST -> new Vector2d(-1,-1);
        };
    }

    public String getImage(){
        return switch (this) {
            case NORTH -> "file:src/main/resources/up.png";
            case EAST -> "file:src/main/resources/right.png";
            case SOUTH -> "file:src/main/resources/down.png";
            case WEST -> "file:src/main/resources/left.png";
            case SEAST -> "file:src/main/resources/down_right.png";
            case SWEST -> "file:src/main/resources/down_left.png";
            case NEAST -> "file:src/main/resources/up_right.png";
            case NWEST -> "file:src/main/resources/up_left.png";
        };
    }

}
