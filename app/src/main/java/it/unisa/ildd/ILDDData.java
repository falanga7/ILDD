package it.unisa.ildd;

import java.util.ArrayList;

public class ILDDData {
    private long currentMs;
    private LinearAcceleration linearAcceleration;

    public long getCurrentMs() {
        return currentMs;
    }

    public void setCurrentMs(long currentMs) {
        this.currentMs = currentMs;
    }

    public LinearAcceleration getLinearAcceleration() {
        return linearAcceleration;
    }

    public void setLinearAcceleration(LinearAcceleration linearAcceleration) {
        this.linearAcceleration = linearAcceleration;
    }

    public OrientationAPR getOrientationAPR() {
        return orientationAPR;
    }

    public void setOrientationAPR(OrientationAPR orientationAPR) {
        this.orientationAPR = orientationAPR;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public ArrayList<NetworkRecord> getNetworkRecordArrayList() {
        return networkRecordArrayList;
    }

    public void setNetworkRecordArrayList(ArrayList<NetworkRecord> networkRecordArrayList) {
        this.networkRecordArrayList = networkRecordArrayList;
    }



    private OrientationAPR orientationAPR;
    private Position position;
    private ArrayList<NetworkRecord> networkRecordArrayList;
}
