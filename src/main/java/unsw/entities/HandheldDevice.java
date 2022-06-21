package unsw.entities;

public class HandheldDevice extends Device {
    private static final int HANDHELD_RANGE = 50000;

    public HandheldDevice() {
        super("HandheldDevice", HANDHELD_RANGE);
    }
}
