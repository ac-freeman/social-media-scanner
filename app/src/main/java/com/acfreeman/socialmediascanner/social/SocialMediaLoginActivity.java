package com.acfreeman.socialmediascanner.social;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.acfreeman.socialmediascanner.CustomDialogFragment;
import com.acfreeman.socialmediascanner.MainActivity;
import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Social;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static android.view.MotionEvent.ACTION_BUTTON_PRESS;

public class SocialMediaLoginActivity extends AppCompatActivity implements CustomDialogFragment.NoticeDialogListener {

    private TwitterLoginButton loginButton;
    private ImageView liButton;
    private ImageView spotifyButton;
    private static final String SPOTIFY_CLIENT_ID = "b8d2cf358e334542837ba4ae37e09d4b";
    private static final int SPOTIFY_REQUEST_CODE = 1337;
    private static final String SPOTIFY_REDIRECT_URI = "scanner://callback";

    public LocalDatabase database;
    public List<Owner> owners;
    public Owner owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = new LocalDatabase(getApplicationContext());
        owners = database.getAllOwner();
        owner = owners.get(0);

        Twitter.initialize(this);

        setContentView(R.layout.activity_social_media_login);


//        Owner owner = new Owner(0, editName.getText().toString());
//        database.addOwner(owner);



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


                /////add to database//////////
                Social twitter = new Social(owner.getId(),"tw",String.valueOf(session.getUserId()));
                database.addSocial(twitter);
                //////////////////////////////

            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), "ERROR: Could not login to Twitter", Toast.LENGTH_LONG).show();
                try{
                    ApplicationInfo info = getPackageManager().
                            getApplicationInfo("com.twitter.android", 0 );
                } catch( PackageManager.NameNotFoundException e ){
                    // Ask if user would like to install the Twitter app
                    showNoticeDialog("Twitter", "https://play.google.com/store/apps/details?id=com.twitter.android");

                }
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

                        String url = "https://api.linkedin.com/v1/people/~?format=json";


                        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                        apiHelper.getRequest(getApplicationContext(), url, new ApiListener() {
                            @Override
                            public void onApiSuccess(ApiResponse apiResponse) {
                                // Success!
                                Log.d("linkedin response", apiResponse.getResponseDataAsJson().toString());

                                JSONObject obj = null;
                                try {
                                    obj = new JSONObject( apiResponse.getResponseDataAsJson().toString());
                                    JSONObject obj2 = obj.getJSONObject("siteStandardProfileRequest");
                                    String url = obj2.getString("url");
                                    String li_id = url.substring(41);

                                    Log.i("LINKEDINDEBUG", li_id);

                                    /////add to database//////////
                                    Social linkedin = new Social(owner.getId(),"li", li_id);
                                    database.addSocial(linkedin);
                                    //////////////////////////////
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "ERROR: Could not login to LinkedIn", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }


                                }

                            @Override
                            public void onApiError(LIApiError liApiError) {
                                Toast.makeText(getApplicationContext(), "ERROR: Could not login to LinkedIn", Toast.LENGTH_LONG).show();
                            }
                        });



                    }

                    @Override
                    public void onAuthError(LIAuthError error) {

                    }
                }, true);
            }
        });

        spotifyButton = findViewById(R.id.spotify_button);
        spotifyButton.setBackgroundResource(R.drawable.spotify_login);
        spotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationRequest.Builder builder =
                        new AuthenticationRequest.Builder(SPOTIFY_CLIENT_ID, AuthenticationResponse.Type.TOKEN, SPOTIFY_REDIRECT_URI);

                builder.setScopes(new String[]{"user-follow-modify", "user-read-private"});
                AuthenticationRequest request = builder.build();

                AuthenticationClient.openLoginActivity(SocialMediaLoginActivity.this, SPOTIFY_REQUEST_CODE, request);



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

        // Check if result comes from the correct activity
        if (requestCode == SPOTIFY_REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    final String authToken = response.getAccessToken();
                    Log.e("AAAAAAAAAAAAA", "authtoken: " + authToken);

                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                URL url = new URL("https://api.spotify.com/v1/me");

                                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                                urlConnection.setRequestProperty("Authorization", "Bearer " + authToken);
                                urlConnection.setRequestMethod("GET");

                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                                StringBuilder sb = new StringBuilder();
                                String line;
                                while((line = bufferedReader.readLine()) != null) {
                                    sb.append(line);
                                    //Log.e("A", line);

//                                    String[] words = s.split(" ");
//                                    int place = 0;
//
//                                    while(words[place] == null) {
//                                        place++;
//                                    }
//
//                                    if(words[place] == "\"id\"") {
//                                        Log.e("BBBBBBB", words[place +2]);
//                                    }

                                }

                                JSONObject json = new JSONObject(sb.toString());

                                String user_id = json.getString("id");

                                Log.e("SDKFJ", user_id);

                                /////add to database//////////
                                Social spotify = new Social(owner.getId(),"sp", user_id);
                                database.addSocial(spotify);
                                //////////////////////////////

                                for (int i = 0; i < database.getSocialCount(); i++) {
                                    Log.e("DATABASE", database.getSocial(0).toString());
                                    Log.e("DATABASE", database.getSocial(1).toString());
                                    Log.e("DATABASE", database.getSocial(2).toString());
                                }

                            }
                            catch (Exception ex) {
                                Log.e("Exception: ", ex.toString());
                            }
                            return null;
                        }
                    };

                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        } else { //for linkedin
            // Pass the activity result to the login button.
            loginButton.onActivityResult(requestCode, resultCode, data);

            //linkedin
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);

        }
    }

    public void showNoticeDialog(String social_title, String uri) {
        DialogFragment dialog = new CustomDialogFragment();



        Bundle args = new Bundle();
        args.putString("dialog_title", "You must install the " + social_title + " app in order to login");
        args.putString("uri", uri);
        args.putString("action","appInstall");


        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "CustomDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //Add user intent
        //Go to next social media dialog

        Bundle mArgs = dialog.getArguments();
        String uri = mArgs.getString("uri");

        Intent startIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(startIntent);



    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //Go to next social media dialog
//        Bundle mArgs = dialog.getArguments();
//        String name = mArgs.getString("name");

    }


    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE);
    }
}


