package ui;
import java.awt.*;
import javax.swing.*;

public class ContentPanel extends JPanel{

    public ContentPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("LOLOLOL");
        JLabel a = new JLabel("LOLOLOL");
        add(label);
        add(a);

        label.setHorizontalAlignment(SwingConstants.CENTER);
        a.setHorizontalAlignment(SwingConstants.CENTER);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        a.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
    }
    
}
