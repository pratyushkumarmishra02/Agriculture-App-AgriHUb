package com.example.Agri_Hub;

public class SharedData {
    private static SharedData instance;
    //Get the singleTon instance
    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    private SharedData(){}

    //For farmerId
    private String farmerId;
    public void setFarmerId(String farmerId){
        this.farmerId=farmerId;
    }
    public String getFarmerId(){
        return farmerId;
    }


}


