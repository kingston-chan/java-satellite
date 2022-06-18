package unsw.entities;

public class FileInTransfer {
    private Communicator sender;
    private Communicator reciever;
    private int transferRate;
    private FileInfo originalFile;
    private FileInfo transferFile;

    public FileInTransfer(Communicator sender, Communicator reciever, FileInfo originalFile,
            FileInfo transferFile) {
        this.sender = sender;
        this.reciever = reciever;
        this.transferRate = 0;
        this.originalFile = originalFile;
        this.transferFile = transferFile;
    }

    public Communicator getSender() {
        return this.sender;
    }

    public Communicator getReciever() {
        return this.reciever;
    }

    public void setTransferRate(int rate) {
        this.transferRate = rate;
    }

    public int getTransferRate() {
        return this.transferRate;
    }

    public FileInfo getOriginalFile() {
        return this.originalFile;
    }

    public FileInfo getTransferFile() {
        return this.transferFile;
    }

    public void startTransfer() {
        this.transferFile.setFileData(
                this.originalFile.getFileData().substring(
                        0, Math.min((this.transferFile.getFileDataSize() + transferRate),
                                this.originalFile.getFileDataSize())));

        if (isCompleted()) {
            this.transferFile.setTransferCompleted();
        }
    }

    public boolean isCompleted() {
        return this.originalFile.getFileDataSize() == this.transferFile.getFileDataSize();
    }

}
