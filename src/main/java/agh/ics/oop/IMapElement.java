package agh.ics.oop;

public interface IMapElement {

    Vector2d getPosition();
    // zwraca pozycje
    String toString();
    // wyswietlanie (kierunek zwierzaka/kępka trawy)
    String getImage();
}