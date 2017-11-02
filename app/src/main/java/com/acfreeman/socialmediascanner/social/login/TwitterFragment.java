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
import android.widget.Toast;

import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.social.SocialMediaLoginActivity;
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
    TwitterLoginButton loginButton;

    public LocalDatabase database;
    public List<Owner> owners;
    public Owner owner;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Twitter.initialize(getActivity());

        final View view = inflater.inflate(R.layout.fragment_login,
                container, false);

        database = new LocalDatabase(getApplicationContext());
        owners = database.getAllOwner();
        owner = owners.get(0);

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.twitter_logo_white);
        android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = SocialMediaLoginActivity.convertDpToPixel(200, getContext());
        layoutParams.height = SocialMediaLoginActivity.convertDpToPixel(200, getContext());
        imageView.setLayoutParams(layoutParams);


        loginButton = new TwitterLoginButton(getContext());
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();

                Log.i("TWITTERTEST","user_id: " +session.getUserId());
                Log.i("TWITTERTEST","username: " +session.getUserName());

                boolean cont = true;
                ArrayList<Social> socials = database.getUserSocials(owner.getId());
                for(Social s: socials){
                    if (s.getType().equals("tw")){
                        cont = false;
                    }
                }

                if(cont) {
                    /////add to database//////////
                    Social twitter = new Social(owner.getId(), "tw", String.valueOf(session.getUserId()));
                    database.addSocial(twitter);
                    //////////////////////////////
                }
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), "ERROR: Could not login to Twitter", Toast.LENGTH_LONG).show();
                try{
                    ApplicationInfo info = getActivity().getPackageManager().
                            getApplicationInfo("com.twitter.android", 0 );
                } catch( PackageManager.NameNotFoundException e ){
                    // Ask if user would like to install the Twitter app
//                    showNoticeDialog("Twitter", "https://play.google.com/store/apps/details?id=com.twitter.android");     //TODO TODO

                }
            }
        });


        Button visibleButton = view.findViewById(R.id.login_button);
        visibleButton.setText("Sign in with Twitter");
        visibleButton.setTextColor(ContextCompat.getColor(getContext(), R.color.twitter_blue));
        visibleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}