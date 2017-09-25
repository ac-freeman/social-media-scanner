package com.acfreeman.socialmediascanner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private TextView mTextMessage;
    private ImageView mImageView;
    private ZXingScannerView mScannerView;
    private boolean camera;

    /**
     * Called when activity begins
     * Creates basic layout with bottom navigation
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
                    frameLayout.removeAllViews();
                    if(camera) {
                        camera = false;
                        mScannerView.stopCamera();
                    }
                    showCode();

                    return true;

                case R.id.navigation_friends:
                    frameLayout.removeAllViews();
                    if(camera) {
                        camera = false;
                        mScannerView.stopCamera();
                    }
                    showFriends();

                    return true;

                case R.id.navigation_camera:
                    frameLayout.removeAllViews();
                    camera = true;
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


    private ArrayList<SocialSwitch> switchList = new ArrayList<>();
    private void showCode(){
        QRCodeWriter writer = new QRCodeWriter();
        final FrameLayout frameLayout = findViewById(R.id.content);
        mImageView = new ImageView(this);




        ScrollView scroll = new ScrollView(this);
        TableLayout table = new TableLayout(this);
        table.setVerticalScrollBarEnabled(true);
        TableRow tableRow;
        TextView t1;
        Switch t2;


        frameLayout.addView(scroll);
        scroll.addView(table);
        generateCode(frameLayout);
//        //set image position
//        mImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));

        //add view to layout
//        frameLayout.addView(mImageView);

        tableRow = new TableRow(this);
        tableRow.addView(mImageView);
        table.addView(tableRow);

        //////
        List socials = new ArrayList();
        LocalDatabase db = new LocalDatabase(getApplicationContext());
        List<Owner> owner = db.getAllOwner();

        ArrayList<Social> sociallist = db.getUserSocials(owner.get(0).getId());
        for(Social s : sociallist) {
            tableRow = new TableRow(this);
            final SocialSwitch socialSwitch = new SocialSwitch(s.getType(), s.getUsername(), this);


            socialSwitch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    socialSwitch.toggleEnabled();
                    generateCode(frameLayout);
                }
            });
            switchList.add(socialSwitch);
            tableRow.addView(socialSwitch.getSwitch());
            table.addView(tableRow);
        }

        //////
        
    }


    public void generateCode(FrameLayout frameLayout) {

        /////
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        long user_id = session.getUserId();
        //////

        try {
            int width = frameLayout.getWidth();
            int height = frameLayout.getHeight();
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            StringBuilder builder = new StringBuilder();
            builder.append("|");


            for(SocialSwitch sw : switchList){
                if(sw.getEnabled()){
                    builder.append(sw.getType_db() + "|" + sw.getUser_id() + "|");
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
    private void scanCode(){

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

    private void showFriends(){
        //TODO: Add scrollable friends page
        FrameLayout frameLayout = findViewById(R.id.content);
        mTextMessage = new TextView(this);
        mTextMessage.setText(R.string.title_friends);
//        List data = readDB();
        List data = readDatabaseTest();
        String text = "";
        for(int x = 0; x < data.size(); x++){
            text += data.get(x);
        }
        mTextMessage.setText(text);
        frameLayout.addView(mTextMessage);



    }



    private List readDatabaseTest(){

        List res = new ArrayList();
        LocalDatabase db = new LocalDatabase(getApplicationContext());

        List<Owner> owner = db.getAllOwner();
        res.add(owner.get(0).getId());
        res.add(owner.get(0).getName());


        ArrayList<Phones> phonelist= db.getUserPhones(owner.get(0).getId());
        for(Phones p : phonelist) {
            res.add(p.getNumber());
            res.add(p.getType());
        }

        ArrayList<Emails> emaillist= db.getUserEmails(owner.get(0).getId());
        for(Emails e : emaillist) {
            res.add(e.getEmail());
            res.add(e.getType());
        }

        ArrayList<Social> sociallisttest = db.getUserSocials(owner.get(0).getId());
        for(Social s : sociallisttest) {
            res.add(s.getType());
            res.add(s.getUsername());
        }

        return res;

    }


    /**
     * From https://github.com/dm77/barcodescanner
     * @param rawResult the raw data contained by the scanned QR code
     */
    @Override
    public void handleResult(Result rawResult) {
        Toast.makeText(this, "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

        String raw = rawResult.getText();
        String[] rawArray = raw.split("\\|");   //pipe character must be escaped in regex

        for(int i = 0; i<rawArray.length; i++){

            String t = rawArray[i];
            String uri;
            switch(t){

                //when adding a new social media platform, simply copy this format
                case "twitter":
                    String twitter_id = rawArray[i+1];
                    uri = "https://twitter.com/intent/follow?user_id="+(twitter_id);
                    socialAdd(uri);
                    break;
                case "linkedin":
                    String linkedin_id = rawArray[i+1];
                    uri = linkedin_id;
                    socialAdd(uri);
                    break;




            }
        }





        // Wait 2 seconds to resume the preview
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(MainActivity.this);
            }
        }, 2000);
    }

    public void socialAdd(String uri){
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse(uri));
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);   //Makes it so that a single back-button press brings you back to our app
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(camera){
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(camera) {
            mScannerView.stopCamera();
        }
    }


    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = this.openFileInput("ppl.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
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
                tradeMarkCenter = canvas.getWidth()/2;
            }
            PAINT.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(TEXT, tradeMarkCenter, tradeMarkTop, PAINT);

        }
    }
}
