package unsw.entities;

import unsw.utils.Angle;

public class Satellite extends Communicator {
    private int linearSpeed;
    private int uploadSpeed;
    private int downloadSpeed;
    private boolean clockwiseDirection;
    private double height;
    private String type;
    private int maxFiles;
    private int availFileStorage; // in bytes
    private int downloadCount;
    private int uploadCount;

    public Satellite(String id, int maxRange, Angle position, int linearSpeed, int uploadSpeed, int downloadSpeed,
            boolean clockwiseDirection, double height, String type, int maxFiles, int availFileStorage) {
        super(id, maxRange, position);
        this.linearSpeed = linearSpeed;
        this.uploadSpeed = uploadSpeed;
        this.downloadSpeed = downloadSpeed;
        this.clockwiseDirection = clockwiseDirection;
        this.height = height;
        this.type = type;
        this.maxFiles = maxFiles;
        this.availFileStorage = availFileStorage;
        this.downloadCount = 0;
        this.uploadCount = 0;
    }

    public int getLinearSpeed() {
        return linearSpeed;
    }

    public void setLinearSpeed(int linearSpeed) {
        this.linearSpeed = linearSpeed;
    }

    public int getUploadSpeed() {
        return uploadSpeed;
    }

    public void setUploadSpeed(int uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }

    public int getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(int downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public boolean isClockwiseDirection() {
        return clockwiseDirection;
    }

    public void setClockwiseDirection(boolean clockwiseDirection) {
        this.clockwiseDirection = clockwiseDirection;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void move() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMaxFiles() {
        return maxFiles;
    }

    public void setMaxFiles(int maxFiles) {
        this.maxFiles = maxFiles;
    }

    public int getAvailFileStorage() {
        return availFileStorage;
    }

    public void setAvailFileStorage(int availFileStorage) {
        this.availFileStorage = availFileStorage;
    }

    public int getUploadCount() {
        return uploadCount;
    }

    public void setUploadCount(int uploadCount) {
        this.uploadCount = uploadCount;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public boolean supports(Device device) {
        return true;
    }
}
