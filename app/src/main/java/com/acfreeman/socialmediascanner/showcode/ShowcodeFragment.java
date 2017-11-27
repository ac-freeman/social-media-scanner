package com.acfreeman.socialmediascanner.showcode;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Phone;
import com.acfreeman.socialmediascanner.db.Social;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 10/23/2017.
 */

public class ShowcodeFragment extends Fragment {

    ArrayList<SwitchModel> switchModels = new ArrayList<>();
    private static ShowcodeAdapter showcodeAdapter;
    private static ImageView mImageView;
    ImageView qrImage;
    RelativeLayout qrContainer;

    ListView codeListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_showcode,
                container, false);

        qrContainer = view.findViewById(R.id.qr_container);
        qrImage = view.findViewById(R.id.qr_image);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth() * 3 / 5;
        qrContainer.getLayoutParams().width = width;

        switchModels = new ArrayList<>();
        codeListView = view.findViewById(R.id.switch_list);


        LocalDatabase db = new LocalDatabase(getActivity());
        List<Owner> owner = db.getAllOwner();

        for (Phone p : db.getUserPhones(owner.get(0).getId())) {
            switchModels.add(new SwitchModel(p.getType() + ": " + p.getNumber(), "ph", R.drawable.ic_phone_black_24dp, p));
        }
        for (Email e : db.getUserEmails(owner.get(0).getId())) {
            switchModels.add(new SwitchModel(e.getType() + ": " + e.getEmail(), "em", R.drawable.ic_email_black_24dp, e));
        }


        List socials = new ArrayList();

        ArrayList<Social> sociallist = db.getUserSocials(owner.get(0).getId());
        for (Social s : sociallist) {
            switchModels.add(new SwitchModel(s.getType(), s.getUsername()));
        }

        showcodeAdapter = new ShowcodeAdapter(switchModels, getActivity());
        codeListView.setAdapter(showcodeAdapter);


        codeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SwitchModel switchModel = switchModels.get(position);
                Log.i("SWITCHDEBUG", "Something clicked");
                switchModel.getSwitcher().toggle();
                switchModel.toggleState();
                Log.i("SWITCHDEBUG", "Switch toggled to " + switchModel.getState());
                generateCode(switchModels);
            }
        });

        generateCode(switchModels);


        return view;
    }

    public void generateCode(ArrayList<SwitchModel> switchSet) {

        try {
            int width = qrContainer.getLayoutParams().width;
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            StringBuilder builder = new StringBuilder();
            builder.append("|");

            // personal information
            LocalDatabase database = new LocalDatabase(getActivity());
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
                            builder.append("ph" + "|" + sw.getPhone().getNumber() + "|" + sw.getPhone().getType() + "|");
                            break;
                        case "em":
                            builder.append("em" + "|" + sw.getEmail().getEmail() + "|" + sw.getEmail().getType() + "|");
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

            qrImage.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    public boolean allowRefresh = false;

    @Override
    public void onResume() {
        super.onResume();
        if (allowRefresh) {
            allowRefresh = false;
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("max_brightness", true)) {
            WindowManager.LayoutParams layout = getActivity().getWindow().getAttributes();
            layout.screenBrightness = 1F;
            getActivity().getWindow().setAttributes(layout);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("BRIGHTDEBUG", "On pause");
        WindowManager.LayoutParams layout = getActivity().getWindow().getAttributes();
        layout.screenBrightness = android.provider.Settings.System.getInt(getActivity().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, -1);
        getActivity().getWindow().setAttributes(layout);
    }
}
