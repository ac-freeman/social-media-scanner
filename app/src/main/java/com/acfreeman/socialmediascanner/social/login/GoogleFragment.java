package com.acfreeman.socialmediascanner.social.login;

import android.content.Context;
import android.content.Intent;
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

import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.social.SocialMediaLoginActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Andrew on 11/2/2017.
 */


public class GoogleFragment extends Fragment {

    ConnectionChangedListener mCallback;

    // Container Activity must implement this interface
    public interface ConnectionChangedListener {
        public void onConnectionChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.onAttach(context);
        if (context instanceof ConnectionChangedListener) {
            mCallback = (ConnectionChangedListener) context;
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


    public LocalDatabase database;
    public List<Owner> owners;
    public Owner owner;
    private int RC_SIGN_IN = 9001;
    GoogleApiClient apiClient;
    View view;
    Boolean connected = false;
    Social googleSocial;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        connected = false;


        database = new LocalDatabase(getApplicationContext());
        owners = database.getAllOwner();
        owner = owners.get(0);

        List<Social> socials = database.getUserSocials(owner.getId());

        for (Social s : socials) {
            if (s.getType().equals("go")) {
                googleSocial = s;
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
        background.setBackgroundColor(Color.WHITE);

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.google_title_color);
        android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = SocialMediaLoginActivity.convertDpToPixel(200, getContext());
        layoutParams.height = SocialMediaLoginActivity.convertDpToPixel(68, getContext());
        imageView.setLayoutParams(layoutParams);

        if (!connected) {


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            apiClient = new GoogleApiClient.Builder(getContext())
                    .enableAutoManage(getActivity() /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            //handle if connection fails
                            Log.e("Google", "Connection failed");
                        }
                    } /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();


            Button visibleButton = view.findViewById(R.id.login_button);
            visibleButton.setText("Sign in with Google");
            visibleButton.setTextColor(ContextCompat.getColor(getContext(), R.color.google_blue));
            visibleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Auth.GoogleSignInApi.signOut(apiClient);
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });

        } else {


            Button visibleButton = view.findViewById(R.id.login_button);
            visibleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (googleSocial != null) {
                        database.deleteUserSocial(googleSocial);
                    }
                    if (apiClient != null) {
                        apiClient.stopAutoManage(getActivity());
                        apiClient.disconnect();
                    }
                    mCallback.onConnectionChanged();
                }
            });
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String googleId = acct.getId();
                //String id2 = acct.getIdToken();
                Log.i("GOOGLE", googleId);

                /////add to database//////////
                Social google = new Social(owner.getId(), "go", googleId);
                database.addSocial(google);

                String email = acct.getEmail();
                Email gmail = new Email((long) owner.getId(), email, "GMail");
                ArrayList<Email> userEmails = database.getUserEmails(owner.getId());
                Boolean addEmail = true;
                for (Email e : userEmails) {
                    if (e.getEmail().equals(email)) {
                        addEmail = false;
                    }
                }
                if (addEmail) {
                    database.addEmail(gmail);
                }
                connected = true;
                mCallback.onConnectionChanged();

                //////////////////////////////

//                signed_in = true;
                //Toast.makeText(GoogleLoginActivity.this, "Google_id: " + database.getSocialCount(), Toast.LENGTH_LONG).show();
            } else {
                Log.e("CCCCCCCCCCCCCCC", "Could not login");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (apiClient != null) {
            apiClient.stopAutoManage(getActivity());
            apiClient.disconnect();
        }
    }
}