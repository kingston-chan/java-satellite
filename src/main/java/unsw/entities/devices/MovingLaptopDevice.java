package unsw.entities.devices;

import unsw.entities.movement.MoveAnticlockwiseOnly;

public class MovingLaptopDevice extends LaptopDevice {
    private static final int DESKTOP_SPEED = 50;

    public MovingLaptopDevice() {
        super(new MoveAnticlockwiseOnly());
        this.setLinearSpeed(DESKTOP_SPEED);
    }
}
