package com.acfreeman.socialmediascanner;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
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
import android.view.Display;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acfreeman.socialmediascanner.db.Contact;
import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Phone;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.showcode.ShowcodeAdapter;
import com.acfreeman.socialmediascanner.showcode.SwitchModel;
import com.acfreeman.socialmediascanner.showfriends.CardDataModel;
import com.acfreeman.socialmediascanner.showfriends.ContactCardAdapter;
import com.acfreeman.socialmediascanner.showfriends.ContactsAdapter;
import com.acfreeman.socialmediascanner.showfriends.DataModel;
import com.acfreeman.socialmediascanner.social.SocialAdder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.twitter.sdk.android.core.Twitter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private static final int MY_PERMISSIONS_REQUEST_CONTACTS = 2;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    SharedPreferences mPrefs;
    final String firstMainActivityPref = "firstMainActivity";
    Boolean firstMainActivity;

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

    public void hideAppbarButtons() {
        MenuItem deleteButton = myToolbar.getMenu().findItem(R.id.action_delete);
        MenuItem saveContactsButton = myToolbar.getMenu().findItem(R.id.action_save_contact);
        if (deleteButton != null && saveContactsButton != null) {
            deleteButton.setVisible(false);
            saveContactsButton.setVisible(false);
        }
    }

    public void showAppbarButtons() {
        MenuItem deleteButton = myToolbar.getMenu().findItem(R.id.action_delete);
        MenuItem saveContactsButton = myToolbar.getMenu().findItem(R.id.action_save_contact);
        if (deleteButton != null && saveContactsButton != null) {
            deleteButton.setVisible(true);
            saveContactsButton.setVisible(true);
        }
    }

    public void toggleAppbarButtons() {
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
    ArrayList<SwitchModel> switchModels = new ArrayList<>();
    private static ShowcodeAdapter showcodeAdapter;

    ListView codeListView;
    Boolean showCode;

    private void showCode() {
        showCode = true;
        QRCodeWriter writer = new QRCodeWriter();
        final FrameLayout frameLayout = findViewById(R.id.content);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        mImageView = new ImageView(this);
        mImageView.setId(1);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params1.addRule(RelativeLayout.BELOW, mImageView.getId());
        params1.addRule(RelativeLayout.CENTER_IN_PARENT);


        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth() * 3 / 4;
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(width, width);
        params2.addRule(RelativeLayout.CENTER_IN_PARENT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        switchModels = new ArrayList<>();
        codeListView = new ListView(this);


        switchModels.add(new SwitchModel("Phone number(s)", "ph", R.drawable.ic_phone_black_24dp));
        switchModels.add(new SwitchModel("Email address(es)", "em", R.drawable.ic_email_black_24dp));

        List socials = new ArrayList();
        LocalDatabase db = new LocalDatabase(getApplicationContext());
        List<Owner> owner = db.getAllOwner();
        ArrayList<Social> sociallist = db.getUserSocials(owner.get(0).getId());
        for (Social s : sociallist) {
            switchModels.add(new SwitchModel(s.getType(), s.getUsername()));
        }


        showcodeAdapter = new ShowcodeAdapter(switchModels, getApplicationContext());
        codeListView.setAdapter(showcodeAdapter);


        codeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SwitchModel switchModel = switchModels.get(position);
                Log.i("SWITCHDEBUG", "Something clicked");
                switchModel.getSwitcher().toggle();
                switchModel.toggleState();
                Log.i("SWITCHDEBUG", "Switch toggled to " + switchModel.getState());
                generateCode(frameLayout, switchModels);
            }
        });

        relativeLayout.addView(mImageView, params2);
        relativeLayout.addView(codeListView, params1);
        frameLayout.addView(relativeLayout);

        generateCode(frameLayout, switchModels);
    }


    public void generateCode(FrameLayout frameLayout, ArrayList<SwitchModel> switchSet) {

        try {
            int width = frameLayout.getWidth();
            int height = frameLayout.getHeight();
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            StringBuilder builder = new StringBuilder();
            builder.append("|");

            // personal information
            LocalDatabase database = new LocalDatabase(this);
            Owner owner = database.getOwner(0);
            String ownerName = owner.getName();
            ArrayList<Phone> ownerPhones = database.getUserPhones(owner.getId());
            ArrayList<Email> ownerEmails = database.getUserEmails(owner.getId());

            builder.append(ownerName + "|");
            for (SwitchModel sw : switchSet) {
                Log.i("SWITCHERDEBUG", sw.getSwitchName() + ", " + sw.getState());
                if (sw.getState()) {
                    switch (sw.getTag()) {
                        case "ph":

                            for (Phone p : ownerPhones) {
                                builder.append("ph" + "|" + p.getNumber() + "|" + p.getType() + "|");
                            }
                            break;
                        case "em":
                            for (Email e : ownerEmails) {
                                builder.append("em" + "|" + e.getEmail() + "|" + e.getType() + "|");
                            }
                            break;
                        default:
                            builder.append(sw.getTag() + "|" + sw.getUser_id() + "|");
                            break;

                    }
                }
            }

            String encodeStr = builder.toString();

            BitMatrix bitMatrix = multiFormatWriter.encode(encodeStr, BarcodeFormat.QR_CODE, width, width);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            mImageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
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

    private ContactsAdapter adapter;
    ArrayList<DataModel> dataModels;
    ListView listView;
    private ContactCardAdapter cardAdapter;
    ArrayList<CardDataModel> cardDataModels;
    ListView cardListView;

    private void showFriends() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        firstMainActivity = mPrefs.getBoolean(firstMainActivityPref, true);

        FrameLayout frameLayout = findViewById(R.id.content);
        listView = new ListView(getApplicationContext());

        dataModels = new ArrayList<>();

        if (firstMainActivity) {
            addDummyData();
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean(firstMainActivityPref, false);
            editor.commit(); // Very important to save the preference
        }
        LocalDatabase db = new LocalDatabase(getApplicationContext());
        List<Contact> contactslist = db.getAllContacts();


        //sort contacts alphabetically
        if (contactslist.size() > 0) {
            Collections.sort(contactslist, new Comparator<Contact>() {
                @Override
                public int compare(final Contact object1, final Contact object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
        }

        for (Contact c : contactslist) {
            ArrayList<Phone> userphoneslist = db.getUserPhones(c.getId());
            ArrayList<Email> useremailslist = db.getUserEmails(c.getId());
            ArrayList<Social> sociallist = db.getUserSocials(c.getId());
            dataModels.add(new DataModel(c.getName(), c.getId(), userphoneslist, useremailslist, sociallist));
        }

        adapter = new ContactsAdapter(dataModels, getApplicationContext());

        listView.setAdapter(adapter);
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                toggleAppbarButtons();

                Log.i("CONTACTDEBUG", "Long click");
                adapter.toggleEditMode();
                adapter.checks.set(i, 1);

                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

                //tells system to stop listening for another click in same action
                return true;
            }

        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final DataModel dataModel = dataModels.get(position);

//                Snackbar.make(view, dataModel.getName() + "\n" + dataModel.getPhones().get(0).getNumber() + "\n" + dataModel.getEmails().get(0).getEmail() + "\n" + dataModel.getSocials().get(0).getType(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();

                if (adapter.inEditmode) {
                    if (adapter.checks.get(position) == 1)
                        adapter.checks.set(position, 0);
                    else
                        adapter.checks.set(position, 1);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    final RelativeLayout layout = findViewById(R.id.activity_contact_card);
                    final TextView cardName = findViewById(R.id.activity_contact_card_name);
                    cardName.setText(dataModel.getName());


                    cardListView = findViewById(R.id.activity_contact_card_listview);
                    cardListView.setClickable(false);
                    cardDataModels = new ArrayList<>();

                    for(Phone p : dataModel.getPhones()){
                        cardDataModels.add(new CardDataModel(p));
                    }
                    for(Email e : dataModel.getEmails()){
                        cardDataModels.add(new CardDataModel(e));
                    }
                    for(Social s : dataModel.getSocials()) {
                        cardDataModels.add(new CardDataModel(s));
                    }

                    cardAdapter= new ContactCardAdapter(cardDataModels, getApplicationContext());
                    cardListView.setAdapter(cardAdapter);

                    layout.setVisibility(View.VISIBLE);
                    final int height = getResources().getDisplayMetrics().heightPixels;

                    final ImageView darkener = (ImageView) findViewById(R.id.darken_frame);
                    darkener.setAlpha(0.6f);
                    darkener.setClickable(true);

                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout, "TranslationY", height, 500);
                    objectAnimator.setDuration(400);
                    objectAnimator.start();


                    darkener.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout, "TranslationY", 500, height);
                            objectAnimator.setDuration(400);
                            objectAnimator.start();
                            darkener.setAlpha(0.0f);
                            darkener.setClickable(false);
                        }
                    });

                    layout.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                                startY = layout.getY();
                                Log.i("MOVEDEBUG","STARTY "+startY);
                                float motionY = motionEvent.getRawY();
                                margin = motionY - startY;
                            }
                            else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){

                                float motionY = motionEvent.getRawY();

                                float newY = motionY - margin;
                                Log.i("MOVEDEBUG","Moving to "+newY);
                                layout.setVisibility(View.INVISIBLE);
                                if(newY<0)
                                    newY=0;
                                layout.setY(newY);
                                layout.setVisibility(View.VISIBLE);
                            }
                            else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                                Log.i("MOVEDEBUG","ACTION UP "+layout.getY());
                                if(layout.getY()>startY){
                                    //lower
                                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout, "TranslationY", layout.getY(), height);
                                    objectAnimator.setDuration(400);
                                    objectAnimator.start();
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            darkener.setAlpha(0.0f);
                                            darkener.setClickable(false);
                                        }
                                    }, 400);

                                } else {
                                    //raise
                                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout, "TranslationY", layout.getY(), 0);
