package com.example.qq.utilities;

import android.content.Context;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

public class SetUserDetail {
    public static PreferenceManager pref;

    static public void SetUserAvatarAndName(Context context, TextView name, RoundedImageView avatar){
        pref = new PreferenceManager(context);
        String imageString = pref.getString(Constants.KEY_IMAGE);
        avatar.setImageBitmap(DecodeImage.decoder(imageString));
        name.setText(pref.getString(Constants.KEY_NAME));
    }
}
