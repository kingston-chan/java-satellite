package unsw.entities.filemanagement;

/**
 * This class is used to store information about the file
 * - File name
 * - The data that the file contains
 * - The file size
 * - Whether the file is being transfered (sent)
 * 
 * @author Kingston Chan
 */
public class FileInfo {
    private String fileName;
    private String fileData;
    private int fileSize;
    private boolean inTransfer;

    /**
     * Creates an instance to store information about a file
     * 
     * @param fileName   the file name
     * @param fileData   the file contents
     * @param fileSize   the file size
     * @param inTransfer whether the file is being sent to another device/satellite
     */
    public FileInfo(String fileName, String fileData, int fileSize, boolean inTransfer) {
        this.fileName = fileName;
        this.fileData = fileData;
        this.inTransfer = inTransfer;
        this.fileSize = fileSize;
    }

    /**
     * Gets the name of the file
     * 
     * @return name of the file
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Gets the contents of the file
     * 
     * @return contents of the file
     */
    public String getFileData() {
        return this.fileData;
    }

    /**
     * Gets the size of the file content
     * (how many bytes in the content currently)
     * 
     * @return current number of bytes of file content
     */
    public int getFileDataSize() {
        return this.fileData.length();
    }

    /**
     * Gets the size the file can store
     * 
     * @return size of the file can store
     */
    public int getFileSize() {
        return this.fileSize;
    }

    /**
     * Sets the file data
     * 
     * @param fileData new file data
     */
    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    /**
     * Updates the file content size to match
     * the file size. This should only be called
     * when the data is modified not when the
     * file is in transfer.
     */
    public void updateFileSize() {
        this.fileSize = this.getFileDataSize();
    }

    /**
     * Returns whether the file is in being sent
     * 
     * @return whether file is being sent
     */
    public boolean isInTransfer() {
        return inTransfer;
    }

    /**
     * Indicates the file transfer has been completed.
     */
    public void completeTransfer() {
        this.inTransfer = false;
    }

    /**
     * Remove "t" bytes from given file starting from given starting index
     * 
     * @param file         file to remove "t" bytes from
     * @param originalData original data that was being uploaded/downloaded
     * @param startIndex   index at which to start removing "t" bytes
     */
    public void removeTBytes(String originalData, int startIndex) {
        this.setFileData(
                originalData.substring(0, startIndex) + originalData.substring(startIndex).replaceAll("t", ""));
        this.updateFileSize();
        this.completeTransfer();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this.getClass() != obj.getClass()) {
            return false;
        }

        FileInfo file = (FileInfo) obj;

        return this.getFileName() == file.getFileName();
    }
}
