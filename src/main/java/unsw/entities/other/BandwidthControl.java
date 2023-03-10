package unsw.entities.other;

/**
 * BandwidthControl helps manage the bandwidth restrictions of
 * blackout objects when uploading/downloading files
 * 
 * @author Kingston Chan
 */
public class BandwidthControl {
    private int maxUploadBandwidth; // If set to -1, unlimited bandwidth
    private int maxDownloadBandwidth; // If set to -1, unlimited bandwidth
    private int numUploads;
    private int numDownloads;
    private int finishedUploads;
    private int finishedDownloads;

    public BandwidthControl(int maxUploadBandwidth, int maxDownloadBandwidth) {
        this.maxUploadBandwidth = maxUploadBandwidth;
        this.maxDownloadBandwidth = maxDownloadBandwidth;
        this.numUploads = 0;
        this.numDownloads = 0;
        this.finishedUploads = 0;
        this.finishedDownloads = 0;
    }

    /**
     * Gets the download bandwidth of the owner of this bandwidth control
     * 
     * @return download bandwidth of the owner of this bandwidth control
     */
    public int getDownloadBandwidth() {
        if (numDownloads == 0) {
            return this.maxDownloadBandwidth / 1;
        }
        return this.maxDownloadBandwidth / this.numDownloads;
    }

    /**
     * Gets the upload bandwidth of the owner of this bandwidth control
     * 
     * @return upload bandwidth of the owner of this bandwidth control
     */
    public int getUploadBandwidth() {
        if (numUploads == 0) {
            return this.maxUploadBandwidth / 1;
        }
        return this.maxUploadBandwidth / this.numUploads;
    }

    /**
     * Get the maximum transfer rate between an uploader and downloader. The one
     * calling this method should be the uploader. Either the downloader or uploader
     * should have a set bandwidth.
     * 
     * @param downloaderBandwidthControl downloader object's BandwidthControl
     * @return the maximum transfer rate allowed
     */
    public int getMaxTransferRate(BandwidthControl downloaderBandwidthControl) {
        if (downloaderBandwidthControl.getDownloadBandwidth() < 0) {
            return this.getUploadBandwidth();
        }
        if (this.getUploadBandwidth() < 0) {
            return downloaderBandwidthControl.getDownloadBandwidth();
        }
        return Math.min(this.getUploadBandwidth(), downloaderBandwidthControl.getDownloadBandwidth());
    }

    /**
     * Checks with the given download bandwidth whether it can initiate an download
     * If it can, initiates a download
     * 
     * @return whether it can initiate a download
     */
    public boolean initiateDownload() {
        if (this.numDownloads + 1 <= maxDownloadBandwidth || maxDownloadBandwidth == -1) {
            this.numDownloads++;
            return true;
        }
        return false;
    }

    /**
     * Checks with the given upload bandwidth whether it can initiate an upload
     * If it can, initiates an upload
     * 
     * @return whether it can initiate an upload
     */
    public boolean initiateUpload() {
        if (this.numUploads + 1 <= maxUploadBandwidth || maxDownloadBandwidth == -1) {
            this.numUploads++;
            return true;
        }
        return false;
    }

    public void endDownload() {
        finishedDownloads++;
    }

    public void endUpload() {
        finishedUploads++;
    }

    /**
     * Included two variables to keep track of number of downloads/uploads
     * after a file transfer. This is to keep the bandwidth consistent
     * during file transfers. If it goes negative then whatever calling
     * it has made any error in ending upload/download. Also, this should
     * always be called after doing the file transfers.
     */
    public void correctUploadDownloadValues() {
        this.numDownloads -= this.finishedDownloads;
        this.numUploads -= this.finishedUploads;
        this.finishedDownloads = 0;
        this.finishedUploads = 0;
    }
}
