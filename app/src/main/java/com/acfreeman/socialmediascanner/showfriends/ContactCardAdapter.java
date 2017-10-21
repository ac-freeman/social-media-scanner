package com.acfreeman.socialmediascanner.showfriends;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acfreeman.socialmediascanner.R;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Andrew on 10/20/2017.
 */

public class ContactCardAdapter extends ArrayAdapter<CardDataModel> implements View.OnClickListener{

private ArrayList<CardDataModel> dataSet;
        Context mContext;

// View lookup cache
private static class ViewHolder {
    TextView txtName;
    ImageView image;
}

    public ContactCardAdapter(ArrayList<CardDataModel> data, Context context) {
        super(context, R.layout.row_item_contact_card, data);
        this.dataSet = data;
        this.mContext=context;
    }

    public void clearData(){
        this.dataSet = null;
    }


    private int lastPosition = -1;

    @Override
    public void onClick(View v) {
       

    }


    public void actionTest(){


    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        CardDataModel cardDataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ContactCardAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        viewHolder = new ContactCardAdapter.ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_contact_card, parent, false);
            result=convertView;
        }
        else {
            viewHolder = (ContactCardAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }
        viewHolder.image = (ImageView) result.findViewById(R.id.contact_card_item_image);
        viewHolder.txtName = (TextView) result.findViewById(R.id.contact_card_item_text);
//            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
//            Log.i("CONTACTDEBUG", "In edit mode? " + inEditmode);

        result.setTag(viewHolder);



//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;

        switch ((cardDataModel.getTag())){
            case 'p':
                viewHolder.txtName.setText(Long.toString(cardDataModel.getPhone().getNumber()));
                break;
            case 'e':
                viewHolder.txtName.setText(cardDataModel.getEmail().getEmail());
                break;
            case 's':
                viewHolder.txtName.setText(cardDataModel.getSocial().getType());
                break;
        }

        viewHolder.image.setImageResource(cardDataModel.getImage());

//        viewHolder.image.setOnClickListener(this);
        viewHolder.image.setTag(position);
        // Return the completed view to render on screen
//        convertView.setOnClickListener(this);
        return convertView;
    }

}
