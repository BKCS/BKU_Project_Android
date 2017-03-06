package com.application.hieu_nt.bkcs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by HIEU_NT on 30/11/2016.
 */
public class ListAdapter extends ArrayAdapter<About> {

    public ListAdapter(Context context, int resource, List<About> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view =  inflater.inflate(R.layout.activity_line_about, null);
        }
        About p = getItem(position);
        if (p != null) {
            // Anh xa + Gan gia tri
            TextView txt1 = (TextView) view.findViewById(R.id.textViewTieuDe);
            txt1.setText(p.TieuDe);

            TextView txt2 = (TextView) view.findViewById(R.id.textViewNoiDung);
            txt2.setText(p.NoiDung);

        }
        return view;
    }

}
