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

    public void simulate() {
        for (BlackoutObject blackoutObject : this.blackoutObjects.values()) {
            blackoutObject.doMove();
        }

        // Do transfer files
        List<FileInTransfer> stillActiveFITs = new ArrayList<FileInTransfer>();
        for (FileInTransfer fit : filesInTransfer) {
            // First check if communicable, if not 2 cases, either sender or reciever
            // teleported or just moved out of range
            BlackoutObject sender = fit.getSender();
            BlackoutObject reciever = fit.getReciever();

            BandwidthControl senderBandwidthControl = sender.getBandwidthControl();
            BandwidthControl receiverBandwidthControl = reciever.getBandwidthControl();

            FileStorage senderFileStorage = reciever.getFileStorage();
            FileStorage recieverFileStorage = reciever.getFileStorage();

            FileInfo transferFile = fit.getTransferFile();
            FileInfo originalFile = fit.getOriginalFile();

            if (communicableEntitiesInRange(sender.getId()).contains(reciever.getId())) {
                // do file transfer
                int transferRate = senderBandwidthControl.getMaxTransferRate(receiverBandwidthControl);
                fit.startTransfer(transferRate);
                if (fit.isCompleted()) {
                    senderBandwidthControl.endUpload();
                    receiverBandwidthControl.endDownload();
                } else {
                    stillActiveFITs.add(fit);
                }
            } else if (sender.canTeleport() && sender.getPosition().compareTo(Angle.fromDegrees(0)) == 0) {
                BlackoutHelpers.removeTBytes(transferFile);
                senderBandwidthControl.endUpload();
                receiverBandwidthControl.endDownload();
            } else if (reciever.canTeleport() && reciever.getPosition().compareTo(Angle.fromDegrees(0)) == 0) {
                // Reciever teleported
                // Sender must be either satellite or device
                // if sender is satellite same behaviour as above
                if (sender.doesOrbit()) {
                    BlackoutHelpers.removeTBytes(transferFile);
                } else {
                    BlackoutHelpers.removeTBytes(originalFile);
                    senderFileStorage.removeFile(transferFile.getFileName());
                }
                senderBandwidthControl.endUpload();
                receiverBandwidthControl.endDownload();
            } else {
                // out of range
                recieverFileStorage.removeFile(transferFile.getFileName());
                receiverBandwidthControl.endDownload();
                senderBandwidthControl.endUpload();
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

    public List<String> communicableEntitiesInRange(String id) {
        BlackoutObject source = this.blackoutObjects.get(id);
        return BlackoutHelpers.dfsFindCommunicables(source, this.blackoutObjects);
    }

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

        if (!senderBandwidthControl.initiateUpload()) {
            throw new VirtualFileNoBandwidthException(fromId);
        }

        if (!recieverBandwidthControl.initiateDownload()) {
            senderBandwidthControl.endUpload();
            throw new VirtualFileNoBandwidthException(toId);
        }

        FileInfo transferFile = new FileInfo(originalFile.getFileName(), "", originalFile.getFileSize(), true);

        receiverFileStorage.addFile(transferFile);

        filesInTransfer.add(new FileInTransfer(sender, receiver, originalFile, transferFile));
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }

}
