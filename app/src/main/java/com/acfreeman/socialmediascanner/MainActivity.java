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
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FrameLayout frameLayout = findViewById(R.id.content);
            switch (item.getItemId()) {
                case R.id.navigation_show:
                    frameLayout.removeView(mImageView);
                    showCode();

                    return true;
                case R.id.navigation_friends:
                    frameLayout.removeAllViews();
                    mTextMessage.setText(R.string.title_friends);
                    return true;
                case R.id.navigation_camera:
                    frameLayout.removeAllViews();
                    scanCode2();

                    return true;
            }
            return false;
        }

    };

    private void showCode(){
        QRCodeWriter writer = new QRCodeWriter();
        FrameLayout frameLayout = findViewById(R.id.content);
        try {
//            int width = mImageView.getWidth();
//            int height = mImageView.getHeight();
            int width = frameLayout.getWidth();
            int height = frameLayout.getHeight();
//            BitMatrix bitMatrix = writer.encode("test", BarcodeFormat.QR_CODE, width, height);
//            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            for (int i = 0; i < width; i++) {
//                for (int j = 0; j < height; j++) {
//                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK: Color.WHITE);
//                }
//            }
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode("test", BarcodeFormat.QR_CODE, width, height);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            mImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        //setting image position
        mImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

//adding view to layout


        frameLayout.addView(mImageView);
        //setContentView(R.layout.activity_main);

    }

    private void scanCode(){
        new IntentIntegrator(this).initiateScan();
    }

    private void scanCode2(){


//        FrameLayout frameLayout = findViewById(R.id.content);
//        barcodeScannerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));
//        frameLayout.addView(barcodeScannerView);

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

        //TEMP
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private ViewGroup contentFrame;
    private ZXingScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        mImageView = new ImageView(this);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        contentFrame = (ViewGroup) findViewById(R.id.content);


    }


    @Override
    public void handleResult(Result rawResult) {
        Toast.makeText(this, "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(MainActivity.this);
            }
        }, 2000);
    }


    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "ZXing";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 40;
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
            float tradeMarkLeft;
            if (framingRect != null) {
                tradeMarkTop = framingRect.bottom + PAINT.getTextSize() + 10;
                tradeMarkLeft = framingRect.left;
            } else {
                tradeMarkTop = 10;
                tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
            }
            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);
        }
    }
}
