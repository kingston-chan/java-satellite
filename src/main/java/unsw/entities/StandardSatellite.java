package unsw.entities;

import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public class StandardSatellite extends Satellite {

    public StandardSatellite(String id, Angle position, double height) {
        super(id, 150000, position, 2500, 1, 1, true, height, "StandardSatellite", 3, 80);
    }

    @Override
    public boolean supports(Device device) {
        return !(device.getType() == "DesktopDevice");
    }

}
