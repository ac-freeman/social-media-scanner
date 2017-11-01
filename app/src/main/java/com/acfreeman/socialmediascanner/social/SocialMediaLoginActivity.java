package com.acfreeman.socialmediascanner.social;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.acfreeman.socialmediascanner.CustomDialogFragment;
import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.social.login.LinkedInFragment;
import com.acfreeman.socialmediascanner.social.login.SpotifyFragment;
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
                    } else if(fragment instanceof LinkedInFragment) {
                        SpotifyFragment spotifyFragment = new SpotifyFragment();

                        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                        ft.replace(R.id.content, spotifyFragment);
                        ft.commit();
                    }
                }
            }
        });



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
            } else if(fragment instanceof SpotifyFragment){
                ((SpotifyFragment) fragment).onActivityResult(requestCode, resultCode, data);
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

    public static int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)px;
    }


}



