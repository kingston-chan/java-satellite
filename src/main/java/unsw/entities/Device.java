package unsw.entities;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import unsw.blackout.FileTransferException.VirtualFileAlreadyExistsException;

public class Device extends Communicator implements FileStorage {
    public static final int HANDHELD_RANGE = 50000;
    public static final int LAPTOP_RANGE = 100000;
    public static final int DESKTOP_RANGE = 200000;

    private String type;
    private HashMap<String, FileInfo> files;

    public Device(String id, int range, Angle position, String type) {
        super(id, range, position, MathsHelper.RADIUS_OF_JUPITER);
        this.type = type;
        this.files = new HashMap<String, FileInfo>();
    }

    public String getType() {
        return type;
    }

    public HashMap<String, FileInfo> getMappedFiles() {
        return this.files;
    }

    @Override
    public List<FileInfo> getFiles() {
        return new ArrayList<>(this.files.values());
    }

    @Override
    public void addFile(FileInfo file) throws VirtualFileAlreadyExistsException {
        for (FileInfo deviceFile : this.files.values()) {
            if (file.equals(deviceFile)) {
                throw new VirtualFileAlreadyExistsException(file.getFileName());
            }
        }

        this.files.put(file.getFileName(), file);
    }

    @Override
    public void removeFile(String fileName) {
        this.files.remove(fileName);
    }

    @Override
    public FileInfo getFile(String fileName) {
        return this.files.get(fileName);
    }
}