//                                    objectAnimator.setDuration(400);
                                    objectAnimator.start();


//                                    layout.addView(cardListView);
                                }
                            }
                            return false;
                        }
                    });

                }

                Log.i("CONTACTDEBUG", "Item clicked!");
            }
        });

        frameLayout.addView(listView);


    }
    float margin;
    float startY;

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
        contact = new Contact("Queen Rita");
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
        startActivityForResult(i, 1);
    }

    private void saveContactsToDevice() {


        for (int i = 0; i < adapter.checks.size(); i++) {
            if (adapter.checks.get(i) == 1) {
                DataModel model = adapter.getItem(i);

                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                // Sets the MIME type to match the Contacts Provider
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                intent.putExtra(ContactsContract.Intents.Insert.NAME, model.getName());
                for (Phone p : model.getPhones()) {
                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, Long.toString(p.getNumber()))
                            .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, p.getType());
                }

                for (Email em : model.getEmails()) {
                    intent.putExtra(ContactsContract.Intents.Insert.EMAIL, em.getEmail())
                            .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, em.getType());
                }

                startActivity(intent);
            }
        }
        adapter.inEditmode = false;
        adapter.notifyDataSetChanged();

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
        switch (action) {
            case "socialAdd":
                String uri = mArgs.getString("uri");
                socialAdd(uri);

                showNoticeDialog(name);
                break;
            case "delete":
                for (int i = 0; i < adapter.checks.size(); i++) {
                    if (adapter.checks.get(i) == 1) {
                        adapter.checks.remove(i);
                        //TODO: remove from listview
                        DataModel model = adapter.getItem(i);
                        long contactId = model.getId();
                        adapter.remove(model);
                        Log.i("CONTACTDEBUG", "Removing item from list at position " + i);
                        LocalDatabase db = new LocalDatabase(getApplicationContext());
                        db.deleteContactById(contactId);

                        adapter.inEditmode = false;
                        adapter.notifyDataSetChanged();
                        i--;
                    }

                }
                hideAppbarButtons();
                break;
            case "saveContact":
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_CONTACTS},
                            MY_PERMISSIONS_REQUEST_CONTACTS);
                } else {
                    saveContactsToDevice();
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
        switch (action) {
            case "socialAdd":
                showNoticeDialog(name);
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
                    saveContactsToDevice();
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
