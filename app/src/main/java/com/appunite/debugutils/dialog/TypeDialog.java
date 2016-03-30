package com.appunite.debugutils.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.appunite.debugutils.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class TypeDialog extends Activity {

    private static final String ARGS_ID = "args_id";
    private static final String TYPE_VALUE = "type_value";
    private String typeString;

    @InjectView(R.id.type_radio_group)
    RadioGroup radioGroup;

    public static Intent newIntent(Context context, String id) {
        Intent intent = new Intent(context, TypeDialog.class);
        intent.putExtra(ARGS_ID, id);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        typeString = getIntent().getStringExtra(ARGS_ID);
        setContentView(R.layout.type_dialog_layout);
        ButterKnife.inject(this);

        final Intent returnIntent = new Intent();

        radioGroup.clearCheck();

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.getText().toString().equals(typeString)) {
                radioButton.setChecked(true);
            }
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = (RadioButton) findViewById(checkedId);
                returnIntent.putExtra(TYPE_VALUE, button.getText().toString());
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

    }
}