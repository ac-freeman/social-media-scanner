package com.acfreeman.socialmediascanner.social;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.acfreeman.socialmediascanner.CustomDialogFragment;
import com.acfreeman.socialmediascanner.MainActivity;
import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.RegistrationInformation;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.social.login.FacebookFragment;
import com.acfreeman.socialmediascanner.social.login.GoogleFragment;
import com.acfreeman.socialmediascanner.social.login.LinkedInFragment;
import com.acfreeman.socialmediascanner.social.login.SpotifyFragment;
import com.acfreeman.socialmediascanner.social.login.TwitterFragment;

import java.util.List;

import static com.acfreeman.socialmediascanner.MainActivity.firstMainActivityPref;

public class SocialMediaLoginActivity extends AppCompatActivity implements CustomDialogFragment.NoticeDialogListener,
        GoogleFragment.ConnectionChangedListener, FacebookFragment.ConnectionChangedListener, TwitterFragment.ConnectionChangedListener,
        LinkedInFragment.ConnectionChangedListener, SpotifyFragment.ConnectionChangedListener {


    public LocalDatabase database;
    public List<Owner> owners;
    public Owner owner;

    private static Toolbar myToolbar;

    String caller;
    Class callerClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        caller = getIntent().getStringExtra("caller");
        try {
            callerClass = Class.forName(caller);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        database = new LocalDatabase(getApplicationContext());

        LocalDatabase database = new LocalDatabase(getApplicationContext());
        if (database.getAllOwner().size() > 0)
            database.deleteOwner(0);
        Owner owner = new Owner(0);
        database.addOwner(owner);


        final Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.google_blue));

        }


        setContentView(R.layout.activity_social_media_login_container);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        GoogleFragment googleFragment = new GoogleFragment();

        FragmentManager fm = getSupportFragmentManager();

        fm.addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {

                        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
                        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_bottom);
                        fab.startAnimation(slide);

                        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_forward_white_24dp);
                        final Drawable d2 = d.getConstantState().newDrawable();

                        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
                        Log.i("BACKSTACK", "Backstack entry count: " + getSupportFragmentManager().getBackStackEntryCount());
                        for (Fragment fragment : allFragments) {
                            if (fragment instanceof GoogleFragment) {
                                d2.mutate().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.google_blue), PorterDuff.Mode.MULTIPLY);
                            } else if (fragment instanceof TwitterFragment) {
                                d2.mutate().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.twitter_blue), PorterDuff.Mode.MULTIPLY);
                            } else if (fragment instanceof LinkedInFragment) {
                                d2.mutate().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.linkedin_blue), PorterDuff.Mode.MULTIPLY);
                            } else if (fragment instanceof SpotifyFragment) {
                                d2.mutate().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.spotify_green), PorterDuff.Mode.MULTIPLY);
                            } else if (fragment instanceof FacebookFragment) {
                                d2.mutate().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.com_facebook_blue), PorterDuff.Mode.MULTIPLY);
                            }
                        }



                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                fab.setVisibility(View.VISIBLE);
                                fab.setImageDrawable(d2);
                                Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_bottom);
                                fab.startAnimation(slide);
                            }
                        }, 250);

                    }
                });


        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        ft.addToBackStack("google");
        ft.replace(R.id.content, googleFragment);
        ft.commit();


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_forward_white_24dp);
        Drawable d2 = d.getConstantState().newDrawable();
        d2.mutate().

                setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.google_blue), PorterDuff.Mode.MULTIPLY);
        fab.setImageDrawable(d2);
        fab.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                List<Fragment> allFragments = getSupportFragmentManager().getFragments();
                for (Fragment fragment : allFragments) {
                    if (fragment instanceof GoogleFragment) {
                        Log.i("SOCIALDEBUG", "Fragment: google");
                        TwitterFragment twitterFragment = new TwitterFragment();

                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        ft.addToBackStack("twitter");
                        ft.replace(R.id.content, twitterFragment);
                        ft.commit();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.twitter_blue));
                        }
                    } else if (fragment instanceof TwitterFragment) {
                        Log.i("SOCIALDEBUG", "Fragment: twitter");
                        LinkedInFragment linkedinFragment = new LinkedInFragment();

                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        ft.addToBackStack("linkedin");
                        ft.replace(R.id.content, linkedinFragment);
                        ft.commit();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.linkedin_blue));
                        }
                    } else if (fragment instanceof LinkedInFragment) {
                        Log.i("SOCIALDEBUG", "Fragment: linkedin");
                        SpotifyFragment spotifyFragment = new SpotifyFragment();

                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        ft.addToBackStack("spotify");
                        ft.replace(R.id.content, spotifyFragment);
                        ft.commit();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.spotify_green));
                        }
                    } else if (fragment instanceof SpotifyFragment) {
                        FacebookFragment facebookFragment = new FacebookFragment();

                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        ft.addToBackStack("facebook");
                        ft.replace(R.id.content, facebookFragment);
                        ft.commit();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.com_facebook_blue));
                        }
                    } else if (fragment instanceof FacebookFragment) {
                        Log.i("SOCIALDEBUG", "Fragment: facebook");
                        if (callerClass.getName().equals("com.acfreeman.socialmediascanner.MainActivity")) {
                            Log.i("CRASHDEBUG", "Caller class MainActivity");
                            Intent startIntent = new Intent(getApplicationContext(), RegistrationInformation.class);
                            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startIntent.putExtra("caller", "com.acfreeman.socialmediascanner.social.SocialMediaLoginActivity");
                            startActivity(startIntent);
                        } else {
                            Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(startIntent);
                        }
//                        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    }
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<android.support.v4.app.Fragment> allFragments = getSupportFragmentManager().getFragments();
        for (android.support.v4.app.Fragment fragment : allFragments) {
            if (fragment instanceof GoogleFragment) {
                ((GoogleFragment) fragment).onActivityResult(requestCode, resultCode, data);
            } else if (fragment instanceof TwitterFragment) {
                ((TwitterFragment) fragment).onActivityResult(requestCode, resultCode, data);
            } else if (fragment instanceof LinkedInFragment) {
                ((LinkedInFragment) fragment).onActivityResult(requestCode, resultCode, data);
            } else if (fragment instanceof SpotifyFragment) {
                ((SpotifyFragment) fragment).onActivityResult(requestCode, resultCode, data);
            } else if (fragment instanceof FacebookFragment) {
                ((FacebookFragment) fragment).onActivityResult(requestCode, resultCode, data);
            }
        }
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

    public static int convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }


    @Override
    public void onConnectionChanged() {
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : allFragments) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.detach(fragment);
            fragmentTransaction.attach(fragment);
            fragmentTransaction.commit();
        }
    }
}



