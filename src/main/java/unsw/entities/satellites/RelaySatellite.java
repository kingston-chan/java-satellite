package unsw.entities.satellites;

import unsw.entities.BlackoutObject;
import unsw.entities.filemanagement.FileStorage;
import unsw.entities.movement.MoveWithRelay;
import unsw.entities.other.BandwidthControl;

/**
 * Unlike the other satellites, this satellite cannot send and recieve files,
 * therefore cannot store files. However, it helps extend the ranges of other
 * devices and satellites if they are in range with a relay satellite.
 * 
 * It also has special movement properties; it alternates direction everytime it
 * reaches the edge of its travel region [140 degrees, 190 degrees]. If it does
 * not start from this region it moves in the quickest direction to reach the
 * region.
 * 
 * @author Kingston Chan
 */
public class RelaySatellite extends BlackoutObject {
    private static final int RELAY_SPEED = 1500;
    private static final int RELAY_RANGE = 300000;

    /**
     * Like all creation of satellites, it should be created using the
     * SatelliteFactory class, instead of creating an instance with this
     * constructor.
     */
    public RelaySatellite() {
        super(new MoveWithRelay());
        this.setLinearSpeed(RELAY_SPEED);
        this.setRange(RELAY_RANGE);
        // Does not have limitation on bandwidth
        this.setBandwidthControl(new BandwidthControl(-1, -1));
        // Cannot store any files
        this.setFileStorage(new FileStorage(0, 0));
    }

    @Override
    public boolean canTeleport() {
        return false;
    }

    @Override
    public boolean canExtendRange() {
        return true;
    }

    @Override
    public String getType() {
        return "RelaySatellite";
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
        if (obj instanceof RelaySatellite) {
            if (super.equals(obj) && this.getClass() == obj.getClass()) {
                RelaySatellite otherObj = (RelaySatellite) obj;

                return this.getId() == otherObj.getId() && this.getHeight() == otherObj.getHeight()
                        && this.getPosition().compareTo(otherObj.getPosition()) == 0;
            }

            return false;
        }
        return false;
    }
}
