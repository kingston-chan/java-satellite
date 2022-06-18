package unsw.blackout;

import unsw.utils.MathsHelper;
import unsw.entities.Device;
import unsw.entities.RelaySatellite;
import unsw.entities.Communicator;
import unsw.entities.FileTransferSatellite;
import unsw.entities.Satellite;

import java.util.HashMap;
import java.util.Stack;

public class BlackoutHelpers {
    public static Device toDevice(Communicator communicator) {
        return (Device) communicator;
    }

    public static Satellite toSatellite(Communicator communicator) {
        return (Satellite) communicator;
    }

    public static FileTransferSatellite toFileTransferSatellite(Communicator communicator) {
        return (FileTransferSatellite) communicator;
    }

    public static boolean isVisible(Satellite toSat, Satellite fromSat) {
        return MathsHelper.isVisible(
                toSat.getHeight(), toSat.getPosition(),
                fromSat.getHeight(), fromSat.getPosition());
    }

    public static boolean isVisible(Satellite toSat, Device fromDev) {
        return MathsHelper.isVisible(
                toSat.getHeight(), toSat.getPosition(), fromDev.getPosition());
    }

    public static boolean isCommunicableFromSatToSat(Satellite fromSat, Satellite toSat,
            HashMap<String, Communicator> comms) {
        return isVisible(fromSat, toSat)
                || isReachableWithRelays(fromSat.getId(), toSat.getId(), comms);
    }

    public static boolean isCommunicableFromSatToDev(Device toDev, Satellite fromSat,
            HashMap<String, Communicator> comms) {
        return (isVisible(fromSat, toDev) && fromSat.supports(toDev))
                || isReachableWithRelays(toDev.getId(), fromSat.getId(), comms);
    }

    public static boolean isCommunicableFromDevToSat(Device fromDev, Satellite toSat,
            HashMap<String, Communicator> comms) {
        return (MathsHelper.getDistance(toSat.getHeight(), toSat.getPosition(),
                fromDev.getPosition()) <= (double) fromDev.getRange() && toSat.supports(fromDev))
                || isReachableWithRelays(fromDev.getId(), toSat.getId(), comms);
    }

    public static boolean isReachableWithRelays(String start, String dest, HashMap<String, Communicator> comms) {
        HashMap<String, Integer> visited = new HashMap<String, Integer>();

        for (String comm : comms.keySet()) {
            visited.put(comm, -1);
        }

        Stack<String> stack = new Stack<String>();

        stack.push(start);

        while (!stack.empty()) {
            String currCommStr = stack.pop();

            if (visited.get(currCommStr) == -1) {

                visited.replace(currCommStr, 0);

                Communicator currComm = comms.get(currCommStr);

                for (String nodeCommStr : comms.keySet()) {
                    Communicator nodeComm = comms.get(nodeCommStr);

                    // From device to satellite
                    if (nodeComm instanceof Satellite && currComm instanceof Device) {
                        if (MathsHelper.getDistance(toSatellite(nodeComm).getHeight(),
                                toSatellite(nodeComm).getPosition(),
                                toDevice(currComm).getPosition()) <= toDevice(currComm).getRange()) {
                            if (nodeCommStr == dest) {
                                return true;
                            }

                            if (nodeComm instanceof RelaySatellite && visited.get(nodeCommStr) == -1) {
                                stack.push(nodeCommStr);
                            }
                        }
                    }

                    // From satellite to another satellite
                    if (nodeComm instanceof Satellite && currComm instanceof Satellite) {
                        if (isVisible(toSatellite(nodeComm), toSatellite(currComm))) {
                            if (nodeCommStr == dest) {
                                return true;
                            }

                            if (nodeComm instanceof RelaySatellite && visited.get(nodeCommStr) == -1) {
                                stack.push(nodeCommStr);
                            }
                        }
                    }

                    // From satellite to device
                    if (nodeComm instanceof Device && currComm instanceof Satellite) {
                        if (isVisible(toSatellite(currComm), toDevice(nodeComm))) {
                            if (nodeCommStr == dest) {
                                return true;
                            }
                        }
                    }
                }
            }

        }

        return false;
    }

}
