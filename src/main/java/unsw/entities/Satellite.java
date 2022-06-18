package unsw.entities;

import unsw.utils.Angle;

public abstract class Satellite extends Communicator implements Moving {
    private int linearSpeed;

    public Satellite(String id, int range, Angle position, double height, int linearSpeed) {
        super(id, range, position, height);
        this.linearSpeed = linearSpeed;
    }

    public int getLinearSpeed() {
        return linearSpeed;
    }

    @Override
    public void move() {
        this.setPosition(Angle.fromRadians(
                this.getPosition().toRadians() - (this.linearSpeed / this.getHeight())));
    }

    public String getType() {
        if (this instanceof RelaySatellite) {
            return "RelaySatellite";
        }

        if (this instanceof TeleportingSatellite) {
            return "TeleportingSatellite";
        }

        return "StandardSatellite";
    }

    public boolean supports(Device device) {
        if (this instanceof StandardSatellite) {
            return !(device.getType() == "DesktopDevice");
        }
        return true;
    }
}
