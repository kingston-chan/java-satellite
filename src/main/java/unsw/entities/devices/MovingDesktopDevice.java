package unsw.entities.devices;

import unsw.entities.movement.MoveAnticlockwiseOnly;

public class MovingDesktopDevice extends DesktopDevice {
    private static final int DESKTOP_SPEED = 30;

    public MovingDesktopDevice() {
        super(new MoveAnticlockwiseOnly());
        this.setLinearSpeed(DESKTOP_SPEED);
    }
}
