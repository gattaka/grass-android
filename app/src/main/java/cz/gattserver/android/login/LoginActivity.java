package cz.gattserver.android.login;

import android.app.AlertDialog;
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
import cz.gattserver.android.common.LoginUtils;
import cz.gattserver.android.common.OnSuccessAction;
import cz.gattserver.android.common.URLPostTask;
import cz.gattserver.android.common.URLTaskInfoBundle;
import cz.gattserver.android.common.URLTaskParamTO;

public class LoginActivity extends GrassActivity {

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
                                        LoginUtils.saveSessionId(LoginActivity.this, result.getResultAsStringUTF());
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
                uploadTask.execute(new URLTaskParamTO(Config.LOGIN, null).setParams("login", login, "password", password));
            }
        });
    }

}
