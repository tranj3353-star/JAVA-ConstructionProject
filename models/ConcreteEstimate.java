package models;

public class ConcreteEstimate {
    private double volumeCubicFeet;
    private double volumeCubicYards;
    private double costPerYard;
    private double totalMaterialCost;

    public ConcreteEstimate(double volumeCubicFeet, double volumeCubicYards,
                            double costPerYard, double totalMaterialCost) {
        this.volumeCubicFeet = volumeCubicFeet;
        this.volumeCubicYards = volumeCubicYards;
        this.costPerYard = costPerYard;
        this.totalMaterialCost = totalMaterialCost;
    }

    // getters
}
