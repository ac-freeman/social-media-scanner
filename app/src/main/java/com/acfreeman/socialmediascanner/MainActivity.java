package com.acfreeman.socialmediascanner;

import android.Manifest;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acfreeman.socialmediascanner.db.Contact;
import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Phone;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.showcode.ShowcodeFragment;
import com.acfreeman.socialmediascanner.showfriends.ShowfriendsFragment;
import com.acfreeman.socialmediascanner.social.SocialAdder;
import com.google.zxing.Result;
import com.twitter.sdk.android.core.Twitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, CustomDialogFragment.NoticeDialogListener {

    private TextView mTextMessage;
    private static ImageView mImageView;
    private ZXingScannerView mScannerView;
    private boolean camera;
    public boolean handleScan;
    private static Toolbar myToolbar;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    public static final int MY_PERMISSIONS_REQUEST_CONTACTS = 2;
    public static final int MY_PERMISSIONS_REQUEST_PHONE = 3;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    SharedPreferences mPrefs;
    public static final String firstMainActivityPref = "firstMainActivity";
    Boolean firstMainActivity;
    ShowfriendsFragment showfriendsFragment = new ShowfriendsFragment();

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
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mTextMessage = findViewById(R.id.message);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_friends);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.appbar, menu);
        return true;
    }

    public static void hideAppbarButtons() {
        MenuItem deleteButton = myToolbar.getMenu().findItem(R.id.action_delete);
        MenuItem saveContactsButton = myToolbar.getMenu().findItem(R.id.action_save_contact);
        if (deleteButton != null && saveContactsButton != null) {
            deleteButton.setVisible(false);
            saveContactsButton.setVisible(false);
        }
    }

    public static void showAppbarButtons() {
        MenuItem deleteButton = myToolbar.getMenu().findItem(R.id.action_delete);
        MenuItem saveContactsButton = myToolbar.getMenu().findItem(R.id.action_save_contact);
        if (deleteButton != null && saveContactsButton != null) {
            deleteButton.setVisible(true);
            saveContactsButton.setVisible(true);
        }
    }

    public static void toggleAppbarButtons() {
        MenuItem deleteButton = myToolbar.getMenu().findItem(R.id.action_delete);
        MenuItem saveContactsButton = myToolbar.getMenu().findItem(R.id.action_save_contact);
        if (deleteButton != null && saveContactsButton != null) {
            if (deleteButton.isVisible() || saveContactsButton.isVisible())
                hideAppbarButtons();
            else
                showAppbarButtons();
        }
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
            FrameLayout frameLayout = findViewById(R.id.content);
            switch (item.getItemId()) {
                case R.id.navigation_show:
                    if (!showCode) {
                        frameLayout.removeAllViews();
                        if (camera) {
                            camera = false;
                            mScannerView.stopCamera();
                        }
                        showCode();
                        hideAppbarButtons();
                    }
                    return true;

                case R.id.navigation_friends:
                    frameLayout.removeAllViews();
                    if (camera) {
                        camera = false;
                        mScannerView.stopCamera();
                    }
                    showCode = false;
                    showFriends();
                    hideAppbarButtons();

                    return true;

                case R.id.navigation_camera:
                    frameLayout.removeAllViews();
                    camera = true;
                    handleScan = true;
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
        ft.replace(R.id.content, new ShowcodeFragment());
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


        FrameLayout frameLayout = findViewById(R.id.content);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        mScannerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        frameLayout.addView(mScannerView);


        mScannerView.setResultHandler(this);
        mScannerView.startCamera();


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

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, showfriendsFragment);
        ft.commit();
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
        db.addPhone(new Phone(contact.getId(),2142186153,"Cell"));
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


    private boolean wait = true;
    public ArrayList<SocialAdder> socialAdderArrayList = new ArrayList<>();

    /**
     * From https://github.com/dm77/barcodescanner
     *
     * @param rawResult the raw data contained by the scanned QR code
     */
    @Override
    public void handleResult(Result rawResult) {


        if (handleScan) {    //if screen is not blocked by our dialog fragments
            handleScan = false;
//            Toast.makeText(this, "Contents = " + rawResult.getText() +
//                    ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

            String raw = rawResult.getText();
            String[] rawArray = raw.split("\\|");   //pipe character must be escaped in regex

            LocalDatabase database = new LocalDatabase(getApplicationContext());
            List<Contact> allContacts = database.getAllContacts();

            String t = rawArray[1];
            String userName = t;
//            Toast.makeText(this, "Name: " + userName, Toast.LENGTH_SHORT).show();
            Contact contact = new Contact(userName);
            database.addContact(contact);

            for (int i = 2; i < rawArray.length; i++) {

                t = rawArray[i];
                String uri;


                switch (t) {

                    case "ph":
                        String phoneNumber = rawArray[i + 1];
//                        Toast.makeText(this, "Phone: " + phoneNumber, Toast.LENGTH_SHORT).show();
                        String typePhone = rawArray[i + 2];
                        Log.i("PHONEDEBUG", "Contact id: " + contact.getId());
                        Phone phone = new Phone(contact.getId(), Integer.parseInt(phoneNumber), typePhone);
                        database.addPhone(phone);
                        break;

                    case "em":
                        String emailStr = rawArray[i + 1];
//                        Toast.makeText(this, "Email: " + emailStr, Toast.LENGTH_SHORT).show();
                        String typeEmail = rawArray[i + 2];
                        Email email = new Email(contact.getId(), emailStr, typeEmail);
                        database.addEmail(email);
                        break;


                    //when adding a new social media platform, simply copy this format
//                    case "go":
//                        String google_id = rawArray[i + 1];
//                        uri = "https://www.linkedin.com/profile/view?id=" + (google_id);
//                        socialAdderArrayList.add(new SocialAdder(uri, "Google"));
//                        Social googleSocial = new Social(contact.getId(), "Google", google_id);
//                        database.addSocial(googleSocial);
//                        break;

                    case "tw":
                        String twitter_id = rawArray[i + 1];
                        uri = "https://twitter.com/intent/follow?user_id=" + (twitter_id);
                        socialAdderArrayList.add(new SocialAdder(uri, "Twitter"));
                        Social twitterSocial = new Social(contact.getId(), "Twitter", twitter_id);
                        database.addSocial(twitterSocial);
                        break;
                    case "li":

                        String linkedin_id = rawArray[i + 1];
                        uri = "https://www.linkedin.com/profile/view?id=" + (linkedin_id);
                        socialAdderArrayList.add(new SocialAdder(uri, "LinkedIn"));
                        Social linkedinSocial = new Social(contact.getId(), "LinkedIn", linkedin_id);
                        database.addSocial(linkedinSocial);
                        break;

                    case "sp":
                        String spotify_id = rawArray[i + 1];
                        uri = "spotify:user:" + spotify_id;
                        socialAdderArrayList.add(new SocialAdder(uri, "Spotify"));
                        Social spotifySocial = new Social(contact.getId(), "Spotify", spotify_id);
                        database.addSocial(spotifySocial);
                        break;

                    case "fb":
                        String facebook_id = rawArray[i + 1];

                        try {
                            this.getPackageManager().getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
                            uri = "fb://facewebmodal/f?href=" + "https://www.facebook.com/" + facebook_id; //Tries with FB's URI
                        } catch (Exception e) {
                            uri = "https://www.facebook.com/" + (facebook_id); //catches a url to the desired page
                        }

                        socialAdderArrayList.add(new SocialAdder(uri, "Facebook"));
                        Social facebookSocial = new Social(contact.getId(), "Facebook", facebook_id);
                        database.addSocial(facebookSocial);
                        break;

                }

            }

            BottomNavigationView bottomNavigationView;
            bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
            bottomNavigationView.setSelectedItemId(R.id.navigation_friends);
            showNoticeDialog(userName);

        }
//        mScannerView.resumeCameraPreview(MainActivity.this);
    }

    public void socialAdd(String uri) {
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse(uri));
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);   //Makes it so that a single back-button press brings you back to our app
        this.startActivityForResult(i, 1);
    }



    @Override
    public void onResume() {
        super.onResume();
        if (camera) {
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (camera) {
            mScannerView.stopCamera();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (camera) {
            mScannerView.stopCamera();
        }
    }




    public void showNoticeDialog(String name) {
        if (!socialAdderArrayList.isEmpty()) {
            // Create an instance of the dialog fragment and show it
            DialogFragment dialog = new CustomDialogFragment();


            SocialAdder currentSocial = socialAdderArrayList.get(0);
            String type = currentSocial.getType();
            String uri = currentSocial.getUri();

            Bundle args = new Bundle();
            args.putString("dialog_title", "Would you like to add " + name + " on " + type + "?");
            args.putString("name", name);
            args.putString("uri", uri);
            args.putString("action", "socialAdd");

            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "CustomDialogFragment");

            socialAdderArrayList.remove(0);
        }
        if (socialAdderArrayList.isEmpty()) {
            handleScan = true;
        }

    }




    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //Add user intent
        //Go to next social media dialog

        Bundle mArgs = dialog.getArguments();
        String name = mArgs.getString("name");
        String action = mArgs.getString("action");
        String uri;
        switch (action) {
            case "socialAdd":
                uri = mArgs.getString("uri");
                socialAdd(uri);

                showNoticeDialog(name);
                break;
            case "singleSocialAdd":
                uri = mArgs.getString("uri");
               Log.i("SOCIALDEBUG",uri);
                socialAdd(uri);
                break;
            case "delete":
                showfriendsFragment.deleteContacts();

                MainActivity.hideAppbarButtons();
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

                MainActivity.hideAppbarButtons();
                break;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //Go to next social media dialog
        Bundle mArgs = dialog.getArguments();
        String name = mArgs.getString("name");
        String action = mArgs.getString("action");
        switch (action) {
            case "socialAdd":
                showNoticeDialog(name);
                break;
            case "singleSocialAdd":
                break;
            case "delete":
                break;
            case "saveContact":
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


    /**
     * From https://github.com/dm77/barcodescanner
     */
    private static class CustomViewFinderView extends ViewFinderView {


        public static final String TEXT = "Align code in box";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 20;
        public final Paint PAINT = new Paint();


        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            PAINT.setColor(Color.WHITE);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);

            setSquareViewFinder(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawTradeMark(canvas);
        }

        private void drawTradeMark(Canvas canvas) {
            Rect framingRect = getFramingRect();
            float tradeMarkTop;
            float tradeMarkCenter;
            if (framingRect != null) {
                tradeMarkTop = framingRect.top - PAINT.getTextSize() - 10;
                tradeMarkCenter = framingRect.centerX();
            } else {
                tradeMarkTop = 10;
                tradeMarkCenter = canvas.getWidth() / 2;
            }
            PAINT.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(TEXT, tradeMarkCenter, tradeMarkTop, PAINT);

        }
    }
}
