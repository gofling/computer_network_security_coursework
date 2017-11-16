import java.applet.Applet;
import java.awt.*;

public class Main extends Applet {
    public void paint(Graphics g) {
        setBackground(Color.YELLOW);
        g.setColor(Color.RED);
        g.setFont(new Font("Helvetica", Font.BOLD, 48));
        g.drawString("Hello World!", 50, 60);
    }

}


