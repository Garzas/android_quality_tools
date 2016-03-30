package com.appunite.debugutils.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.appunite.debugutils.BaseActivity;
import com.appunite.debugutils.R;

import rx.Observable;


public class TypeDialog extends BaseActivity {

    private static final String ARGS_ID = "args_id";
    private String typeId;

    public static Intent newIntent(Context context, String id) {
        Intent intent = new Intent(context, TypeDialog.class);
        intent.putExtra(ARGS_ID, id);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeId = getIntent().getStringExtra(ARGS_ID);
        setContentView(R.layout.type_dialog_layout);

        Observable.just(typeId)
                .compose(this.<String>bindToLifecycle())
                .subscribe()


    }

}