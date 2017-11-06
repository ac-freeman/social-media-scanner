package com.acfreeman.socialmediascanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.showcode.ShowcodeAdapter;
import com.acfreeman.socialmediascanner.showcode.SwitchModel;
import com.acfreeman.socialmediascanner.showfriends.ContactsAdapter;
import com.acfreeman.socialmediascanner.showfriends.DataModel;
import com.acfreeman.socialmediascanner.showfriends.ShowfriendsFragment;
import com.acfreeman.socialmediascanner.social.login.buttons.GoogleButton;
import com.google.android.gms.auth.api.Auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 11/6/2017.
 */

public class ConnectedAccountsAdapter extends ArrayAdapter<SwitchModel> implements View.OnClickListener {

    private ArrayList<SwitchModel> switchSet;
    Context mContext;
    private LocalDatabase database;
    private List<Owner> owners;
    private Owner owner;

    // View lookup cache
    private static class ViewHolder {
        ImageView info;
        TextView switcherText;
        Button button;
    }

    public ConnectedAccountsAdapter(ArrayList<SwitchModel> data, Context context) {
        super(context, R.layout.row_item_showcode, data);
        this.switchSet = data;
        this.mContext = context;
    }


    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        SwitchModel switchModel = (SwitchModel) object;

    }

    private int lastPosition = -1;
    GoogleButton googleButton;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final SwitchModel switchModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ConnectedAccountsAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new ConnectedAccountsAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            if (switchModel.getUser_id() != null) {
                convertView = inflater.inflate(R.layout.row_item_connected_accounts_on, parent, false);
            } else {

                convertView = inflater.inflate(R.layout.row_item_connected_accounts_off, parent, false);
            }


            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_image);
            viewHolder.switcherText = (TextView) convertView.findViewById(R.id.switcher_text);
//            viewHolder.switcher = (Switch) convertView.findViewById(R.id.switcher);


            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ConnectedAccountsAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        lastPosition = position;


        viewHolder.switcherText.setText(switchModel.getSwitchName());
        viewHolder.switcherText.setFocusable(false);
        viewHolder.switcherText.setClickable(false);
        viewHolder.info.setTag(position);
        viewHolder.info.setImageResource(switchModel.getSwitchImage());
        viewHolder.info.setFocusable(false);
        viewHolder.info.setClickable(false);


        if (switchModel.getUser_id() != null) {
            //Social account is connected

            viewHolder.button = (Button) convertView.findViewById(R.id.button);

            database = new LocalDatabase(mContext);
            owners = database.getAllOwner();
            owner = owners.get(0);


            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Social s = new Social(owner.getId(), switchModel.getTag(), switchModel.getUser_id());

                    database.deleteUserSocial(s);
                    remove(switchModel);
                    add(new SwitchModel(s.getType()));
                    notifyDataSetChanged();

                }
            });


        } else {

            viewHolder.button = (Button) convertView.findViewById(R.id.button);
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = switchModel.getTag();
                    switch (tag) {
                        case "go": {
                            googleButton = new GoogleButton(getContext(), (FragmentActivity) mContext);
                            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleButton.apiClient);
                            ((FragmentActivity) mContext).startActivityForResult(signInIntent, googleButton.RC_SIGN_IN);
                        }


                    }
                    remove(switchModel);
                    for (Social s : database.getUserSocials(owner.getId())) {
                        if (s.getType().equals(tag)) {
                            add(new SwitchModel(tag, s.getUsername()));
                            notifyDataSetChanged();
                            break;
                        }
                    }
                }
            });
        }

        // Return the completed view to render on screen
        return convertView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");
        if (requestCode == googleButton.RC_SIGN_IN) {
            googleButton.onActivityResult(requestCode, resultCode, data);
        }

    }


}