package ev3.robot;


import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
//import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.motor.Motor;

public class BarrierDetector extends Thread {
	
	EV3UltrasonicSensor ir = new EV3UltrasonicSensor(SensorPort.S4);
	SampleProvider distance = ir.getDistanceMode();
	
	boolean stop = false;
	
	public boolean getBarrier(){
		return stop;
	}
	
	@Override
	public void run(){
		
		System.out.println("Distance: starting sensor...");
			        		
		while(true) {
			if(getDistance() < 10){
				Button.LEDPattern(2);
				System.out.println("Distance: barrier found!");
				System.out.println("    Moving camera up");
				stop = true;
				Motor.B.rotate(1000);
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					System.out.println("Error sleeping!");
				}
				 
				while(getDistance() < 10){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						System.out.println("Error sleeping!");
					}
				}
				
				if(getDistance() >= 10){
					System.out.println("    Route free");
					System.out.println("    Moving camera down");
					Button.LEDPattern(1);
					Motor.B.rotate(-1000);
					
					stop = false;
				}
			} 
		}
	}
	
	public float getDistance(){
		float[] sample = new float[distance.sampleSize()];
		distance.fetchSample(sample, 0);
		return sample[0];
	}
	
	/**
	 * Displays the current sensor values.
	 */
	public void printLiveData() {
		LCD.clearDisplay();
		while (true) {
			LCD.setAutoRefreshPeriod(2);
			LCD.drawString("- sonic sensor -", 0, 1);
			float[] sample = new float[distance.sampleSize()];
			distance.fetchSample(sample, 0);
		    LCD.drawString( String.valueOf(sample[0]), 0, 3);
			
		    if(Button.readButtons() == Button.ID_ENTER){
				LCD.clearDisplay();
				break;
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		BarrierDetector barrier = new BarrierDetector();
		new EV3Exit().start();
		
		barrier.printLiveData();
		barrier.start();
	}

}
