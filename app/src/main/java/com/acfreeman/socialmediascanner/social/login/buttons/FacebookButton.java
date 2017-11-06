package com.acfreeman.socialmediascanner.social.login.buttons;

import android.content.Context;
import android.util.Log;

import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Social;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Andrew on 11/4/2017.
 */

public class FacebookButton {

    private LoginButton facebookButton;
    public CallbackManager callbackManager = CallbackManager.Factory.create();
    public LocalDatabase database;
    public List<Owner> owners;
    public Owner owner;

    public FacebookButton(Context context) {

        database = new LocalDatabase(getApplicationContext());
        owners = database.getAllOwner();
        owner = owners.get(0);



        facebookButton = new LoginButton(context);
        facebookButton.setReadPermissions("email");
        // Other app specific specialization


        // Callback registration
        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()

        {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();

                GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        String facebook_id = user.optString("id");
                        Log.d("facebook", user.optString("id"));


                        ArrayList<Social> socials = database.getUserSocials(owner.getId());
                        for (Social s : socials) {
                            if (s.getType().equals("fb")) {
                                database.deleteUserSocial(s);
                            }
                        }

                            /////add to database//////////
                            Social facebook = new Social(owner.getId(), "fb", facebook_id);
                            database.addSocial(facebook);
                            //////////////////////////////
                        
                    }
                }).executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    public LoginButton getButton(){
        return facebookButton;
    }
}
