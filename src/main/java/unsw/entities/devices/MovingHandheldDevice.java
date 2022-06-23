package unsw.entities.devices;

import unsw.entities.movement.MoveAnticlockwiseOnly;

public class MovingHandheldDevice extends HandheldDevice {
    private static final int DESKTOP_SPEED = 50;

    public MovingHandheldDevice() {
        super(new MoveAnticlockwiseOnly());
        this.setLinearSpeed(DESKTOP_SPEED);
    }
}
