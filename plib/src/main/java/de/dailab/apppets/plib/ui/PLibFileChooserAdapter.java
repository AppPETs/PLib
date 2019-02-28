package de.dailab.apppets.plib.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Date;
import java.util.List;

import apppets.plib.R;
import de.dailab.apppets.plib.data.Constants;
import de.dailab.apppets.plib.general.Stuff;

/**
 * Created by arik on 02.03.2017.
 */

final class PLibFileChooserAdapter extends ArrayAdapter<File> {


    private List<File> listData;
    private LayoutInflater layoutInflater;

    /**
     * @param aContext
     * @param textViewResourceId
     * @param listData
     */
    protected PLibFileChooserAdapter(Context aContext, int textViewResourceId,
                                     List<File> listData) {

        super(aContext, textViewResourceId, listData);
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }


    @Override
    public int getCount() {

        return listData.size();
    }

    @Override
    public File getItem(int position) {

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
            convertView = layoutInflater.inflate(R.layout.plib_fc_enty_layout, null);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.file_name);
            holder.tvDate = convertView.findViewById(R.id.sub_file_info);
            holder.tvSize = convertView.findViewById(R.id.file_size);
            holder.iv = convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        File fen = listData.get(position);

        long lm = fen.lastModified();
        long sz = fen.length();

        if (fen.isDirectory()) {
            holder.iv.setImageResource(R.drawable.dai_folder);
            holder.tvSize.setText("");
        } else {
            holder.iv.setImageResource(R.drawable.dai_file);
            holder.tvSize.setText(Stuff.getSizeFromByteLong(sz));
        }
        holder.tvName.setText(fen.getName());
        holder.tvDate.setText(Constants.SF.format(new Date(lm)));
        return convertView;
    }

    protected List<File> getData() {

        return listData;

    }

    final static class ViewHolder {

        TextView tvName;
        TextView tvDate;
        TextView tvSize;
        ImageView iv;

    }

}


