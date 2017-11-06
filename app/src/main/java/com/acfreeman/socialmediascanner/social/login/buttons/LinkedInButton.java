package com.acfreeman.socialmediascanner.social.login.buttons;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Andrew on 11/6/2017.
 */

public class LinkedInButton {

    private LocalDatabase database;
    private List<Owner> owners;
    private Owner owner;
    public Button button;

    public LinkedInButton(Context context, final FragmentActivity activity) {
        button = new Button(context);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LISessionManager.getInstance(getApplicationContext()).init(activity, buildScope(), new AuthListener() {
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


                                    database = new LocalDatabase(getApplicationContext());
                                    owners = database.getAllOwner();
                                    owner = owners.get(0);


                                    //Delete any existing entry for this social
                                    ArrayList<Social> socials = database.getUserSocials(owner.getId());
                                    for (Social s : socials) {
                                        if (s.getType().equals("li")) {
                                            database.deleteUserSocial(s);
                                        }
                                    }

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

    }

    public Button getButton() {
        return button;
    }

    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, FragmentActivity activity) {
        //linkedin
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(activity, requestCode, resultCode, data);
    }
}
