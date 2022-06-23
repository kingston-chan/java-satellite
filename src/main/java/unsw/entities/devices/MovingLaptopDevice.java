package unsw.entities.devices;

import unsw.entities.movement.MoveAnticlockwiseOnly;

public class MovingLaptopDevice extends LaptopDevice {
    private final int LAPTOP_SPEED = 30;

    public MovingLaptopDevice() {
        super(new MoveAnticlockwiseOnly());
        this.setLinearSpeed(LAPTOP_SPEED);
    }
}
