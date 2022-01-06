package cz.gattserver.android.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.LoginUtils;
import cz.gattserver.android.common.OnSuccessAction;
import cz.gattserver.android.common.URLPostTask;
import cz.gattserver.android.common.URLTaskInfoBundle;
import cz.gattserver.android.common.URLTaskParamTO;
import cz.gattserver.android.interfaces.NoteTO;

public class NotesActivity extends GrassActivity {

    private Map<String, NoteTO> choosenNotes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        setTitle("Poznámky");

        constructTable();
    }

    @Override
    protected void onResume() {
        constructTable();
        super.onResume();
    }

    private void constructTable() {
        TableLayout tableLayout = findViewById(R.id.noteTableLayout);
        tableLayout.removeAllViews();

        Button newNoteButton = new Button(this);
        newNoteButton.setText("Nová poznámka");
        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotesActivity.this, NoteActivity.class);
                startActivity(intent);
            }
        });
        tableLayout.addView(newNoteButton);

        final File directory = getDir(Config.NOTES_DIR, MODE_PRIVATE);
        File[] files = directory.listFiles();

        if (files.length == 0) {
            TableRow row = new TableRow(this);
            tableLayout.addView(row);

            TextView bodyTextView = new TextView(this);
            bodyTextView.setText("Nebyly nalezeny žádné poznámky");
            bodyTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT));
            bodyTextView.setGravity(Gravity.CENTER);
            row.addView(bodyTextView);
        } else {
            List<NoteTO> notes = new ArrayList<>();
            for (int i = files.length - 1; i >= 0; i--) {
                File noteFile = files[i];
                String id = noteFile.getName();
                String text = null;
                Date date = new Date(Long.valueOf(noteFile.getName()));
                try {
                    BufferedReader br = new BufferedReader(new FileReader(noteFile));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                        sb.append('\n');
                    }
                    br.close();
                    text = sb.toString();
                } catch (Exception e) {
                    text = "-Došlo k chybě během čtení záznamu-";
                }
                notes.add(new NoteTO(id, text, date));
            }

            final Button sendBtn = new Button(this);
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(NotesActivity.this)
                            .setTitle("Odeslání poznámek")
                            .setMessage("Opravdu odeslat poznámky?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    URLPostTask<NotesActivity> uploadTask = new URLPostTask<>(NotesActivity.this,
                                            new OnSuccessAction<NotesActivity>() {
                                                @Override
                                                public void run(NotesActivity urlTaskClient, URLTaskInfoBundle result) {
                                                    if (result.isSuccess()) {
                                                        if (result.getResponseCode() == 200) {
                                                            Toast.makeText(urlTaskClient, "Poznámky byly úspěšně nahrány", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            new AlertDialog.Builder(NotesActivity.this)
                                                                    .setTitle("Chyba")
                                                                    .setMessage("Poznámky se nezdařilo odeslat -- server code: " + result.getResponseCode())
                                                                    .setIcon(android.R.drawable.ic_dialog_alert).show();
                                                        }
                                                    } else {
                                                        new AlertDialog.Builder(NotesActivity.this)
                                                                .setTitle("Chyba")
                                                                .setMessage("Poznámky se nezdařilo odeslat: " + result.getError().getLocalizedMessage())
                                                                .setIcon(android.R.drawable.ic_dialog_alert).show();
                                                    }
                                                }
                                            });
                                    StringBuilder sb = new StringBuilder();
                                    SimpleDateFormat sdf = new SimpleDateFormat("d.M.yyyy HH:mm");
                                    for (NoteTO noteTO : choosenNotes.values()) {
                                        sb.append(sdf.format(noteTO.getDate()));
                                        sb.append("\n");
                                        sb.append(noteTO.getText());
                                        sb.append("\n\n");
                                    }
                                    uploadTask.execute(new URLTaskParamTO(Config.ARTICLES_CREATE, LoginUtils.getSessionid(NotesActivity.this)).setParams("text", sb.toString()));
                                    constructTable();
                                }
                            }).setNegativeButton(android.R.string.no, null).show();
                }
            });
            final String btnCaptionPrefix = "Odeslat na server";
            sendBtn.setGravity(Gravity.CENTER);
            sendBtn.setEnabled(false);
            tableLayout.addView(sendBtn);

            for (final NoteTO noteTO : notes) {
                TableRow row = new TableRow(this);
                tableLayout.addView(row);
                row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

                CheckBox checkBox = new CheckBox(this);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            choosenNotes.put(noteTO.getId(), noteTO);
                        } else {
                            choosenNotes.remove(noteTO.getId());
                        }
                        if (choosenNotes.isEmpty()) {
                            sendBtn.setText(btnCaptionPrefix);
                            sendBtn.setEnabled(false);
                        } else {
                            sendBtn.setText(btnCaptionPrefix + " (" + choosenNotes.size() + ")");
                            sendBtn.setEnabled(true);
                        }
                    }
                });
                checkBox.setChecked(true);
                row.addView(checkBox);
                checkBox.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT ));

                TextView bodyTextView = new TextView(this);
                bodyTextView.setText(noteTO.getPreview());
                row.addView(bodyTextView);
                bodyTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT));

                Button editBtn = new Button(this);
                editBtn.setText("Upravit");
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NotesActivity.this, NoteActivity.class);
                        intent.putExtra("id", noteTO.getId());
                        startActivity(intent);
                    }
                });
                row.addView(editBtn);
                editBtn.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                Button deleteBtn = new Button(this);
                deleteBtn.setText("Smazat");
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(NotesActivity.this)
                                .setTitle("Smazání poznámky")
                                .setMessage("Opravdu smazat poznámku?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        File toDeleteFile = new File(directory, noteTO.getId());
                                        toDeleteFile.delete();
                                        constructTable();
                                    }
                                }).setNegativeButton(android.R.string.no, null).show();
                    }
                });
                row.addView(deleteBtn);
                deleteBtn.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            }
        }

    }


}
