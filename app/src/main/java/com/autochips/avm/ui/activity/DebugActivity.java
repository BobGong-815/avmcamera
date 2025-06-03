package com.autochips.avm.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.autochips.avm.R;
import com.autochips.avm.ai.SignalQuery;

public class DebugActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_layout);

        findViewById(R.id.btn_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btn_query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.et_content);
                String content = editText.getText().toString();
                Log.d("AVM_DEBUG", "query text is " + content);
                if (content.length() > 2) {
                    SignalQuery signalQuery = new SignalQuery();
                    content = signalQuery.work(content);
                    TextView textView = findViewById(R.id.tv_display_content);
                    if (TextUtils.isEmpty(content)) {
                        textView.setText("Null");
                    } else {
                        textView.setText(content);
                    }
                }
            }
        });
    }

}
