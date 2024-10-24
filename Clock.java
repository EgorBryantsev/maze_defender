import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * class Klok - geef hier een beschrijving van deze class
 *
 * @author (jouw naam)
 * @version (versie nummer of datum)
 */
public class Clock {
    public String melding;
    public boolean status, tijdIsOm;
    public int snelheid, alarmtijd, verlopenTijd;
    private long vorigeTijd;
    
    // Store position and size for potential future use
    private int x, y, breedte, hoogte;

    public Clock(int x, int y, int breedte, int hoogte, int alarmtijd){
        this.x = x;
        this.y = y;
        this.breedte = breedte;
        this.hoogte = hoogte;
        status = true;
        snelheid = 1000000000; // 1 second in nanoseconds
        this.alarmtijd = alarmtijd;
        tijdIsOm = false;
        verlopenTijd = 0;
        vorigeTijd = System.nanoTime();
        melding = "gestopt";
    }

    /**
     * Update the clock's state.
     * @param s The time delta in seconds (not used in current implementation).
     */
    public void beweeg(float s){
        loopt();
    }
    
    /**
     * Reset the clock to its initial state.
     */
    public void reset(){
        verlopenTijd = 0;
        tijdIsOm = false;
        melding = "New Wave";
        vorigeTijd = System.nanoTime();
    }
    
    /**
     * Start the clock.
     */
    public void start() {
        status = true;  // Set clock to running state
        melding = "Time " + (alarmtijd - verlopenTijd);
        vorigeTijd = System.nanoTime();  // Set the start time
    }
    

    /**
     * The main loop that updates the clock's time.
     */
    public void loopt() {
        long now = System.nanoTime();
        if (status) {
            if (now - vorigeTijd >= snelheid) {
                verlopenTijd++;
                System.out.println("Elapsed Time: " + verlopenTijd);  // Debugging line
                melding = "Time " + (alarmtijd - verlopenTijd);
                vorigeTijd = now;
            }
            if (verlopenTijd >= alarmtijd) {
                status = false;
                tijdIsOm = true;
                melding = "New Wave: Press Space";
                System.out.println("Timer stopped at: " + verlopenTijd);  // Debugging line
            }
        }
    }
    
    

    /**
     * Draw the clock on the screen.
     * @param g The Graphics2D object to draw with.
     */
    public void teken(Graphics2D g){
        // Draw the clock background
        g.setColor(Color.BLACK);
        g.fillRect(x, y, 80, 40);
        
        // Set font for the melding text
        g.setFont(new Font(Font.SERIF, Font.ITALIC, 18));
        
        // Draw the melding text
        g.setColor(Color.RED);
        g.drawString(melding, x + 5, y + 30);
    }
    
    /**
     * Get the bounding rectangle of the clock for click detection.
     * @return An array containing x, y, width, height.
     */
    public int[] getBounds() {
        return new int[] { x, y, 80, 40 };
    }
}