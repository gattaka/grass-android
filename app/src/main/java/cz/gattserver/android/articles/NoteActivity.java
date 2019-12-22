package cz.gattserver.android.articles;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.OnSuccessAction;
import cz.gattserver.android.common.URLGetTask;
import cz.gattserver.android.common.URLTaskInfoBundle;
import cz.gattserver.android.common.URLTaskParamTO;

public class NoteActivity extends GrassActivity {

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle("Nová poznámka");

        id = String.valueOf(new Date().getTime());
    }

    private void save() {
        EditText noteTextArea = findViewById(R.id.noteText);
        File directory = getDir(Config.NOTES_DIR, MODE_PRIVATE);
        File noteFile = new File(directory, id);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(noteFile);
            fos.write(noteTextArea.getText().toString().getBytes("UTF-8"));
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        save();
        super.onPause();
    }
}
