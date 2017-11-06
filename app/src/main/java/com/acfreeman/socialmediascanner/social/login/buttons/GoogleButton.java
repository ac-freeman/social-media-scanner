package com.acfreeman.socialmediascanner.social.login.buttons;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Social;
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
 * Created by Andrew on 11/6/2017.
 */

public class GoogleButton {

    public int RC_SIGN_IN = 9001;
    public GoogleApiClient apiClient;

    private LocalDatabase database;
    private List<Owner> owners;
    private Owner owner;

    public GoogleButton(Context context, FragmentActivity activity){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        apiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(activity /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        //handle if connection fails
                        Log.e("Google", "Connection failed");
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {

                database = new LocalDatabase(getApplicationContext());
                owners = database.getAllOwner();
                owner = owners.get(0);

                GoogleSignInAccount acct = result.getSignInAccount();
                String googleId = acct.getId();
                //String id2 = acct.getIdToken();
                Log.i("GOOGLE", googleId);


                //Delete any existing entry for this social
                ArrayList<Social> socials = database.getUserSocials(owner.getId());
                for (Social s : socials) {
                    if (s.getType().equals("go")) {
                        database.deleteUserSocial(s);
                    }
                }

                /////add to database//////////
                Social google = new Social(owner.getId(),"go",googleId);
                database.addSocial(google);

                String email = acct.getEmail();
                Email gmail = new Email((long) owner.getId(), email, "GMail");
                database.addEmail(gmail);

                //////////////////////////////

//                signed_in = true;
                //Toast.makeText(GoogleLoginActivity.this, "Google_id: " + database.getSocialCount(), Toast.LENGTH_LONG).show();
            } else {
                Log.e("CCCCCCCCCCCCCCC", "Could not login");
            }
        }

    }
}
