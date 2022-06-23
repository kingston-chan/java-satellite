package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.FileInfoResponse;
import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;
import java.util.HashMap;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

@TestInstance(value = Lifecycle.PER_CLASS)
public class NewBlackoutTests {
        private final int DESKTOP_SPEED = 20;
        private final int LAPTOP_SPEED = 30;
        private final int HANDHELD_SPEED = 50;
        private final int RELAY_SPEED = 1500;
        private final int STANDARD_SPEED = 2500;
        private final int TELEPORTING_SPEED = 1000;

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

                System.out.println(Math.toDegrees(expectedSatellite1Position));
                System.out.println(Math.toDegrees(expectedSatellite2Position));
        }

        @Test
        public void testTeleportMidTransferDownload() {
                BlackoutController controller = new BlackoutController();

                controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(90));

                controller.createSatellite("Satellite1", "StandardSatellite", 86458, Angle.fromDegrees(126));

                String message1 = "ttttaaaattttt";

                controller.addFileToDevice("DeviceA", "Testfile1",
                                message1);

                assertDoesNotThrow(() -> controller.sendFile("Testfile1", "DeviceA", "Satellite1"));

                controller.simulate(8);

                assertEquals(new FileInfoResponse("Testfile1", "ttttaaaa",
                                message1.length(),
                                false),
                                controller.getInfo("Satellite1").getFiles().get("Testfile1"));

                controller.simulate(71);

                assertEquals(new FileInfoResponse("Testfile1", "ttttaaaattttt",
                                message1.length(),
                                true),
                                controller.getInfo("Satellite1").getFiles().get("Testfile1"));

                HashMap<String, FileInfoResponse> satellite1files = new HashMap<>();

                satellite1files.put("Testfile1",
                                new FileInfoResponse("Testfile1", "ttttaaaattttt",
                                                message1.length(),
                                                true));

                assertEquals(
                                new EntityInfoResponse("Satellite1", Angle.fromDegrees(355.11),
                                                86458,
                                                "StandardSatellite", satellite1files),
                                controller.getInfo("Satellite1"));

                controller.simulate(90);

                controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(171));

                String message2 = "This file is used to test teleporting again";

                controller.addFileToDevice("DeviceB", "Testfile2", message2);

                controller.createSatellite("Satellite2", "TeleportingSatellite", 81758,
                                Angle.fromDegrees(179));
                controller.createSatellite("Satellite3", "TeleportingSatellite", 93365,
                                Angle.fromDegrees(177));

                assertDoesNotThrow(() -> controller.sendFile("Testfile2", "DeviceB", "Satellite2"));

                assertDoesNotThrow(() -> controller.sendFile("Testfile1", "Satellite1",
                                "Satellite3"));

                controller.simulate(2);

                // Satellite2 teleported and was downloading from DeviceB, therefore all "t"
                // bytes should be removed
                // from DeviceB's file and the file should be deleted from Satellite2
                assertEquals(Angle.fromDegrees(0), controller.getInfo("Satellite2").getPosition());

                assertTrue(controller.getInfo("Satellite2").getFiles().isEmpty());

                assertEquals(new FileInfoResponse("Testfile2", "This file is used o es eleporing again",
                                38,
                                true),
                                controller.getInfo("DeviceB").getFiles().get("Testfile2"));

                controller.simulate(3);

                // Satellite3 teleported and was downloading from Satellite1, therefore all
                // remaining "t" bytes should be removed and the file is instantly downloaded on
                // Satellite3, but Satellite1 should have its file unchanged.
                assertEquals(Angle.fromDegrees(0), controller.getInfo("Satellite3").getPosition());

                assertEquals(new FileInfoResponse("Testfile1", "ttttaaaa",
                                8,
                                true),
                                controller.getInfo("Satellite3").getFiles().get("Testfile1"));

                assertEquals(new FileInfoResponse("Testfile1", message1,
                                message1.length(),
                                true),
                                controller.getInfo("Satellite1").getFiles().get("Testfile1"));
        }

        @Test
        public void testTeleportMidTransferUpload() {
                BlackoutController controller = new BlackoutController();

                controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(90), false);

                controller.createSatellite("Satellite1", "TeleportingSatellite", 82092.0, Angle.fromDegrees(62));

                String message1 = "tatttattaatttatttattaatttatttattaatttatttattaatttatttattaatt";

                controller.addFileToDevice("DeviceA", "testfile1", message1);

                assertDoesNotThrow(() -> controller.sendFile("testfile1", "DeviceA", "Satellite1"));

                controller.simulate(80);

                assertEquals(new FileInfoResponse("testfile1", message1,
                                message1.length(),
                                true),
                                controller.getInfo("Satellite1").getFiles().get("testfile1"));

                double expectedSatellite1Position = Math.toRadians(62) + ((TELEPORTING_SPEED / 82092.0) * 80);

                assertEquals((double) Math.round(expectedSatellite1Position * 100) / 100,
                                (double) Math.round(controller.getInfo("Satellite1").getPosition().toRadians() * 100)
                                                / 100);

                controller.simulate(80);

                expectedSatellite1Position += ((TELEPORTING_SPEED / 82092.0) * 80);

                assertEquals((double) Math.round(expectedSatellite1Position * 100) / 100,
                                (double) Math.round(controller.getInfo("Satellite1").getPosition().toRadians() * 100)
                                                / 100);

                controller.createDevice("DeviceB", "DesktopDevice", Angle.fromDegrees(177));

                controller.createSatellite("Satellite2", "StandardSatellite", 94895.0, Angle.fromDegrees(178));

                assertDoesNotThrow(() -> controller.sendFile("testfile1", "Satellite1", "DeviceB"));

                assertDoesNotThrow(() -> controller.sendFile("testfile1", "Satellite1", "Satellite2"));

                controller.simulate(10);

                assertEquals(Angle.fromDegrees(0), controller.getInfo("Satellite1").getPosition());

                // Satellite1 teleports but is still uploading to DeviceB and Satellite2,
                // therefore the download for DeviceB and
                // Satellite2 should instantly complete. Since DeviceB recieves the full 5 bytes
                // per min download from Satellite1 it
                // already downloaded 45 bytes, remaining "t" bytes in the 15 bytes that was not
                // downloaded is removed. However,
                // for Satellite2, it is bottlenecked by its download speed of 1 byte per min,
                // it only downloaded 10 bytes, then
                // its remaining 50 bytes has its "t" bytes removed. Satellite1's file should
                // not be changed.

                assertEquals(new FileInfoResponse("testfile1", "tatttattaatttatttattaatttatttattaatttatttattaaaaaa",
                                50,
                                true),
                                controller.getInfo("DeviceB").getFiles().get("testfile1"));

                assertEquals(new FileInfoResponse("testfile1", "tatttattaaaaaaaaaaaaaaaaaa",
                                26,
                                true),
                                controller.getInfo("Satellite2").getFiles().get("testfile1"));

                assertEquals(new FileInfoResponse("testfile1",
                                "tatttattaatttatttattaatttatttattaatttatttattaatttatttattaatt",
                                60,
                                true),
                                controller.getInfo("Satellite1").getFiles().get("testfile1"));
        }

        @Test
        public void testMidFileTransferOutOfRange() {
                BlackoutController controller = new BlackoutController();

                controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(90), true);

                controller.createSatellite("Satellite1", "StandardSatellite", 84920.0, Angle.fromDegrees(122));

                String message = "ThisIsALargeFileIfYouSendThisToAStandardSatelliteItWillNotWork";

                controller.addFileToDevice("DeviceA", "VeryLongFile", message);

                assertDoesNotThrow(() -> controller.sendFile("VeryLongFile", "DeviceA", "Satellite1"));

                controller.simulate(41);

                assertTrue(controller.getInfo("Satellite1").getFiles().isEmpty());

                assertFalse(controller.getInfo("DeviceA").getFiles().isEmpty());

                assertTrue(controller.communicableEntitiesInRange("DeviceA").isEmpty());

                assertTrue(controller.communicableEntitiesInRange("Satellite1").isEmpty());
        }

        @Test
        public void testSendFileOutOfRange() {
                BlackoutController controller = new BlackoutController();

                controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(180), true);

                String message = "ThisIsALargeFileIfYouSendThisToAStandardSatelliteItWillNotWork";

                controller.addFileToDevice("DeviceA", "VeryLongFile", message);

                controller.createSatellite("Satellite1", "StandardSatellite", 84920.0, Angle.fromDegrees(0));

                assertThrows(FileTransferException.class,
                                () -> controller.sendFile("VeryLongFile", "DeviceA", "Satellite1"));
        }
}
