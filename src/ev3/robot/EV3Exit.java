package ev3.robot;

import lejos.hardware.Button;


/**
 * Simple thread which quits the application if a button is pressed
 * @author Oliver
 */
public class EV3Exit extends Thread{

	private int exitButton;
	
	/**
	 * Default constructor with escape as exit button
	 */
	public EV3Exit(){
		this.exitButton = Button.ID_ESCAPE;
	}
	
	/**
	 * User defined exit button
	 * @param exitButton
	 */
	public EV3Exit(int exitButton){
		this.exitButton = exitButton;
	}
	
	/**
	 * Starts the thread
	 */
	@Override
	public void run() {
		while (true) {
			if(Button.readButtons() == exitButton){
				System.exit(MAX_PRIORITY);
			}
		}
	}

}
