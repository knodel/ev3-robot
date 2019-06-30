package ev3.robot;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lejos.hardware.Battery;
import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.robotics.navigation.DifferentialPilot;

public class TestPilot {
	
	private DifferentialPilot pilot;
	private LineDetector bwSensor;
	
	private BarrierDetector detect;

	private static int rotateStep = 20;
		
	public boolean lineSearch(int angle){
		System.out.println("Searching...");
		System.out.println("   Rotating: " + angle);
		pilot.rotate(angle, true);
	    while(pilot.isMoving()){
	    		if(bwSensor.isLine()) {
	    			System.out.println("   Found line");
	    			pilot.stop();
	    			return true;
	    		}
	    }
	    System.out.println("   Rotating: " + (-angle) );
	    pilot.rotate(-angle, true);
	    return false;
	}
	
	public void setBarrierDetector(BarrierDetector detect) {
		this.detect = detect;
	}
	
	public boolean scan360() {
		
		List<Integer> lineList = new ArrayList<Integer>();
		
		int position = 0;
		int count = 0;
		
		pilot.rotate(360, true);
		while (pilot.isMoving()) {
			if(bwSensor.isLine()) {
				while(bwSensor.isLine()) {
					position += pilot.getAngleIncrement();
					count++;
				}
				lineList.add(position / count);
			}
		}
		
		System.out.println("Lines: " + lineList.size());
		
		if (lineList.size() == 0) {
			System.out.println("ERROR!");
			return false;
		}
		
		if (lineList.size() == 1) {
			System.out.println("END!");
			pilot.rotate(lineList.get(0));
			return true;
		}
		
		Iterator<Integer> iter = lineList.iterator();
		while(iter.hasNext()){
			int nextPosition = iter.next();
			System.out.println(nextPosition);
			if(nextPosition < 160) {
				lineSearch(160);
			}
			if(nextPosition > 200) {
				lineSearch(-160);
			}
		}
		return true;
	}
	
	public void go() {
		System.out.println("Battery: " + Battery.getVoltage());
		
		bwSensor = new LineDetector();
		
		boolean foundLine = false;
		int angle = 0;
		
		//calibrate Light sensor
		while(foundLine == false) {
			if(bwSensor.calibrate(pilot)) {
				System.out.println("Found line:");
				if(bwSensor.getLineColor()) {
					System.out.println("   bright");
				} else {
					System.out.println("   dark");
				}
				foundLine = true;
			} else{
				System.out.println("No line!");
				Button.waitForAnyPress();
			}
		}
		
		System.out.println("Starting...");
		
		//set travel speed
		pilot.setAcceleration(5);//5
		pilot.setTravelSpeed(5);//3
		pilot.setRotateSpeed(12);//12
		
		//search start
		lineSearch(360);
		
		while(true){
			foundLine = false;
			angle = rotateStep;
			
			System.out.println("Moving forward");
			pilot.forward();
			
		    while(pilot.isMoving()){
		    		if(detect.getBarrier()) {
		    			pilot.stop();
		    			while(detect.getBarrier()){
		    				try {
		    					Thread.sleep(10);
		    				} catch (InterruptedException e) {
		    					System.out.println("Error sleeping!");
		    				}
		    			}
		    			pilot.forward();
		    		}
		    			
			    	if( bwSensor.isLine() == false ) {
			    		pilot.stop();
			    		System.out.println("Lost line");
			    	}
		    }
		    
		    while(angle < 180) {
		    		foundLine = lineSearch(angle);
		    		if(foundLine) break;
		    		angle = (angle + rotateStep);
		    		foundLine = lineSearch(-angle);
		    		if(foundLine) break;
		    		angle = angle + rotateStep;
		    		
		    		if(detect.getBarrier()) {
		    			while(detect.getBarrier()){
		    				try {
		    					Thread.sleep(10);
		    				} catch (InterruptedException e) {
		    					System.out.println("Error sleeping!");
		    				}
		    			}
		    		}
		    }
		    /*
		    if(angle >= 40) {
				scan360();
		    }
		    */
		}
		
	}
	
    public static void main(String[] args) {
    		
    		new EV3Exit().start();
    		
    		BarrierDetector detect = new BarrierDetector();
    		detect.start();
    		
    	    System.out.println("Starting pilot!");
    		
    		TestPilot traveler = new TestPilot();
    		traveler.setBarrierDetector(detect);
    		traveler.pilot = new DifferentialPilot(2.42f, 11.5f, Motor.A, Motor.D, true);
    		traveler.go();
    		
    		
    }
}
