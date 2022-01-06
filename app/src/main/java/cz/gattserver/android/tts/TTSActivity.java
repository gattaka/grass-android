package cz.gattserver.android.tts;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Locale;

import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;

public class TTSActivity extends GrassActivity {

    String text;
    EditText et;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);

        et = (EditText) findViewById(R.id.editText1);
        tts = new TextToSpeech(TTSActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(new Locale("cs"));
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "This Language is not supported");
                    } else {
                        convertTextToSpeech();
                    }
                } else
                    Log.e("error", "Initilization Failed!");
            }
        });

        Button btn = new Button(TTSActivity.this);
        btn.setText("Read");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertTextToSpeech();
            }
        });
        final LinearLayout loginLayout = findViewById(R.id.ttsLayout);
        loginLayout.addView(btn);
    }

    private void convertTextToSpeech() {
        if (et == null)
            return;
        text = et.getText().toString();
        if (text == null || "".equals(text)) {
            text = "Prázdný text";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
