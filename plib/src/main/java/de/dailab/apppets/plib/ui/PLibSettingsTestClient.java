package de.dailab.apppets.plib.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import apppets.plib.R;
import de.dailab.apppets.plib.data.Constants;
import de.dailab.apppets.plib.keyGenerator.keystore.KeyStoreHandler;

/**
 * Created by arik on 20.06.2017.
 */

final class PLibSettingsTestClient {


    protected static void startTestClient(final Activity activity, final Handler handler,
                                       final int type, final boolean withOsTrustedCertificates) {


        // 1. get listen port
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.plib_adr_port, null);
        final TextView vPort = v.findViewById(R.id.EditText_Port);
        final TextView vAdr = v.findViewById(R.id.EditText_Adr);

        setLastFromHint(activity.getApplicationContext(), vAdr, vPort);
        builder.setView(v);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.apppets);

        builder.setPositiveButton(R.string.theplib_start_client,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        try {
                            String s = vPort.getText().toString();
                            if (s.equals("")) {
                                s = vPort.getHint().toString();
                            }
                            int port = Integer.parseInt(s);
                            if (port < 1 || port > 65535) {
                                throw new Exception();
                            }
                            String a = vAdr.getText().toString();
                            if (a.equals("")) {
                                a = vAdr.getHint().toString();


                            }
                            final String adr = a;
                            final int fport = port;
                            Thread t = new Thread() {

                                @Override
                                public void run() {

                                    startTestClient(activity, handler, type, adr, fport,
                                            withOsTrustedCertificates);
                                }
                            };
                            t.start();

                        } catch (Exception e) {
                            Toast.makeText(activity, R.string.theplib_illegal_adr_port,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        builder.setNegativeButton(R.string.theplib_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {

                dialog.cancel();
            }
        });


        builder.show();
    }

    private static void setLastFromHint(Context context, TextView vAdr, TextView vPort) {
        final SharedPreferences prefs = context
                .getSharedPreferences(Constants.PREF_NAME_SETT_UI,
                        Context.MODE_PRIVATE);
        String ip = prefs.getString(Constants.PREF_KEY_SETT_UI_SERVER_IP, "");
        int port = prefs.getInt(Constants.PREF_KEY_SETT_UI_SERVER_PORT, 0);
        vAdr.setHint(ip);
        vPort.setHint(port == 0 ? "" : "" + port);
    }

    private static void setLastInStorage(Context context, String adr, int port) {
        final SharedPreferences prefs = context
                .getSharedPreferences(Constants.PREF_NAME_SETT_UI,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(Constants.PREF_KEY_SETT_UI_SERVER_IP, adr);
        ed.putInt(Constants.PREF_KEY_SETT_UI_SERVER_PORT, port);
        ed.commit();
    }

    private static void startTestClient(final Activity activity, final Handler handler,
                                        final int type, final String adr, final int port,
                                        boolean withOsTrustedCertificates) {

        final Socket s;

        try {
            if (type == 0) {
                s = new Socket(adr, port);
            } else {
                s = KeyStoreHandler.getSecureSocket(activity.getApplicationContext(),
                        withOsTrustedCertificates);
                s.setSoTimeout(15000);
                s.connect(new InetSocketAddress(adr, port));
                setLastInStorage(activity.getApplicationContext(), adr, port);
            }
        } catch (Exception e) {
            e.printStackTrace();
            error(activity, handler, activity.getString(R.string.theplib_could_not_start_client),
                    e.getLocalizedMessage());
            return;
        }

        try {

            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));

            w.write("Hello from client");
            w.newLine();
            w.flush();
            String response = "Connection accepted, received from server: " + r.readLine();
            info(activity, handler, response);
        } catch (Exception e) {
            e.printStackTrace();
            error(activity, handler, activity.getString(R.string.theplib_client_error),
                    e.getLocalizedMessage());
        }


    }


    private static void error(Activity activity, final Handler handler, String descr,
                              String error) {
        String msg = descr + "\n\n" + activity.getString(R.string.theplib_error_msg) + ":\n" +
                error;
        msg(activity, handler, msg);
    }

    private static void info(Activity activity, final Handler handler, String descr) {
        msg(activity, handler, descr);
    }

    private static void msg(final Activity activity, final Handler handler, final String msg) {
        Message message = handler.obtainMessage();
        message.obj = msg;
        handler.sendMessage(message);

    }
}
