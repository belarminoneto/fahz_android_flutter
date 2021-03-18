package br.com.avanade.fahz.model;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;

import java.util.Observable;

public class ImageObservable extends Observable {
    private RoundedBitmapDrawable mProfileImageDrawable;
    private static ImageObservable INSTANCE = null;


    // Returns a single instance of this class, creating it if necessary.
    public static ImageObservable getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ImageObservable();
        }
        return INSTANCE;
    }

    public void setProfile(RoundedBitmapDrawable bitmapDrawable) {
        mProfileImageDrawable = bitmapDrawable;
        setChanged();
        notifyObservers();
    }

    public RoundedBitmapDrawable GetProfileImageDrawable() {
        return mProfileImageDrawable;
    }
}
