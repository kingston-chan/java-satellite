package unsw.blackout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;
import unsw.entities.Device;
import unsw.entities.RelaySatellite;
import unsw.entities.Communicator;
import unsw.entities.Satellite;
import unsw.entities.StandardSatellite;
import unsw.entities.TeleportingSatellite;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class BlackoutController {

    private List<Communicator> activeCommunicators;

    public BlackoutController() {
        this.activeCommunicators = new ArrayList<Communicator>();
    }

    private Device getActiveDevice(String deviceId) {
        for (Communicator c : this.activeCommunicators) {
            if (c instanceof Device && c.getId() == deviceId) {
                return (Device) c;
            }
        }
        return null;
    }

    private Satellite getActiveSatellite(String satelliteId) {
        for (Communicator c : this.activeCommunicators) {
            if (c instanceof Satellite && c.getId() == satelliteId) {
                return (Satellite) c;
            }
        }
        return null;
    }

    private void removeCommunicator(String commId) {
        Iterator<Communicator> tempItr = this.activeCommunicators.iterator();
        while (tempItr.hasNext()) {
            String id = tempItr.next().getId();
            if (id == commId) {
                tempItr.remove();
            }
        }
    }

    public void createDevice(String deviceId, String type, Angle position) {
        switch (type) {
            case "HandheldDevice":
                this.activeCommunicators.add(new Device(deviceId, 50000, position, "HandheldDevice"));
                break;
            case "LaptopDevice":
                this.activeCommunicators.add(new Device(deviceId, 100000, position, "LaptopDevice"));
                break;
            case "DesktopDevice":
                this.activeCommunicators.add(new Device(deviceId, 200000, position, "DesktopDevice"));
                break;
        }
    }

    public void removeDevice(String deviceId) {
        this.removeCommunicator(deviceId);
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        switch (type) {
            case "StandardSatellite":
                this.activeCommunicators.add(new StandardSatellite(satelliteId, position, height));
                break;
            case "TeleportingSatellite":
                this.activeCommunicators.add(new TeleportingSatellite(satelliteId, position, height));
                break;
            case "RelaySatellite":
                boolean startClockwise = (position.compareTo(Angle.fromDegrees(140)) >= 0
                        && position.compareTo(Angle.fromDegrees(190)) <= 0)
                        || (position.compareTo(Angle.fromDegrees(345)) >= 0
                                && position.compareTo(Angle.fromDegrees(140)) < 0);
                this.activeCommunicators.add(new RelaySatellite(satelliteId, position, startClockwise, height));
                break;
        }
    }

    public void removeSatellite(String satelliteId) {
        this.removeCommunicator(satelliteId);
    }

    public List<String> listDeviceIds() {
        List<String> deviceIds = new ArrayList<String>();
        for (Communicator c : this.activeCommunicators) {
            if (c instanceof Device) {
                deviceIds.add(c.getId());
            }
        }
        return deviceIds;
    }

    public List<String> listSatelliteIds() {
        List<String> satelliteIds = new ArrayList<String>();
        for (Communicator c : this.activeCommunicators) {
            if (c instanceof Satellite) {
                satelliteIds.add(c.getId());
            }
        }
        return satelliteIds;
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        Device device = this.getActiveDevice(deviceId);
        device.addFile(new FileInfoResponse(filename, content, content.length(), true));
    }

    public EntityInfoResponse getInfo(String id) {
        HashMap<String, FileInfoResponse> files = new HashMap<String, FileInfoResponse>();
        EntityInfoResponse entityInfo;
        Device device = this.getActiveDevice(id);
        if (device != null) {
            for (FileInfoResponse file : device.getFiles()) {
                files.put(file.getFilename(), file);
            }
            entityInfo = new EntityInfoResponse(id, device.getPosition(), RADIUS_OF_JUPITER, device.getType(),
                    files);
        } else {
            Satellite satellite = this.getActiveSatellite(id);
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
        // TODO: Task 2 b)
        return new ArrayList<>();
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        // TODO: Task 2 c)
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
