package unsw.entities;

public class DesktopDevice extends Device {
    private static final int DESKTOP_RANGE = 200000;

    public DesktopDevice() {
        super("DesktopDevice", DESKTOP_RANGE);
    }
}
