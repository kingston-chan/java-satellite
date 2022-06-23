package unsw.entities.filemanagement;

import unsw.entities.BlackoutObject;

/**
 * This class helps store information about files that are in transfer
 * It stores the:
 * - Sender's id
 * - Reciever's id
 * - How many bytes it should transfer
 * - Original file
 * - File being transfered
 * 
 * @author Kingston Chan
 */
public class FileInTransfer {
    private BlackoutObject senderId;
    private BlackoutObject recieverId;
    private FileInfo originalFile;
    private FileInfo transferFile;
    private int transferRate = 0;

    /**
     * Creates a new file in transfer
     * 
     * @param senderId     source of the file transfer
     * @param recieverId   target of the file transfer
     * @param originalFile original file from source
     * @param transferFile file being transfered to target
     */
    public FileInTransfer(BlackoutObject senderId, BlackoutObject recieverId, FileInfo originalFile,
            FileInfo transferFile) {
        this.senderId = senderId;
        this.recieverId = recieverId;
        this.originalFile = originalFile;
        this.transferFile = transferFile;
    }

    /**
     * Checks whether the transfer is complete
     * 
     * @return whether the transfer is complete
     */
    public boolean isCompleted() {
        return this.originalFile.getFileDataSize() == this.transferFile.getFileDataSize();
    }

    /**
     * Gets the blackout object sender
     * 
     * @return sender id
     */
    public BlackoutObject getSender() {
        return senderId;
    }

    /**
     * Gets the blackout object reciever
     * 
     * @return reciever id
     */
    public BlackoutObject getReciever() {
        return this.recieverId;
    }

    /**
     * Gets the original file
     * 
     * @return orignal file
     */
    public FileInfo getOriginalFile() {
        return this.originalFile;
    }

    /**
     * Gets the transfer file
     * 
     * @return transfer file
     */
    public FileInfo getTransferFile() {
        return this.transferFile;
    }

    public int getTransferRate() {
        return this.transferRate;
    }

    public void setTransferRate(int transferRate) {
        this.transferRate = transferRate;
    }

    /**
     * Transfer the original file's content to the transfer file
     * according to the transfer rate.
     * 
     * @return whether the transfer has been completed.
     */
    public boolean startTransfer() {
        String originalFileData = this.originalFile.getFileData();

        int maxDataSizeForTransfer = Math.min(
                this.transferFile.getFileData().length() + this.transferRate,
                this.originalFile.getFileDataSize());

        String newTransferedString = originalFileData.substring(0, maxDataSizeForTransfer);

        this.transferFile.setFileData(newTransferedString);

        if (isCompleted()) {
            this.transferFile.completeTransfer();
            return true;
        }

        return false;
    }
}
