package com.acfreeman.socialmediascanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.acfreeman.socialmediascanner.social.SocialSwitch;

import java.util.ArrayList;

/**
 * Created by Andrew on 10/13/2017.
 */

public class CustomShowcodeAdapter extends ArrayAdapter<SwitchModel> implements View.OnClickListener{

    private ArrayList<SwitchModel> switchSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        Switch swName;
        ImageView info;
    }

    public CustomShowcodeAdapter(ArrayList<SwitchModel> data, Context context) {
        super(context, R.layout.row_item_showcode, data);
        this.switchSet = data;
        this.mContext=context;

    }



    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        SwitchModel switchModel=(SwitchModel)object;

        switch (v.getId())
        {
            case R.id.item_info:
//                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SwitchModel switchModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        CustomShowcodeAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new CustomShowcodeAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_showcode, parent, false);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_image);
            viewHolder.swName = (Switch) convertView.findViewById(R.id.name);
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

        viewHolder.swName.setText(switchModel.getSwitch().getText());
//        viewHolder.txtType.setText(dataModel.getType());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}