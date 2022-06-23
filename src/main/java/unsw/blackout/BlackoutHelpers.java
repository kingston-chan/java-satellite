package unsw.blackout;

import unsw.utils.MathsHelper;
import unsw.entities.BlackoutObject;
import unsw.entities.filemanagement.FileInTransfer;
import unsw.entities.filemanagement.FileInfo;
import unsw.entities.filemanagement.FileStorage;
import unsw.entities.other.BandwidthControl;
import unsw.response.models.FileInfoResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * Helper functions for BlackoutController
 * 
 * @author Kingston Chan
 */
public class BlackoutHelpers {

    /**
     * Helps map the devices/satellite's file storage into a hash map of
     * FileInfoResponses
     * 
     * @param fileStorage device/satellite file storage
     * @return HashMap containing filenames as keys and FileInfoResponses as values,
     *         empty if no file storage
     */
    public static HashMap<String, FileInfoResponse> mapToFileInfoResponse(FileStorage fileStorage) {
        HashMap<String, FileInfoResponse> files = new HashMap<String, FileInfoResponse>();

        if (fileStorage == null) {
            return files;
        }

        for (FileInfo file : fileStorage.getFiles()) {
            files.put(file.getFileName(), new FileInfoResponse(file.getFileName(), file.getFileData(),
                    file.getFileSize(), !file.isInTransfer()));
        }
        return files;
    }

    /**
     * Gets a list of either satellites or devices ids
     * 
     * @param isSatellites    whether to get a list of satellites
     * @param blackoutObjects HashMap containing a id key and a blackout object
     *                        value
     * @return list of string ids (either of satellites or devices)
     */
    public static List<String> getSatOrDeviceIds(boolean isSatellites,
            HashMap<String, BlackoutObject> blackoutObjects) {
        List<String> blackoutObjectsIds = new ArrayList<String>();
        for (BlackoutObject bo : blackoutObjects.values()) {
            if (bo.doesOrbit() == isSatellites) {
                blackoutObjectsIds.add(bo.getId());
            }
        }
        return blackoutObjectsIds;
    }

    /**
     * Checks whether two blackout objects are visible
     * 
     * @param bo1
     * @param bo2
     * @return whether two blackout objects are visible
     */
    private static boolean isVisible(BlackoutObject bo1, BlackoutObject bo2) {
        if (!bo1.doesOrbit()) {
            return MathsHelper.isVisible(bo2.getHeight(), bo2.getPosition(), bo1.getPosition());
        } else if (!bo2.doesOrbit()) {
            return MathsHelper.isVisible(bo1.getHeight(), bo1.getPosition(), bo2.getPosition());
        }
        return MathsHelper.isVisible(bo1.getHeight(), bo1.getPosition(), bo2.getHeight(), bo2.getPosition());
    }

    /**
     * Gets the distance between two blackout objects
     * 
     * @param bo1
     * @param bo2
     * @return distance between two blackout objects
     */
    private static double getDistance(BlackoutObject bo1, BlackoutObject bo2) {
        return MathsHelper.getDistance(bo1.getHeight(), bo1.getPosition(), bo2.getHeight(),
                bo2.getPosition());
    }

    /**
     * Checks whether two different blackout objects support each other
     * 
     * @param source
     * @param target
     * @return two different blackout objects support each other
     */
    private static boolean supportsEachOther(BlackoutObject source, BlackoutObject target) {
        return source.doesSupport(target.getType()) && target.doesSupport(source.getType())
                && !source.equals(target);
    }

    /**
     * Checks whether the source blackout object can communicate with the target
     * blackout object such that they are not the same blackout object
     * 
     * @param source
     * @param target
     * @return whether two different blackout objects can communicate
     */
    private static boolean isCommunicable(BlackoutObject source, BlackoutObject target) {
        return isVisible(source, target) && getDistance(source, target) <= source.getRange() && !source.equals(target)
                && supportsEachOther(source, target);
    }

    /**
     * Use depth first search to find all the communicable blackout objects since
     * relay acts as nodes in a graph
     * 
     * @param source          is the blackout object that requires the list of its
     *                        communicable blackout object
     * @param blackoutObjects hashmap of active blackout objects
     * @return list of communicable blackout objects
     */
    public static List<String> dfsFindCommunicables(BlackoutObject source,
            HashMap<String, BlackoutObject> blackoutObjects) {
        List<String> communicables = new ArrayList<String>();

        HashMap<String, Integer> visited = new HashMap<String, Integer>();

        Stack<String> stack = new Stack<String>();

        for (BlackoutObject blackoutObject : blackoutObjects.values()) {
            if (isCommunicable(source, blackoutObject)) {
                stack.push(blackoutObject.getId());
            }
            visited.put(blackoutObject.getId(), -1);
        }

        while (!stack.empty()) {
            String currentBlackoutObjectId = stack.pop();

            if (visited.get(currentBlackoutObjectId) == -1) {

                visited.replace(currentBlackoutObjectId, 0);

                communicables.add(currentBlackoutObjectId);

                BlackoutObject currentBlackoutObject = blackoutObjects.get(currentBlackoutObjectId);

                if (currentBlackoutObject.canExtendRange()) {
                    for (BlackoutObject blackoutObject : blackoutObjects.values()) {
                        if (isCommunicable(currentBlackoutObject, blackoutObject) &&
                                supportsEachOther(source, blackoutObject)) {
                            stack.push(blackoutObject.getId());
                        }
                    }
                }

            }

        }

        return communicables;
    }

    /**
     * Remove "t" bytes from given file
     * 
     * @param file
     */
    public static void removeTBytes(FileInfo file, String originalData) {
        file.setFileData(originalData.replaceAll("t", ""));
        file.updateFileSize();
        file.completeTransfer();
    }

    /**
     * Does the file transfer for the given file in transfer. Either the sender or
     * reciever must have a bandwidth control, otherwise this will not work.
     * 
     * @param senderBC       sender's bandwidth control, if sender does not have one
     *                       it is null
     * @param receiverBC     reciever's bandwidth control, if reciever does not have
     *                       on it is null
     * @param fileInTransfer the file that is currently being uploaded/downloaded
     * @return whether the transfer is complete or not
     */
    public static boolean doFileTransfer(BandwidthControl senderBC, BandwidthControl receiverBC,
            FileInTransfer fileInTransfer) {
        int transferRate;

        if (senderBC == null) {
            transferRate = receiverBC.getDownloadBandwidth();
        } else if (receiverBC == null) {
            transferRate = senderBC.getUploadBandwidth();
        } else {
            transferRate = senderBC.getMaxTransferRate(receiverBC);
        }

        fileInTransfer.startTransfer(transferRate);

        if (fileInTransfer.isCompleted()) {
            finishUploadDownload(senderBC, receiverBC);
            return true;
        }
        return false;
    }

    /**
     * Ends the download for the sender or reciever
     * 
     * @param senderBC
     * @param receiverBC
     */
    public static void finishUploadDownload(BandwidthControl senderBC, BandwidthControl receiverBC) {
        if (senderBC != null) {
            senderBC.endUpload();
        }

        if (receiverBC != null) {
            receiverBC.endDownload();
        }
    }
}
