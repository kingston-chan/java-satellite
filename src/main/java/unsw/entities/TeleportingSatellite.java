package unsw.entities;

import unsw.utils.Angle;
import unsw.blackout.FileTransferException;
import unsw.blackout.FileTransferException.VirtualFileAlreadyExistsException;
import unsw.blackout.FileTransferException.VirtualFileNoBandwidthException;
import unsw.blackout.FileTransferException.VirtualFileNoStorageSpaceException;

public class TeleportingSatellite extends FileTransferSatellite {
    private static final int TELEPORTING_SPEED = 1000;
    private static final int TELEPORTING_RANGE = 200000;
    private static final int TELEPORTING_UPLOAD = 10;
    private static final int TELEPORTING_DOWNLOAD = 15;
    private final int MAX_BYTES = 200;

    public TeleportingSatellite(String id, Angle position, double height) {
        super(id, TELEPORTING_RANGE, position, height, TELEPORTING_SPEED, TELEPORTING_UPLOAD, TELEPORTING_DOWNLOAD);
    }

    @Override
    public void addFile(FileInfo file) throws FileTransferException {
        for (FileInfo satFile : this.getFiles()) {
            if (file.equals(satFile)) {
                throw new VirtualFileAlreadyExistsException(file.getFileName());
            }
        }

        if (file.getFileDataSize() + this.getBytesUsed() > MAX_BYTES) {
            throw new VirtualFileNoStorageSpaceException("Max Storage Reached");
        }

        if ((MAX_BYTES / (this.getNumDownloads() + 1)) == 0) {
            throw new VirtualFileNoBandwidthException(this.getId());
        }

        this.addBytesUsed(file.getFileDataSize());

        this.startDownload();

        this.getMappedFiles().put(file.getFileName(), file);
    }

    @Override
    public void move() {
        this.setPosition(Angle.fromRadians(
                this.getPosition().toRadians() + (this.getLinearSpeed() / this.getHeight())));

        if (this.getPosition().compareTo(Angle.fromDegrees(180)) >= 0) {
            this.setPosition(Angle.fromDegrees(0));
        }
    }

}
