package unsw.entities;

import unsw.utils.Angle;
import unsw.blackout.FileTransferException;
import unsw.blackout.FileTransferException.VirtualFileAlreadyExistsException;
import unsw.blackout.FileTransferException.VirtualFileNoBandwidthException;
import unsw.blackout.FileTransferException.VirtualFileNoStorageSpaceException;

public class StandardSatellite extends FileTransferSatellite {
    private static final int STANDARD_SPEED = 2500;
    private static final int STANDARD_RANGE = 150000;
    private static final int STANDARD_UPLOAD = 1;
    private static final int STANDARD_DOWNLOAD = 1;
    private final int MAX_FILES = 3;
    private final int MAX_BYTES = 80;

    public StandardSatellite(String id, Angle position, double height) {
        super(id, STANDARD_RANGE, position, height, STANDARD_SPEED, STANDARD_UPLOAD, STANDARD_DOWNLOAD);
    }

    @Override
    public void addFile(FileInfo file) throws FileTransferException {
        for (FileInfo satFile : this.getFiles()) {
            if (file.equals(satFile)) {
                throw new VirtualFileAlreadyExistsException(file.getFileName());
            }
        }

        if (this.getNumFiles() == MAX_FILES) {
            throw new VirtualFileNoStorageSpaceException("Max Files Reached");
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
}
