import ui.*;
import ui.panels.CostPanel;
import ui.panels.DimensionsPanel;
import ui.panels.LaborPanel;
import ui.panels.ProjectPanel;
import ui.panels.SummaryPanel;
import ui.panels.WelcomePanel;

public class App {

    public static void main(String[] args) {


        SummaryPanel summaryPanel = new SummaryPanel();

        MainFrame window = new MainFrame(summaryPanel);
        ContentPanel contentPanel = window.getContentPanel();

        contentPanel.addCard("WELCOME", new WelcomePanel());
        contentPanel.addCard("PROJECT", new ProjectPanel());
        contentPanel.addCard("DIMENSIONS", new DimensionsPanel());
        contentPanel.addCard("LABOR", new LaborPanel());
        contentPanel.addCard("COST", new CostPanel());
        contentPanel.addCard("SUMMARY", summaryPanel);
    }
}