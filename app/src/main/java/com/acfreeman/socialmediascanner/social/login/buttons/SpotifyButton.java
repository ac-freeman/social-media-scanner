package com.acfreeman.socialmediascanner.social.login.buttons;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Social;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by Andrew on 11/6/2017.
 */

public class SpotifyButton {

    private LocalDatabase database;
    private List<Owner> owners;
    private Owner owner;
    private Context context;
    private FragmentActivity activity;

    Button button;
    private static final String SPOTIFY_CLIENT_ID = "b8d2cf358e334542837ba4ae37e09d4b";
    private static final int SPOTIFY_REQUEST_CODE = 1337;
    private static final String SPOTIFY_REDIRECT_URI = "scanner://callback";

    public SpotifyButton(Context c, FragmentActivity a) {
        this.context = c;
        this.activity = a;

        button = new Button(context);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationRequest.Builder builder =
                        new AuthenticationRequest.Builder(SPOTIFY_CLIENT_ID, AuthenticationResponse.Type.TOKEN, SPOTIFY_REDIRECT_URI);

                builder.setScopes(new String[]{"user-follow-modify", "user-read-private"});
                AuthenticationRequest request = builder.build();

                AuthenticationClient.openLoginActivity(activity, SPOTIFY_REQUEST_CODE, request);
            }
        });
    }

    public Button getButton() {
        return button;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //        // Check if result comes from the correct activity
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
                                while ((line = bufferedReader.readLine()) != null) {
                                    sb.append(line);
                                }

                                JSONObject json = new JSONObject(sb.toString());

                                String user_id = json.getString("id");


                                database = new LocalDatabase(context);
                                owners = database.getAllOwner();
                                owner = owners.get(0);

                                //Delete any existing entry for this social
                                ArrayList<Social> socials = database.getUserSocials(owner.getId());
                                for (Social s : socials) {
                                    if (s.getType().equals("sp")) {
                                        database.deleteUserSocial(s);
                                    }
                                }


                                /////add to database//////////
                                Social spotify = new Social(owner.getId(), "sp", user_id);
                                database.addSocial(spotify);
                                //////////////////////////////


                            } catch (Exception ex) {
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
        }
    }
}
