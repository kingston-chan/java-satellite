package unsw.interfaces;

import java.util.List;
import java.util.HashMap;

import unsw.blackout.FileTransferException;
import unsw.entities.FileInfo;

/**
 * Communicators are able to store file
 * 
 * @author Kingston Chan
 */

public interface ManageFiles {

    /**
     * Returns a copy of the filenames in a list
     * 
     * @return List<FileInfo> copy of filenames in a list
     */
    public List<FileInfo> getFiles();

    /**
     * Adds a file to the file storage
     * 
     * @param file the file in FileInfo object
     * @throws VirtualFileAlreadyExistsException  the file storage already contains
     *                                            given file
     * @throws VirtualFileNoBandwidthException    satellite does not have enough
     *                                            bandwidth to upload/download
     * @throws VirtualFileNoStorageSpaceException satellite does not have enough
     *                                            bytes to store file
     */
    public void addFile(FileInfo file) throws FileTransferException;

    /**
     * Adds a file to the file storage with no checking
     * Main use for adding files to devices.
     * 
     * @param file file to add to storage
     */
    public void addFileUnsafe(FileInfo file);

    /**
     * Removes the file from the file storage
     * 
     * @param fileName the filename in a string
     * @return true on successfully removing file, otherwise false
     */
    public boolean removeFile(String fileName);

    /**
     * Gets the file corresponding to file name from the file storage
     * 
     * @param fileName the filename in a string
     * @return FileInfo the information regarding the file
     *         and null if the file is not found
     */
    public FileInfo getFile(String fileName);

    /**
     * Gets the number of files stored in communicator
     * 
     * @return int number of files stored in communicator
     */
    public int getNumFiles();

    /**
     * Get the number of bytes stored on the communicator
     * Bytes management is mainly used for satellites as they have a
     * storage capacity. Devices do not.
     * 
     * @return int bytes used
     */
    public int getBytesUsed();

    /**
     * Remove bytes from communicator's total bytes used.
     * 
     * @param bytes
     */
    public void removeBytes(int bytes);

    /**
     * Add bytes to communicator's total bytes used.
     * 
     * @param bytes number of bytes the file added to satellite's storage
     */
    public void addBytes(int bytes);

    /**
     * Gets the actual storage of how the files are stored on the communicator,
     * compared to getFiles() which returns a copy
     * 
     * @return HashMap<String, FileInfo> files stored on the communicator
     */
    public HashMap<String, FileInfo> getFileStorage();
}
