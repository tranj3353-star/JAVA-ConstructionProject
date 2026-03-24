package ui;
import java.awt.BorderLayout;
import javax.swing.*;

public class MainFrame extends JFrame {

    ContentPanel contentPanel;

    public MainFrame()
    {
        setTitle("Concrete Price Calculator");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.contentPanel = new ContentPanel();
        add(this.contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public ContentPanel getContentPanel()
    {
        return this.contentPanel;
    }
}
