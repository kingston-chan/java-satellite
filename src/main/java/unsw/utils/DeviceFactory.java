package unsw.utils;

import unsw.entities.DesktopDevice;
import unsw.entities.Device;
import unsw.entities.HandheldDevice;
import unsw.entities.LaptopDevice;

import java.util.HashMap;

public class DeviceFactory {
    private HashMap<String, Device> deviceFactory = new HashMap<String, Device>();

    public DeviceFactory() {
        this.deviceFactory.put("HandheldDevice", new HandheldDevice());
        this.deviceFactory.put("LaptopDevice", new LaptopDevice());
        this.deviceFactory.put("DesktopDevice", new DesktopDevice());
    }

    public Device createNewDevice(String id, Angle position, String type) {
        Device newDevice = deviceFactory.get(type);
        newDevice.setId(id);
        newDevice.setPosition(position);
        return newDevice;
    }
}
