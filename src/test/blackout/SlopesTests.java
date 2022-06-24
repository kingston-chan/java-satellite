package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(value = Lifecycle.PER_CLASS)
public class SlopesTests {
    @Test
    public void testSimpleDeviceMovesUpSlope() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(150), true);

        int gradient = 200;

        controller.createSlope(155, 175, gradient);

        controller.simulate(120);

        double deviceAngularVelocity = 50 / 69911.0;
        double expectedDeviceAPosition = Math.toRadians(150) + (deviceAngularVelocity) * 120;

        assertEquals((double) Math.round(expectedDeviceAPosition * 1000) / 1000,
                (double) Math.round(controller.getInfo("DeviceA").getPosition().toRadians() * 1000) / 1000);

        controller.simulate(3);

        expectedDeviceAPosition += (deviceAngularVelocity) * 3;

        assertEquals((double) Math.round(expectedDeviceAPosition * 1000) / 1000,
                (double) Math.round(controller.getInfo("DeviceA").getPosition().toRadians() * 1000) / 1000);

        double currentHeight = 69911.0;
        double changeInHeight = gradient * Math.toDegrees(50 / currentHeight);
        currentHeight += changeInHeight;

        assertEquals((double) Math.round(currentHeight * 1000) / 1000,
                (double) Math.round(controller.getInfo("DeviceA").getHeight() * 1000) / 1000);

        for (int i = 0; i < 100; i++) {
            expectedDeviceAPosition += 50 / currentHeight;
            changeInHeight = gradient * Math.toDegrees(50 / currentHeight);
            currentHeight += changeInHeight;
        }

        controller.simulate(100);

        assertEquals((double) Math.round(expectedDeviceAPosition * 1000) / 1000,
                (double) Math.round(controller.getInfo("DeviceA").getPosition().toRadians() * 1000) / 1000);

        assertEquals((double) Math.round(expectedDeviceAPosition * 1000) / 1000,
                (double) Math.round(controller.getInfo("DeviceA").getPosition().toRadians() * 1000) / 1000);

        for (int i = 0; i < 400; i++) {
            expectedDeviceAPosition += 50 / currentHeight;
            changeInHeight = gradient * Math.toDegrees(50 / currentHeight);
            currentHeight += changeInHeight;
        }

        controller.simulate(400);

        assertEquals((double) Math.round(expectedDeviceAPosition * 1000) / 1000,
                (double) Math.round(controller.getInfo("DeviceA").getPosition().toRadians() * 1000) / 1000);

        assertEquals((double) Math.round(currentHeight * 1000) / 1000,
                (double) Math.round(controller.getInfo("DeviceA").getHeight() * 1000) / 1000);

        controller.simulate(2);
        expectedDeviceAPosition += (50 / currentHeight) * 2;
        currentHeight = 69911.0;

        assertEquals((double) Math.round(expectedDeviceAPosition * 1000) / 1000,
                (double) Math.round(controller.getInfo("DeviceA").getPosition().toRadians() * 1000) / 1000);

        assertEquals(currentHeight,
                controller.getInfo("DeviceA").getHeight());

    }
}
