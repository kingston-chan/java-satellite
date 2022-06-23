package unsw.entities.movement;

import unsw.interfaces.MoveBehavior;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;
import unsw.utils.MovingHelpers;

/**
 * This calculates the new position of the blackout object should it
 * move only anticlockwise
 * 
 * @author Kingston Chan
 */
public class MoveAnticlockwiseOnly implements MoveBehavior {
    @Override
    public Angle move(Angle position, double angularVelocity) {
        return MovingHelpers.moveUsingDirection(MathsHelper.ANTI_CLOCKWISE, position, angularVelocity);
    }
}
