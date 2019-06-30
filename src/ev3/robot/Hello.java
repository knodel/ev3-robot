package ev3.robot;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class Hello {

    public static void main(String[] args) {
        LCD.clear();
        LCD.drawString("Hallo EV3...", 0, 5);
        Button.waitForAnyPress();
        LCD.clear();
        LCD.refresh();

    }

}