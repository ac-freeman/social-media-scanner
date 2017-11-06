package com.acfreeman.socialmediascanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.acfreeman.socialmediascanner.db.Contact;
import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Phone;
import com.acfreeman.socialmediascanner.db.Social;
import com.acfreeman.socialmediascanner.showcode.ShowcodeAdapter;
import com.acfreeman.socialmediascanner.showcode.SwitchModel;
import com.acfreeman.socialmediascanner.showfriends.ContactsAdapter;
import com.acfreeman.socialmediascanner.showfriends.DataModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Andrew on 11/6/2017.
 */

public class ConnectedAccounts extends AppCompatActivity{

//    public ContactsAdapter adapter;
public ConnectedAccountsAdapter adapter;


    ArrayList<SwitchModel> switchModels = new ArrayList<>();
    private static ShowcodeAdapter showcodeAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_accounts);


        listView = findViewById(R.id.contact_list);





        switchModels = new ArrayList<>();


        List socials = new ArrayList();
        LocalDatabase db = new LocalDatabase(this);
        List<Owner> owner = db.getAllOwner();
        ArrayList<Social> sociallist = db.getUserSocials(owner.get(0).getId());

        ArrayList<String> socialPossibleList = new ArrayList<>(Arrays.asList("tw","li","sp","fb","go"));

        for (Social s : sociallist) {
            switchModels.add(new SwitchModel(s.getType(), s.getUsername()));
            if(socialPossibleList.contains(s.getType())){
                socialPossibleList.remove(s.getType());
            }
        }
        for (int i = 0; i < socialPossibleList.size(); i++) {
            switchModels.add(new SwitchModel(socialPossibleList.get(i)));
        }

        Log.i("SOCIALDEBUG","Unconnected: " + socialPossibleList.size());

//        showcodeAdapter = new ShowcodeAdapter(switchModels, this);
//        listView.setAdapter(showcodeAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SwitchModel switchModel = switchModels.get(position);
                Log.i("SWITCHDEBUG", "Something clicked");
//                switchModel.getSwitcher().toggle();
//                switchModel.toggleState();
//                Log.i("SWITCHDEBUG", "Switch toggled to " + switchModel.getState());
            }
        });




//        adapter = new ContactsAdapter(dataModels, this);
        adapter = new ConnectedAccountsAdapter(switchModels,this);

        listView.setAdapter(adapter);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.onActivityResult(requestCode, resultCode, data);
    }
}
