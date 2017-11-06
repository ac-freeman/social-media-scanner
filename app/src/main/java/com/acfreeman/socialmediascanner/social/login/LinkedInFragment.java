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
import android.widget.Toast;

import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.social.SocialMediaLoginActivity;
import com.acfreeman.socialmediascanner.social.login.buttons.LinkedInButton;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.ACTION_BUTTON_PRESS;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Andrew on 11/1/2017.
 */

public class LinkedInFragment extends Fragment {
    private ImageView liButton;

    Button loginButton;
    LinkedInButton linkedInButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_login,
                container, false);



        RelativeLayout background = view.findViewById(R.id.background);
        background.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.linkedin_blue));

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.linkedin_title_white);
        android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = SocialMediaLoginActivity.convertDpToPixel(200, getContext());
        layoutParams.height = SocialMediaLoginActivity.convertDpToPixel(50, getContext());
        imageView.setLayoutParams(layoutParams);


        linkedInButton = new LinkedInButton(getContext(), getActivity());

        loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setText("Sign in with LinkedIn");
        loginButton.setTextColor(ContextCompat.getColor(getContext(), R.color.linkedin_blue));

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linkedInButton.getButton().performClick();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        linkedInButton.onActivityResult(requestCode,resultCode,data);
    }


}


