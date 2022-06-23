package unsw.entities.movement;

import unsw.interfaces.MoveBehavior;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;
import unsw.utils.MovingHelpers;

/**
 * Should a blackout object teleport when it reaches
 * 180 degrees and alternate its direction when it teleports,
 * use this
 * 
 * @author Kingston Chan
 */
public class MoveWithTeleporting implements MoveBehavior {
    private int direction = MathsHelper.ANTI_CLOCKWISE;
    private boolean hasTeleportedBefore = false;
    private boolean passed360 = false;

    private boolean reachedTeleportAngle(Angle position) {
        return (position.compareTo(Angle.fromDegrees(180)) >= 0 && this.direction == MathsHelper.ANTI_CLOCKWISE) ||
                (position.compareTo(Angle.fromDegrees(180)) <= 0 && this.direction == MathsHelper.CLOCKWISE);
    }

    /**
     * Checks whether the teleporting satellite started in the region greater than
     * 180 but less than 360
     * 
     * @param position
     * @return
     */
    private boolean startsGreaterThan180(Angle position) {
        return !hasTeleportedBefore && position.compareTo(Angle.fromDegrees(180)) > 0 && !passed360;
    }

    /**
     * Checks if the teleporting satellite wrapped back around jupiter
     * 
     * @param position
     * @return
     */
    private boolean hasPassed360(Angle position) {
        return !passed360 && !hasTeleportedBefore && position.compareTo(Angle.fromDegrees(0)) >= 0
                && position.compareTo(Angle.fromDegrees(180)) < 0;
    }

    @Override
    public Angle move(Angle position, double angularVelocity) {
        Angle newPosition = MovingHelpers.moveUsingDirection(direction, position, angularVelocity);
        // check if teleported before and check if in greater than 180 to 360 region
        // if this is not checked then below will teleport satellite if it was initiated
        // in this region
        if (hasPassed360(newPosition)) {
            passed360 = true;
        }

        if (startsGreaterThan180(newPosition)) {
            return newPosition;
        }

        if (reachedTeleportAngle(newPosition)) {
            this.hasTeleportedBefore = true;
            this.direction = this.direction * -1; // Alternates direction when teleports
            return Angle.fromDegrees(0);
        }

        return newPosition;
    }
}
