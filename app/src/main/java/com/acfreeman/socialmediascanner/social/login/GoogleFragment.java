package com.acfreeman.socialmediascanner.social.login;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.social.SocialMediaLoginActivity;
import com.acfreeman.socialmediascanner.social.login.buttons.GoogleButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;


import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Andrew on 11/2/2017.
 */



public class GoogleFragment extends Fragment {



    GoogleButton googleButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_login,
                container, false);

        googleButton = new GoogleButton(getContext(), getActivity());



        RelativeLayout background = view.findViewById(R.id.background);
        background.setBackgroundColor(Color.WHITE);

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.google_title_color);
        android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = SocialMediaLoginActivity.convertDpToPixel(200, getContext());
        layoutParams.height = SocialMediaLoginActivity.convertDpToPixel(68, getContext());
        imageView.setLayoutParams(layoutParams);





        Button visibleButton = view.findViewById(R.id.login_button);
        visibleButton.setText("Sign in with Google");
        visibleButton.setTextColor(ContextCompat.getColor(getContext(), R.color.google_blue));
        visibleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleButton.apiClient);
                startActivityForResult(signInIntent, googleButton.RC_SIGN_IN);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        googleButton.onActivityResult(requestCode,resultCode,data);


    }

    @Override
    public void onPause() {
        super.onPause();
        if(googleButton.apiClient!=null) {
            googleButton.apiClient.stopAutoManage(getActivity());
            googleButton.apiClient.disconnect();
        }
    }
}