package com.acfreeman.socialmediascanner.showfriends;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import com.acfreeman.socialmediascanner.R;

import java.util.ArrayList;

/**
 * Created by Andrew on 10/13/2017.
 */

public class ContactsAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener{

    private ArrayList<DataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        ImageView info;
        CheckBox checkBox;
    }

    public ContactsAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.row_item_contacts, data);
        this.dataSet = data;
        this.mContext=context;
        for(int b=0;b<dataSet.size();b++){
            checks.add(b,0);  //assign 0 by default in each position of ArrayList
        }

    }

    public void clearData(){
        this.dataSet = null;
    }

    public void toggleEditMode() {
        this.inEditmode = !this.inEditmode;
    }

    @Override
    public void onClick(View v) {
        //WE OVERRIDE THIS LISTENER IN MAINACTIVITY
//        int position=(Integer) v.getTag();
//        Object object= getItem(position);
//        DataModel dataModel=(DataModel)object;
//        Log.i("CONTACTDEBUG", "Item clicked!");
//
////        switch (v.getId())
////        {
////            case R.id.item_info:
//////                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
//////                        .setAction("No action", null).show();
////                break;
////        }
    }

    private int lastPosition = -1;
    public Boolean inEditmode = false;
    public ArrayList<Integer> checks=new ArrayList<Integer>();

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_contacts, parent, false);
            result=convertView;
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
            viewHolder.info = (ImageView) result.findViewById(R.id.item_info);
            viewHolder.txtName = (TextView) result.findViewById(R.id.name);
            viewHolder.checkBox = (CheckBox) result.findViewById(R.id.checkbox);
//            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            Log.i("CONTACTDEBUG", "In edit mode? " + inEditmode);
            if(inEditmode){
                if(checks.get(position)==1) {
                    Log.i("CONTACTDEBUG","Position = " + position);
                    viewHolder.checkBox.setChecked(true);
                } else {
                    viewHolder.checkBox.setChecked(false);
                }
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkBox.setVisibility(View.INVISIBLE);
            }

            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(((CheckBox)view).isChecked()){
                        checks.set(position,1);
                        Log.i("CONTACTDEBUG", "CheckBox at  " +position+ " is 1");
                    } else {
                        checks.set(position, 0);
                    }
                }
            });


            result.setTag(viewHolder);



//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getName());
//        viewHolder.txtType.setText(dataModel.getType());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}