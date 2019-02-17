package cz.gattserver.android.messages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.URLPostTask;
import cz.gattserver.android.common.URLTaskInfoBundle;
import cz.gattserver.android.interfaces.MessageTO;

public class MessagesActivity extends GrassActivity {

    private Map<String, MessageTO> choosenMessages = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        setTitle("Zprávy");

        constructTable();
    }

    private void constructTable() {
        TableLayout tableLayout = findViewById(R.id.messageTableLayout);
        tableLayout.removeAllViews();

        final List<MessageTO> messages = new ArrayList<>();
        for (String directory : new String[]{"content://sms/inbox", "content://sms/sent", "content://sms/draft"}) {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(Uri.parse(directory), null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String id = cursor.getString(cursor.getColumnIndex("_id"));
                        String body = cursor.getString(cursor.getColumnIndex("body"));
                        String date = cursor.getString(cursor.getColumnIndex("date"));
                        String number = cursor.getString(cursor.getColumnIndex("service_center"));
                        messages.add(new MessageTO(id, body, date, number));
                    } while (cursor.moveToNext());
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }

        if (messages.isEmpty()) {

            TableRow row = new TableRow(this);
            tableLayout.addView(row);

            TextView bodyTextView = new TextView(this);
            bodyTextView.setText("Nebyly nalezeny žádné zprávy");
            bodyTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT));
            bodyTextView.setGravity(Gravity.CENTER);
            row.addView(bodyTextView);

        } else {

            final Button sendBtn = new Button(this);
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(MessagesActivity.this)
                            .setTitle("Odeslání zpráv")
                            .setMessage("Opravdu odeslat zprávy?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    URLPostTask<MessagesActivity> uploadTask = new URLPostTask<>(MessagesActivity.this,
                                            new URLPostTask.OnSuccessAction<MessagesActivity>() {
                                                @Override
                                                public void run(MessagesActivity urlTaskClient, URLTaskInfoBundle result) {
                                                    if (result.isSuccess()) {
                                                        if (result.getResponseCode() == 200) {
                                                            Toast.makeText(urlTaskClient, "SMS byly úspěšně nahrány", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            new AlertDialog.Builder(MessagesActivity.this)
                                                                    .setTitle("Chyba")
                                                                    .setMessage("SMS se nezdařilo odeslat -- server code: " + result.getResponseCode())
                                                                    .setIcon(android.R.drawable.ic_dialog_alert).show();
                                                        }
                                                    } else {
                                                        new AlertDialog.Builder(MessagesActivity.this)
                                                                .setTitle("Chyba")
                                                                .setMessage("SMS se nezdařilo odeslat: " + result.getError().getLocalizedMessage())
                                                                .setIcon(android.R.drawable.ic_dialog_alert).show();
                                                    }
                                                }
                                            });
                                    StringBuilder sb = new StringBuilder();
                                    SimpleDateFormat sdf = new SimpleDateFormat("d.M.yyyy HH:mm");
                                    for (MessageTO m : choosenMessages.values()) {
                                        sb.append(sdf.format(new Date(Long.valueOf(m.getDate()))));
                                        sb.append("\n");
                                        sb.append(m.getBody());
                                        sb.append("\n\n");
                                    }
                                    uploadTask.execute(Config.ARTICLES_CREATE, "text=" + sb.toString());
                                    constructTable();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });
            final String btnCaptionPrefix = "Odeslat na server";
            sendBtn.setGravity(Gravity.CENTER);
            sendBtn.setEnabled(false);
            tableLayout.addView(sendBtn);

            for (final MessageTO m : messages) {
                TableRow row = new TableRow(this);
                tableLayout.addView(row);

                CheckBox checkBox = new CheckBox(this);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            choosenMessages.put(m.getId(), m);
                        } else {
                            choosenMessages.remove(m.getId());
                        }
                        if (choosenMessages.isEmpty()) {
                            sendBtn.setText(btnCaptionPrefix);
                            sendBtn.setEnabled(false);
                        } else {
                            sendBtn.setText(btnCaptionPrefix + " (" + choosenMessages.size() + ")");
                            sendBtn.setEnabled(true);
                        }
                    }
                });
                checkBox.setChecked(true);
                row.addView(checkBox);

                TextView bodyTextView = new TextView(this);
                bodyTextView.setText(m.getBody());
                row.addView(bodyTextView);
            }
        }

    }


}
