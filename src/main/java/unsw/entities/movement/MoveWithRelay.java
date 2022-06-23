package unsw.entities.movement;

import unsw.interfaces.MoveBehavior;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;
import unsw.utils.MovingHelpers;

/**
 * Should a blackout object require to move between
 * 140 degrees and 190 degrees and switch directions
 * once it reaches the above degrees, use this
 * 
 * @author Kingston Chan
 */
public class MoveWithRelay implements MoveBehavior {
    private int direction = MathsHelper.CLOCKWISE;

    /**
     * Checks if the current position of the blackout object is in
     * [140, 190] degrees
     * 
     * @param position current position
     * @return whether in region [140, 190] degrees
     */
    private boolean inRelayRange(Angle position) {
        return position.compareTo(Angle.fromDegrees(140)) >= 0 &&
                position.compareTo(Angle.fromDegrees(190)) <= 0;
    }

    /**
     * Changes direction so that the blackout object would move back to
     * region the fastest
     * 
     * @param position current position
     */
    private void setDirectionFastestToRegion(Angle position) {
        if (position.compareTo(Angle.fromDegrees(345)) <= 0 &&
                position.compareTo(Angle.fromDegrees(190)) > 0) {
            this.direction = MathsHelper.CLOCKWISE;
        } else {
            this.direction = MathsHelper.ANTI_CLOCKWISE;
        }
    }

    @Override
    public Angle move(Angle currentPosition, double angularVelocity) {
        if (inRelayRange(currentPosition)) {
            return MovingHelpers.moveUsingDirection(direction, currentPosition, angularVelocity);
        }
        setDirectionFastestToRegion(currentPosition);
        return MovingHelpers.moveUsingDirection(direction, currentPosition, angularVelocity);
    }
}
