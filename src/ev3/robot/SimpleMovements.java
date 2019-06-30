package ev3.robot;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.Button;

public class SimpleMovements {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EV3LargeRegulatedMotor m1 = new EV3LargeRegulatedMotor(MotorPort.A );//NXT
				
		m1.forward();
		Button.waitForAnyPress();
		m1.isMoving();
		m1.getTachoCount();
		
		m1.close();
	}

}
