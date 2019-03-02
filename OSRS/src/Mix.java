import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

public class Mix implements Runnable {
	//private int nClick = 0;
	private int interval = 0;
	private Robot robot;
	private Task mixPotion;
	private int delay = 600;
	
	public Mix(Task mixPotion, int interval) {
		try {
			robot = new Robot();
			
			this.interval = interval;
			this.mixPotion = mixPotion;
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			mixPotion.setProgressState(false);
			Thread.sleep(3000); //wait 3 seconds before running
			
			//running task
			for (int i=0; i < interval; i++){
					/** START **/
				
				System.out.println("Mixing Herb - Interval: " + (i+1) + ".");
				
				/** Click on Bank **/
				//click on bank teller
				robot.mouseMove(240, 330);
				robot.delay(delay);
				
				//left click
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
				robot.delay(delay);
				
				
				/** empty inventory to bank **/
				//move mouse to dump inventory button
				robot.mouseMove(460, 435);
				robot.delay(delay);
				
				//left click
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
				robot.delay(delay);
				
				/** Vials of water **/
				//move mouse to vials of water
				robot.mouseMove(440, 195);
				robot.delay(delay);
				//right click
				robot.mousePress(InputEvent.BUTTON3_MASK);
				robot.mouseRelease(InputEvent.BUTTON3_MASK);
				robot.delay(delay);
				
				//move to get 14 option
				robot.mouseMove(440, 262);
				robot.delay(delay);
				
				//left click option
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
				robot.delay(delay);
				
				/** Herb **/ //items are typically 45 px apart from each other
				//move mouse to herb
				robot.mouseMove(110, 260); /** Changes depending on herb **/
				robot.delay(delay);
				
				//right click
				robot.mousePress(InputEvent.BUTTON3_MASK);
				robot.mouseRelease(InputEvent.BUTTON3_MASK);
				robot.delay(delay);
				
				//move to get 14 option
				robot.mouseMove(110, 330); /** Changes depending on herb **/
				robot.delay(delay);
				
				//left click option
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
				robot.delay(delay);
				
				/** Leave bank **/
				//press esc
				robot.keyPress(KeyEvent.VK_ESCAPE);
				robot.keyRelease(KeyEvent.VK_ESCAPE);
				robot.delay(delay);
				
				/** Mix **/
				//move mouse to vial of water
				robot.mouseMove(645, 427);
				robot.delay(delay);
				
				//left click
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
				robot.delay(delay);
				
				//move mouse to herb
				robot.mouseMove(690, 427);
				robot.delay(delay);
				
				//left click
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
				robot.delay(delay);
				
				// press space 
				robot.delay(delay);
				robot.keyPress(KeyEvent.VK_SPACE);
				robot.keyRelease(KeyEvent.VK_SPACE);
				robot.delay(delay);
				
				/** Delay for duration of time until 14 are finished **/
				//wait
				Thread.sleep(12000);
			}
			
			mixPotion.setProgressState( true );
			JOptionPane.showMessageDialog( null, "Done." );
		} catch (InterruptedException e) {
		e.printStackTrace();
		}
	}
}