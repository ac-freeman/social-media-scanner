package com.acfreeman.socialmediascanner.social.login;


import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.social.SocialMediaLoginActivity;
import com.acfreeman.socialmediascanner.social.login.buttons.TwitterButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Andrew on 11/1/2017.
 */

public class TwitterFragment extends Fragment {

    TwitterButton twitterButton;
    public LocalDatabase database;
    public List<Owner> owners;
    public Owner owner;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Twitter.initialize(getActivity());

        final View view = inflater.inflate(R.layout.fragment_login,
                container, false);



        RelativeLayout background = view.findViewById(R.id.background);
        background.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.twitter_blue));

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.twitter_logo_white);
        android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = SocialMediaLoginActivity.convertDpToPixel(125, getContext());
        layoutParams.height = SocialMediaLoginActivity.convertDpToPixel(125, getContext());
        imageView.setLayoutParams(layoutParams);

        twitterButton = new TwitterButton(getContext(), getActivity());



        Button visibleButton = view.findViewById(R.id.login_button);
        visibleButton.setText("Sign in with Twitter");
        visibleButton.setTextColor(ContextCompat.getColor(getContext(), R.color.twitter_blue));
        visibleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterButton.getButton().performClick();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        twitterButton.getButton().onActivityResult(requestCode, resultCode, data);
    }
}