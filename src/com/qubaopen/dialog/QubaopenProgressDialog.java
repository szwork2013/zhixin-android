package com.qubaopen.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.qubaopen.R;


/**
 * Created by duel on 14-3-20.
 */
public class QubaopenProgressDialog extends ProgressDialog {

    private ImageView progress_loading;
    AnimationDrawable progressAnimation;


    public QubaopenProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_progress_loading);
        progress_loading=(ImageView) this.findViewById(R.id.progress_loading);
        progressAnimation=(AnimationDrawable) progress_loading.getBackground();

        progressAnimation.start();

    }
}
