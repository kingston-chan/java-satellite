package unsw.entities;

import unsw.interfaces.ManageFiles;
import unsw.interfaces.Move;
import unsw.utils.Angle;
import java.util.HashMap;

public abstract class Satellite implements Move, ManageFiles {
    private String id;
    private int range;
    private Angle position;
    private double height;
    private String type;
    private HashMap<String, Boolean> devicesSupported;

    public Satellite() {
        devicesSupported = new HashMap<>();
        devicesSupported.put("HandheldDevice", true);
        devicesSupported.put("LaptopDevice", true);
        devicesSupported.put("DesktopDevice", true);
    }

    public Satellite(String type, int range) {
        this();
        this.id = type;
        this.range = range;
        this.position = null;
        this.height = 0.0;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void removeSupport(String type) {
        this.devicesSupported.remove(type);
    }

}
