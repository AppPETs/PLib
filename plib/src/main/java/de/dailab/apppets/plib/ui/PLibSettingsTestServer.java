package de.dailab.apppets.plib.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLSocket;

import apppets.plib.R;
import de.dailab.apppets.plib.data.Constants;
import de.dailab.apppets.plib.keyGenerator.keystore.KeyStoreHandler;

/**
 * Created by arik on 20.06.2017.
 */

final class PLibSettingsTestServer {

    final private static int NOTIFICATION_ID = 1234;

    private static boolean isServerRuning = false;


    protected static void startTestServer(final Activity activity, final Handler handler,
                                          final int type, final boolean withOsTrustedCertificates) {

        if (isServerRuning) {
            Toast.makeText(activity, R.string.theplib_server_is_already_running, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // 1. get listen port
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.plib_port, null);
        final TextView vPort = v.findViewById(R.id.EditText_Port);
        setLastFromHint(activity.getApplicationContext(), vPort);
        builder.setView(v);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.apppets);

        builder.setPositiveButton(R.string.theplib_start_server,
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
                            startTestServer(activity, handler, type, port,
                                    withOsTrustedCertificates);
                        } catch (Exception e) {
                            Toast.makeText(activity, R.string.theplib_illegal_port,
                                    Toast.LENGTH_SHORT).show();
                            return;
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

    private static void startTestServer(final Activity activity, final Handler handler,
                                        final int type, final int port,
                                        final boolean withOsTrustedCertificates) {

        final ServerSocket ss;

        try {
            if (type == 0) {
                ss = new ServerSocket(port);
            } else {
                ss = KeyStoreHandler
                        .getSecureSocketServer(activity.getApplicationContext(), port, type == 2,
                                withOsTrustedCertificates);
            }
        } catch (Exception e) {
            e.printStackTrace();
            error(activity, handler, activity.getString(R.string.theplib_could_not_start_server),
                    e.getLocalizedMessage());
            return;
        }
        isServerRuning = true;

        setLastInStorage(activity.getApplicationContext(), port);

        Toast.makeText(activity, R.string.theplib_server_for_120_sec, Toast.LENGTH_SHORT).show();

        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(activity)
                        .setSmallIcon(R.mipmap.apppets)
                        .setContentTitle(activity.getString(R.string.theplib_server))
                        .setContentText(activity.getString(R.string.theplib_run_plib_test_server));
        final NotificationManager mNotifyMgr =
                (NotificationManager) activity.getSystemService(Activity.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        if (mNotifyMgr != null) {
            mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
        }

        final Thread t = new Thread() {

            @Override
            public void run() {

                try {
                    Socket s;
                    if (type == 0) {
                        s = ss.accept();
                    } else {
                        s = ss.accept();

                    }

                    BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                            s.getOutputStream()));
                    BufferedReader r = new BufferedReader(
                            new InputStreamReader(s.getInputStream()));
                    String l = r.readLine();

                    String msg = "Connection accepted, received from client: " + l;

                    info(activity, handler, msg);
                    final NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(activity)
                                    .setSmallIcon(R.mipmap.apppets)
                                    .setContentTitle(activity.getString(R.string.theplib_server))
                                    .setContentText(msg);
                    mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());

                    w.write("Hello from Server - OK (" + l + ")");
                    w.newLine();
                    w.flush();
                    w.close();
                    r.close();
                    s.close();
                    try {
                        ss.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    if (e instanceof SocketException &&
                            e.getLocalizedMessage().contains("Socket closed")) {
                        isServerRuning = false;
                        mNotifyMgr.cancel(NOTIFICATION_ID);
                        return;
                    } else {
                        try {
                            ss.close();
                        } catch (Exception e0) {
                            e0.printStackTrace();
                        }
                        final NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(activity)
                                        .setSmallIcon(R.mipmap.apppets)
                                        .setContentTitle(
                                                activity.getString(R.string.theplib_server))
                                        .setContentText("Error: " + e.getLocalizedMessage());
                        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
                        error(activity, handler, "Error", e.getLocalizedMessage());
                    }
                }

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        try {
                            ss.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        isServerRuning = false;
                        mNotifyMgr.cancel(NOTIFICATION_ID);
                    }
                }, 10000);

            }
        };
        t.start();


        TimerTask tt = new TimerTask() {

            @Override
            public void run() {
                try {

                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(tt, 120000);


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

    private static void setLastFromHint(Context context, TextView vPort) {
        final SharedPreferences prefs = context
                .getSharedPreferences(Constants.PREF_NAME_SETT_UI,
                        Context.MODE_PRIVATE);
        int port = prefs.getInt(Constants.PREF_KEY_SETT_UI_SERVER_PORT_LISTEN, 0);
        vPort.setHint(port == 0 ? "" : "" + port);
    }

    private static void setLastInStorage(Context context, int port) {
        final SharedPreferences prefs = context
                .getSharedPreferences(Constants.PREF_NAME_SETT_UI,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putInt(Constants.PREF_KEY_SETT_UI_SERVER_PORT_LISTEN, port);
        ed.commit();
    }
}
