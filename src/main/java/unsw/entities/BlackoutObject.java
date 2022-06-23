package unsw.entities;

import unsw.entities.filemanagement.FileStorage;
import unsw.entities.other.BandwidthControl;
import unsw.interfaces.MoveBehavior;
import unsw.utils.Angle;
import java.util.HashMap;

/**
 * BlackoutObjects are objects that can be created via the blackout
 * controller. These are devices and satellites. Satellites orbit Jupiter
 * and devices stay on Jupiter's surface. There are different types of devices
 * and satellites, each with different characteristics. Most devices and
 * satellites can send/recieve/store files.
 * 
 * @author Kingston Chan
 */
public abstract class BlackoutObject {
    private String id;
    private int range;
    private Angle position;
    private double height;
    private int linearSpeed;
    private HashMap<String, Boolean> objectsSupported = new HashMap<>();
    private FileStorage fileStorage;
    private BandwidthControl bandwidthControl;
    private MoveBehavior moveBehavior;

    /**
     * Creates a new blackout object. This should not be used
     * when creating a satellite or device, use the SatelliteFactory
     * or DeviceFactory class to create a new satellite/device.
     */
    public BlackoutObject(MoveBehavior moveBehavior) {
        objectsSupported.put("HandheldDevice", true);
        objectsSupported.put("LaptopDevice", true);
        objectsSupported.put("DesktopDevice", true);
        objectsSupported.put("StandardSatellite", true);
        objectsSupported.put("TeleportingSatellite", true);
        objectsSupported.put("RelaySatellite", true);
        this.moveBehavior = moveBehavior;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the max range it can communicate with
     * other blackout object
     * 
     * @return max range of blackout object
     */
    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public Angle getPosition() {
        return position;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    /**
     * Gets the height in km from the centre of Jupiter of the blackout object
     * 
     * @return height in km from centre of Jupiter
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the height in km from the centre of Jupiter of the blackout object
     * 
     * @param height in km from the centre of Jupiter
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Sets the linear speed (km/min) of blackout object
     * 
     * @param linearSpeed speed of blackout object
     */
    public void setLinearSpeed(int linearSpeed) {
        this.linearSpeed = linearSpeed;
    }

    /**
     * Gets the linear speed (km/min) of blackout object
     * 
     * @return linear speed of blackout object
     */
    public int getLinearSpeed() {
        return this.linearSpeed;
    }

    /**
     * Removes support (communicate with) for the given blackout
     * object type
     * 
     * @param type of blackout object
     */
    public void removeSupport(String objectType) {
        this.objectsSupported.replace(objectType, false);
    }

    /**
     * Some blackout objects only support specific objects, i.e. Standard
     * satellites can only communicate with Handheld and Laptop devices,
     * Devices cannot communicate with each other.
     * 
     * @param deviceType the type of blackout object
     * @return whether blackout object supports other blackout object
     */
    public boolean doesSupport(String blackoutObjectType) {
        return this.objectsSupported.get(blackoutObjectType);
    }

    /**
     * Sets a file storage for the blackout object
     * 
     * @param fileStorage for the blackout object
     */
    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    /**
     * Gets the file storage of the blackout object
     * 
     * @return file storage of blackout object, null if blackout object does not
     *         store files
     */
    public FileStorage getFileStorage() {
        return this.fileStorage;
    }

    /**
     * Sets a bandwidth control for the blackout object
     * 
     * @param bandwidthControl for the blackout object
     */
    public void setBandwidthControl(BandwidthControl bandwidthControl) {
        this.bandwidthControl = bandwidthControl;
    }

    /**
     * Gets the bandwidth limitations of the blackout object
     * 
     * @return bandwidth control of blackout object
     */
    public BandwidthControl getBandwidthControl() {
        return this.bandwidthControl;
    }

    /**
     * Simulates the movement of a blackout object for one minute.
     */
    public void doMove() {
        this.position = moveBehavior.move(this.position, this.linearSpeed / this.height);
    }

    /**
     * Gets the type of blackout object i.e. StandardSatellite, HandheldDevice
     * 
     * @return type of blackout object
     */
    public abstract String getType();

    /**
     * Checks whether the blackout object can teleport or not
     * 
     * @return whether it can teleport
     */
    public abstract boolean canTeleport();

    /**
     * Checks whether the blackout object can acts as a range extender for other
     * blackout objects
     * 
     * @return whether it can extend range for other blackout objects
     */
    public abstract boolean canExtendRange();

    /**
     * Checks whether the blackout object orbits jupiter or not
     * 
     * @return whether the blackout object orbits jupiter
     */
    public abstract boolean doesOrbit();
}
