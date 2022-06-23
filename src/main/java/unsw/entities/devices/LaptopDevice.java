package unsw.entities.devices;

import unsw.entities.BlackoutObject;
import unsw.entities.filemanagement.FileStorage;
import unsw.entities.other.BandwidthControl;
import unsw.interfaces.MoveBehavior;
import unsw.utils.MathsHelper;

/**
 * LaptopDevice is a type of device in Blackout
 * 
 * @author Kingston Chan
 */
public class LaptopDevice extends BlackoutObject {
    private static final int LAPTOP_RANGE = 100000;

    /**
     * Like all devices, it LaptopDevices should
     * only be created with DeviceFactory
     */
    public LaptopDevice(MoveBehavior moveBehavior) {
        super(moveBehavior);
        this.setRange(LAPTOP_RANGE);
        this.setHeight(MathsHelper.RADIUS_OF_JUPITER);
        this.setBandwidthControl(new BandwidthControl(-1, -1));
        this.setFileStorage(new FileStorage(-1, -1));
        this.removeSupport("HandheldDevice");
        this.removeSupport("LaptopDevice");
        this.removeSupport("DesktopDevice");
        this.setLinearSpeed(0);
    }

    @Override
    public String getType() {
        return "LaptopDevice";
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
        if (obj instanceof LaptopDevice) {
            if (super.equals(obj) && this.getClass() == obj.getClass()) {
                LaptopDevice otherObj = (LaptopDevice) obj;

                return this.getId() == otherObj.getId() && this.getHeight() == otherObj.getHeight()
                        && this.getPosition().compareTo(otherObj.getPosition()) == 0;
            }

            return false;
        }
        return false;
    }
}
