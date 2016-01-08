package dron.mkapiczynski.pl.gpsvisualiser.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import dron.mkapiczynski.pl.gpsvisualiser.R;
import dron.mkapiczynski.pl.gpsvisualiser.domain.User;
import dron.mkapiczynski.pl.gpsvisualiser.helper.SessionManager;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button logginButton;
    private EditText inputUsername, inputPassword;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUsername = (EditText) findViewById(R.id.editTextUsername);
        inputPassword = (EditText) findViewById(R.id.editTextPassword);
        logginButton = (Button) findViewById(R.id.loginButton);

        session = new SessionManager(getApplicationContext());
        session.clear();

        //Sprawdzamy, czy użytkownik jest już zalogowany
        if (session.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, VisualizeActivity.class);
            startActivity(intent);
            finish();
        }

        logginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();
                User userToLogin = new User(username, password);
                if (!username.isEmpty() && !password.isEmpty()) {
                    if(checkLoginData(userToLogin)){
                        logUser(userToLogin);
                    } else{
                        Toast.makeText(getApplicationContext(),
                                "Błąd podczas logowania", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Nie wprowadzono nazwy użytkownika lub hasła!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean checkLoginData(User userToLogIn){
        String mockUsername = "Mix";
        String mockPassword = "pass";
        if(mockUsername.equals(userToLogIn.getUserName()) && mockPassword.equals(userToLogIn.getPassword())){
            return true;
        } else{
            return false;
        }
    }

    private void logUser(User userToLogIn){
        session.setLogin(true);
        session.setLoggedUser(userToLogIn);
        Intent intent = new Intent(LoginActivity.this,
                VisualizeActivity.class);
        intent.putExtra("prevActivity", "Login");
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Zostałeś zalogowany", Toast.LENGTH_SHORT).show();
        finish();
    }
}
