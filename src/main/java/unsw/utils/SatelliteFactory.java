package unsw.utils;

import java.util.HashMap;

import unsw.entities.BlackoutObject;
import unsw.entities.satellites.RelaySatellite;
import unsw.entities.satellites.StandardSatellite;
import unsw.entities.satellites.TeleportingSatellite;

/**
 * SatelliteFactory helps create new satellites by giving a type.
 * To add more types, just add into the hashmap in constructor
 */
public class SatelliteFactory {
    private HashMap<String, BlackoutObject> satelliteFactory = new HashMap<String, BlackoutObject>();

    public SatelliteFactory() {
        this.satelliteFactory.put("RelaySatellite", new RelaySatellite());
        this.satelliteFactory.put("TeleportingSatellite", new TeleportingSatellite());
        this.satelliteFactory.put("StandardSatellite", new StandardSatellite());
    }

    /**
     * Returns a new satellite type
     * 
     * @param id       name of satellite
     * @param position position of satellite
     * @param height   height of satellite from jupiter's centre
     * @param type     type of satellite
     * @return new given type of satellite
     */
    public BlackoutObject createNewSatellite(String id, Angle position, double height, String type) {
        BlackoutObject newSatellite = satelliteFactory.get(type);
        newSatellite.setId(id);
        newSatellite.setPosition(position);
        newSatellite.setHeight(height);
        return newSatellite;
    }
}
