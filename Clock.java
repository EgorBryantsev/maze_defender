import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Clock {
    public String message;
    public boolean status, timeOver;
    public int clockSpeed, alarmTime, timePassed;
    private long previousTime;
    private int currentX, currentY, currentWidth, currentHeight;

    public Clock(int alarmTime){
        status = true;
        clockSpeed = 1000000000;
        this.alarmTime = alarmTime;
        timeOver = false;
        timePassed = 0;
        previousTime = System.nanoTime();
        message = "stopped";
    }

    /**
     * Update the clock's state.
     * @param s The time delta in seconds 
     */
    public void move(float s){
        runs();
    }

    /**
     * Reset the clock to start a new wave
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
        status = true; 
        message = "Time " + (/*alarmTime - */timePassed);
        previousTime = System.nanoTime();
    }


    /**
     * The main loop that updates the clock's time.
     */
    public void runs() {
        long now = System.nanoTime();
        if (status) {
            if (now - previousTime >= clockSpeed) {
                timePassed++;
                System.out.println("Elapsed Time: " + timePassed);
                message = "Time " + (/*alarmTime - */timePassed);
                previousTime = now;
            }
            if (timePassed >= alarmTime) {
                status = false;
                timeOver = true;
                message = "New Wave: Press Space";
                System.out.println("Timer stopped at: " + timePassed);
            }
        }
    }

    // Drawing the clock, width is dependent on textlength
    public void draw(Graphics2D g, int x, int y){
        Font font = new Font(Font.SERIF, Font.BOLD, 18);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(message) + 20;

        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(x, y, textWidth, 40, 15, 15);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, textWidth, 40, 15, 15);
        g.setColor(Color.WHITE);

        int textX = x + 10;
        int textY = y + ((40 - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(message, textX, textY);

        currentX = x;
        currentY = y;
        currentWidth = textWidth;
        currentHeight = 40;
    }

    /**
     * Get the bounding rectangle of the clock for click detection.
     * @return An array containing x, y, width, height.
     */
    public int[] getBounds() {
        return new int[] { currentX, currentY, currentWidth, currentHeight };
    }

    public void setPrompt(String promptMessage) {
        this.message = promptMessage;
        this.timeOver = true;
        this.status = false; // Stop the clock from updating further
    }
}
