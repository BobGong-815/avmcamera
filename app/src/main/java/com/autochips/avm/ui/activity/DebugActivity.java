package com.autochips.avm.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.autochips.avm.R;
import com.autochips.avm.ai.SignalQuery;
import com.bim.sdk.BimCallback;
import com.bim.sdk.CmdSet;

import org.w3c.dom.Text;

public class DebugActivity extends Activity {

    private TextView textView;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_layout);

        handler = new Handler(Looper.getMainLooper());
        textView = findViewById(R.id.tv_display_content);

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
