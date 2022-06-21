package unsw.interfaces;

/**
 * Communicators are allowed to move according to
 * their specified characteristics
 * 
 * @author Kingston Chan
 */
public interface Move {
    /**
     * Simulates the movement of 1 minute of the
     * communicator according to its angular velocity
     * Its position is updated accordingly
     */
    public void move();
}
