package dron.mkapiczynski.pl.dronvision.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

import dron.mkapiczynski.pl.dronvision.R;

/**
 * Widok rejestracji
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText emailText, nameText, surnameText, loginText, passwordText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailText = (EditText) findViewById(R.id.emailText);
        nameText = (EditText) findViewById(R.id.nameText);
        surnameText = (EditText) findViewById(R.id.surnameText);
        loginText = (EditText) findViewById(R.id.loginText);
        passwordText = (EditText) findViewById(R.id.passwordText);

        registerButton = (Button) findViewById(R.id.buttonRegister);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailText.getText() == null || emailText.getText().toString().length() < 1 ||
                        nameText.getText() == null || nameText.getText().toString().length() < 1 ||
                        surnameText.getText() == null || surnameText.getText().toString().length() < 1 ||
                        loginText.getText() == null || loginText.getText().toString().length() < 1 ||
                        passwordText.getText() == null || passwordText.getText().toString().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Wypełnij wszystkie pola!", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Konto zostało utworzone i czeka na zatwierdzenie przez administratora", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void register(){

    }
}
