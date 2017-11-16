package com.acfreeman.socialmediascanner.social.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.social.SocialMediaLoginActivity;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Andrew on 11/16/2017.
 */

public class IntroFragment extends Fragment {


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public LocalDatabase database;
    public List<Owner> owners;
    public Owner owner;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        database = new LocalDatabase(getApplicationContext());
        owners = database.getAllOwner();
        owner = owners.get(0);

        List<Social> socials = database.getUserSocials(owner.getId());


            view = inflater.inflate(R.layout.fragment_login_intro,
                    container, false);
        TextView tv = (TextView) view.findViewById(R.id.appNameTextView);
        Typeface tf = Typeface.createFromAsset(view.getContext().getAssets(), "font/gabriola.ttf");
        tv.setTypeface(tf);

        TextView tv2 = (TextView) view.findViewById(R.id.instructionsTextView);
        tv2.setTypeface(tf);

        LinearLayout background = view.findViewById(R.id.background);
        background.setBackgroundColor(Color.WHITE);

//        ImageView imageView = view.findViewById(R.id.imageView);
//        imageView.setImageResource(R.drawable.google_title_color);
//        android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
//        layoutParams.width = SocialMediaLoginActivity.convertDpToPixel(200, getContext());
//        layoutParams.height = SocialMediaLoginActivity.convertDpToPixel(68, getContext());
//        imageView.setLayoutParams(layoutParams);





        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onPause() {
        super.onPause();

    }
}