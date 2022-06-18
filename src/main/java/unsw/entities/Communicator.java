package unsw.entities;

import unsw.utils.Angle;

public class Communicator {
    private String id;
    private int range;
    private Angle position;
    private double height;

    public Communicator(String id, int range, Angle position, double height) {
        this.id = id;
        this.range = range;
        this.position = position;
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public int getRange() {
        return range;
    }

    public Angle getPosition() {
        return position;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public double getHeight() {
        return height;
    }

}
