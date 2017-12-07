package com.acfreeman.socialmediascanner.scancode;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.acfreeman.socialmediascanner.CustomDialogFragment;
import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.Contact;
import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Phone;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.social.SocialAdder;
import com.google.zxing.Result;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Andrew on 10/27/2017.
 */

public class ScancodeFragment extends Fragment implements ZXingScannerView.ResultHandler {


    private static ImageView mImageView;
    public me.dm7.barcodescanner.zxing.ZXingScannerView scannerView;
    private boolean connectedInternet = false;

    public boolean handleScan = true;
    private CustomDialogFragment internetWarning;
    private boolean showAddDialog = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scancode,
                container, false);

        FrameLayout frameLayout = view.findViewById(R.id.scancode_frame);
        scannerView = new ZXingScannerView(getActivity()) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };

        scannerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        frameLayout.addView(scannerView);
//
//
        scannerView.setResultHandler(this);
        scannerView.startCamera();

        ConnectivityReceiver cr = new ConnectivityReceiver();
        cr.onReceive(getApplicationContext(), new Intent());
        if (!connectedInternet) {
            Log.e("FFFFFFFFFFFFFF", "Internet not connected");
            //Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            internetWarning = new CustomDialogFragment();

            Bundle args = new Bundle();
            args.putString("dialog_title", "No Internet Connection Detected");
            args.putString("action", "acknowledge");

            internetWarning.setArguments(args);
            internetWarning.show(getFragmentManager(), "CustomDialogFragment");

            showAddDialog = false;
        } else {
            showAddDialog = true;
        }

        return view;
    }

    private boolean isDialogShowing() {
        for (DialogFragment d : dialogsList) {
            if (d.isVisible()) {
                return false;
            }
        }
        return true;
    }

    private boolean wait = true;
    public ArrayList<SocialAdder> socialAdderArrayList = new ArrayList<>();

    @Override
    public void handleResult(Result rawResult) {
//        handleScan = isDialogShowing();
        Log.i("SCANDEBUG", "handlescan: " + handleScan);
        if (handleScan) {    //if screen is not blocked by our dialog fragments
            handleScan = false;
//            Toast.makeText(this, "Contents = " + rawResult.getText() +
//                    ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

            String raw = rawResult.getText();
            String[] rawArray = raw.split("\\|");   //pipe character must be escaped in regex

            LocalDatabase database = new LocalDatabase(getActivity());
            List<Contact> allContacts = database.getAllContacts();

            String t = rawArray[1];
            String userName = t;
//            Toast.makeText(this, "Name: " + userName, Toast.LENGTH_SHORT).show();
            Contact contact = new Contact(userName);
            database.addContact(contact);

            Owner owner = database.getOwner(0);
            ArrayList<Social> socials = database.getUserSocials(owner.getId());
            String[] socialNameArray = new String[socials.size()];
            for (int i = 0; i < socials.size(); i++) {
                socialNameArray[i] = socials.get(i).getType();
            }
            List socialNameList = Arrays.asList(socialNameArray);


            Boolean hideUnconnected = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("hide_disconnected_socials_switch", true);

            for (int i = 2; i < rawArray.length; i++) {

                t = rawArray[i];
                String uri;


                switch (t) {

                    case "ph":
                        String phoneNumber = rawArray[i + 1];
                        String typePhone = rawArray[i + 2];
                        Log.i("PHONEDEBUG", "Contact id: " + contact.getId());
                        Phone phone = new Phone(contact.getId(), Long.parseLong(phoneNumber), typePhone);
                        database.addPhone(phone);
                        break;

                    case "em":
                        String emailStr = rawArray[i + 1];
                        String typeEmail = rawArray[i + 2];
                        Email email = new Email(contact.getId(), emailStr, typeEmail);
                        database.addEmail(email);
                        break;


                    //when adding a new social media platform, simply copy this format
                    case "tw":
                        String twitter_id = rawArray[i + 1];
                        uri = "https://twitter.com/intent/follow?user_id=" + (twitter_id);
                        if (!hideUnconnected || socialNameList.contains("tw"))
                            socialAdderArrayList.add(new SocialAdder(uri, "Twitter"));
                        Social twitterSocial = new Social(contact.getId(), "Twitter", twitter_id);
                        database.addSocial(twitterSocial);
                        break;
                    case "li":

                        String linkedin_id = rawArray[i + 1];
                        uri = "https://www.linkedin.com/profile/view?id=" + (linkedin_id);
                        if (!hideUnconnected || socialNameList.contains("li"))
                            socialAdderArrayList.add(new SocialAdder(uri, "LinkedIn"));
                        Social linkedinSocial = new Social(contact.getId(), "LinkedIn", linkedin_id);
                        database.addSocial(linkedinSocial);
                        break;

                    case "sp":
                        String spotify_id = rawArray[i + 1];
                        uri = "spotify:user:" + spotify_id;
                        if (!hideUnconnected || socialNameList.contains("sp"))
                            socialAdderArrayList.add(new SocialAdder(uri, "Spotify"));
                        Social spotifySocial = new Social(contact.getId(), "Spotify", spotify_id);
                        database.addSocial(spotifySocial);
                        break;

                    case "fb":
                        String facebook_id = rawArray[i + 1];

                        try {
                            getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
                            uri = "fb://facewebmodal/f?href=" + "https://www.facebook.com/" + facebook_id; //Tries with FB's URI
                        } catch (Exception e) {
                            uri = "https://www.facebook.com/" + (facebook_id); //catches a url to the desired page
                        }

                        if (!hideUnconnected || socialNameList.contains("fb"))
                            socialAdderArrayList.add(new SocialAdder(uri, "Facebook"));
                        Social facebookSocial = new Social(contact.getId(), "Facebook", facebook_id);
                        database.addSocial(facebookSocial);
                        break;

                    case "go":
                        String google_id = rawArray[i + 1];
                        uri = "https://plus.google.com/" + google_id;
                        if (!hideUnconnected || socialNameList.contains("go"))
                            socialAdderArrayList.add(new SocialAdder(uri, "Google+"));
                        Social googlePlusSocial = new Social(contact.getId(), "Google+", google_id);
                        database.addSocial(googlePlusSocial);
                        break;
                }

            }

            showCameraRequestDialog(contact);

        } else {

            scannerView.setResultHandler(this);
            scannerView.startCamera();
            //TODO: Doesn't work
        }
    }


    private Contact currentContact;
    static final int REQUEST_TAKE_PHOTO = 1111;
    static final int REQUEST_IMAGE_CAPTURE = 1112;

    public void showCameraPreview() {
        scannerView.stopCamera();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.acfreeman.socialmediascanner.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                List res = new ArrayList();
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + "lastcontactscan";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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


    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();


    }

    @Override
    public void onPause() {
        super.onPause();

        scannerView.stopCamera();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        scannerView.stopCamera();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            LocalDatabase db = new LocalDatabase(getActivity());

            Bitmap image = BitmapFactory.decodeFile(mCurrentPhotoPath);

            int width = image.getWidth();
            int height = image.getHeight();

            Bitmap resizedBitmap;
            if (width < height) {
                resizedBitmap = Bitmap.createBitmap(image, 0, (height - width) / 2, width, width);
            } else {
                resizedBitmap = Bitmap.createBitmap(image, (width - height) / 2, 0, height, height);
            }

// convert bitmap to byte

            Bitmap resizedBitmap2 = Bitmap.createScaledBitmap(resizedBitmap, 500, 500, true);

//            ByteArrayOutputStream stream = new ByteArrayOutputStream();

//            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 5, stream);     //TODO: improve

//            byte imageInByte[] = stream.toByteArray();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resizedBitmap2.compress(Bitmap.CompressFormat.JPEG, 20, stream);
            byte imageInByte[] = stream.toByteArray();
            currentContact.setImage(imageInByte);
            db.updateImage(currentContact);

            File fdelete = new File(mCurrentPhotoPath);
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    Log.e("-->", "file Deleted :" + mCurrentPhotoPath);
                } else {
                    Log.e("-->", "file not Deleted :" + mCurrentPhotoPath);
                }
            }

            showSocialAddDialog(currentContact.getName());

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            LocalDatabase db = new LocalDatabase(getActivity());

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            int width = imageBitmap.getWidth();
            int height = imageBitmap.getHeight();

            Bitmap resizedBitmap;
            if (width < height) {
                resizedBitmap = Bitmap.createBitmap(imageBitmap, 0, (height - width) / 2, width, width);
            } else {
                resizedBitmap = Bitmap.createBitmap(imageBitmap, (width - height) / 2, 0, height, height);
            }

            Bitmap resizedBitmap2 = Bitmap.createScaledBitmap(resizedBitmap, 500, 500, true);

            currentContact.setBitmap(resizedBitmap2);
            db.updateImage(currentContact);

            showSocialAddDialog(currentContact.getName());
        }
    }

    ArrayList<DialogFragment> dialogsList = new ArrayList<>();

    public void showCameraRequestDialog(Contact contact) {
        handleScan = false;
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new CustomDialogFragment();
        dialogsList.add(dialog);

        Bundle args = new Bundle();
        args.putString("dialog_title", "Would you like to take a photo of " + contact.getName() + "?");
        args.putString("name", contact.getName());
        args.putString("action", "photoCapture");
        currentContact = contact;


        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "CustomDialogFragment");
    }


    public void showSocialAddDialog(String name) {
        if (!socialAdderArrayList.isEmpty() && showAddDialog) {
            handleScan = false;
            // Create an instance of the dialog fragment and show it
            DialogFragment dialog = new CustomDialogFragment();
            dialogsList.add(dialog);


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
            showSocialAddDialog(name);
        }
        if (socialAdderArrayList.isEmpty()) {

//            handleScan = true;
            //todo?
        }

    }

    public class ConnectivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (isConnected) {
                connectedInternet = true;
            } else {
                connectedInternet = false;
            }
        }
    }
}
