package unsw.interfaces;

/**
 * Communicators can download files, however satellites have a limited
 * bandwidth, these methods will help manage it
 * 
 * @author Kingston Chan
 */
public interface Download {
    /**
     * Gets the download bandwidth of the communicator in
     * bytes/minute
     * 
     * @return int bytes it is able to download at a rate of a minute
     */
    public int getDownloadBandwidth();

    /**
     * Starts a download for the communicator. This should be
     * called for the reciever when a file transfer allowed.
     */
    public void startDownload();

    /**
     * Ends a download for the communicator. This should be
     * called when a download finishes or is cancelled. If
     * it is cancelled, its respective upload should also
     * be cancelled.
     */
    public void endDownload();
}
