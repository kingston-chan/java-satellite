package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import unsw.blackout.FileTransferException.VirtualFileAlreadyExistsException;
import unsw.blackout.FileTransferException.VirtualFileNoBandwidthException;
import unsw.blackout.FileTransferException.VirtualFileNoStorageSpaceException;
import unsw.blackout.FileTransferException.VirtualFileNotFoundException;

import unsw.utils.Angle;
import unsw.utils.DeviceFactory;
import unsw.utils.SatelliteFactory;
import unsw.entities.BlackoutObject;
import unsw.entities.filemanagement.FileInTransfer;
import unsw.entities.filemanagement.FileInfo;
import unsw.entities.filemanagement.FileStorage;
import unsw.entities.other.BandwidthControl;

/**
 * BlackoutController helps stores and get devices and satellites.
 * It also provides information for active devices and satellites
 * and simulates what would happen for a given minute for the devices
 * and satellites.
 */
public class BlackoutController {

    private HashMap<String, BlackoutObject> blackoutObjects = new HashMap<String, BlackoutObject>();
    private List<FileInTransfer> filesInTransfer = new ArrayList<FileInTransfer>();

    public void createDevice(String deviceId, String type, Angle position) {
        DeviceFactory deviceFactory = new DeviceFactory();
        this.blackoutObjects.put(deviceId, deviceFactory.createNewDevice(deviceId, position, type));
    }

    public void removeDevice(String deviceId) {
        this.blackoutObjects.remove(deviceId);
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        SatelliteFactory satelliteFactory = new SatelliteFactory();
        this.blackoutObjects.put(satelliteId, satelliteFactory.createNewSatellite(satelliteId, position, height, type));
    }

    public void removeSatellite(String satelliteId) {
        this.blackoutObjects.remove(satelliteId);
    }

    public List<String> listDeviceIds() {
        return BlackoutHelpers.getSatOrDeviceIds(false, blackoutObjects);
    }

    public List<String> listSatelliteIds() {
        return BlackoutHelpers.getSatOrDeviceIds(true, blackoutObjects);
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        BlackoutObject device = this.blackoutObjects.get(deviceId);
        FileStorage deviceFileStorage = device.getFileStorage();
        deviceFileStorage.addFile(new FileInfo(filename, content, content.length(), false));
    }

    public EntityInfoResponse getInfo(String id) {
        BlackoutObject blackoutObject = this.blackoutObjects.get(id);
        HashMap<String, FileInfoResponse> files = BlackoutHelpers
                .mapToFileInfoResponse(blackoutObject.getFileStorage());

        return new EntityInfoResponse(id, blackoutObject.getPosition(), blackoutObject.getHeight(),
                blackoutObject.getType(), files);
    }

    /**
     * Simulates the blackout app of one minute. The blackout objects
     * moves before the transfer of files begins so it ensures that
     * if the reciever becomes out of range for the sender it stops the
     * file transfer.
     */
    public void simulate() {
        for (BlackoutObject blackoutObject : this.blackoutObjects.values()) {
            blackoutObject.doMove();
        }

        List<FileInTransfer> stillActiveFITs = new ArrayList<FileInTransfer>();
        for (FileInTransfer fit : filesInTransfer) {
            BlackoutObject sender = fit.getSender();
            BlackoutObject reciever = fit.getReciever();

            BandwidthControl senderBandwidthControl = sender.getBandwidthControl();
            BandwidthControl receiverBandwidthControl = reciever.getBandwidthControl();

            FileStorage senderFileStorage = reciever.getFileStorage();
            FileStorage recieverFileStorage = reciever.getFileStorage();

            FileInfo transferFile = fit.getTransferFile();
            FileInfo originalFile = fit.getOriginalFile();

            if (communicableEntitiesInRange(sender.getId()).contains(reciever.getId())) {
                // Reciever is in range of sender
                if (!BlackoutHelpers.doFileTransfer(senderBandwidthControl, receiverBandwidthControl, fit)) {
                    stillActiveFITs.add(fit);
                }
            } else if (sender.canTeleport() && sender.getPosition().compareTo(Angle.fromDegrees(0)) == 0) {
                // Sender is downloading file and it teleported, so the transfer file is
                // instantly downloaded, but "t" bytes are removed from transfer file.
                BlackoutHelpers.removeTBytes(transferFile, originalFile.getFileData(), transferFile.getFileDataSize());

                BlackoutHelpers.finishUploadDownload(senderBandwidthControl, senderBandwidthControl);
            } else if (reciever.canTeleport() && reciever.getPosition().compareTo(Angle.fromDegrees(0)) == 0) {
                // Reciever teleported
                if (sender.doesOrbit()) {
                    // Sender is satellite so it does the same thing as if it was the reciever
                    BlackoutHelpers.removeTBytes(transferFile, originalFile.getFileData(),
                            transferFile.getFileDataSize());
                } else {
                    // Device is the sender so its file gets all its "t" bytes removed and the
                    // reciever cancels its download.
                    BlackoutHelpers.removeTBytes(originalFile, originalFile.getFileData(), 0);
                    senderFileStorage.removeFile(transferFile.getFileName());
                }

                BlackoutHelpers.finishUploadDownload(senderBandwidthControl, senderBandwidthControl);
            } else {
                // Reciever is no longer in range of sender
                recieverFileStorage.removeFile(transferFile.getFileName());
                BlackoutHelpers.finishUploadDownload(senderBandwidthControl, senderBandwidthControl);
            }
        }

        filesInTransfer = stillActiveFITs;
    }

