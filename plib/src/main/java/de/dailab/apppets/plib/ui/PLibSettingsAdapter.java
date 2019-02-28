package de.dailab.apppets.plib.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import apppets.plib.R;


/**
 * Created by arik on 27.02.2017.
 */

final class PLibSettingsAdapter extends ArrayAdapter<PlibSettingsItem> {

    private List<PlibSettingsItem> listData;
    private LayoutInflater layoutInflater;

    protected PLibSettingsAdapter(Context aContext, int textViewResourceId,
                                  List<PlibSettingsItem> listData) {

        super(aContext, textViewResourceId, listData);
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {

        return listData.size();
    }

    @Override
    public PlibSettingsItem getItem(int position) {

        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.plib_settings_entry, null);
            holder = new ViewHolder();
            holder.tvTitle = convertView.findViewById(R.id.title);
            holder.tvSubTitle = convertView.findViewById(R.id.title2);
            holder.imL = convertView.findViewById(R.id.icon_left);
            holder.imR = convertView.findViewById(R.id.icon_right);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PlibSettingsItem set = listData.get(position);
        int action = set.getAction();
        holder.tvTitle.setText(set.getTitle());
        holder.tvSubTitle.setText(set.getSubTitle());
        holder.tvTitle.setSelected(true);
        holder.tvSubTitle.setSelected(true);
        int icL = set.getIconLeft();
        int icR = set.getIconRight();
        if (icL != -1) {
            holder.imL.setImageResource(icL);
        } else {
            holder.imL.setImageResource(0);
        }
        if (icR != -1) {
            holder.imR.setImageResource(icR);
        } else {
            holder.imR.setImageResource(0);
        }
        Typeface tf = holder.tvTitle.getTypeface();
        if (action > 0) {
            holder.tvTitle.setTypeface(tf, Typeface.BOLD);
        } else {
            holder.tvTitle.setTypeface(tf, Typeface.ITALIC);
        }
        return convertView;
    }

    protected List<PlibSettingsItem> getData() {

        return listData;

    }

    final static class ViewHolder {

        TextView tvTitle;
        TextView tvSubTitle;
        ImageView imL;
        ImageView imR;
    }

}
