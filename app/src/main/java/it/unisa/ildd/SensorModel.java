package it.unisa.ildd;


import android.text.Spanned;

public class SensorModel {
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Spanned getValues() {
        return values;
    }

    public void setValues(Spanned values) {
        this.values = values;
    }

    private String header;
    private Spanned values;


}
