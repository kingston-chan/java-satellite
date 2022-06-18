package unsw.entities;

public class FileInfo {
    private String fileName;
    private String fileData;
    private int fileDataSize;
    private boolean inTransfer;

    public FileInfo(String fileName, String fileData, int fileDataSize, boolean inTransfer) {
        this.fileName = fileName;
        this.fileData = fileData;
        this.inTransfer = inTransfer;
        this.fileDataSize = fileDataSize;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFileData() {
        return this.fileData;
    }

    public int getFileDataSize() {
        return this.fileDataSize;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    public void updateFileDataSize() {
        this.fileDataSize = this.fileData.length();
    }

    public boolean isInTransfer() {
        return inTransfer;
    }

    public void setTransferCompleted() {
        this.inTransfer = false;
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
