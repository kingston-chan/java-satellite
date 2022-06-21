package unsw.interfaces;

/**
 * There are two special actions that a communicator
 * can do, it can teleport and relay information
 * 
 * @author Kingston Chan
 */
public interface SpecialActions {
    /**
     * Returns whether this communicator can relay information
     * 
     * @return boolean whether communicator can relay information
     */
    public boolean canRelayInfo();

    /**
     * Returns whether this communicator can teleport
     * 
     * @return boolean whether communicator can teleport
     */
    public boolean canTeleport();
}
