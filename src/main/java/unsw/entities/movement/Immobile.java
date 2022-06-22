package unsw.entities.movement;

import unsw.interfaces.MoveBehavior;
import unsw.utils.Angle;

/**
 * If blackout objects are stationary use this
 * 
 * @author Kingston Chan
 */
public class Immobile implements MoveBehavior {
    @Override
    public Angle move(Angle position, double angularVelocity) {
        return position;
    }
}
