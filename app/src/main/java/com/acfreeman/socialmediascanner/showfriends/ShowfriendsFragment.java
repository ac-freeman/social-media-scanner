package com.acfreeman.socialmediascanner.showfriends;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acfreeman.socialmediascanner.CustomDialogFragment;
import com.acfreeman.socialmediascanner.MainActivity;
import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.Contact;
import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Phone;
import com.acfreeman.socialmediascanner.db.Social;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.acfreeman.socialmediascanner.MainActivity.MY_PERMISSIONS_REQUEST_CONTACTS;
import static com.acfreeman.socialmediascanner.MainActivity.firstMainActivityPref;

/**
 * Created by Andrew on 10/23/2017.
 */

public class ShowfriendsFragment extends Fragment implements CustomDialogFragment.NoticeDialogListener{
    private ContactsAdapter adapter;
    ArrayList<DataModel> dataModels;
    ListView listView;
    private ContactCardAdapter cardAdapter;
    ArrayList<CardDataModel> cardDataModels;
    ListView cardListView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_showfriends,
                container, false);

        listView = view.findViewById(R.id.contact_list);

        dataModels = new ArrayList<>();


        LocalDatabase db = new LocalDatabase(getActivity());
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

        adapter = new ContactsAdapter(dataModels, getActivity());

        listView.setAdapter(adapter);
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.toggleAppbarButtons();

                Log.i("CONTACTDEBUG", "Long click");
                adapter.toggleEditMode();
                adapter.checks.set(i, 1);

                getActivity().runOnUiThread(new Runnable() {
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
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                final DataModel dataModel = dataModels.get(position);

                if (adapter.inEditmode) {
                    if (adapter.checks.get(position) == 1)
                        adapter.checks.set(position, 0);
                    else
                        adapter.checks.set(position, 1);

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    final RelativeLayout layout = view.findViewById(R.id.activity_contact_card);
                    final TextView cardName = view.findViewById(R.id.activity_contact_card_name);
                    cardName.setText(dataModel.getName());


                    cardListView = view.findViewById(R.id.activity_contact_card_listview);
                    cardListView.setClickable(false);
                    cardDataModels = new ArrayList<>();

                    for (Phone p : dataModel.getPhones()) {
                        cardDataModels.add(new CardDataModel(p));
                    }
                    for (Email e : dataModel.getEmails()) {
                        cardDataModels.add(new CardDataModel(e));
                    }
                    for (Social s : dataModel.getSocials()) {
                        cardDataModels.add(new CardDataModel(s));
                    }

                    cardAdapter = new ContactCardAdapter(cardDataModels, getActivity());
                    cardListView.setAdapter(cardAdapter);

                    layout.setVisibility(View.VISIBLE);
                    final int height = getResources().getDisplayMetrics().heightPixels;

                    final ImageView darkener = (ImageView) view.findViewById(R.id.darken_frame);
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


                    //drag listener??


                    cardListView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent motionEvent) {
                            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                startY = layout.getY();
                                Log.i("MOVEDEBUG", "STARTY " + startY);
                                float motionY = motionEvent.getRawY();
                                margin = motionY - startY;
                            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                                float motionY = motionEvent.getRawY();

                                float newY = motionY - margin;
                                if (margin != 0) {
                                    Log.i("MOVEDEBUG", "Moving to " + newY);
                                    layout.setVisibility(View.INVISIBLE);
                                    if (newY < 0)
                                        newY = 0;
                                    layout.setY(newY);
                                    layout.setVisibility(View.VISIBLE);
                                }
                            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                Log.i("MOVEDEBUG", "ACTION UP " + layout.getY());
                                if (layout.getY() > startY) {
                                    //lower
                                    lowerCard(layout, height, darkener);
                                } else if (layout.getY() < startY) {
                                    //raise
                                    raiseCard(layout);
                                } else {
                                    //TODO: Do list item actions
                                    Log.i("CARDDEBUG", "Card item clicked!");
//                                    cardAdapter.onClick(cardListView.);
//                                    view.setVisibility(View.INVISIBLE);
                                }
                            }
                            return false;
                        }
                    });

                    cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View v, int position, long l) {
                            Log.i("CARDDEBUG", "ITEM clicked!");
                            final CardDataModel cardDataModel = cardDataModels.get(position);
                            switch (cardDataModel.getTag()) {
                                case 'p':
                                    phoneNum_forCall = Uri.parse("tel:" + cardDataModel.getPhone().getNumber());
//                                    MainActivity.callPhone(phoneNum_forCall);
                                    //TODO
                                    break;
                                case 'e':
                                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                            "mailto", cardDataModel.getEmail().getEmail(), null));
                                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                                    break;
                                case 's':
