package com.acfreeman.socialmediascanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import static android.R.attr.height;
import static android.R.attr.left;
import static android.R.attr.top;
import static android.R.attr.width;
import static com.acfreeman.socialmediascanner.R.menu.navigation;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ImageView mImageView;


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
//                    scanCode();

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
            BitMatrix bitMatrix = writer.encode("test", BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK: Color.WHITE);
                }
            }
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

//    private void scanCode(){
//        MultiFormatReader reader = new MultiFormatReader();
//        LuminanceSource source = new PlanarYUVLuminanceSource(yuvData, dataWidth, dataHeight, left, top, width, height, false);
//        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//        Result result;
//        try {
//            result = reader.decode(bitmap);
//            if (result != null) {
//                mDialog.setTitle("Result");
//                mDialog.setMessage(result.getText());
//                mDialog.show();
//            }
//        } catch (NotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        mImageView = new ImageView(this);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
