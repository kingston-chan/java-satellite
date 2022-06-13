package unsw.entities;

import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public class TeleportingSatellite extends Satellite {

    public TeleportingSatellite(String id, Angle position, double height) {
        super(id, 200000, position, 1000, 10, 15, false, height, "TeleportingSatellite", -1, 200);
    }

    @Override
    public void move() {

    }
}
