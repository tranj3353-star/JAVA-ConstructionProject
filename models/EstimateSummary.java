package models;

public class EstimateSummary {
    private Project project;
    private ConcreteEstimate concreteEstimate;
    private LaborEstimate laborEstimate;
    private double discount;
    private double finalCost;

    public EstimateSummary(Project project,
                           ConcreteEstimate concreteEstimate,
                           LaborEstimate laborEstimate,
                           double discount,
                           double finalCost) {
        this.project = project;
        this.concreteEstimate = concreteEstimate;
        this.laborEstimate = laborEstimate;
        this.discount = discount;
        this.finalCost = finalCost;
    }

    // getters
}
