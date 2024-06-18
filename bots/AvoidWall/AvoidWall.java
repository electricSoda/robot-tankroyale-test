import dev.robocode.tankroyale.botapi.*;
import dev.robocode.tankroyale.botapi.events.*;
import java.util.*;
import java.net.URI;

// ------------------------------------------------------------------
// AvoidWall
// ------------------------------------------------------------------
// Moves randomly around the board, if it sees an enemy it will shoot.
// Will not run into walls
// ------------------------------------------------------------------
public class AvoidWall extends Bot {

    // The main method starts our bot
    public static void main(String[] args) {
        URI uri;
        try {
            uri = new URI("ws://192.168.1.31:3000");
        } catch (Exception e) {
            e.printStackTrace();
            uri = null;
        }
        new AvoidWall(uri).start();
    }

    // Constructor, which loads the bot config file
    AvoidWall(URI uri) {
        super(BotInfo.fromFile("AvoidWall.json"), uri, "Xo0vViwZ9oBeokHKBOFU4g");
    }

    /**
     *
     * @param bearing amount we are turning to the left
     * @param magnitude the amount we are going forward in the bearing
     * @return if we will run into a wall, returns true
     */
    public boolean checkIfWillRunIntoWall(int bearing, int magnitude) {
        double currentHeading = getDirection();
        double newHeading = (bearing + currentHeading) % 360; // in degrees
        // get the predicted new positions
        double newX = getX() + Math.cos(newHeading*Math.PI/180) * magnitude;
        double newY = getY() + Math.sin(newHeading*Math.PI/180) * magnitude;


        // check if the new position will result in the tank crashing into a wall
        if (newX < 36 || newY < 36 || newX > getArenaWidth() - 36 || newY > getArenaHeight() - 36)
            return true; // we will hit a wall!

        return false; // we will not hit a wall
    }

    // Called when a new round is started -> initialize and do some movement
    @Override
    public void run() {
        // Repeat while the bot is running

        while (isRunning()) {
            int magnitude = 100;
            int bearing = -180 + (int)(Math.random() * (180-(-180)+1)); // between -180 and 180
            while (checkIfWillRunIntoWall(bearing, magnitude)) {
                bearing = -180 + (int)(Math.random() * (180-(-180)+1)); // between -180 and 180
            }
            turnLeft(bearing);
            forward(magnitude);

            turnGunRight(360); // turns the gun 360 to scan it's environment
        }
    }

    // We saw another bot -> fire!
    @Override
    public void onScannedBot(ScannedBotEvent e) {
        fire(1);
    }
}
