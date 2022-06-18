package unsw.entities;

import java.util.List;
import unsw.blackout.FileTransferException;

public interface FileStorage {
    public List<FileInfo> getFiles();

    public FileInfo getFile(String fileName);

    public void removeFile(String fileName);

    public void addFile(FileInfo file) throws FileTransferException;
}
