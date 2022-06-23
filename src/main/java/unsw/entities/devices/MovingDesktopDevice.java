package unsw.entities.devices;

import unsw.entities.movement.MoveAnticlockwiseOnly;

public class MovingDesktopDevice extends DesktopDevice {
    private final int DESKTOP_SPEED = 20;

    public MovingDesktopDevice() {
        super(new MoveAnticlockwiseOnly());
        this.setLinearSpeed(DESKTOP_SPEED);
    }
}
