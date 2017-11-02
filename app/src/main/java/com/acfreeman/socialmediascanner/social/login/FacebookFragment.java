package com.acfreeman.socialmediascanner.social.login;

import android.content.Intent;
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

import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.social.SocialMediaLoginActivity;
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
 * Created by Andrew on 11/1/2017.
 */

public class FacebookFragment extends Fragment {
    private ImageView liButton;

    public LocalDatabase database;
    public List<Owner> owners;
    public Owner owner;
    Button loginButton;
    CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_login,
                container, false);

        database = new LocalDatabase(getApplicationContext());
        owners = database.getAllOwner();
        owner = owners.get(0);

        RelativeLayout background = view.findViewById(R.id.background);
        background.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.com_facebook_blue));

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.fb_logo_white);
        android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = SocialMediaLoginActivity.convertDpToPixel(125, getContext());
        layoutParams.height = SocialMediaLoginActivity.convertDpToPixel(125, getContext());
        imageView.setLayoutParams(layoutParams);





        final LoginButton facebookButton = new LoginButton(getContext());
        facebookButton.setReadPermissions("email");
        // Other app specific specialization


        // Callback registration
        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();

                GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        String facebook_id = user.optString("id");
                        Log.d("facebook", user.optString("id"));


                        boolean cont = true;
                        ArrayList<Social> socials = database.getUserSocials(owner.getId());
                        for(Social s: socials){
                            if (s.getType().equals("fb")){
                                cont = false;
                            }
                        }

                        if(cont) {
                            /////add to database//////////
                            Social facebook = new Social(owner.getId(), "fb", facebook_id);
                            database.addSocial(facebook);
                            //////////////////////////////
                        }
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

        loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setText("Sign in with Facebook");
        loginButton.setTextColor(ContextCompat.getColor(getContext(), R.color.com_facebook_blue));
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookButton.performClick();
            }
        });



        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

                callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
