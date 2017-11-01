package com.acfreeman.socialmediascanner.social;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.acfreeman.socialmediascanner.CustomDialogFragment;
import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.social.login.LinkedInFragment;
import com.acfreeman.socialmediascanner.social.login.TwitterFragment;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.List;

public class SocialMediaLoginActivity extends AppCompatActivity implements CustomDialogFragment.NoticeDialogListener {

    private TwitterLoginButton loginButton;
    private LoginButton facebookButton;
    private ImageView liButton;

    private ImageView spotifyButton;

    CallbackManager callbackManager = CallbackManager.Factory.create();

    private static final String SPOTIFY_CLIENT_ID = "b8d2cf358e334542837ba4ae37e09d4b";
    private static final int SPOTIFY_REQUEST_CODE = 1337;
    private static final String SPOTIFY_REDIRECT_URI = "scanner://callback";

    public LocalDatabase database;
    public List<Owner> owners;
    public Owner owner;

    private static Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = new LocalDatabase(getApplicationContext());
        owners = database.getAllOwner();
        owner = owners.get(0);


        setContentView(R.layout.activity_social_media_login_container);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        TwitterFragment twitterFragment = new TwitterFragment();

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, twitterFragment);
        ft.commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<android.support.v4.app.Fragment> allFragments = getSupportFragmentManager().getFragments();
                for (android.support.v4.app.Fragment fragment : allFragments) {
                    if (fragment instanceof TwitterFragment) {
                        LinkedInFragment linkedinFragment = new LinkedInFragment();

                        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                        ft.replace(R.id.content, linkedinFragment);
                        ft.commit();
                    }
                }
            }
        });


//        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_button);
//        loginButton.setCallback(new Callback<TwitterSession>() {
//            @Override
//            public void success(Result<TwitterSession> result) {
//                // Do something with result, which provides a TwitterSession for making API calls
//                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
//                TwitterAuthToken authToken = session.getAuthToken();
//                String token = authToken.token;
//                String secret = authToken.secret;
//
//
//                Toast.makeText(SocialMediaLoginActivity.this, "Logged in to Twitter", Toast.LENGTH_SHORT).show();
//
//
//
//                Log.i("TWITTERTEST","user_id: " +session.getUserId());
//                Log.i("TWITTERTEST","username: " +session.getUserName());
//
//                Intent startIntent = new Intent(getApplicationContext(), SocialMediaLoginActivity.class);
//                 startActivity(startIntent);
//
//
//                /////add to database//////////
//                Social twitter = new Social(owner.getId(),"tw",String.valueOf(session.getUserId()));
//                database.addSocial(twitter);
//                //////////////////////////////
//
//            }
//
//            @Override
//            public void failure(TwitterException exception) {
//                Toast.makeText(getApplicationContext(), "ERROR: Could not login to Twitter", Toast.LENGTH_LONG).show();
//                try{
//                    ApplicationInfo info = getPackageManager().
//                            getApplicationInfo("com.twitter.android", 0 );
//                } catch( PackageManager.NameNotFoundException e ){
//                    // Ask if user would like to install the Twitter app
//                    showNoticeDialog("Twitter", "https://play.google.com/store/apps/details?id=com.twitter.android");
//
//                }
//            }
//        });
//
//
//
//
//        final Button linkedinButton = (Button) findViewById(R.id.linkedin_button);
//        linkedinButton.setBackgroundResource(R.drawable.li_default);
//        linkedinButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                if(motionEvent.getAction() == ACTION_BUTTON_PRESS) {
//                    linkedinButton.setBackgroundResource(R.drawable.li_active);
//                }
//                else {
//                    linkedinButton.setBackgroundResource(R.drawable.li_default);
//                }
//                return false;
//            }
//        });
//        linkedinButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LISessionManager.getInstance(getApplicationContext()).init(SocialMediaLoginActivity.this,  buildScope(), new AuthListener() {
//                    @Override
//                    public void onAuthSuccess(){
//
//                        String url = "https://api.linkedin.com/v1/people/~?format=json";
//
//
//                        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
//                        apiHelper.getRequest(getApplicationContext(), url, new ApiListener() {
//                            @Override
//                            public void onApiSuccess(ApiResponse apiResponse) {
//                                // Success!
//                                Log.d("linkedin response", apiResponse.getResponseDataAsJson().toString());
//
//                                JSONObject obj = null;
//                                try {
//                                    obj = new JSONObject( apiResponse.getResponseDataAsJson().toString());
//                                    JSONObject obj2 = obj.getJSONObject("siteStandardProfileRequest");
//                                    String url = obj2.getString("url");
//                                    String li_id = url.substring(41);
//
//                                    Log.i("LINKEDINDEBUG", li_id);
//
//                                    /////add to database//////////
//                                    Social linkedin = new Social(owner.getId(),"li", li_id);
//                                    database.addSocial(linkedin);
//                                    //////////////////////////////
//                                } catch (JSONException e) {
//                                    Toast.makeText(getApplicationContext(), "ERROR: Could not login to LinkedIn", Toast.LENGTH_LONG).show();
//                                    e.printStackTrace();
//                                }
//
//
//                                }
//
//                            @Override
//                            public void onApiError(LIApiError liApiError) {
//                                Toast.makeText(getApplicationContext(), "ERROR: Could not login to LinkedIn", Toast.LENGTH_LONG).show();
//                            }
//                        });
//
//
//
//                    }
//
//                    @Override
//                    public void onAuthError(LIAuthError error) {
//
//                    }
//                }, true);
//            }
//        });
//
//        spotifyButton = findViewById(R.id.spotify_button);
//        spotifyButton.setBackgroundResource(R.drawable.spotify_login);
//        spotifyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AuthenticationRequest.Builder builder =
//                        new AuthenticationRequest.Builder(SPOTIFY_CLIENT_ID, AuthenticationResponse.Type.TOKEN, SPOTIFY_REDIRECT_URI);
//
//                builder.setScopes(new String[]{"user-follow-modify", "user-read-private"});
//                AuthenticationRequest request = builder.build();
//
//                AuthenticationClient.openLoginActivity(SocialMediaLoginActivity.this, SPOTIFY_REQUEST_CODE, request);
//
//
//
//            }
//        });
//
//
//
//        facebookButton = (LoginButton) findViewById(R.id.facebook_button);
//        facebookButton.setReadPermissions("email");
//        // Other app specific specialization
//
//
//        // Callback registration
//        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                final AccessToken accessToken = loginResult.getAccessToken();
//
//                GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
//                        String facebook_id = user.optString("id");
//                        Log.d("facebook", user.optString("id"));
//                        /////add to database//////////
//                        Social facebook = new Social(owner.getId(),"fb", facebook_id);
//                        database.addSocial(facebook);
//                        //////////////////////////////
//                    }
//                }).executeAsync();
//
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//            }
//        });
//
//
//
//
//        Button nextButton = findViewById(R.id.next_button);
//        nextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(startIntent);
//            }
//        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<android.support.v4.app.Fragment> allFragments = getSupportFragmentManager().getFragments();
        for (android.support.v4.app.Fragment fragment : allFragments) {
            if (fragment instanceof TwitterFragment) {
                ((TwitterFragment) fragment).onActivityResult(requestCode, resultCode, data);
            }
        }


