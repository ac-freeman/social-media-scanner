package com.acfreeman.socialmediascanner.social.login;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Andrew on 11/1/2017.
 */

public class SpotifyFragment extends Fragment {

    LinkedInFragment.ConnectionChangedListener mCallback;

    // Container Activity must implement this interface
    public interface ConnectionChangedListener {
        public void onConnectionChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.onAttach(context);
        if (context instanceof LinkedInFragment.ConnectionChangedListener) {
            mCallback = (LinkedInFragment.ConnectionChangedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ConnectionChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    View view;
    Boolean connected = false;
    Social spotifySocial;

    public LocalDatabase database;
    public List<Owner> owners;
    public Owner owner;
    Button spotifyButton;

    private static final String SPOTIFY_CLIENT_ID = "b8d2cf358e334542837ba4ae37e09d4b";
    private static final int SPOTIFY_REQUEST_CODE = 1337;
    private static final String SPOTIFY_REDIRECT_URI = "scanner://callback";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        database = new LocalDatabase(getApplicationContext());
        owners = database.getAllOwner();
        owner = owners.get(0);

        List<Social> socials = database.getUserSocials(owner.getId());
        for (Social s : socials) {
            if (s.getType().equals("sp")) {
                spotifySocial = s;
                connected = true;
            }
        }

        if (!connected) {
            view = inflater.inflate(R.layout.fragment_login_connect,
                    container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_login_disconnect,
                    container, false);
        }


        RelativeLayout background = view.findViewById(R.id.background);
        background.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.spotify_green));

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.spotify_title_white);
        android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = SocialMediaLoginActivity.convertDpToPixel(200, getContext());
        layoutParams.height = SocialMediaLoginActivity.convertDpToPixel(60, getContext());
        imageView.setLayoutParams(layoutParams);

        spotifyButton = view.findViewById(R.id.login_button);

        if (!connected) {
            spotifyButton.setText("Sign in with Spotify");
            spotifyButton.setTextColor(ContextCompat.getColor(getContext(), R.color.spotify_green));

            spotifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AuthenticationRequest.Builder builder =
                            new AuthenticationRequest.Builder(SPOTIFY_CLIENT_ID, AuthenticationResponse.Type.TOKEN, SPOTIFY_REDIRECT_URI);

                    builder.setScopes(new String[]{"user-follow-modify", "user-read-private"});
                    AuthenticationRequest request = builder.build();

                    AuthenticationClient.openLoginActivity(getActivity(), SPOTIFY_REQUEST_CODE, request);


                }
            });
        } else {
            spotifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (spotifySocial != null) {
                        database.deleteUserSocial(spotifySocial);
                    }
                    connected = false;
                    mCallback.onConnectionChanged();


                }
            });
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                                while ((line = bufferedReader.readLine()) != null) {
                                    sb.append(line);
                                }

                                JSONObject json = new JSONObject(sb.toString());

                                String user_id = json.getString("id");

                                Log.e("SDKFJ", user_id);

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
                                connected = true;
                                mCallback.onConnectionChanged();


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
