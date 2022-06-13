package unsw.blackout;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;

import unsw.blackout.FileTransferException.VirtualFileAlreadyExistsException;
import unsw.blackout.FileTransferException.VirtualFileNoBandwidthException;
import unsw.blackout.FileTransferException.VirtualFileNoStorageSpaceException;
import unsw.blackout.FileTransferException.VirtualFileNotFoundException;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;
import unsw.entities.Device;
import unsw.entities.RelaySatellite;
import unsw.entities.Communicator;
import unsw.entities.Satellite;
import unsw.entities.StandardSatellite;
import unsw.entities.TeleportingSatellite;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class BlackoutController {

    private HashMap<String, Communicator> activeCommunicators;

    public BlackoutController() {
        this.activeCommunicators = new HashMap<String, Communicator>();
    }

    private boolean isCommunicable(Device device, Satellite satellite) {
        boolean inRange = MathsHelper.isVisible(satellite.getHeight(), satellite.getPosition(),
                device.getPosition());
        boolean isSupported = satellite.supports(device);

        return inRange && isSupported;
    }

    private void sendFileToTargetSatellite(Satellite targetSat, FileInfoResponse file) throws FileTransferException {
        int maxFilesSupported = targetSat.getMaxFiles();
        int currFiles = targetSat.getFiles().size();
        int currSpace = targetSat.getAvailFileStorage();

        if (currFiles + 1 > maxFilesSupported && maxFilesSupported >= 0) {
            throw new VirtualFileNoStorageSpaceException("Max Files Reached");
        }

        if (currSpace < file.getFileSize()) {
            throw new VirtualFileNoStorageSpaceException("Max Storage Reached");
        }

        int newDownloadCount = targetSat.getDownloadCount() + 1;

        if ((targetSat.getDownloadSpeed() / (newDownloadCount)) == 0) {
            throw new VirtualFileNoBandwidthException(targetSat.getId());
        }

        targetSat.addFile(new FileInfoResponse(file.getFilename(), "", file.getFileSize(), false));

        targetSat.setDownloadCount(newDownloadCount);

        targetSat.setAvailFileStorage(targetSat.getAvailFileStorage() - file.getFileSize());
    }

    public void createDevice(String deviceId, String type, Angle position) {
        switch (type) {
            case "HandheldDevice":
                this.activeCommunicators.put(deviceId, new Device(deviceId, 50000, position, "HandheldDevice"));
                break;
            case "LaptopDevice":
                this.activeCommunicators.put(deviceId, new Device(deviceId, 100000, position, "LaptopDevice"));
                break;
            case "DesktopDevice":
                this.activeCommunicators.put(deviceId, new Device(deviceId, 200000, position, "DesktopDevice"));
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
                boolean startClockwise = (position.compareTo(Angle.fromDegrees(140)) >= 0
                        && position.compareTo(Angle.fromDegrees(190)) <= 0)
                        || (position.compareTo(Angle.fromDegrees(345)) >= 0
                                && position.compareTo(Angle.fromDegrees(140)) < 0);

                this.activeCommunicators.put(satelliteId,
                        new RelaySatellite(satelliteId, position, startClockwise, height));
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
        this.activeCommunicators.get(deviceId).addFile(new FileInfoResponse(filename, content, content.length(), true));
    }

    public EntityInfoResponse getInfo(String id) {
        HashMap<String, FileInfoResponse> files = new HashMap<String, FileInfoResponse>();
        EntityInfoResponse entityInfo;

        Communicator communicator = this.activeCommunicators.get(id);
        if (communicator instanceof Device) {
            Device device = (Device) communicator;
            for (FileInfoResponse file : device.getFiles()) {
                files.put(file.getFilename(), file);
            }
            entityInfo = new EntityInfoResponse(id, device.getPosition(), RADIUS_OF_JUPITER, device.getType(),
                    files);
        } else {
            Satellite satellite = (Satellite) communicator;
            for (FileInfoResponse file : satellite.getFiles()) {
                files.put(file.getFilename(), file);
            }
            entityInfo = new EntityInfoResponse(id, satellite.getPosition(), satellite.getHeight(), satellite.getType(),
                    files);
        }

        return entityInfo;
    }

    public void simulate() {
        // TODO: Task 2a)
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
            device = (Device) communicator;
            for (String satId : this.listSatelliteIds()) {
                satellite = (Satellite) this.activeCommunicators.get(satId);
                if (isCommunicable(device, satellite)) {
                    communicables.add(satellite.getId());
                }
            }
        } else {
            satellite = (Satellite) this.activeCommunicators.get(id);

            for (Map.Entry<String, Communicator> c : this.activeCommunicators.entrySet()) {
                if (c.getValue() instanceof Device) {
                    if (isCommunicable((Device) c.getValue(), satellite)) {
                        communicables.add(c.getValue().getId());
                    }
                } else {
                    Satellite otherSat = (Satellite) c.getValue();

                    if (otherSat != satellite) {
                        if (MathsHelper.isVisible(satellite.getHeight(), satellite.getPosition(), otherSat.getHeight(),
                                otherSat.getPosition())) {
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

        if (recieverInfo.getFiles().containsKey(fileName)) {
            throw new VirtualFileAlreadyExistsException(fileName);
        }

        FileInfoResponse file = senderInfo.getFiles().get(fileName);

        if (sender instanceof Satellite) {
            Satellite senderSat = (Satellite) sender;

            // Check if sender satellite has enough upload bandwidth
            int newUploadCount = senderSat.getUploadCount() + 1;

            if ((senderSat.getUploadSpeed() / (newUploadCount)) == 0) {
                throw new VirtualFileNoBandwidthException(fromId);
            }

            if (reciever instanceof Device) {
                senderSat.setUploadCount(newUploadCount);

                Device device = (Device) reciever;

                device.addFile(new FileInfoResponse(fileName, "", file.getFileSize(), false));
            } else {
                Satellite targetSat = (Satellite) reciever;

                sendFileToTargetSatellite(targetSat, file);

                senderSat.setUploadCount(newUploadCount);
            }

        } else {
            Satellite targetSat = (Satellite) reciever;

            sendFileToTargetSatellite(targetSat, file);
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
