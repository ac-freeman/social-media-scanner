package com.acfreeman.socialmediascanner.showcode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.acfreeman.socialmediascanner.R;

import java.util.ArrayList;

/**
 * Created by Andrew on 10/13/2017.
 */

public class ShowcodeAdapter extends ArrayAdapter<SwitchModel> implements View.OnClickListener{

    private ArrayList<SwitchModel> switchSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        Switch switcher;
        ImageView info;
        TextView switcherText;
    }

    public ShowcodeAdapter(ArrayList<SwitchModel> data, Context context) {
        super(context, R.layout.row_item_showcode, data);
        this.switchSet = data;
        this.mContext=context;
    }


    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        SwitchModel switchModel=(SwitchModel)object;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final SwitchModel switchModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ShowcodeAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ShowcodeAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_showcode, parent, false);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_image);
            viewHolder.switcherText = (TextView) convertView.findViewById(R.id.switcher_text);
            viewHolder.switcher = (Switch) convertView.findViewById(R.id.switcher);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ShowcodeAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.switcher.setText("");
        viewHolder.switcher.setFocusable(false);
        viewHolder.switcher.setClickable(false);
        viewHolder.switcher.setChecked(switchModel.getState());
        viewHolder.switcherText.setText(switchModel.getSwitchName());
        viewHolder.switcherText.setFocusable(false);
        viewHolder.switcherText.setClickable(false);
        switchModel.setSwitcher(viewHolder.switcher);
        viewHolder.info.setTag(position);
        viewHolder.info.setImageResource(switchModel.getSwitchImage());
        viewHolder.info.setFocusable(false);
        viewHolder.info.setClickable(false);

        // Return the completed view to render on screen
        return convertView;
    }



}