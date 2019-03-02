import java.awt.Robot;
import java.awt.event.InputEvent;

import javax.swing.JOptionPane;


public class StartClick implements Runnable {
	
	private int nClick = 0;
	private int interval = 0;
	private Robot robot;
	private AutoClicker autoClicker;
	
	public StartClick( AutoClicker autoClicker, int nClick, int interval ) {
		try {
			robot = new Robot();
			
			this.nClick = nClick;
			this.interval = interval;
			this.autoClicker = autoClicker;
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			autoClicker.setProgressState( false );
			Thread.sleep( 3000 );

			for( int i=0; i<nClick; i++ ) {
				robot.mousePress( InputEvent.BUTTON1_MASK );
				robot.mouseRelease( InputEvent.BUTTON1_MASK );
				Thread.sleep( interval );
			}
			
			autoClicker.setProgressState( true );
			JOptionPane.showMessageDialog( null, "Done." );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
