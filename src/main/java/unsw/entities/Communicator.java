package unsw.entities;

import java.util.ArrayList;
import java.util.List;

import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public class Communicator {
    private String id;
    private int maxRange;
    private Angle position;
    private List<FileInfoResponse> files;

    public Communicator(String id, int maxRange, Angle position) {
        this.id = id;
        this.maxRange = maxRange;
        this.position = position;
        this.files = new ArrayList<FileInfoResponse>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }

    public Angle getPosition() {
        return position;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public void setFiles(List<FileInfoResponse> files) {
        this.files = files;
    }

    public List<FileInfoResponse> getFiles() {
        return files;
    }

    public void addFile(FileInfoResponse file) {
        this.files.add(file);
    }

}
