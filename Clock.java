import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * class Klok - geef hier een beschrijving van deze class
 *
 * @author (jouw naam)
 * @version (versie nummer of datum)
 */
public class Clock {
    public String message;
    public boolean status, timeOver;
    public int clockSpeed, alarmTime, timePassed;
    private long previousTime;
    
    // Store position and size for potential future use
    private final int x;
    private final int y;

    public Clock(int x, int y, int width, int height, int alarmTime){
        this.x = x;
        this.y = y;
        status = true;
        clockSpeed = 1000000000; // 1 second in nanoseconds
        this.alarmTime = alarmTime;
        timeOver = false;
        timePassed = 0;
        previousTime = System.nanoTime();
        message = "stopped";
    }

    /**
     * Update the clock's state.
     * @param s The time delta in seconds (not used in current implementation).
     */
    public void move(float s){
        runs();
    }
    
    /**
     * Reset the clock to its initial state.
     */
    public void reset(){
        timePassed = 0;
        timeOver = false;
        message = "New Wave";
        previousTime = System.nanoTime();
    }
    
    /**
     * Start the clock.
     */
    public void start() {
        status = true;  // Set clock to running state
        message = "Time " + (alarmTime - timePassed);
        previousTime = System.nanoTime();  // Set the start time
    }
    

    /**
     * The main loop that updates the clock's time.
     */
    public void runs() {
        long now = System.nanoTime();
        if (status) {
            if (now - previousTime >= clockSpeed) {
                timePassed++;
                System.out.println("Elapsed Time: " + timePassed);  // Debugging line
                message = "Time " + (alarmTime - timePassed);
                previousTime = now;
            }
            if (timePassed >= alarmTime) {
                status = false;
                timeOver = true;
                message = "New Wave: Press Space";
                System.out.println("Timer stopped at: " + timePassed);  // Debugging line
            }
        }
    }
    
    

    /**
     * Draw the clock on the screen.
     * @param g The Graphics2D object to draw with.
     */
    public void draw(Graphics2D g){

        g.setFont(new Font(Font.SERIF, Font.ITALIC, 18));

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(message + "  ");
        // Draw the clock background
        g.setColor(Color.BLACK);
        g.fillRect(x, y, textWidth, 40);
        
        // Set font for the message text
      
        // Draw the message text
        g.setColor(Color.RED);
        g.drawString(message, x + 5, y + 30);
    }
    
    /**
     * Get the bounding rectangle of the clock for click detection.
     * @return An array containing x, y, width, height.
     */
    public int[] getBounds() {
        return new int[] { x, y, 80, 40 };
    }
}