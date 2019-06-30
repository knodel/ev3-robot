package ev3.robot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lejos.hardware.Button;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;


/**
 * Detects a line...
 * 
 * @author Oliver
 */
public class LineDetector {
	
    //private LightSensor light = new LightSensor(SensorPort.S3);
    private EV3ColorSensor light = new EV3ColorSensor(SensorPort.S3);
	
	private float high = 0.0f;
	private float low  = 2.0f;
	private float diff = 0;
	private float threshold = 0.5f;
	
	/**
	 * true  : bright line on dark background
	 * false : dark line on bright background 
	 */
	private boolean lineColor = false;
	private boolean detected = false;
	
	/**
	 * @return LightSensor
	 */
	public EV3ColorSensor getLightSensor() {
		return light;
	}

	/**
	 * @return the computed threshold between the two "colors"
	 */
	public float getThreshold() {
		return threshold;
	}
	
	/**
	 * @return true if the line is bright and false if it is dark
	 */
	public boolean getLineColor() {
		return lineColor;
	}
	
	/**
	 * @return true if a line is detected during the calibration
	 */
	public boolean lineDetected() {
		return detected;
	}
	
	/**
	 * @return true if a line is found
	 */
	public boolean isLine(){
		if(getRedSensorValue() > threshold){
			if(lineColor){
				return true;
			} else {
				return false;
			}
		} else {
			if(lineColor){
				return false;
			} else {
				return true;
			}
		}
	}
	
	/**
	 * Measures the brightness of the surrounding underground
	 * and computes threshold and line "color"
	 * 
	 * @param pilot - necessary for the rotation
	 * @return true if a line is detected
	 */
	public boolean calibrate(DifferentialPilot pilot){
		
		List<Float> lightList = new ArrayList<Float>();
		float sum = 0;
		
		high = 0.0f;
		low  = 2.0f;
		diff = 0;
		threshold = 0.3f;
		pilot.setRotateSpeed(16);//12
	    pilot.rotate(360, true);
		while (pilot.isMoving()) {
			lightList.add(getRedSensorValue());
		}
		
		Iterator<Float> iter = lightList.iterator();
		
		while(iter.hasNext()){
			float next = iter.next();
			sum += next;
			if(next < low) {
				low = next;
			}
			if(next > high) {
				high = next;
			}
		}
		
		diff = high - low;
		
		//line detected?
		if(diff < 0.2f) {
			detected = false;
			return false;
		}
		detected = true;
		
		if((sum / lightList.size()) < threshold) lineColor = true; else lineColor = false;
		threshold = low + (diff/2);
		
		return true;
	}
	
	/**
	 * Displays the current sensor values.
	 */
	public void printLiveData() {
		LCD.clearDisplay();
		while (true) {
			LCD.setAutoRefreshPeriod(2);
			LCD.drawString("- light sensor -", 0, 1);	
			LCD.drawString(String.valueOf(getRedSensorValue()), 4, 3);
			
			if(Button.readButtons() == Button.ID_ENTER){
				LCD.clearDisplay();
				break;
			}
		}
	}
	
	/**
	 * Read float value (Red mode)
	 * @return
	 */
	public float getRedSensorValue(){
		float[] sample = new float[light.getRedMode().sampleSize()];
		light.getRedMode().fetchSample(sample, 0);
		return sample[0];
	}
	
	/**
	 * Displays the data collected during a calibration
	 */
	public void printInfo() {
		LCD.clearDisplay();
		
		System.out.println("- light sensor -");
		System.out.println("high: " + high);
		System.out.println("low: " + low);
		System.out.println("diff: " + diff);
		System.out.println("average: " + threshold);
		
		if(detected) {
			System.out.println("line found:");
			if(lineColor) {
				System.out.println("bright");
			} else {
				System.out.println("dark");
			}
		} else {
			System.out.println("no line found!");
		}
	}

	/**
	 * Test  
	 * @param args
	 */
	public static void main(String[] args) {
		LineDetector sensors = new LineDetector();
		new EV3Exit().start();

		DifferentialPilot pilot = new DifferentialPilot(2.42f, 11.5f, Motor.A, Motor.D, true);
		sensors.printLiveData();
		sensors.calibrate(pilot);
		sensors.printInfo();
		Button.waitForAnyPress();
		
		while(true) {
			if(sensors.isLine()){
				System.out.println("line");
			} else {
				System.out.println("no line");
			}
			Button.waitForAnyPress();
		}
	}

}
