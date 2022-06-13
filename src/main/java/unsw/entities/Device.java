package unsw.entities;

import unsw.utils.Angle;

public class Device extends Communicator {
    private String type;

    public Device(String id, int maxRange, Angle position, String type) {
        super(id, maxRange, position);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
