package com.acfreeman.socialmediascanner;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.acfreeman.socialmediascanner.db.Contacts;
import com.acfreeman.socialmediascanner.db.Emails;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Phones;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.social.SocialAdder;
import com.acfreeman.socialmediascanner.social.SocialDialogFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.twitter.sdk.android.core.Twitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, SocialDialogFragment.NoticeDialogListener {

    private TextView mTextMessage;
    private static ImageView mImageView;
    private ZXingScannerView mScannerView;
    private boolean camera;
    public boolean handleScan;
    private static final int MY_PERMISSIONS_REQUEST = 101;

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
        showFriends();
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
                    if(!showCode) {
                        frameLayout.removeAllViews();
                        if (camera) {
                            camera = false;
                            mScannerView.stopCamera();
                        }
                        showCode();
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

                    return true;

                case R.id.navigation_camera:
                    frameLayout.removeAllViews();
                    camera = true;
                    handleScan = true;
                    showCode = false;
                    scanCode();

                    return true;
            }
            return false;
        }
    };


    private final int socialCount = 2;  //TODO: retrieve value dynamically?
    /**
     * Generating and displaying QR code
     * Uses ZXing
     */


    public Switch phonesSwitch, emailsSwitch;

    ArrayList<SwitchModel> switchModels;
    private static CustomShowcodeAdapter showcodeAdapter;

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


        switchModels.add(new SwitchModel("Phone number(s)", "ph", R.drawable.icons8_phone));
        switchModels.add(new SwitchModel("Email address(es)", "em", R.drawable.icons8_gmail));

        List socials = new ArrayList();
        LocalDatabase db = new LocalDatabase(getApplicationContext());
        List<Owner> owner = db.getAllOwner();
        ArrayList<Social> sociallist = db.getUserSocials(owner.get(0).getId());
        for(Social s : sociallist) {


            switchModels.add(new SwitchModel(s.getType(), s.getUsername()));

        }


        showcodeAdapter = new CustomShowcodeAdapter(switchModels, getApplicationContext());
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
            ArrayList<Phones> ownerPhones = database.getUserPhones(owner.getId());
            ArrayList<Emails> ownerEmails = database.getUserEmails(owner.getId());
            ArrayList<Social> sociallist = database.getUserSocials(owner.getId());

            builder.append(ownerName + "|");
            for (SwitchModel sw : switchSet) {
                Log.i("SWITCHERDEBUG", sw.getSwitchName() + ", " + sw.getState());
                if(sw.getState()) {
                    switch (sw.getTag()) {
                        case "ph":

                            for (Phones p : ownerPhones) {
                                builder.append("ph" + "|" + p.getNumber() + "|" + p.getType() + "|");
                            }
                            break;
                        case "em":
                            for (Emails e : ownerEmails) {
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

//            Double  dcrop = width*1.0*3/4;
//            int crop =  dcrop.intValue();
//            Bitmap bm = Bitmap.createBitmap(bitmap, width/8, width/8, crop, crop);  //crop the qrcode image obtained from bitmatrix
//            mImageView.setImageBitmap(bm);


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
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

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

    private static CustomContactsAdapter adapter;
    ArrayList<DataModel> dataModels;
    ListView listView;

    private void showFriends() {

        FrameLayout frameLayout = findViewById(R.id.content);
        listView = new ListView(this);
        dataModels = new ArrayList<>();


        LocalDatabase db = new LocalDatabase(getApplicationContext());
        List<Contacts> contactslist = db.getAllContacts();
        for (Contacts c : contactslist) {
            ArrayList<Phones> userphoneslist = db.getUserPhones(c.getId());
            ArrayList<Emails> useremailslist = db.getUserEmails(c.getId());
            ArrayList<Social> sociallist = db.getUserSocials(c.getId());
            dataModels.add(new DataModel(c.getName(), userphoneslist, useremailslist, sociallist));
        }


//        dataModels.add(new DataModel("Apple Pie", "Android 1.0", "1","September 23, 2008"));
//        dataModels.add(new DataModel("Banana Bread", "Android 1.1", "2","February 9, 2009"));

        adapter = new CustomContactsAdapter(dataModels, getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModel dataModel = dataModels.get(position);

                Snackbar.make(view, dataModel.getName() + "\n" + dataModel.getPhones().get(0).getNumber() + "\n" + dataModel.getEmails().get(0).getEmail() + "\n" + dataModel.getSocials().get(0).getType(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
        frameLayout.addView(listView);


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


    private List readDatabaseTest() {

        List res = new ArrayList();
        LocalDatabase db = new LocalDatabase(getApplicationContext());

        List<Owner> owner = db.getAllOwner();
        res.add(owner.get(0).getId());
        res.add(owner.get(0).getName());


        ArrayList<Phones> phonelist = db.getUserPhones(owner.get(0).getId());
        for (Phones p : phonelist) {
            res.add(p.getNumber());
            res.add(p.getType());
        }

        ArrayList<Emails> emaillist = db.getUserEmails(owner.get(0).getId());
        for (Emails e : emaillist) {
            res.add(e.getEmail());
            res.add(e.getType());
        }

        ArrayList<Social> sociallisttest = db.getUserSocials(owner.get(0).getId());
        for (Social s : sociallisttest) {
            res.add(s.getType());
            res.add(s.getUsername());
        }

        List<Contacts> contactstest = db.getAllContacts();
        for (Contacts c : contactstest) {
            res.add(c.getName());

            ArrayList<Phones> userphoneslist = db.getUserPhones(c.getId());
            for (Phones p : userphoneslist) {
                res.add(p.getNumber());
                res.add(p.getType());
            }

            ArrayList<Emails> useremailslist = db.getUserEmails(c.getId());
            for (Emails em : useremailslist) {
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
            Toast.makeText(this, "Contents = " + rawResult.getText() +
                    ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

            String raw = rawResult.getText();
            String[] rawArray = raw.split("\\|");   //pipe character must be escaped in regex

            LocalDatabase database = new LocalDatabase(getApplicationContext());
            List<Contacts> allContacts = database.getAllContacts();
            int contactId;
//            if(allContacts.isEmpty()){
//                contactId = 0;
//            } else {
//                contactId = allContacts.get(allContacts.size()-1);
//            }

//            Owner owner = new Owner(0, nameEditText.getText().toString());
//            database.addOwner(owner);

            String t = rawArray[1];
            String userName = t;
            Toast.makeText(this, "Name: " + userName, Toast.LENGTH_SHORT).show();
            Contacts contact = new Contacts(userName);
            database.addContacts(contact);

            for (int i = 2; i < rawArray.length; i++) {

                t = rawArray[i];
                String uri;


                switch (t) {

                    case "ph":
                        String phoneNumber = rawArray[i + 1];
                        Toast.makeText(this, "Phone: " + phoneNumber, Toast.LENGTH_SHORT).show();
                        String typePhone = rawArray[i + 2];
                        Log.i("PHONEDEBUG", "Contact id: " + contact.getId());
                        Phones phone = new Phones(contact.getId(), Integer.parseInt(phoneNumber), typePhone);
                        database.addPhones(phone);
                        break;

                    case "em":
                        String emailStr = rawArray[i + 1];
                        Toast.makeText(this, "Email: " + emailStr, Toast.LENGTH_SHORT).show();
                        String typeEmail = rawArray[i + 2];
                        Emails email = new Emails(contact.getId(), emailStr, typeEmail);
                        database.addEmails(email);
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

                }

            }

            showNoticeDialog("Andrew Freeman");

        }
        mScannerView.resumeCameraPreview(MainActivity.this);
    }

    public void socialAdd(String uri) {
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse(uri));
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);   //Makes it so that a single back-button press brings you back to our app
        startActivityForResult(i, 1);
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


    public void showNoticeDialog(String name) {
        if (!socialAdderArrayList.isEmpty()) {
            // Create an instance of the dialog fragment and show it
            DialogFragment dialog = new SocialDialogFragment();


            SocialAdder currentSocial = socialAdderArrayList.get(0);
            String type = currentSocial.getType();
            String uri = currentSocial.getUri();

            Bundle args = new Bundle();
            args.putString("dialog_title", "Would you like to add " + name + " on " + type + "?");
            args.putString("name", name);
            args.putString("uri", uri);

            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "SocialDialogFragment");

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
        String uri = mArgs.getString("uri");
        socialAdd(uri);

        showNoticeDialog(name);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //Go to next social media dialog
        Bundle mArgs = dialog.getArguments();
        String name = mArgs.getString("name");
        showNoticeDialog(name);
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
