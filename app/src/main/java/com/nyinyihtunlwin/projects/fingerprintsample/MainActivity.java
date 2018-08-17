package com.nyinyihtunlwin.projects.fingerprintsample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FingerprintManagerCompat manager = FingerprintManagerCompat.from(MainActivity.this);

                if (manager.isHardwareDetected() && manager.hasEnrolledFingerprints()) {
                    showFingerprintAuth();
                } else {
                    Snackbar.make(view, "Fingerprint authentication is not supported.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showFingerprintAuth() {
        FingerprintDialog dialog = FingerprintDialog.newInstance();
        dialog.show(getSupportFragmentManager(), FingerprintDialog.FRAGMENT_TAG);
        dialog.setListener(new FingerprintDialog.FDCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelClicked() {
                Toast.makeText(getApplicationContext(),"Canceled",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
