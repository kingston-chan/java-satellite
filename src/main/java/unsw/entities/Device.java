package unsw.entities;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import unsw.blackout.FileTransferException.VirtualFileAlreadyExistsException;
import unsw.interfaces.ManageFiles;

public abstract class Device implements ManageFiles {
    private String id;
    private int range;
    private Angle position;
    private String type;
    private HashMap<String, FileInfo> files;

    /**
     * Creates a new instance of device. This should 
     * not be called when creating a device. To create
     * a device use the device factory methods.
     * @param type      type of device
     * @param range     max range it is able to communicate to
     */
    public Device(String type, int range) {
        this.type = type;
        this.id = type;
        this.range = range;
        this.position = null;
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public Angle getPosition() {
        return position;
    }


    public void setPosition(Angle position) {
        this.position = position;
    }

    public int getRange() {
        return this.range;
    }

    public String getType() {
        return type;
    }
}
