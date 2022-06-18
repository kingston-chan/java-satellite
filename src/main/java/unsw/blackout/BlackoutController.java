package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unsw.blackout.FileTransferException.VirtualFileAlreadyExistsException;
import unsw.blackout.FileTransferException.VirtualFileNotFoundException;
import unsw.blackout.FileTransferException.VirtualFileNoStorageSpaceException;

import unsw.utils.Angle;
import unsw.entities.Device;
import unsw.entities.FileInTransfer;
import unsw.entities.RelaySatellite;
import unsw.entities.Communicator;
import unsw.entities.Satellite;
import unsw.entities.StandardSatellite;
import unsw.entities.TeleportingSatellite;
import unsw.entities.FileInfo;
import unsw.entities.FileTransferSatellite;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class BlackoutController {

    private HashMap<String, Communicator> activeCommunicators;
    private List<FileInTransfer> filesInTransfer;

    public BlackoutController() {
        this.activeCommunicators = new HashMap<String, Communicator>();
        this.filesInTransfer = new ArrayList<FileInTransfer>();
    }

    public void createDevice(String deviceId, String type, Angle position) {
        switch (type) {
            case "HandheldDevice":
                this.activeCommunicators.put(deviceId, new Device(deviceId, Device.HANDHELD_RANGE, position, type));
                break;
            case "LaptopDevice":
                this.activeCommunicators.put(deviceId, new Device(deviceId, Device.LAPTOP_RANGE, position, type));
                break;
            case "DesktopDevice":
                this.activeCommunicators.put(deviceId, new Device(deviceId, Device.DESKTOP_RANGE, position, type));
                break;
        }
    }

    public void removeDevice(String deviceId) {
        this.activeCommunicators.remove(deviceId);
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        switch (type) {
            case "StandardSatellite":
                this.activeCommunicators.put(satelliteId, new StandardSatellite(satelliteId, position, height));
                break;
            case "TeleportingSatellite":
                this.activeCommunicators.put(satelliteId, new TeleportingSatellite(satelliteId, position, height));
                break;
            case "RelaySatellite":
                this.activeCommunicators.put(satelliteId, new RelaySatellite(satelliteId, position, height));
                break;
        }
    }

    public void removeSatellite(String satelliteId) {
        this.activeCommunicators.remove(satelliteId);
    }

    public List<String> listDeviceIds() {
        List<String> deviceIds = new ArrayList<String>();
        for (Map.Entry<String, Communicator> c : this.activeCommunicators.entrySet()) {
            if (c.getValue() instanceof Device) {
                deviceIds.add(c.getKey());
            }
        }
        return deviceIds;
    }

    public List<String> listSatelliteIds() {
        List<String> satelliteIds = new ArrayList<String>();
        for (Map.Entry<String, Communicator> c : this.activeCommunicators.entrySet()) {
            if (c.getValue() instanceof Satellite) {
                satelliteIds.add(c.getKey());
            }
        }
        return satelliteIds;
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        Device device = BlackoutHelpers.toDevice(this.activeCommunicators.get(deviceId));
        device.getMappedFiles().put(filename, new FileInfo(filename, content, content.length(), false));
    }

    public EntityInfoResponse getInfo(String id) {
        HashMap<String, FileInfoResponse> files = new HashMap<String, FileInfoResponse>();
        EntityInfoResponse entityInfo;

        Communicator communicator = this.activeCommunicators.get(id);
        if (communicator instanceof Device) {
            Device device = BlackoutHelpers.toDevice(communicator);
            for (FileInfo file : device.getFiles()) {
                files.put(file.getFileName(), new FileInfoResponse(file.getFileName(), file.getFileData(),
                        file.getFileDataSize(), !file.isInTransfer()));
            }
            entityInfo = new EntityInfoResponse(id, device.getPosition(), RADIUS_OF_JUPITER, device.getType(),
                    files);
        } else {
            if (communicator instanceof FileTransferSatellite) {
                FileTransferSatellite satellite = (FileTransferSatellite) communicator;
                for (FileInfo file : satellite.getFiles()) {
                    files.put(file.getFileName(), new FileInfoResponse(file.getFileName(), file.getFileData(),
                            file.getFileDataSize(), !file.isInTransfer()));
                }
                entityInfo = new EntityInfoResponse(id, satellite.getPosition(), satellite.getHeight(),
                        satellite.getType(),
                        files);
            } else {
                Satellite satellite = (Satellite) communicator;
                entityInfo = new EntityInfoResponse(id, satellite.getPosition(), satellite.getHeight(),
                        satellite.getType(),
                        files);
            }
        }

        return entityInfo;
    }

    public void simulate() {
        for (Communicator comm : this.activeCommunicators.values()) {
            if (comm instanceof Satellite) {
                BlackoutHelpers.toSatellite(comm).move();
            }
        }

        List<FileInTransfer> stillActiveFileIT = new ArrayList<FileInTransfer>();

        for (FileInTransfer fit : this.filesInTransfer) {
            // check if the two communicators are communicable
            if (fit.getSender() instanceof Device) {
                Device sender = BlackoutHelpers.toDevice(fit.getSender());
                FileTransferSatellite reciever = BlackoutHelpers.toFileTransferSatellite(fit.getReciever());
                if (BlackoutHelpers.isCommunicableFromDevToSat(sender, reciever, this.activeCommunicators)) {
                    // Increase the transfer file's string by recievers download bandwidth and lower
                    // bandwidth if needed
                    if (fit.getTransferRate() == 0) {
                        fit.setTransferRate(reciever.getDownloadBandwidth());
                    } else if (fit.getTransferRate() > reciever.getDownloadBandwidth()) {
                        fit.setTransferRate(reciever.getDownloadBandwidth());
                    }

                    fit.startTransfer();

                    if (!fit.isCompleted()) {
                        stillActiveFileIT.add(fit);
                    }
                } else if (reciever instanceof TeleportingSatellite) {
                    TeleportingSatellite tpRec = (TeleportingSatellite) reciever;
                    if (tpRec.getPosition().compareTo(Angle.fromDegrees(0)) == 0) {
                        // TeleportingSatellite tp'ed and is reciever
                        // Remove file from tp satellite
                        FileInfo originalFile = fit.getOriginalFile();
                        tpRec.removeFile(originalFile.getFileName());
                        // Remove t bytes from sender's file
                        originalFile.setFileData(originalFile.getFileData().replaceAll("t", ""));
                        originalFile.updateFileDataSize();
                    }
                }
            } else if (fit.getSender() instanceof TeleportingSatellite) {
                // Check whether reciever is satellite or device
                TeleportingSatellite sender = (TeleportingSatellite) fit.getSender();
                if (fit.getReciever() instanceof Device) {
                    Device reciever = BlackoutHelpers.toDevice(fit.getReciever());
                    if (BlackoutHelpers.isCommunicableFromSatToDev(reciever, sender, this.activeCommunicators)) {
                        if (fit.getTransferRate() == 0) {
                            fit.setTransferRate(sender.getUploadBandwidth());
                        } else if (fit.getTransferRate() > sender.getUploadBandwidth()) {
                            fit.setTransferRate(sender.getUploadBandwidth());
                        }

                        fit.startTransfer();

                        if (!fit.isCompleted()) {
                            stillActiveFileIT.add(fit);
                        }
                    } else if (sender.getPosition().compareTo(Angle.fromDegrees(0)) == 0) {
                        FileInfo originalFile = fit.getOriginalFile();
                        FileInfo transferFile = fit.getTransferFile();
                        String removedTBytes = originalFile.getFileData().replaceAll("t", "");
                        transferFile.setFileData(removedTBytes);
                        transferFile.updateFileDataSize();
                        transferFile.setTransferCompleted();
                    }
                } else {
                    FileTransferSatellite reciever = (FileTransferSatellite) fit.getReciever();
                    if (BlackoutHelpers.isCommunicableFromSatToSat(sender, reciever, this.activeCommunicators)) {
                        int transferRate = Math.min(sender.getUploadBandwidth(), reciever.getDownloadBandwidth());
                        if (fit.getTransferRate() == 0) {
                            fit.setTransferRate(transferRate);
                        } else if (fit.getTransferRate() > transferRate) {
                            fit.setTransferRate(transferRate);
                        }

                        fit.startTransfer();

                        if (!fit.isCompleted()) {
                            stillActiveFileIT.add(fit);
                        }
                    } else if (sender.getPosition().compareTo(Angle.fromDegrees(0)) == 0) {
                        FileInfo originalFile = fit.getOriginalFile();
                        FileInfo transferFile = fit.getTransferFile();
                        String removedTBytes = originalFile.getFileData().replaceAll("t", "");
                        transferFile.setFileData(removedTBytes);
                        transferFile.updateFileDataSize();
                        transferFile.setTransferCompleted();
                    }
                }
            } else {
                StandardSatellite sender = (StandardSatellite) fit.getSender();
                if (fit.getReciever() instanceof Device) {
                    Device reciever = BlackoutHelpers.toDevice(fit.getReciever());
                    if (BlackoutHelpers.isCommunicableFromSatToDev(reciever, sender, this.activeCommunicators)) {
                        if (fit.getTransferRate() == 0) {
                            fit.setTransferRate(sender.getUploadBandwidth());
                        }

                        fit.startTransfer();

                        if (!fit.isCompleted()) {
                            stillActiveFileIT.add(fit);
                        }
                    }
                } else {
                    FileTransferSatellite reciever = (FileTransferSatellite) fit.getReciever();
                    if (BlackoutHelpers.isCommunicableFromSatToSat(sender, reciever, this.activeCommunicators)) {
                        int transferRate = Math.min(sender.getUploadBandwidth(), reciever.getDownloadBandwidth());
                        if (fit.getTransferRate() == 0) {
                            fit.setTransferRate(transferRate);
                        } else if (fit.getTransferRate() > transferRate) {
                            fit.setTransferRate(transferRate);
                        }

                        fit.startTransfer();

                        if (!fit.isCompleted()) {
                            stillActiveFileIT.add(fit);
                        }
                    } else if (reciever instanceof TeleportingSatellite) {
                        if (reciever.getPosition().compareTo(Angle.fromDegrees(0)) == 0) {
                            FileInfo originalFile = fit.getOriginalFile();
                            FileInfo transferFile = fit.getTransferFile();
                            String removedTBytes = originalFile.getFileData().replaceAll("t", "");
                            transferFile.setFileData(removedTBytes);
                            transferFile.updateFileDataSize();
                            transferFile.setTransferCompleted();
                        }
                    }
                }
            }
        }
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
        Satellite satellite;
        Device device;

        List<String> communicables = new ArrayList<String>();
        Communicator communicator = this.activeCommunicators.get(id);

        if (communicator instanceof Device) {
            device = BlackoutHelpers.toDevice(communicator);
            for (String satId : this.listSatelliteIds()) {
                satellite = BlackoutHelpers.toSatellite(this.activeCommunicators.get(satId));
                if (BlackoutHelpers.isCommunicableFromDevToSat(device, satellite, this.activeCommunicators)) {
                    communicables.add(satellite.getId());
                }
            }
        } else {
            satellite = BlackoutHelpers.toSatellite(this.activeCommunicators.get(id));

            for (Map.Entry<String, Communicator> c : this.activeCommunicators.entrySet()) {
                if (c.getValue() instanceof Device) {
                    if (BlackoutHelpers.isCommunicableFromSatToDev(BlackoutHelpers.toDevice(c.getValue()), satellite,
                            this.activeCommunicators)) {
                        communicables.add(c.getValue().getId());
                    }
                } else {
                    Satellite otherSat = BlackoutHelpers.toSatellite(c.getValue());

                    if (otherSat != satellite) {
                        if (BlackoutHelpers.isCommunicableFromSatToSat(satellite, otherSat, this.activeCommunicators)) {
                            communicables.add(otherSat.getId());
                        }
                    }
                }
            }
        }

        return communicables;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        if (!this.communicableEntitiesInRange(fromId).contains(toId)) {
            throw new FileTransferException("Not in range");
        }

        Communicator sender = this.activeCommunicators.get(fromId);
        Communicator reciever = this.activeCommunicators.get(toId);

        EntityInfoResponse senderInfo = this.getInfo(fromId);
        EntityInfoResponse recieverInfo = this.getInfo(toId);

        if (!senderInfo.getFiles().containsKey(fileName)) {
            throw new VirtualFileNotFoundException(fileName);
        }

        if (!senderInfo.getFiles().get(fileName).hasTransferCompleted()) {
            throw new VirtualFileNotFoundException(fileName);
        }

        if (recieverInfo.getFiles().containsKey(fileName)) {
            throw new VirtualFileAlreadyExistsException(fileName);
        }

        if (reciever instanceof RelaySatellite) {
            throw new VirtualFileNoStorageSpaceException("Max Storage Reached");
        }

        if (sender instanceof Device) {
            FileTransferSatellite recSat = (FileTransferSatellite) reciever;
            Device sendDev = (Device) sender;
            FileInfo originalFile = sendDev.getFile(fileName);
            FileInfo sentFile = new FileInfo(originalFile.getFileName(), "", originalFile.getFileDataSize(), true);
            recSat.addFile(sentFile);
            filesInTransfer.add(new FileInTransfer(sender, reciever, originalFile, sentFile));
        }

        if (sender instanceof FileTransferSatellite) {
            FileTransferSatellite sendSat = (FileTransferSatellite) sender;
            FileInfo originalFile = sendSat.getFile(fileName);
            FileInfo sentFile = new FileInfo(originalFile.getFileName(), "", originalFile.getFileDataSize(), true);

            if (reciever instanceof Device) {
                Device recDev = BlackoutHelpers.toDevice(reciever);
                recDev.addFile(sentFile);
            }

            if (reciever instanceof FileTransferSatellite) {
                FileTransferSatellite recSat = (FileTransferSatellite) reciever;
                recSat.addFile(sentFile);
            }

            filesInTransfer.add(new FileInTransfer(sender, reciever, originalFile, sentFile));
            sendSat.startUpload();
        }
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