//                                    showNoticeDialog((String) cardName.getText(), cardDataModel.getSocial());
                                    //TODO
                                    break;
                            }

                        }
                    });

                    layout.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent motionEvent) {
                            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                startY = layout.getY();
                                Log.i("MOVEDEBUG", "STARTY " + startY);
                                float motionY = motionEvent.getRawY();
                                margin = motionY - startY;
                            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                                float motionY = motionEvent.getRawY();

                                float newY = motionY - margin;
                                Log.i("MOVEDEBUG", "Moving to " + newY);
                                layout.setVisibility(View.INVISIBLE);
                                if (newY < 0)
                                    newY = 0;
                                layout.setY(newY);
                                layout.setVisibility(View.VISIBLE);
                            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                Log.i("MOVEDEBUG", "ACTION UP " + layout.getY());
                                if (layout.getY() > startY) {
                                    //lower
                                    lowerCard(layout, height, darkener);
                                } else {
                                    //raise
                                    raiseCard(layout);
                                }
                            }
                            return false;
                        }
                    });

                }

                Log.i("CONTACTDEBUG", "Item clicked!");
            }
        });

        return view;
    }

    float margin;
    float startY;
    Uri phoneNum_forCall;



    public void lowerCard(RelativeLayout layout,int height, final ImageView darkener){
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
    }
    public void raiseCard(RelativeLayout layout){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout, "TranslationY", layout.getY(), 0);
//                                    objectAnimator.setDuration(400);
        objectAnimator.start();
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



    public void showNoticeDialog(String name, Social social) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new CustomDialogFragment();

        String type = social.getType();
        String uri ="";
        switch (type) {
            case "Twitter":
                uri = "https://twitter.com/intent/follow?user_id=" + social.getUsername();
                break;
            case "LinkedIn":
                uri = "https://www.linkedin.com/profile/view?id=" + social.getUsername();
                break;
            case "Spotify":
                uri = "spotify:user:" + social.getUsername();
                break;
            case "Facebook":
                try {
                    getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
                    uri = "fb://facewebmodal/f?href=" + "https://www.facebook.com/" + social.getUsername(); //Tries with FB's URI
                } catch (Exception e) {
                    uri = "https://www.facebook.com/" + social.getUsername(); //catches a url to the desired page
                }
                break;
        }


        Bundle args = new Bundle();
        args.putString("dialog_title", "Would you like to add " + name + " on " + type + "?");
        args.putString("name", name);
        args.putString("uri", uri);
        args.putString("action", "singleSocialAdd");

        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "CustomDialogFragment");

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
            case "singleSocialAdd":
                uri = mArgs.getString("uri");
                Log.i("SOCIALDEBUG",uri);
                MainActivity.socialAdd(uri);
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
                        LocalDatabase db = new LocalDatabase(getActivity());
                        db.deleteContactById(contactId);

                        adapter.inEditmode = false;
                        adapter.notifyDataSetChanged();
                        i--;
                    }

                }
                MainActivity.hideAppbarButtons();
                break;
            case "saveContact":
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_CONTACTS},
                            MY_PERMISSIONS_REQUEST_CONTACTS);
                } else {
                    saveContactsToDevice();
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
            case "singleSocialAdd":
                break;
            case "delete":
                break;
            case "saveContact":
                break;
        }

    }

}
