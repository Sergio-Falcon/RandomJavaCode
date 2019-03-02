import java.awt.MouseInfo;
import java.util.concurrent.TimeUnit;

public class Cords {

    public static void main(String[] args) throws InterruptedException {

        //get cords of mouse code, outputs to console every 1/2 second
        //make sure to import and include the "throws in the main method"

        while(true)
        {
        TimeUnit.SECONDS.sleep(1/2);
        double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
        double mouseY = MouseInfo.getPointerInfo().getLocation().getY();
        //wSystem.out.println("X: " + mouseX + " Y: " + mouseY);
        System.out.println("Coordinates: (" + mouseX +", " + mouseY + ")");
        //System.out.println("Y:" + mouseY);
        //make sure to import 
        }

    }

}