    /**
     * Simulate for the specified number of minutes.
     * You shouldn't need to modify this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    /**
     * This uses a depth first search to find communicable
     * entities. To know how it works look in BlackoutHelpers
     * 
     * @param id
     * @return list of communicable entities
     */
    public List<String> communicableEntitiesInRange(String id) {
        BlackoutObject source = this.blackoutObjects.get(id);
        return BlackoutHelpers.dfsFindCommunicables(source, this.blackoutObjects);
    }

    /**
     * Sends a file from device/satellite to another device/satellite
     * (but no device to device)
     * 
     * @param fileName
     * @param fromId
     * @param toId
     * @throws VirtualFileNotFoundException       if file to be sent is not to be
     *                                            found in sender's file storage
     *                                            or it is in sender's file storage
     *                                            but its being downloaded
     * @throws VirtualFileAlreadyExistsException  if file to be send already exists
     *                                            in reciever's file storage
     * @throws VirtualFileNoStorageSpaceException if reciever does not have a file
     *                                            storage or reciever has file
     *                                            storage and has reach max capacity
     *                                            on either the number of files
     *                                            it can store or the number of
     *                                            bytes it can store
     * @throws VirtualFileNoBandwidthException    if either the sender does not have
     *                                            enough upload bandwidth or the
     *                                            reciever does not have enought
     *                                            download bandwidth
     */
    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        if (!this.communicableEntitiesInRange(fromId).contains(toId)) {
            throw new FileTransferException("Not in range");
        }

        BlackoutObject sender = this.blackoutObjects.get(fromId);
        BlackoutObject receiver = this.blackoutObjects.get(toId);

        FileStorage senderFileStorage = sender.getFileStorage();
        FileStorage receiverFileStorage = receiver.getFileStorage();

        FileInfo originalFile = senderFileStorage.getFile(fileName);

        if (originalFile == null) {
            throw new VirtualFileNotFoundException(fileName);
        }

        if (originalFile.isInTransfer()) {
            throw new VirtualFileNotFoundException(fileName);
        }

        if (receiverFileStorage == null) {
            throw new VirtualFileNoStorageSpaceException("Max Files Reached");
        }

        if (receiverFileStorage.fileStorageContainsFile(fileName)) {
            throw new VirtualFileAlreadyExistsException(fileName);
        }

        if (receiverFileStorage.willExceedMaxFilesStored(1)) {
            throw new VirtualFileNoStorageSpaceException("Max Files Reached");
        }

        if (receiverFileStorage.willExceedMaxStorageSize(originalFile.getFileSize())) {
            throw new VirtualFileNoStorageSpaceException("Max Storage Reached");
        }

        BandwidthControl senderBandwidthControl = sender.getBandwidthControl();
        BandwidthControl recieverBandwidthControl = receiver.getBandwidthControl();

        if (senderBandwidthControl != null && !senderBandwidthControl.initiateUpload()) {
            throw new VirtualFileNoBandwidthException(fromId);
        }

        if (recieverBandwidthControl != null && !recieverBandwidthControl.initiateDownload()) {
            if (senderBandwidthControl != null) {
                senderBandwidthControl.endUpload();
            }
            throw new VirtualFileNoBandwidthException(toId);
        }

        FileInfo transferFile = new FileInfo(originalFile.getFileName(), "", originalFile.getFileSize(), true);

        receiverFileStorage.addFile(transferFile);

        filesInTransfer.add(new FileInTransfer(sender, receiver, originalFile, transferFile));
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        if (isMoving) {
            DeviceFactory deviceFactory = new DeviceFactory();
            this.blackoutObjects.put(deviceId, deviceFactory.createNewMovingDevice(deviceId, position, type));
        } else {
            createDevice(deviceId, type, position);
        }
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }

}
