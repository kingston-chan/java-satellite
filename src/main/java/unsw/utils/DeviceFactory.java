package unsw.utils;

import unsw.entities.BlackoutObject;
import unsw.entities.devices.DesktopDevice;
import unsw.entities.devices.HandheldDevice;
import unsw.entities.devices.LaptopDevice;

import java.util.HashMap;

/**
 * DeviceFactory helps create new devices by giving a type.
 * To add more types, just add into the hashmap in constructor
 */
public class DeviceFactory {
    private HashMap<String, BlackoutObject> deviceFactory = new HashMap<String, BlackoutObject>();

    public DeviceFactory() {
        this.deviceFactory.put("HandheldDevice", new HandheldDevice());
        this.deviceFactory.put("LaptopDevice", new LaptopDevice());
        this.deviceFactory.put("DesktopDevice", new DesktopDevice());
    }

    /**
     * Returns a new device type
     * 
     * @param id       name of device
     * @param position of device
     * @param type     of device
     * @return a new given type of device
     */
    public BlackoutObject createNewDevice(String id, Angle position, String type) {
        BlackoutObject newDevice = deviceFactory.get(type);
        newDevice.setId(id);
        newDevice.setPosition(position);
        return newDevice;
    }
}
