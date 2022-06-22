package unsw.utils;

/**
 * Helper functions to move to new position with provided angular velocity
 * and direction.
 * 
 * @author Kingston Chan
 */
public class MovingHelpers {
    /**
     * Calculates the new position after moving clockwise and makes sure
     * to wrap back to 359 degrees
     * 
     * @param currentPos      initial position
     * @param angularVelocity angular velocity
     * @return new position
     */
    private static Angle moveClockwise(Angle currentPos, double angularVelocity) {
        double newPosition = currentPos.toRadians() - angularVelocity;
        if (newPosition < 0) {
            return Angle.fromRadians((2 * Math.PI) + newPosition);
        }
        return Angle.fromRadians(newPosition);
    }

    /**
     * Calculates the new position after moving anticlockwise and makes sure
     * to wrap back to 0 after reaching position greater than 359
     * 
     * @param currentPos      initial position
     * @param angularVelocity angular velocity
     * @return new position
     */
    private static Angle moveAnticlockwise(Angle currentPos, double angularVelocity) {
        return Angle.fromRadians((currentPos.toRadians() + angularVelocity) % (2 * Math.PI));
    }

    /**
     * Calculates the new position after moving using the given direction
     * 
     * @param currentPos      initial position
     * @param angularVelocity angular velocity
     * @return new position
     */
    public static Angle moveUsingDirection(int direction, Angle currentPos, double angularVelocity) {
        if (direction == MathsHelper.CLOCKWISE) {
            return moveClockwise(currentPos, angularVelocity);
        }
        return moveAnticlockwise(currentPos, angularVelocity);
    }

}