//        // Check if result comes from the correct activity
//        if (requestCode == SPOTIFY_REQUEST_CODE) {
//            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
//            switch (response.getType()) {
//                // Response was successful and contains auth token
//                case TOKEN:
//                    // Handle successful response
//                    final String authToken = response.getAccessToken();
//                    Log.e("AAAAAAAAAAAAA", "authtoken: " + authToken);
//
//                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
//                        @Override
//                        protected Void doInBackground(Void... params) {
//                            try {
//                                URL url = new URL("https://api.spotify.com/v1/me");
//
//                                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                                urlConnection.setRequestProperty("Authorization", "Bearer " + authToken);
//                                urlConnection.setRequestMethod("GET");
//
//                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//
//                                StringBuilder sb = new StringBuilder();
//                                String line;
//                                while((line = bufferedReader.readLine()) != null) {
//                                    sb.append(line);
//                                }
//
//                                JSONObject json = new JSONObject(sb.toString());
//
//                                String user_id = json.getString("id");
//
//                                Log.e("SDKFJ", user_id);
//
//                                /////add to database//////////
//                                Social spotify = new Social(owner.getId(),"sp", user_id);
//                                database.addSocial(spotify);
//                                //////////////////////////////
//
//                                for (int i = 0; i < database.getSocialCount(); i++) {
//                                    Log.e("DATABASE", database.getSocial(0).toString());
//                                    Log.e("DATABASE", database.getSocial(1).toString());
//                                    Log.e("DATABASE", database.getSocial(2).toString());
//                                }
//
//                            }
//                            catch (Exception ex) {
//                                Log.e("Exception: ", ex.toString());
//                            }
//                            return null;
//                        }
//                    };
//
//                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//                    break;
//
//                // Auth flow returned an error
//                case ERROR:
//                    // Handle error response
//                    break;
//
//                // Most likely auth flow was cancelled
//                default:
//                    // Handle other cases
//            }
//        } else { //for linkedin
//            // Pass the activity result to the login button.
////            loginButton.onActivityResult(requestCode, resultCode, data);      //TODO TODO
//
//            //linkedin
////            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);      //TODO TODO
//
//        }

//        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void showNoticeDialog(String social_title, String uri) {
        DialogFragment dialog = new CustomDialogFragment();


        Bundle args = new Bundle();
        args.putString("dialog_title", "You must install the " + social_title + " app in order to login");
        args.putString("uri", uri);
        args.putString("action", "appInstall");


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


}



