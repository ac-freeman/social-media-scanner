package com.acfreeman.socialmediascanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Andrew on 10/13/2017.
 */

public class CustomShowcodeAdapter extends ArrayAdapter<SwitchModel> implements View.OnClickListener{

    private ArrayList<SwitchModel> switchSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        Switch switcher;
        ImageView info;
        TextView switcherText;
    }

    public CustomShowcodeAdapter(ArrayList<SwitchModel> data, Context context) {
        super(context, R.layout.row_item_showcode, data);
        this.switchSet = data;
        this.mContext=context;
    }


    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
//        ViewGroup parent = v.getvi();
        Object object= getItem(position);
        SwitchModel switchModel=(SwitchModel)object;

//        CustomShowcodeAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
//        viewHolder = new CustomShowcodeAdapter.ViewHolder();
////        LayoutInflater inflater = LayoutInflater.from(getContext());
////        v = inflater.inflate(R.layout.row_item_showcode, parent, false);
//        viewHolder.switcher = (Switch) v.findViewById(R.id.switcher);
//        viewHolder.switcher.setChecked(!switchModel.getState());

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final SwitchModel switchModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final CustomShowcodeAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new CustomShowcodeAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_showcode, parent, false);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_image);
            viewHolder.switcherText = (TextView) convertView.findViewById(R.id.switcher_text);
            viewHolder.switcher = (Switch) convertView.findViewById(R.id.switcher);
//            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);


            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CustomShowcodeAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.switcher.setText(switchModel.getSwitchName());
        viewHolder.switcher.setFocusable(false);
        viewHolder.switcher.setClickable(false);
        viewHolder.switcherText.setFocusable(false);
        viewHolder.switcherText.setClickable(false);
        switchModel.setSwitcher(viewHolder.switcher);
        viewHolder.info.setTag(position);
        viewHolder.info.setImageResource(switchModel.getSwitchImage());
        viewHolder.info.setFocusable(false);
        viewHolder.info.setClickable(false);


        viewHolder.switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                switchModel.setState(b);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }



}