package com.acfreeman.socialmediascanner;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.Phone;

import java.util.ArrayList;

/**
 * Created by Andrew on 11/26/2017.
 */

public class RegistrationAdapter extends BaseAdapter {
    Context context;
    int[] data;
    ArrayList<Phone> phones;
    ArrayList<Email> emails;
    private static LayoutInflater inflater = null;
    public RegistrationAdapter(Context context, int[] data, ArrayList<Phone> phones, ArrayList<Email> emails){
        this.context = context;
        this.data = data;
        this.phones = phones;
        this.emails = emails;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }



    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.row_item_registration, null);

        EditText editText = (EditText) view.findViewById(R.id.edit_text);
        ImageButton button = (ImageButton) view.findViewById(R.id.row_button);
        ArrayAdapter<String> spinnerArrayAdapter;
        String[] spinnerList;
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);


        RelativeLayout rl = view.findViewById(R.id.registration_row_relative_layout);


//        Button submit = view.findViewById(R.id.submit_button);
//
//        switch (data[position]){
//            case 0:
//                Log.i("REGISTRATION","Case 0");
//                rl.setVisibility(View.VISIBLE);
//                submit.setVisibility(View.INVISIBLE);
//                editText.setHint("Name");
//                spinner.setVisibility(View.INVISIBLE);
//                button.setVisibility(View.INVISIBLE);
//                editText.setText("");
//                break;
//            case 1:
//                rl.setVisibility(View.VISIBLE);
//                submit.setVisibility(View.INVISIBLE);
//                editText.setText("");
//
//                editText.setHint("Phone number");
//
//                spinnerList = new String[]{"Cell","Work","Home","Other"};
//                spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerList);
//                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
//                spinner.setAdapter(spinnerArrayAdapter);
//                spinner.setVisibility(View.VISIBLE);
//                button.setVisibility(View.VISIBLE);
//                if(data[position+1] == 2) {
//                    button.setImageResource(R.drawable.ic_add_circle_green_24px);
//
//                } else {
//                    button.setImageResource(R.drawable.ic_remove_circle_red_24dp);
//                    if(phones.size() >0){
//                        editText.setText((int) phones.remove(0).getNumber());
//                    }
//                }
//                break;
//            case 2:
//                rl.setVisibility(View.VISIBLE);
//                submit.setVisibility(View.INVISIBLE);
//                editText.setText("");
//                editText.setHint("E-mail address");
//
//                spinnerList = new String[]{"Personal","Work","School","Other"};
//                spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerList);
//                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
//                spinner.setAdapter(spinnerArrayAdapter);
//                spinner.setVisibility(View.VISIBLE);
//                button.setVisibility(View.VISIBLE);
//                if(position+1 <data.length && data[position+1] == 3) {
//                    button.setImageResource(R.drawable.ic_add_circle_green_24px);
//
//                } else {
//                    button.setImageResource(R.drawable.ic_remove_circle_red_24dp);
//                    if(emails.size() >0){
//                        Log.i("REGISTRATION","Emails > 0");
//                        editText.setText( emails.remove(0).getEmail());
//                    }
//                }
//                break;
//            case 3:
//                Log.i("REGISTRATION","Case 3, position "+position);
//                rl.setVisibility(View.INVISIBLE);
//                submit.setVisibility(View.VISIBLE);
//                editText.setText("");
//                break;
//
//        }
//
        return view;
    }
}
