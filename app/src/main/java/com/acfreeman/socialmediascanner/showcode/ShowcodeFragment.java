package com.acfreeman.socialmediascanner.showcode;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static com.facebook.FacebookSdk.getApplicationContext;

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
        int width = display.getWidth() * 3 / 4;
        qrContainer.getLayoutParams().width = width;

        switchModels = new ArrayList<>();
        codeListView = view.findViewById(R.id.switch_list);

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
                generateCode( switchModels);
            }
        });

        generateCode( switchModels);


        return view;
    }

    public void generateCode(ArrayList<SwitchModel> switchSet) {

        try {
            int width = qrContainer.getLayoutParams().width;
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            StringBuilder builder = new StringBuilder();
            builder.append("|");

            // personal information
            LocalDatabase database = new LocalDatabase(getApplicationContext());
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

            qrImage.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
