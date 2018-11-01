package it.unisa.ildd;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class SensorsHolder extends RecyclerView.ViewHolder {

    private TextView header, values;

    public SensorsHolder(View v) {
        super(v);
        header = (TextView) v.findViewById(R.id.header);
        values = (TextView) v.findViewById(R.id.values);
    }

    public TextView getHeader() {
        return header;
    }

    public void setHeader(TextView header) {
        this.header = header;
    }

    public TextView getValues() {
        return values;
    }

    public void setValues(TextView values) {
        this.values = values;
    }
}