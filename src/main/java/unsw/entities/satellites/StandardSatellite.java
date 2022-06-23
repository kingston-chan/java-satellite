package unsw.entities.satellites;

import unsw.entities.BlackoutObject;
import unsw.entities.filemanagement.FileStorage;
import unsw.entities.movement.MoveClockwiseOnly;
import unsw.entities.other.BandwidthControl;

/**
 * A Standard satellite does not have much capabilities.
 * It only moves clockwise, sends 1 file and uploads 1 file
 * at a time and it only supports Handheld and Laptop devices.
 * 
 * @author Kingston Chan
 */
public class StandardSatellite extends BlackoutObject {
    private final int STANDARD_SPEED = 2500;
    private final int STANDARD_RANGE = 150000;
    private final int STANDARD_UPLOAD = 1;
    private final int STANDARD_DOWNLOAD = 1;
    private final int MAX_FILES = 3;
    private final int MAX_BYTES = 80;

    /**
     * Like all creation of satellites, it should be created using the
     * SatelliteFactory class, instead of creating an instance with this
     * constructor.
     */
    public StandardSatellite() {
        super(new MoveClockwiseOnly());
        this.setLinearSpeed(STANDARD_SPEED);
        this.setRange(STANDARD_RANGE);
        this.setBandwidthControl(new BandwidthControl(STANDARD_UPLOAD, STANDARD_DOWNLOAD));
        this.setFileStorage(new FileStorage(MAX_BYTES, MAX_FILES));
        this.removeSupport("DesktopDevice");
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
    public String getType() {
        return "StandardSatellite";
    }

    @Override
    public boolean doesOrbit() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof StandardSatellite) {
            if (super.equals(obj) && this.getClass() == obj.getClass()) {
                StandardSatellite otherObj = (StandardSatellite) obj;

                return this.getId() == otherObj.getId() && this.getHeight() == otherObj.getHeight()
                        && this.getPosition().compareTo(otherObj.getPosition()) == 0;
            }

            return false;
        }
        return false;
    }
}
