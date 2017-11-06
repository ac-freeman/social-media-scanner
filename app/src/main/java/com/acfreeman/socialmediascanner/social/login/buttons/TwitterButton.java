package com.acfreeman.socialmediascanner.social.login.buttons;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Social;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Andrew on 11/6/2017.
 */

public class TwitterButton {

    TwitterLoginButton loginButton;
    private LocalDatabase database;
    private List<Owner> owners;
    private Owner owner;

    public TwitterButton(Context context, final FragmentActivity activity) {

        loginButton = new TwitterLoginButton(context);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();

                Log.i("TWITTERTEST", "user_id: " + session.getUserId());
                Log.i("TWITTERTEST", "username: " + session.getUserName());


                database = new LocalDatabase(getApplicationContext());
                owners = database.getAllOwner();
                owner = owners.get(0);


                //Delete any existing entry for this social
                ArrayList<Social> socials = database.getUserSocials(owner.getId());
                for (Social s : socials) {
                    if (s.getType().equals("tw")) {
                        database.deleteUserSocial(s);
                    }
                }

                /////add to database//////////
                Social twitter = new Social(owner.getId(), "tw", String.valueOf(session.getUserId()));
                database.addSocial(twitter);
                //////////////////////////////

            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), "ERROR: Could not login to Twitter", Toast.LENGTH_LONG).show();
                try {
                    ApplicationInfo info = activity.getPackageManager().
                            getApplicationInfo("com.twitter.android", 0);
                } catch (PackageManager.NameNotFoundException e) {
                    // Ask if user would like to install the Twitter app
//                    showNoticeDialog("Twitter", "https://play.google.com/store/apps/details?id=com.twitter.android");     //TODO TODO

                }
            }
        });

    }

    public TwitterLoginButton getButton() {
        return loginButton;
    }
}
