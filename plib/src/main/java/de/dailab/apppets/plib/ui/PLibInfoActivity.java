package de.dailab.apppets.plib.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import apppets.plib.R;

/**
 * Created by arik on 27.02.2017.
 */

final public class PLibInfoActivity extends AppCompatActivity {

    Button btn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.plib_activity_info);
        btn = findViewById(R.id.btn_more);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LibsBuilder().withShowLoadingProgress(true)
                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        .withAboutAppName(getString(R.string.theplib_app_name))
                        .withAboutVersionShownCode(true)
                        .withActivityTitle(getString(R.string.theplib_app_name)).withLicenseDialog(true)
                        .withLicenseShown(true).withAboutIconShown(true).withAboutVersionShown(true)

                        .withAboutVersionShownCode(true)

                        .withAboutDescription("bla").start(getApplicationContext());
            }

        });
    }
}
