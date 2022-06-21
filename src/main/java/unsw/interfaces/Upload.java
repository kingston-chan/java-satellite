package unsw.interfaces;

/**
 * Communicators can also upload. These methods
 * help manage its bandwidth and the active uploads.
 * 
 * @author Kingston Chan
 */
public interface Upload {
    /**
     * Gets the upload bandwidth of the communicator in
     * bytes/minute
     * 
     * @return int bytes it is able to upload at a rate of a minute
     */
    public int getUploadBandwidth();

    /**
     * Starts a upload for the communicator. This should be
     * called for the sender when a file transfer allowed.
     */
    public void startUpload();

    /**
     * Ends a upload for the communicator. This should be
     * called when a upload finishes or is cancelled. If
     * it is cancelled, its respective download should also
     * be cancelled.
     */
    public void endUpload();
}
