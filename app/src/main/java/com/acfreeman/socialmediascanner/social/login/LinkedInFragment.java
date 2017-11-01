package com.acfreeman.socialmediascanner.social.login;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.social.SocialMediaLoginActivity;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
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

import java.util.List;

import static android.view.MotionEvent.ACTION_BUTTON_PRESS;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Andrew on 11/1/2017.
 */

public class LinkedInFragment extends Fragment {
    private ImageView liButton;

    public LocalDatabase database;
    public List<Owner> owners;
    public Owner owner;
    Button loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_login_linkedin,
                container, false);

        database = new LocalDatabase(getApplicationContext());
        owners = database.getAllOwner();
        owner = owners.get(0);

        loginButton = (Button) view.findViewById(R.id.linkedin_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LISessionManager.getInstance(getApplicationContext()).init(getActivity(), buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {

                        String url = "https://api.linkedin.com/v1/people/~?format=json";


                        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                        apiHelper.getRequest(getApplicationContext(), url, new ApiListener() {
                            @Override
                            public void onApiSuccess(ApiResponse apiResponse) {
                                // Success!
                                Log.d("linkedin response", apiResponse.getResponseDataAsJson().toString());

                                JSONObject obj = null;
                                try {
                                    obj = new JSONObject(apiResponse.getResponseDataAsJson().toString());
                                    JSONObject obj2 = obj.getJSONObject("siteStandardProfileRequest");
                                    String url = obj2.getString("url");
                                    String li_id = url.substring(41);

                                    Log.i("LINKEDINDEBUG", li_id);

                                    /////add to database//////////
                                    Social linkedin = new Social(owner.getId(), "li", li_id);
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

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //linkedin
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(getActivity(), requestCode, resultCode, data);      //TODO TODO
    }

    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE);
    }
}


