package com.acfreeman.socialmediascanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
//import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import com.google.zxing.Result;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.R.attr.height;
import static android.R.attr.left;
import static android.R.attr.top;
import static android.R.attr.width;
import static com.acfreeman.socialmediascanner.R.menu.navigation;

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

        setContentView(R.layout.activity_main);

        mTextMessage = findViewById(R.id.message);
        mImageView = new ImageView(this);
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
                    frameLayout.removeView(mImageView);
                    camera = false;
                    showCode();

                    return true;

                case R.id.navigation_friends:
                    frameLayout.removeAllViews();
                    camera = false;
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

    /**
     * Generating and displaying QR code
     * Uses ZXing
     */
    private void showCode(){
        QRCodeWriter writer = new QRCodeWriter();
        FrameLayout frameLayout = findViewById(R.id.content);
        try {
            int width = frameLayout.getWidth();
            int height = frameLayout.getHeight();
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode("test", BarcodeFormat.QR_CODE, width, height);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            mImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        //set image position
        mImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        //add view to layout
        frameLayout.addView(mImageView);

    }

    /**
     * Scanning a new QR code
     * Uses https://github.com/dm77/barcodescanner
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

        frameLayout.addView(mTextMessage);

    }



    /**
     * From https://github.com/dm77/barcodescanner
     * @param rawResult the raw data contained by the scanned QR code
     */
    @Override
    public void handleResult(Result rawResult) {
        Toast.makeText(this, "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

        // Wait 2 seconds to resume the preview
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(MainActivity.this);
            }
        }, 2000);
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


    /**
     * From https://github.com/dm77/barcodescanner
     */
    private static class CustomViewFinderView extends ViewFinderView {

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            setSquareViewFinder(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
        }
    }
}
