package dron.mkapiczynski.pl.dronvision.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import dron.mkapiczynski.pl.dronvision.activity.LoginActivity;

/**
 * Created by Miix on 2016-01-12.
 */
public class UIHelper {

    public static AlertDialog createLogoutDialog(final Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity.getApplicationContext());

        alertDialogBuilder.setTitle("Wylogowanie");

        alertDialogBuilder
                .setMessage("Jesteś pewien, że chcesz się wylogować?")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
                        intent.putExtra("prevActivity", "Main");
                        activity.startActivity(intent);
                        Toast.makeText(activity.getApplicationContext(), "Zostałeś wylogowany", Toast.LENGTH_SHORT).show();
                        activity.finish();
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog logoutDialog = alertDialogBuilder.create();

        return logoutDialog;
    }
}
