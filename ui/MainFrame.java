package ui;
import java.awt.BorderLayout;
import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame()
    {
        setTitle("Concrete Price Calculator");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        add(new ContentPanel(), BorderLayout.CENTER);
    }
}
