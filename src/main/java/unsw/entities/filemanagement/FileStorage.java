package unsw.entities.filemanagement;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * FileStorage is used to manage the files stored on the blackout
 * object. It can add/remove/get files and do some checking on
 * whether files can be added.
 * 
 * @author Kingston Chan
 */
public class FileStorage {
    private HashMap<String, FileInfo> files = new HashMap<>();
    private int bytesUsed;
    private int maxBytesStored; // If set to -1, it has no limit
    private int maxFilesStored; // If set to -1, it has no limit

    /**
     * Creates a new file storage to manage files e.g.
     * - Add a file
     * - Remove a file
     * - Get current bytes used by all files
     */
    public FileStorage(int maxBytesStored, int maxFilesStored) {
        this.maxBytesStored = maxBytesStored;
        this.maxFilesStored = maxFilesStored;
        this.bytesUsed = 0;
    }

    /**
     * Returns a copy of the filenames in a list
     * 
     * @return List<FileInfo> copy of filenames in a list
     */
    public List<FileInfo> getFiles() {
        return new ArrayList<FileInfo>(this.files.values());
    }

    /**
     * Checks if file storage already contains given file
     * 
     * @param fileName filename of file
     * @return whether file storage already contains file
     */
    public boolean fileStorageContainsFile(String fileName) {
        return this.files.containsKey(fileName);
    }

    /**
     * Checks whether the given number of files will exceed the max
     * storage of files for file storage
     * 
     * @param numFiles number of files want to check
     * @return whether file storage allows numFiles to be stored
     */
    public boolean willExceedMaxFilesStored(int numFiles) {
        return this.getNumFiles() + numFiles > this.maxFilesStored && this.maxFilesStored >= 0;
    }

    /**
     * Checks whether the given number of bytes will exceed the max
     * bytes of file storage
     * 
     * @param bytes number of bytes want to check
     * @return whether file storage allows given bytes to be stored
     */
    public boolean willExceedMaxStorageSize(int bytes) {
        return this.getBytesUsed() + bytes > this.maxBytesStored && maxBytesStored >= 0;
    }

    /**
     * Adds a file to the file storage and increments bytes used aswell.
     * 
     * @param file to add to storage
     */
    public void addFile(FileInfo file) {
        this.bytesUsed += file.getFileSize();
        this.files.put(file.getFileName(), file);
    }

    /**
     * Removes the file from the file storage and removes the bytes used
     * by the file.
     * 
     * @param fileName in a string
     * @return boolean true on successfully removing file, otherwise false
     */
    public boolean removeFile(String fileName) {
        if (!this.files.containsKey(fileName)) {
            return false;
        }

        this.bytesUsed -= this.getFile(fileName).getFileDataSize();
        this.files.remove(fileName);
        return true;
    }

    /**
     * Gets the file corresponding to file name from the file storage
     * 
     * @param fileName in a string
     * @return FileInfo the information regarding the file
     *         and null if the file is not found
     */
    public FileInfo getFile(String fileName) {
        return this.files.get(fileName);
    }

    /**
     * Gets the number of files stored in communicator
     * 
     * @return number of files stored in communicator
     */
    public int getNumFiles() {
        return this.files.size();
    }

    /**
     * Get the number of bytes stored on the communicator
     * Bytes management is mainly used for satellites as they have a
     * storage capacity. Devices do not.
     * 
     * @return bytes used
     */
    public int getBytesUsed() {
        return this.bytesUsed;
    }
}
