package unsw.entities;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {

    public RelaySatellite(String id, Angle position, boolean clockwiseDirection, double height) {
        super(id, 300000, position, 1500, -1, -1, clockwiseDirection, height, "RelaySatellite", 0, 0);
    }

    @Override
    public void move() {

    }

}
