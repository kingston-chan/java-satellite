package unsw.entities.devices;

import unsw.entities.movement.MoveAnticlockwiseOnly;

public class MovingHandheldDevice extends HandheldDevice {
    private final int HANDHELD_SPEED = 50;

    public MovingHandheldDevice() {
        super(new MoveAnticlockwiseOnly());
        this.setLinearSpeed(HANDHELD_SPEED);
    }
}
