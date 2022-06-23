package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.utils.Angle;

import java.util.Arrays;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

@TestInstance(value = Lifecycle.PER_CLASS)
public class CommunicablesTests {
    @Test
    public void testReacheableUsingRelaySatellites() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(150));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(136));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(28));
        controller.createSatellite("Satellite1", "RelaySatellite", 82292, Angle.fromDegrees(169));
        controller.createSatellite("Satellite2", "RelaySatellite", 104863, Angle.fromDegrees(240));
        controller.createSatellite("Satellite3", "StandardSatellite", 92373, Angle.fromDegrees(327));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Satellite2", "Satellite3"),
                controller.communicableEntitiesInRange("DeviceB"));

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB", "Satellite1", "Satellite2", "DeviceA"),
                controller.communicableEntitiesInRange("Satellite3"));
    }

    @Test
    public void testReachableFromSatelliteButNotFromDevice() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(174));
        controller.createSatellite("Satellite1", "StandardSatellite", 129770, Angle.fromDegrees(151));

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB"),
                controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.communicableEntitiesInRange("DeviceB"));
    }

    @Test
    public void testDevicesAndSatellitesSupport() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(270));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(251));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(289));
        controller.createSatellite("Satellite1", "StandardSatellite", 82012, Angle.fromDegrees(270));
        controller.createSatellite("Satellite2", "TeleportingSatellite", 83525, Angle.fromDegrees(255));
        controller.createSatellite("Satellite3", "RelaySatellite", 84017, Angle.fromDegrees(286));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Satellite2", "Satellite3"),
                controller.communicableEntitiesInRange("DeviceA"));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Satellite2", "Satellite3"),
                controller.communicableEntitiesInRange("DeviceB"));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "Satellite3"),
                controller.communicableEntitiesInRange("DeviceC"));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "Satellite3", "DeviceB", "DeviceA"),
                controller.communicableEntitiesInRange("Satellite1"));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Satellite3", "DeviceB", "DeviceA", "DeviceC"),
                controller.communicableEntitiesInRange("Satellite2"));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Satellite2", "DeviceB", "DeviceA", "DeviceC"),
                controller.communicableEntitiesInRange("Satellite3"));
    }
}
