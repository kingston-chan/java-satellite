package unsw.entities;

import unsw.utils.Angle;

public class Satellite extends Communicator {
    private int linearSpeed;
    private int uploadSpeed;
    private int downloadSpeed;
    private boolean clockwiseDirection;
    private double height;
    private String type;

    public Satellite(String id, int maxRange, Angle position, int linearSpeed, int uploadSpeed, int downloadSpeed,
            boolean clockwiseDirection, double height, String type) {
        super(id, maxRange, position);
        this.linearSpeed = linearSpeed;
        this.uploadSpeed = uploadSpeed;
        this.downloadSpeed = downloadSpeed;
        this.clockwiseDirection = clockwiseDirection;
        this.height = height;
        this.type = type;
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
}
