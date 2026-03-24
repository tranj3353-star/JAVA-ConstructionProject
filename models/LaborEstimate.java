package models;

public class LaborEstimate {
    private int numberOfWorkers;
    private double hoursWorked;
    private double hourlyRate;
    private double totalLaborCost;

    public LaborEstimate(int numberOfWorkers, double hoursWorked,
                         double hourlyRate, double totalLaborCost) {
        this.numberOfWorkers = numberOfWorkers;
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
        this.totalLaborCost = totalLaborCost;
    }

    // getters
}
