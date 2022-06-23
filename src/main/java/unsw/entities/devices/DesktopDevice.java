package unsw.entities.devices;

import unsw.entities.BlackoutObject;
import unsw.entities.filemanagement.FileStorage;
import unsw.interfaces.MoveBehavior;
import unsw.utils.MathsHelper;

/**
 * A DesktopDevice is a type of device in Blackout
 * 
 * @author Kingston Chan
 */
public class DesktopDevice extends BlackoutObject {
    private static final int DESKTOP_RANGE = 200000;

    /**
     * Like all devices, it DesktopDevices should
     * only be created with DeviceFactory
     */
    public DesktopDevice(MoveBehavior moveBehavior) {
        super(moveBehavior);
        this.setRange(DESKTOP_RANGE);
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
        return "DesktopDevice";
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
        if (obj instanceof DesktopDevice) {
            if (super.equals(obj) && this.getClass() == obj.getClass()) {
                DesktopDevice otherObj = (DesktopDevice) obj;

                return this.getId() == otherObj.getId() && this.getHeight() == otherObj.getHeight()
                        && this.getPosition().compareTo(otherObj.getPosition()) == 0;
            }

            return false;
        }
        return false;
    }
}
