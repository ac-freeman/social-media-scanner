package com.acfreeman.socialmediascanner;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;

import com.linkedin.platform.utils.Scope;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static android.view.MotionEvent.ACTION_BUTTON_PRESS;

public class SocialMediaLoginActivity extends AppCompatActivity {

    private TwitterLoginButton loginButton;
    private ImageView liButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Twitter.initialize(this);

        setContentView(R.layout.activity_social_media_login);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;


                Toast.makeText(SocialMediaLoginActivity.this, "Logged in to Twitter", Toast.LENGTH_SHORT).show();



                Log.i("TWITTERTEST","user_id: " +session.getUserId());
                Log.i("TWITTERTEST","username: " +session.getUserName());

                Intent startIntent = new Intent(getApplicationContext(), SocialMediaLoginActivity.class);
                 startActivity(startIntent);


            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });




        final Button linkedinButton = (Button) findViewById(R.id.linkedin_button);
        linkedinButton.setBackgroundResource(R.drawable.li_default);
        linkedinButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == ACTION_BUTTON_PRESS) {
                    linkedinButton.setBackgroundResource(R.drawable.li_active);
                }
                else {
                    linkedinButton.setBackgroundResource(R.drawable.li_default);
                }
                return false;
            }
        });
        linkedinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LISessionManager.getInstance(getApplicationContext()).init(SocialMediaLoginActivity.this,  buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess(){

                    }

                    @Override
                    public void onAuthError(LIAuthError error) {

                    }
                }, true);
            }
        });


        Button nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startIntent);
            }
        });
//        liButton = new ImageView();
//        Drawable res = getResources().getDrawable(R.drawable.li_default);   //deprecated, but alternative requires API 21+
//        liButton.setImageDrawable(res);

        //LinkedIn
//        LISessionManager.


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);

        //linkedin
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);

    }

    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE);
    }
}


