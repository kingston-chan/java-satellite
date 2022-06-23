package unsw.entities.satellites;

import unsw.entities.BlackoutObject;
import unsw.entities.movement.MoveWithRelay;

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
    private final int RELAY_SPEED = 1500;
    private final int RELAY_RANGE = 300000;

    /**
     * Like all creation of satellites, it should be created using the
     * SatelliteFactory class, instead of creating an instance with this
     * constructor. Can set the bandwidth control to null since Relay
     * satellites do not have the capability of sending and recieving
     * files. i.e. its bandwidth control should not be accessed for relay
     * satellites and if accessed something went wrong in the implementation
     */
    public RelaySatellite() {
        super(new MoveWithRelay());
        this.setLinearSpeed(RELAY_SPEED);
        this.setRange(RELAY_RANGE);
        this.setBandwidthControl(null);
        this.setFileStorage(null);
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
