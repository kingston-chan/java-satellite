package unsw.utils;

import java.util.HashMap;

import unsw.entities.RelaySatellite;
import unsw.entities.Satellite;
import unsw.entities.TeleportingSatellite;
import unsw.entities.StandardSatellite;

public class SatelliteFactory {
    private HashMap<String, Satellite> satelliteFactory = new HashMap<String, Satellite>();

    public SatelliteFactory() {
        this.satelliteFactory.put("RelaySatellite", new RelaySatellite());
        this.satelliteFactory.put("TeleportingSatellite", new TeleportingSatellite());
        this.satelliteFactory.put("StandardSatellite", new StandardSatellite());
    }

    public Satellite createNewSatellite(String id, Angle position, double height, String type) {
        Satellite newSatellite = satelliteFactory.get(type);
        newSatellite.setId(id);
        newSatellite.setPosition(position);
        newSatellite.setHeight(height);
        return newSatellite;
    }
}
