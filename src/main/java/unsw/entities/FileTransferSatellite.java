package unsw.entities;

import unsw.utils.Angle;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class FileTransferSatellite extends Satellite implements FileStorage {
    private int uploadBandwidth;
    private int downloadBandwidth;
    private int numUploads;
    private int numDownloads;
    private int bytesUsed;
    private HashMap<String, FileInfo> files;

    public FileTransferSatellite(String id, int range, Angle position, double height, int linearSpeed,
            int uploadBandwidth, int downloadBandwidth) {
        super(id, range, position, height, linearSpeed);
        this.uploadBandwidth = uploadBandwidth;
        this.downloadBandwidth = downloadBandwidth;
        this.numUploads = 0;
        this.numDownloads = 0;
        this.bytesUsed = 0;
        this.files = new HashMap<String, FileInfo>();
    }

    public int getUploadBandwidth() {
        return this.uploadBandwidth / this.numUploads;
    }

    public int getDownloadBandwidth() {
        return this.downloadBandwidth / this.numDownloads;
    }

    public int getNumUploads() {
        return this.numUploads;
    }

    public void startUpload() {
        this.numUploads++;
    }

    public void endUpload() {
        this.numUploads--;
    }

    public int getNumDownloads() {
        return this.numDownloads;
    }

    public void startDownload() {
        this.numDownloads++;
    }

    public void endDownload() {
        this.numDownloads--;
    }

    public int getBytesUsed() {
        return bytesUsed;
    }

    public void addBytesUsed(int bytes) {
        this.bytesUsed += bytes;
    }

    public void setBytesUsed(int bytes) {
        this.bytesUsed = bytes;
    }

    public int getNumFiles() {
        return this.files.size();
    }

    public HashMap<String, FileInfo> getMappedFiles() {
        return this.files;
    }

    @Override
    public List<FileInfo> getFiles() {
        return new ArrayList<>(this.files.values());
    }

    @Override
    public void removeFile(String fileName) {
        this.files.remove(fileName);
    }

    @Override
    public FileInfo getFile(String fileName) {
        return this.files.get(fileName);
    }
}
