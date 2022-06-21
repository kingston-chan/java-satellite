package unsw.utils;

public class MovingHelpers {
    public static Angle moveClockwise(Angle currentPos, double movedInRadians) {
        if (currentPos.toRadians() - movedInRadians < 0) {
            return Angle.fromRadians((2 * Math.PI) + (currentPos.toRadians() - movedInRadians));
        }
        return Angle.fromRadians(currentPos.toRadians() - movedInRadians);
    }

    public static Angle moveAnticlockwise(Angle currentPos, double movedInRadians) {
        return Angle.fromRadians((currentPos.toRadians() + movedInRadians) % (2 * Math.PI));
    }

    public static Angle moveUsingDirection(int direction, Angle currentPos, double movedInRadians) {
        if (direction == MathsHelper.CLOCKWISE) {
            return moveClockwise(currentPos, movedInRadians);
        }
        return (moveAnticlockwise(currentPos, movedInRadians));
    }
}
