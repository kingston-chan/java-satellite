package unsw.entities.other;

import java.util.HashMap;

import unsw.entities.BlackoutObject;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

/**
 * Class to create new slopes
 */
public class Slope {
    private int startAngle;
    private int endAngle;
    private int gradient;

    private HashMap<String, BlackoutObject> movingDevicesOnSlope = new HashMap<>();

    public Slope(int startAngle, int endAngle, int gradient) {
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.gradient = gradient;
    }

    /**
     * Checks if angle is in the slope region
     * 
     * @param position
     * @return
     */
    private boolean isInSlope(Angle position) {
        return (position.compareTo(Angle.fromDegrees(this.startAngle)) >= 0 &&
                position.compareTo(Angle.fromDegrees(this.endAngle)) < 0);
    }

    public boolean isDeviceOnSlope(BlackoutObject device) {
        return this.movingDevicesOnSlope.get(device.getId()) != null;
    }

    public void moveDeviceOnSlope(BlackoutObject movingDevice) {
        if (movingDevice.getPosition().compareTo(Angle.fromDegrees(this.endAngle)) >= 0) {
            // Reached end of slope
            movingDevice.setHeight(MathsHelper.RADIUS_OF_JUPITER);
            this.movingDevicesOnSlope.remove(movingDevice.getId());
        } else {
            double newHeight = this.gradient * Math.toDegrees(movingDevice.getLinearSpeed() / movingDevice.getHeight());

            movingDevice.addToHeight(newHeight);
        }

    }

    public void addDeviceToSlope(BlackoutObject device) {
        this.movingDevicesOnSlope.put(device.getId(), device);
    }

    public boolean isSlopeIncreasing() {
        return gradient > 0;
    }

    public boolean hasDeviceEnteredSlope(BlackoutObject device, Angle oldPosition) {
        return !device.doesOrbit() && !isInSlope(oldPosition)
                && isInSlope(device.getPosition());
    }
}
