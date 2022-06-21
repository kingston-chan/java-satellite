package unsw.entities;

public class LaptopDevice extends Device {
    private static final int LAPTOP_RANGE = 100000;

    public LaptopDevice() {
        super("LaptopDevice", LAPTOP_RANGE);
    }
}
