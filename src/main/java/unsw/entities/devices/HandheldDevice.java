package unsw.entities.devices;

import unsw.entities.BlackoutObject;
import unsw.entities.filemanagement.FileStorage;
import unsw.interfaces.MoveBehavior;
import unsw.utils.MathsHelper;

/**
 * A HandheldDevice is a type of device in Blackout
 * 
 * @author Kingston Chan
 */
public class HandheldDevice extends BlackoutObject {
    private static final int HANDHELD_RANGE = 50000;

    /**
     * Like all devices, it HandheldDevices should
     * only be created with DeviceFactory
     */
    public HandheldDevice(MoveBehavior moveBehavior) {
        super(moveBehavior);
        this.setRange(HANDHELD_RANGE);
        this.setHeight(MathsHelper.RADIUS_OF_JUPITER);
        this.setBandwidthControl(null);
        this.setFileStorage(new FileStorage(-1, -1));
        this.removeSupport("HandheldDevice");
        this.removeSupport("LaptopDevice");
        this.removeSupport("DesktopDevice");
        this.setLinearSpeed(0);
    }

    @Override
    public String getType() {
        return "HandheldDevice";
    }

    @Override
    public boolean canTeleport() {
        return false;
    }

    @Override
    public boolean canExtendRange() {
        return false;
    }

    @Override
    public boolean doesOrbit() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof HandheldDevice) {
            if (super.equals(obj) && this.getClass() == obj.getClass()) {
                HandheldDevice otherObj = (HandheldDevice) obj;

                return this.getId() == otherObj.getId() && this.getHeight() == otherObj.getHeight()
                        && this.getPosition().compareTo(otherObj.getPosition()) == 0;
            }

            return false;
        }
        return false;
    }
}
