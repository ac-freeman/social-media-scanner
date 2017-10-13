package com.acfreeman.socialmediascanner;

import android.widget.ImageView;
import android.widget.Switch;

import com.acfreeman.socialmediascanner.db.Emails;
import com.acfreeman.socialmediascanner.db.Phones;
import com.acfreeman.socialmediascanner.db.Social;

import java.util.ArrayList;

/**
 * Created by Andrew on 10/13/2017.
 */

public class SwitchModel {

    Switch sw;
    ImageView switchImage;

    public SwitchModel(Switch sw, ImageView switchImage) {
        this.sw = sw;
        this.switchImage = switchImage;
    }

    public Switch getSwitch() {
        return sw;
    }

    public ImageView getSwitchImage(){
        return switchImage;
    }

}
