import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Clock {
    public String message;
    public boolean status, timeOver;
    public int clockSpeed, alarmTime, timePassed;
    private long previousTime;

    public Clock(int alarmTime){
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
    public void draw(Graphics2D g, int x, int y){
        Font font = new Font(Font.SERIF, Font.ITALIC, 18);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(message) + 20; // Added padding

        // Background rectangle
        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(x, y, textWidth, 40, 15, 15); // Rounded corners for aesthetics

        // Draw border
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, textWidth, 40, 15, 15);

        // Draw the text
        g.setColor(Color.RED);
        // Vertically center the text
        int textX = x + 10;
        int textY = y + ((40 - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(message, textX, textY);
    }
    
    /**
     * Get the bounding rectangle of the clock for click detection.
     * @return An array containing x, y, width, height.
     */
    public int[] getBounds(int x, int y) {
        Font font = new Font(Font.SERIF, Font.ITALIC, 18);
        FontMetrics fm = new FontMetrics(font) {};
        int textWidth = fm.stringWidth(message) + 20;
        return new int[] { x, y, textWidth, 40 };
    }
}