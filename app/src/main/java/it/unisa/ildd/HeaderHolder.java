package it.unisa.ildd;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class HeaderHolder extends RecyclerView.ViewHolder {

    private TextView headerView1;


    public HeaderHolder(View v) {
        super(v);
        headerView1 = (TextView) v.findViewById(R.id.headerView1);


    }

    public TextView getHeaderView1() {
        return headerView1;
    }

    public void setHeaderView1(TextView headerView1) {
        this.headerView1 = headerView1;
    }

}
