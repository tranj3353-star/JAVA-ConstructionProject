package models;

public class InfoManager {
    private static InfoManager instance;

    public String projectName;
    public String projectLocation;
    public String projectClient;
    
    public double length;
    public double width;
    public double totalArea;

    public String slabType;
    public double thickness;
    public double wasteFactor;
    public double concreteNeeded;

    public static InfoManager getInstance() {
        if (instance == null) {
            instance = new InfoManager();
        }
        return instance;
    }

}
