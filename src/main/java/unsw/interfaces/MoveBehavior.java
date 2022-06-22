package unsw.interfaces;

import unsw.utils.Angle;

/**
 * Devices and satellite are allowed to move according to
 * their specified characteristics
 * 
 * @author Kingston Chan
 */
public interface MoveBehavior {
    /**
     * Simulate the movement of the object according to its
     * angular velocity and return its new position.
     * 
     * @param position        object's current position
     * @param angularVelocity object's angular velocity
     * @return objects new position
     */
    public Angle move(Angle position, double angularVelocity);
}
