package com.acfreeman.socialmediascanner;

import android.Manifest;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.acfreeman.socialmediascanner.db.Contact;
import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Phone;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.scancode.ScancodeFragment;
import com.acfreeman.socialmediascanner.showcode.ShowcodeFragment;
import com.acfreeman.socialmediascanner.showfriends.ShowfriendsFragment;
import com.acfreeman.socialmediascanner.social.SocialMediaLoginActivity;
import com.twitter.sdk.android.core.Twitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements CustomDialogFragment.NoticeDialogListener {

    private TextView mTextMessage;
    private ZXingScannerView mScannerView;
    private boolean camera;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    public static final int MY_PERMISSIONS_REQUEST_CONTACTS = 2;
    public static final int MY_PERMISSIONS_REQUEST_PHONE = 3;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static final String firstMainActivityPref = "firstMainActivity";
    public static final String firstProfileCreationPref = "firstProfileCreation";
    Boolean firstMainActivity;
    Boolean firstProfileCreation;
    ShowfriendsFragment showfriendsFragment = new ShowfriendsFragment();
    ShowcodeFragment showcodeFragment = new ShowcodeFragment();

    /**
     * Called when activity begins
     * Creates basic layout with bottom navigation
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);

        mTextMessage = findViewById(R.id.message);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_friends);
    }

    private Menu menu;
    boolean hideMenuButtons = true;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.appbar, menu);
        this.menu = menu;
        MenuItem deleteButton =  menu.findItem(R.id.action_delete);
        MenuItem saveContactsButton = menu.findItem(R.id.action_save_contact);
        if(hideMenuButtons){
            if (deleteButton != null && saveContactsButton != null) {
                deleteButton.setVisible(false);
                saveContactsButton.setVisible(false);
            }
        } else {
            if (deleteButton != null && saveContactsButton != null) {
                deleteButton.setVisible(true);
                saveContactsButton.setVisible(true);
            }
        }
        return true;
    }

    public void hideAppbarButtons() {
        hideMenuButtons = true;
        invalidateOptionsMenu();
    }

    public void showAppbarButtons() {
        hideMenuButtons = false;
        invalidateOptionsMenu();
    }

    public void toggleAppbarButtons() {
        hideMenuButtons = !hideMenuButtons;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DialogFragment dialog;
        Bundle args;
        switch (item.getItemId()) {
            case R.id.action_delete:
                dialog = new CustomDialogFragment();
                args = new Bundle();
                args.putString("dialog_title", "Delete selected contacts?");
                args.putString("action", "delete");

                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "CustomDialogFragment");

                return true;
            case R.id.action_save_contact:
                dialog = new CustomDialogFragment();
                args = new Bundle();
                args.putString("dialog_title", "Save selected contacts to device?");
                args.putString("action", "saveContact");

                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "CustomDialogFragment");
                return true;
            case R.id.action_settings:
                Intent startIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(startIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Event listener for the bottom navigation bar
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_show:
                    if (!showCode) {
                        if (camera) {
                            camera = false;
                            if (mScannerView != null)
                                mScannerView.stopCamera();
                        }
                        showCode();
                        hideAppbarButtons();
                    }
                    return true;

                case R.id.navigation_friends:
                    if (camera) {
                        camera = false;
                    }
                    showCode = false;
                    showFriends();
                    hideAppbarButtons();

                    return true;

                case R.id.navigation_camera:
                    camera = true;
                    showCode = false;
                    scanCode();
                    hideAppbarButtons();

                    return true;
            }
            return false;
        }
    };


    /**
     * Generating and displaying QR code
     * Uses ZXing
     */

    Boolean showCode;

    private void showCode() {
        showCode = true;
        // get fragment manager
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, showcodeFragment);
        ft.commit();
    }

    /**
     * Scanning a new QR code
     * Uses https://github.com/dm77/barcodescanner
     * Requires camera permission in settings
     */

    private void scanCode() {

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                Log.i("Ask Camera Permission", "succeed");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                Log.i("aaaa", "bbbb");

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST is an
                // app-defined int constant. The callback method gets the
                // result of the request.

            }
        }

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, new ScancodeFragment(), "scancodefragment");
        ft.commit();

    }

    private void showFriends() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        firstMainActivity = mPrefs.getBoolean(firstMainActivityPref, true);
        if (firstMainActivity) {
            addDummyData();
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean(firstMainActivityPref, false);
            editor.commit(); // Very important to save the preference
        }
        firstProfileCreation = mPrefs.getBoolean(firstProfileCreationPref, true);
        if(firstProfileCreation){
            Intent intent = new Intent(getApplicationContext(), SocialMediaLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("caller", "com.acfreeman.socialmediascanner.MainActivity");
            startActivity(intent);
        }
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, showfriendsFragment);
        ft.commitAllowingStateLoss();

    }


    //from https://stackoverflow.com/questions/14371092/how-to-make-a-specific-text-on-textview-bold
    public static SpannableStringBuilder makeSectionOfTextBold(String text, String textToBold) {

        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (textToBold.length() > 0 && !textToBold.trim().equals("")) {

            //for counting start/end indexes
            String testText = text.toLowerCase(Locale.US);
            String testTextToBold = textToBold.toLowerCase(Locale.US);
            int startingIndex = testText.indexOf(testTextToBold);
            int endingIndex = startingIndex + testTextToBold.length();
            //for counting start/end indexes

            if (startingIndex < 0 || endingIndex < 0) {
                return builder.append(text);
            } else if (startingIndex >= 0 && endingIndex >= 0) {

                builder.append(text);
                builder.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0);
            }
        } else {
            return builder.append(text);
        }

        return builder;
    }

    private void addDummyData() {
        Contact contact;
        LocalDatabase db = new LocalDatabase(getApplicationContext());

        contact = new Contact("Andrew Freeman");
        db.addContact(contact);
        db.addPhone(new Phone(contact.getId(), 2142186153, "Cell"));
        db.addEmail(new Email(contact.getId(), "afreema4@samford.edu", "Work"));
        db.addSocial(new Social(contact.getId(), "Twitter", "392381109"));
        db.addSocial(new Social(contact.getId(), "LinkedIn", "AAoAAA4bE1IBoMdCU1I23EQvkTgYE_ggW3s39SY&authType=name&authToken=qTgI&trk=api*a4550044*s4612704*"));
        db.addSocial(new Social(contact.getId(), "Spotify", "acfreeman"));
        db.addSocial(new Social(contact.getId(), "Facebook", "1720598201286659"));

        contact = new Contact("Chad Jones");
        db.addContact(contact);
        contact = new Contact("Alex Beck");
        db.addContact(contact);
        contact = new Contact("Chris Da");
        db.addContact(contact);
        contact = new Contact("Eric Frederickson");
        db.addContact(contact);
        contact = new Contact("Gina Halpert");
        db.addContact(contact);
        contact = new Contact("Isaac Jones");
        db.addContact(contact);
        contact = new Contact("Katherine Lopez");
        db.addContact(contact);
        contact = new Contact("Marina Nunez");
        db.addContact(contact);
        contact = new Contact("Opal Patricks");
        db.addContact(contact);
        contact = new Contact("Sam Terry");
        db.addContact(contact);
        contact = new Contact("Ur Very");
        db.addContact(contact);
        contact = new Contact("William Xavier");
        db.addContact(contact);
        contact = new Contact("Yorgos Zechariah");
        db.addContact(contact);
        contact = new Contact("Andrew Zepp");
        db.addContact(contact);
        contact = new Contact("Bert Yusef");
        db.addContact(contact);
        contact = new Contact("Chad Xylophone");
        db.addContact(contact);
    }

    private List readDatabaseTest() {

        List res = new ArrayList();
        LocalDatabase db = new LocalDatabase(getApplicationContext());

        List<Owner> owner = db.getAllOwner();
        res.add(owner.get(0).getId());
        res.add(owner.get(0).getName());


        ArrayList<Phone> phonelist = db.getUserPhones(owner.get(0).getId());
        for (Phone p : phonelist) {
            res.add(p.getNumber());
            res.add(p.getType());
        }

        ArrayList<Email> emaillist = db.getUserEmails(owner.get(0).getId());
        for (Email e : emaillist) {
            res.add(e.getEmail());
            res.add(e.getType());
        }

        ArrayList<Social> sociallisttest = db.getUserSocials(owner.get(0).getId());
        for (Social s : sociallisttest) {
            res.add(s.getType());
            res.add(s.getUsername());
        }

        List<Contact> contactstest = db.getAllContacts();
        for (Contact c : contactstest) {
            res.add(c.getName());

            ArrayList<Phone> userphoneslist = db.getUserPhones(c.getId());
            for (Phone p : userphoneslist) {
                res.add(p.getNumber());
                res.add(p.getType());
            }

            ArrayList<Email> useremailslist = db.getUserEmails(c.getId());
            for (Email em : useremailslist) {
                res.add(em.getEmail());
                res.add(em.getType());
            }

            ArrayList<Social> sociallist = db.getUserSocials(c.getId());
            for (Social s : sociallist) {
                res.add(s.getType());
                res.add(s.getUsername());
            }


        }

        res.add("CONTACTS SIZE: " + contactstest.size());

        return res;

    }


    public void socialAdd(String uri) {
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse(uri));
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);   //Makes it so that a single back-button press brings you back to our app
        this.startActivityForResult(i, 1);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //Add user intent
        //Go to next social media dialog

        Bundle mArgs = dialog.getArguments();
        String name = mArgs.getString("name");
        String action = mArgs.getString("action");
        String uri;
        ScancodeFragment fragment;
        switch (action) {
            case "photoCapture":
                fragment = ((ScancodeFragment) getFragmentManager().findFragmentByTag("scancodefragment"));
                fragment.showCameraPreview();

                break;
            case "socialAdd":
                uri = mArgs.getString("uri");
                socialAdd(uri);

                fragment = ((ScancodeFragment) getFragmentManager().findFragmentByTag("scancodefragment"));
                if (fragment.socialAdderArrayList.isEmpty()) {
                    fragment.handleScan = true;
                }

//                showNoticeDialog(name);
                break;
            case "singleSocialAdd":
                uri = mArgs.getString("uri");
                Log.i("SOCIALDEBUG", uri);
                socialAdd(uri);
                break;
            case "delete":
                showfriendsFragment.deleteContacts();

                hideAppbarButtons();
                break;
            case "saveContact":
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_CONTACTS},
                            MY_PERMISSIONS_REQUEST_CONTACTS);
                } else {
                    showfriendsFragment.saveContactsToDevice();
                }

                hideAppbarButtons();
                break;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //Go to next social media dialog
        Bundle mArgs = dialog.getArguments();
        String name = mArgs.getString("name");
        String action = mArgs.getString("action");
        ScancodeFragment fragment;
        switch (action) {
            case "photoCapture":
                fragment = ((ScancodeFragment) getFragmentManager().findFragmentByTag("scancodefragment"));
                fragment.scannerView.startCamera();
                fragment.handleScan = false;
                fragment.showSocialAddDialog(name);
                break;
            case "socialAdd":
//                showNoticeDialog(name);
                fragment = ((ScancodeFragment) getFragmentManager().findFragmentByTag("scancodefragment"));
                if (fragment.socialAdderArrayList.isEmpty()) {
                    fragment.handleScan = true;
                }
                break;
            case "singleSocialAdd":
                break;
            case "delete":
                break;
            case "saveContact":
                break;
            default:
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    showfriendsFragment.saveContactsToDevice();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_PHONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    showfriendsFragment.callPhone();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        Log.i("BACKBUTTON", "Back pressed");
        if (doubleBackToExitPressedOnce) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                this.finishAffinity();
            } else {
                this.finish();
                System.exit(0);
            }
            return;
        }

        Log.i("BACKBUTTON", "not null");
        if (showfriendsFragment.adapter.inEditmode) {
            showfriendsFragment.adapter.inEditmode = false;
            for (int i = 0; i < showfriendsFragment.adapter.checks.size(); i++) {
                showfriendsFragment.adapter.checks.set(i, 0);
            }
            showfriendsFragment.adapter.notifyDataSetChanged();
            hideAppbarButtons();
        } else {
            Log.i("BACKBUTTON", "null");
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public void onResume() {
        if (showcodeFragment != null) {
            showcodeFragment.allowRefresh = true;
        }
        super.onResume();
    }
}