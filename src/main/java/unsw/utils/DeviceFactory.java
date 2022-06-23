package unsw.utils;

import unsw.entities.BlackoutObject;
import unsw.entities.devices.DesktopDevice;
import unsw.entities.devices.HandheldDevice;
import unsw.entities.devices.LaptopDevice;
import unsw.entities.devices.MovingDesktopDevice;
import unsw.entities.devices.MovingHandheldDevice;
import unsw.entities.devices.MovingLaptopDevice;
import unsw.entities.movement.Immobile;

import java.util.HashMap;

/**
 * DeviceFactory helps create new devices by giving a type.
 * To add more types, just add into the hashmap in constructor
 */
public class DeviceFactory {
    private HashMap<String, BlackoutObject> deviceFactory = new HashMap<String, BlackoutObject>();

    public DeviceFactory() {
        this.deviceFactory.put("HandheldDevice", new HandheldDevice(new Immobile()));
        this.deviceFactory.put("LaptopDevice", new LaptopDevice(new Immobile()));
        this.deviceFactory.put("DesktopDevice", new DesktopDevice(new Immobile()));
        this.deviceFactory.put("MovingHandheldDevice", new MovingHandheldDevice());
        this.deviceFactory.put("MovingLaptopDevice", new MovingLaptopDevice());
        this.deviceFactory.put("MovingDesktopDevice", new MovingDesktopDevice());
    }

    /**
     * Creates a new device type
     * 
     * @param id       name of device
     * @param position of device
     * @param type     of device
     * @return a new given type of device
     */
    public BlackoutObject createNewDevice(String id, Angle position, String type) {
        BlackoutObject newDevice = this.deviceFactory.get(type);
        newDevice.setId(id);
        newDevice.setPosition(position);
        return newDevice;
    }

    /**
     * Creates a new moving device type
     * 
     * @param id       name of device
     * @param position of device
     * @param type     of device
     * @return a new moving given type of device
     */
    public BlackoutObject createNewMovingDevice(String id, Angle position, String type) {
        BlackoutObject newMovingDevice = this.deviceFactory.get("Moving" + type);
        newMovingDevice.setId(id);
        newMovingDevice.setPosition(position);
        return newMovingDevice;
    }
}
