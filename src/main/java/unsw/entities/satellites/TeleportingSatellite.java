package unsw.entities.satellites;

import unsw.entities.BlackoutObject;
import unsw.entities.filemanagement.FileStorage;
import unsw.entities.movement.MoveWithTeleporting;
import unsw.entities.other.BandwidthControl;

/**
 * A teleporting satellite supports all devices,
 * can send and recieve multiple files at a time and
 * is only limited by the amount of bytes it stores
 * for file storage.
 * 
 * It also teleports back to 0 degree when it reaches 180 degrees.
 * If it is recieving a file transfer from a device, the partially uploaded file
 * is removed and all "t" bytes is removed from the device's original file
 * 
 * If it is sending a file to a device or sending/recieving a file from a
 * satellite the file is instantly downloaded, however the source's file has all
 * its "t" bytes removed from the file
 * 
 * @author Kingston Chan
 */
public class TeleportingSatellite extends BlackoutObject {
    private final int TELEPORTING_SPEED = 1000;
    private final int TELEPORTING_RANGE = 200000;
    private final int TELEPORTING_UPLOAD = 10;
    private final int TELEPORTING_DOWNLOAD = 15;
    private final int MAX_FILES = -1;
    private final int MAX_BYTES = 200;

    /**
     * Like all creation of satellites, it should be created using the
     * SatelliteFactory class, instead of creating an instance with this
     * constructor.
     */
    public TeleportingSatellite() {
        super(new MoveWithTeleporting());
        this.setLinearSpeed(TELEPORTING_SPEED);
        this.setRange(TELEPORTING_RANGE);
        this.setBandwidthControl(new BandwidthControl(TELEPORTING_UPLOAD, TELEPORTING_DOWNLOAD));
        this.setFileStorage(new FileStorage(MAX_BYTES, MAX_FILES));
    }

    @Override
    public boolean canTeleport() {
        return true;
    }

    @Override
    public boolean canExtendRange() {
        return false;
    }

    @Override
    public String getType() {
        return "TeleportingSatellite";
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
        if (obj instanceof TeleportingSatellite) {
            if (super.equals(obj) && this.getClass() == obj.getClass()) {
                TeleportingSatellite otherObj = (TeleportingSatellite) obj;

                return this.getId() == otherObj.getId() && this.getHeight() == otherObj.getHeight()
                        && this.getPosition().compareTo(otherObj.getPosition()) == 0;
            }

            return false;
        }
        return false;
    }
}
