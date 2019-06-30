package ev3.robot;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class HelloWorld {

	public static void main(String[] args) {
		
		System.out.println("Hello World!");
		LCD.drawString("Plugin Test", 0, 4);
		Delay.msDelay(5000);
	}

}
