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

import java.util.HashMap;

@TestInstance(value = Lifecycle.PER_CLASS)
public class FileTransferTests {
        private final int TELEPORTING_SPEED = 1000;

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

        @Test
        public void testSendFileThatIsBeingDownloaded() {
                BlackoutController controller = new BlackoutController();

                controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(90), false);

                controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(106), false);

                controller.addFileToDevice("DeviceA", "testfile1",
                                "tatttattaatttatttattaatttatttattaatttatttattaatttatttattaatt");

                controller.createSatellite("Satellite1", "StandardSatellite", 83020.0, Angle.fromDegrees(116));

                assertDoesNotThrow(() -> controller.sendFile("testfile1", "DeviceA", "Satellite1"));

                controller.simulate(5);

                assertThrows(FileTransferException.VirtualFileNotFoundException.class,
                                () -> controller.sendFile("testfile1", "Satellite1", "DeviceB"));
        }

        @Test
        public void testSendFileToRelay() {
                BlackoutController controller = new BlackoutController();

                controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(90), false);

                controller.addFileToDevice("DeviceA", "testfile1",
                                "tatttattaatttatttattaatttatttattaatttatttattaatttatttattaatt");

                controller.createSatellite("Satellite1", "RelaySatellite", 82494.0, Angle.fromDegrees(109));

                assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                                () -> controller.sendFile("testfile1", "DeviceA", "Satellite1"));

        }

        @Test
        public void testMaxFilesReached() {
                BlackoutController controller = new BlackoutController();

                controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(90), false);
                controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(95), false);
                controller.createDevice("DeviceC", "LaptopDevice", Angle.fromDegrees(100), false);
                controller.createDevice("DeviceD", "LaptopDevice", Angle.fromDegrees(85), false);

                controller.addFileToDevice("DeviceA", "testfile1",
                                "t");
                controller.addFileToDevice("DeviceB", "testfile2",
                                "t");
                controller.addFileToDevice("DeviceC", "testfile3",
                                "t");
                controller.addFileToDevice("DeviceD", "testfile4",
                                "t");

                controller.createSatellite("Satellite1", "StandardSatellite", 83709.0, Angle.fromDegrees(92));

                assertDoesNotThrow(() -> controller.sendFile("testfile1", "DeviceA", "Satellite1"));

                controller.simulate();
                assertDoesNotThrow(() -> controller.sendFile("testfile2", "DeviceB", "Satellite1"));

                controller.simulate();
                assertDoesNotThrow(() -> controller.sendFile("testfile3", "DeviceC", "Satellite1"));

                controller.simulate();
                assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                                () -> controller.sendFile("testfile4", "DeviceD", "Satellite1"));
        }

        @Test
        public void testMaxBytesReachedStandardSatellite() {
                BlackoutController controller = new BlackoutController();

                controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(90), false);
                controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(95), false);

                controller.createSatellite("Satellite2", "StandardSatellite", 83854.0, Angle.fromDegrees(121));

                String eightyOneByteMessage = new String(new char[81]).replace("\0", "t");

                controller.addFileToDevice("DeviceB", "Testfile2", eightyOneByteMessage);

                assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                                () -> controller.sendFile("Testfile2", "DeviceB", "Satellite2"));

                controller.addFileToDevice("DeviceA", "Testfile3", "testfile3testfile3testfile3");

                assertDoesNotThrow(() -> controller.sendFile("Testfile3", "DeviceA", "Satellite2"));

                controller.simulate(30);

                assertFalse(controller.getInfo("Satellite2").getFiles().isEmpty());

                controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(37), false);

                controller.addFileToDevice("DeviceC", "testfile4", "testfile4testfile4testfile4testfile4");

                assertDoesNotThrow(() -> controller.sendFile("testfile4", "DeviceC", "Satellite2"));

                controller.simulate(40);

                assertTrue(controller.getInfo("Satellite2").getFiles().size() == 2);
                assertTrue(controller.getInfo("Satellite2").getFiles().get("Testfile3").hasTransferCompleted());
                assertTrue(controller.getInfo("Satellite2").getFiles().get("testfile4").hasTransferCompleted());

                controller.createDevice("DeviceD", "LaptopDevice", Angle.fromDegrees(350), false);

                controller.addFileToDevice("DeviceD", "testfile5", "testfile5testfile5testfile5testfile5");

                assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                                () -> controller.sendFile("testfile5", "DeviceD", "Satellite2"));

        }

        @Test
        public void testMaxBytesReachedTeleportingSatellite() {
                BlackoutController controller = new BlackoutController();

                controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(350), false);

                String twoHundredAndOneByteMessage = new String(new char[201]).replace("\0", "t");
                controller.addFileToDevice("DeviceA", "Testfile1", twoHundredAndOneByteMessage);

                controller.createSatellite("Satellite1", "TeleportingSatellite", 81714.0, Angle.fromDegrees(318));

                assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                                () -> controller.sendFile("Testfile1", "DeviceA", "Satellite1"));

                controller.addFileToDevice("DeviceA", "largefile1",
                                "tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt");

                assertDoesNotThrow(() -> controller.sendFile("largefile1", "DeviceA", "Satellite1"));

                controller.simulate(50);

                assertTrue(controller.getInfo("Satellite1").getFiles().get("largefile1").hasTransferCompleted());

                controller.simulate(30);

                controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(37), false);

                controller.addFileToDevice("DeviceB", "largefile2",
                                "ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt");

                controller.addFileToDevice("DeviceB", "testfile2",
                                "testfile2testfile2testfile2testfile2");

                assertDoesNotThrow(() -> controller.sendFile("largefile2", "DeviceB", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile2", "DeviceB", "Satellite1"));

                controller.simulate(60);

                assertTrue(controller.getInfo("Satellite1").getFiles().get("largefile2").hasTransferCompleted());
                assertTrue(controller.getInfo("Satellite1").getFiles().get("testfile2").hasTransferCompleted());

                controller.createDevice("DeviceC", "LaptopDevice", Angle.fromDegrees(90), false);

                controller.addFileToDevice("DeviceC", "testfile1",
                                "testfile1testfile1testfile1");

                assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                                () -> controller.sendFile("testfile1", "DeviceC", "Satellite1"));

        }

        @Test
        public void testMaxUploadsStandardSatellite() {
                BlackoutController controller = new BlackoutController();
                controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(90), false);
                controller.createSatellite("Satellite1", "StandardSatellite", 81714.0, Angle.fromDegrees(90));

                controller.addFileToDevice("DeviceA", "testfile", "t");

                assertDoesNotThrow(() -> controller.sendFile("testfile", "DeviceA", "Satellite1"));

                controller.simulate();

                controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(100), false);
                controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(80), false);

                assertDoesNotThrow(() -> controller.sendFile("testfile", "Satellite1", "DeviceB"));
                assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                                () -> controller.sendFile("testfile", "Satellite1", "DeviceC"));
        }

        @Test
        public void testMaxDownloadsStandardSatellite() {
                BlackoutController controller = new BlackoutController();

                controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(90), false);
                controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(100), false);

                controller.addFileToDevice("DeviceA", "testfile1", "t");
                controller.addFileToDevice("DeviceB", "testfile2", "t");

                controller.createSatellite("Satellite1", "StandardSatellite", 81714.0, Angle.fromDegrees(90));

                assertDoesNotThrow(() -> controller.sendFile("testfile1", "DeviceA", "Satellite1"));
                assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                                () -> controller.sendFile("testfile2", "DeviceB", "Satellite1"));
        }

        @Test
        public void testMaxUploadsTeleportingSatellite() {
                BlackoutController controller = new BlackoutController();

                controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(100), false);

                controller.addFileToDevice("DeviceA", "testfile1", "t");

                controller.createSatellite("Satellite1", "TeleportingSatellite", 81714.0, Angle.fromDegrees(90));

                assertDoesNotThrow(() -> controller.sendFile("testfile1", "DeviceA", "Satellite1"));

                controller.simulate();

                controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(99), false);
                controller.createDevice("DeviceC", "LaptopDevice", Angle.fromDegrees(98), false);
                controller.createDevice("DeviceD", "DesktopDevice", Angle.fromDegrees(97), false);
                controller.createDevice("DeviceE", "HandheldDevice", Angle.fromDegrees(96), false);
                controller.createDevice("DeviceF", "LaptopDevice", Angle.fromDegrees(95), false);
                controller.createDevice("DeviceG", "DesktopDevice", Angle.fromDegrees(94), false);
                controller.createDevice("DeviceH", "HandheldDevice", Angle.fromDegrees(93), false);
                controller.createDevice("DeviceI", "LaptopDevice", Angle.fromDegrees(92), false);
                controller.createDevice("DeviceJ", "DesktopDevice", Angle.fromDegrees(91), false);
                controller.createDevice("DeviceK", "HandheldDevice", Angle.fromDegrees(90), false);
                controller.createDevice("DeviceL", "LaptopDevice", Angle.fromDegrees(89), false);

                assertDoesNotThrow(() -> controller.sendFile("testfile1", "Satellite1", "DeviceB"));
                assertDoesNotThrow(() -> controller.sendFile("testfile1", "Satellite1", "DeviceC"));
                assertDoesNotThrow(() -> controller.sendFile("testfile1", "Satellite1", "DeviceD"));
                assertDoesNotThrow(() -> controller.sendFile("testfile1", "Satellite1", "DeviceE"));
                assertDoesNotThrow(() -> controller.sendFile("testfile1", "Satellite1", "DeviceF"));
                assertDoesNotThrow(() -> controller.sendFile("testfile1", "Satellite1", "DeviceG"));
                assertDoesNotThrow(() -> controller.sendFile("testfile1", "Satellite1", "DeviceH"));
                assertDoesNotThrow(() -> controller.sendFile("testfile1", "Satellite1", "DeviceI"));
                assertDoesNotThrow(() -> controller.sendFile("testfile1", "Satellite1", "DeviceJ"));
                assertDoesNotThrow(() -> controller.sendFile("testfile1", "Satellite1", "DeviceK"));

                assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                                () -> controller.sendFile("testfile1", "Satellite1", "DeviceL"));
        }

        @Test
        public void testMaxDownloadsTeleportingSatellite() {
                BlackoutController controller = new BlackoutController();
                controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(100), false);
                controller.addFileToDevice("DeviceA", "testfile1", "t");
                controller.createSatellite("Satellite2", "StandardSatellite", 75000.0, Angle.fromDegrees(94));

                assertDoesNotThrow(() -> controller.sendFile("testfile1", "DeviceA", "Satellite2"));

                controller.simulate();

                assertFalse(controller.getInfo("Satellite2").getFiles().isEmpty());

                controller.createSatellite("Satellite1", "TeleportingSatellite", 81714.0, Angle.fromDegrees(94));

                controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(99), false);
                controller.createDevice("DeviceC", "LaptopDevice", Angle.fromDegrees(98), false);
                controller.createDevice("DeviceD", "DesktopDevice", Angle.fromDegrees(97), false);
                controller.createDevice("DeviceE", "HandheldDevice", Angle.fromDegrees(96), false);
                controller.createDevice("DeviceF", "LaptopDevice", Angle.fromDegrees(95), false);
                controller.createDevice("DeviceG", "DesktopDevice", Angle.fromDegrees(94), false);
                controller.createDevice("DeviceH", "HandheldDevice", Angle.fromDegrees(93), false);
                controller.createDevice("DeviceI", "LaptopDevice", Angle.fromDegrees(92), false);
                controller.createDevice("DeviceJ", "DesktopDevice", Angle.fromDegrees(91), false);
                controller.createDevice("DeviceK", "HandheldDevice", Angle.fromDegrees(90), false);
                controller.createDevice("DeviceL", "LaptopDevice", Angle.fromDegrees(89), false);
                controller.createDevice("Device1", "HandheldDevice", Angle.fromDegrees(101), false);
                controller.createDevice("Device2", "HandheldDevice", Angle.fromDegrees(102), false);
                controller.createDevice("Device3", "HandheldDevice", Angle.fromDegrees(103), false);
                controller.createDevice("Device4", "HandheldDevice", Angle.fromDegrees(104), false);

                controller.addFileToDevice("DeviceB", "testfile2", "t");
                controller.addFileToDevice("DeviceC", "testfile3", "t");
                controller.addFileToDevice("DeviceD", "testfile4", "t");
                controller.addFileToDevice("DeviceE", "testfile5", "t");
                controller.addFileToDevice("DeviceF", "testfile6", "t");
                controller.addFileToDevice("DeviceG", "testfile7", "t");
                controller.addFileToDevice("DeviceH", "testfile8", "t");
                controller.addFileToDevice("DeviceI", "testfile9", "t");
                controller.addFileToDevice("DeviceJ", "testfile10", "t");
                controller.addFileToDevice("DeviceK", "testfile11", "t");
                controller.addFileToDevice("DeviceL", "testfile12", "t");
                controller.addFileToDevice("Device1", "testfile13", "t");
                controller.addFileToDevice("Device2", "testfile14", "t");
                controller.addFileToDevice("Device3", "testfile15", "t");
                controller.addFileToDevice("Device4", "testfile16", "t");

                assertDoesNotThrow(() -> controller.sendFile("testfile16", "Device4", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile2", "DeviceB", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile3", "DeviceC", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile4", "DeviceD", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile5", "DeviceE", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile6", "DeviceF", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile7", "DeviceG", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile8", "DeviceH", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile9", "DeviceI", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile10", "DeviceJ", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile11", "DeviceK", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile12", "DeviceL", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile13", "Device1", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile14", "Device2", "Satellite1"));
                assertDoesNotThrow(() -> controller.sendFile("testfile15", "Device3", "Satellite1"));

                assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                                () -> controller.sendFile("testfile1", "Satellite2", "Satellite1"));
        }

}
