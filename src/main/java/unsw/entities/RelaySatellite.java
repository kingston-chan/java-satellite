package unsw.entities;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {
    private static final int RELAY_SPEED = 1500;
    private static final int RELAY_RANGE = 300000;
    private boolean movingClockwise;

    private void moveClockwise() {
        this.setPosition(Angle.fromRadians(
                this.getPosition().toRadians() - (this.getLinearSpeed() / this.getHeight())));
    }

    private void moveAnticlockwise() {
        this.setPosition(Angle.fromRadians(
                this.getPosition().toRadians() + (this.getLinearSpeed() / this.getHeight())));
    }

    private boolean inRelayRange(Angle position) {
        return position.compareTo(Angle.fromDegrees(140)) >= 0 &&
                position.compareTo(Angle.fromDegrees(190)) <= 0;
    }

    private void moveUsingDirection() {
        if (movingClockwise) {
            this.moveClockwise();
        } else {
            this.moveAnticlockwise();
        }
    }

    public RelaySatellite(String id, Angle position, double height) {
        super(id, RELAY_RANGE, position, height, RELAY_SPEED);
        // check if position in relay range -> set to clockwise
        if (inRelayRange(position)) {
            this.movingClockwise = true;
        } else {
            this.movingClockwise = !(position.compareTo(Angle.fromDegrees(140)) > 0
                    && position.compareTo(Angle.fromDegrees(345)) <= 0);
        }
    }

    @Override
    public void move() {
        // Check if current positon in relay range
        if (inRelayRange(this.getPosition())) {
            this.moveUsingDirection();

            if (this.getPosition().compareTo(Angle.fromDegrees(140)) < 0) {
                movingClockwise = false;
            }

            if (this.getPosition().compareTo(Angle.fromDegrees(190)) > 0) {
                movingClockwise = true;
            }
        } else {
            this.moveUsingDirection();
        }
    }
}
