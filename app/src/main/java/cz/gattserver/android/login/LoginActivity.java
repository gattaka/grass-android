package cz.gattserver.android.login;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.OnSuccessAction;
import cz.gattserver.android.common.URLPostTask;
import cz.gattserver.android.common.URLTaskInfoBundle;

public class LoginActivity extends GrassActivity {

    public static final String PREFS_NAME = "GRASS_MOBILE_LOGIN";
    public static final String VAR_NAME = "sessionid";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String login = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginButton.setEnabled(!login.isEmpty() && !password.isEmpty());
            }
        };

        usernameEditText.addTextChangedListener(tw);
        passwordEditText.addTextChangedListener(tw);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                URLPostTask<LoginActivity> uploadTask = new URLPostTask<>(LoginActivity.this,
                        new OnSuccessAction<LoginActivity>() {
                            @Override
                            public void run(LoginActivity urlTaskClient, URLTaskInfoBundle result) {
                                if (result.isSuccess()) {
                                    if (result.getResponseCode() == 200) {
                                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString(VAR_NAME, result.getResultAsStringUTF());
                                        editor.commit();

                                        Toast.makeText(urlTaskClient, "Přihlášení proběhlo úspěšně", Toast.LENGTH_SHORT).show();
                                        setResult(1);
                                        finish();
                                    } else if (result.getResponseCode() == 401) {
                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle("Chyba")
                                                .setMessage("Přihlášení se nezdařilo -- špatné jméno nebo heslo")
                                                .setIcon(android.R.drawable.ic_dialog_alert).show();
                                    } else {
                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle("Chyba")
                                                .setMessage("Přihlášení se nezdařilo -- server code: " + result.getResponseCode())
                                                .setIcon(android.R.drawable.ic_dialog_alert).show();
                                    }
                                } else {
                                    new AlertDialog.Builder(LoginActivity.this)
                                            .setTitle("Chyba")
                                            .setMessage("Přihlášení se nezdařilo: " + result.getError().getLocalizedMessage())
                                            .setIcon(android.R.drawable.ic_dialog_alert).show();
                                }
                            }
                        });
                uploadTask.execute(Config.LOGIN, "login", login, "password", password);
            }
        });
    }

}
