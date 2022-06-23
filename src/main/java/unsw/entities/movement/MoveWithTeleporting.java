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

    private boolean reachedTeleportAngle(Angle position) {
        return (position.compareTo(Angle.fromDegrees(180)) >= 0 && this.direction == MathsHelper.ANTI_CLOCKWISE) ||
                (position.compareTo(Angle.fromDegrees(180)) <= 0 && this.direction == MathsHelper.CLOCKWISE);
    }

    @Override
    public Angle move(Angle position, double angularVelocity) {
        Angle newPosition = MovingHelpers.moveUsingDirection(direction, position, angularVelocity);
        if (reachedTeleportAngle(newPosition)) {
            this.direction = this.direction * -1; // Alternates direction when teleports
            return Angle.fromDegrees(0);
        }

        return newPosition;
    }
}
