package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)
public class MovementTests {
        private final int DESKTOP_SPEED = 20;
        private final int LAPTOP_SPEED = 30;
        private final int HANDHELD_SPEED = 50;
        private final int RELAY_SPEED = 1500;
        private final int STANDARD_SPEED = 2500;
        private final int TELEPORTING_SPEED = 1000;

        @Test
        public void testMovingDevices() {
                BlackoutController controller = new BlackoutController();

                controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(150), true);
                controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(170), true);
                controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(190), true);

                controller.simulate();

                // Positon should equal current position + (angular velocity * minutes)
                double expectedDeviceAPosition = Math.toRadians(150) + (HANDHELD_SPEED / RADIUS_OF_JUPITER);
                assertEquals(new EntityInfoResponse("DeviceA", Angle.fromRadians(expectedDeviceAPosition),
                                RADIUS_OF_JUPITER,
                                "HandheldDevice"), controller.getInfo("DeviceA"));

                double expectedDeviceBPosition = Math.toRadians(170) + LAPTOP_SPEED / RADIUS_OF_JUPITER;
                assertEquals(new EntityInfoResponse("DeviceB", Angle.fromRadians(expectedDeviceBPosition),
                                RADIUS_OF_JUPITER,
                                "LaptopDevice"), controller.getInfo("DeviceB"));

                double expectedDeviceCPosition = Math.toRadians(190) + DESKTOP_SPEED / RADIUS_OF_JUPITER;
                assertEquals(new EntityInfoResponse("DeviceC", Angle.fromRadians(expectedDeviceCPosition),
                                RADIUS_OF_JUPITER,
                                "DesktopDevice"), controller.getInfo("DeviceC"));

                controller.simulate(7);

                expectedDeviceAPosition += (HANDHELD_SPEED / RADIUS_OF_JUPITER) * 7;
                assertEquals(
                                new EntityInfoResponse("DeviceA", Angle.fromRadians(expectedDeviceAPosition),
                                                RADIUS_OF_JUPITER,
                                                "HandheldDevice"),
                                controller.getInfo("DeviceA"));

                expectedDeviceBPosition += (LAPTOP_SPEED / RADIUS_OF_JUPITER) * 7;
                assertEquals(
                                new EntityInfoResponse("DeviceB", Angle.fromRadians(expectedDeviceBPosition),
                                                RADIUS_OF_JUPITER,
                                                "LaptopDevice"),
                                controller.getInfo("DeviceB"));

                expectedDeviceCPosition += (DESKTOP_SPEED / RADIUS_OF_JUPITER) * 7;
                assertEquals(
                                new EntityInfoResponse("DeviceC", Angle.fromRadians(expectedDeviceCPosition),
                                                RADIUS_OF_JUPITER,
                                                "DesktopDevice"),
                                controller.getInfo("DeviceC"));
        }

        @Test
        public void testStaysInOneRevolutionRange() {
                BlackoutController controller = new BlackoutController();

                controller.createSatellite("Satellite1", "StandardSatellite", 79032, Angle.fromDegrees(13));

                controller.simulate();

                double expectedSatellite1Position = Math.toRadians(13) - (STANDARD_SPEED / 79032.0);
                assertEquals(
                                new EntityInfoResponse("Satellite1", Angle.fromRadians(expectedSatellite1Position),
                                                79032,
                                                "StandardSatellite"),
                                controller.getInfo("Satellite1"));

                controller.simulate(10);

                expectedSatellite1Position = (Math.PI * 2)
                                + (expectedSatellite1Position - (STANDARD_SPEED / 79032.0) * 10);

                assertEquals(
                                new EntityInfoResponse("Satellite1", Angle.fromRadians(expectedSatellite1Position),
                                                79032,
                                                "StandardSatellite"),
                                controller.getInfo("Satellite1"));
        }

        @Test
        public void testRelayDoesNotStartInRegion() {
                BlackoutController controller = new BlackoutController();

                double Satellite1Height = 89372;
                double Satellite2Height = 86633;

                controller.createSatellite("Satellite1", "RelaySatellite", Satellite1Height, Angle.fromDegrees(339));
                controller.createSatellite("Satellite2", "RelaySatellite", Satellite2Height, Angle.fromDegrees(359));

                double expectedSatellite1Position = Math.toRadians(339) - (RELAY_SPEED / Satellite1Height);
                double expectedSatellite2Position = Math.toRadians(359) + (RELAY_SPEED / Satellite2Height);

                controller.simulate();

                assertEquals(
                                new EntityInfoResponse("Satellite1", Angle.fromRadians(expectedSatellite1Position),
                                                Satellite1Height,
                                                "RelaySatellite"),
                                controller.getInfo("Satellite1"));

                assertEquals(
                                new EntityInfoResponse("Satellite2", Angle.fromRadians(expectedSatellite2Position),
                                                Satellite2Height,
                                                "RelaySatellite"),
                                controller.getInfo("Satellite2"));

                controller.simulate(5);

                expectedSatellite1Position -= (RELAY_SPEED / Satellite1Height) * 5;
                expectedSatellite2Position += ((RELAY_SPEED / Satellite2Height) * 5);
                expectedSatellite2Position %= (Math.PI * 2);

                assertEquals(
                                new EntityInfoResponse("Satellite1", Angle.fromRadians(expectedSatellite1Position),
                                                Satellite1Height,
                                                "RelaySatellite"),
                                controller.getInfo("Satellite1"));

                assertEquals(
                                new EntityInfoResponse("Satellite2", Angle.fromRadians(expectedSatellite2Position),
                                                Satellite2Height,
                                                "RelaySatellite"),
                                controller.getInfo("Satellite2"));

        }

        @Test
        public void testTeleportingSatelliteChangesDirectionAfterTeleport() {
                BlackoutController controller = new BlackoutController();

                controller.createSatellite("Satellite1", "TeleportingSatellite", 87082, Angle.fromDegrees(175));

                controller.simulate(5);

                // Starts of anticlockwise
                double expectedSatellite1Position = Math.toRadians(175) + (TELEPORTING_SPEED / 87082.0) * 5;

                assertEquals((double) Math.round(expectedSatellite1Position * 1000) / 1000,
                                (double) Math.round(controller.getInfo("Satellite1").getPosition().toRadians() * 1000)
                                                / 1000);

                controller.simulate(15);

                // Should start moving clockwise
                expectedSatellite1Position = (Math.PI * 2) - (TELEPORTING_SPEED / 87082.0) * 12;

                assertEquals((double) Math.round(expectedSatellite1Position * 1000) / 1000,
                                (double) Math.round(controller.getInfo("Satellite1").getPosition().toRadians() * 1000)
                                                / 1000);
        }
}
