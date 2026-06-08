package com.example.gpslocator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final long MIN_TIME_MS = 1000;   // 1 second
    private static final float MIN_DIST_M = 0f;     // any movement

    private LocationManager locationManager;
    private TextView tvStatus;
    private TextView tvLat;
    private TextView tvLon;
    private TextView tvAlt;
    private TextView tvAccuracy;
    private TextView tvSpeed;
    private TextView tvBearing;
    private TextView tvSatellites;
    private TextView tvTime;

    private int satelliteCount = 0;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            // GPS-only: provider must be GPS
            updateUI(location);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            tvStatus.setText("GPS abilitato");
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            tvStatus.setText("GPS disabilitato — abilitare nelle impostazioni");
        }
    };

    private GnssStatus.Callback gnssStatusCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus    = findViewById(R.id.tvStatus);
        tvLat       = findViewById(R.id.tvLat);
        tvLon       = findViewById(R.id.tvLon);
        tvAlt       = findViewById(R.id.tvAlt);
        tvAccuracy  = findViewById(R.id.tvAccuracy);
        tvSpeed     = findViewById(R.id.tvSpeed);
        tvBearing   = findViewById(R.id.tvBearing);
        tvSatellites= findViewById(R.id.tvSatellites);
        tvTime      = findViewById(R.id.tvTime);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // GNSS satellite count callback
        gnssStatusCallback = new GnssStatus.Callback() {
            @Override
            public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                int used = 0;
                for (int i = 0; i < status.getSatelliteCount(); i++) {
                    if (status.usedInFix(i)) used++;
                }
                satelliteCount = used;
                tvSatellites.setText("Satelliti in uso: " + used + " / " + status.getSatelliteCount());
            }
        };

        checkPermissionsAndStart();
    }

    private void checkPermissionsAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            startGps();
        }
    }

    private void startGps() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            tvStatus.setText("GPS disabilitato — abilitare nelle impostazioni");
            return;
        }

        tvStatus.setText("In attesa del segnale GPS...");

        // GPS provider only — no network/fused
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_MS,
                MIN_DIST_M,
                locationListener
        );

        locationManager.registerGnssStatusCallback(gnssStatusCallback, null);
    }

    private void updateUI(Location loc) {
        tvStatus.setText("Fix GPS ottenuto");

        tvLat.setText(String.format(Locale.US, "Latitudine:  %.6f°", loc.getLatitude()));
        tvLon.setText(String.format(Locale.US, "Longitudine: %.6f°", loc.getLongitude()));
        tvAlt.setText(loc.hasAltitude()
                ? String.format(Locale.US, "Altitudine:  %.1f m", loc.getAltitude())
                : "Altitudine:  N/D");
        tvAccuracy.setText(loc.hasAccuracy()
                ? String.format(Locale.US, "Precisione:  ±%.1f m", loc.getAccuracy())
                : "Precisione:  N/D");
        tvSpeed.setText(loc.hasSpeed()
                ? String.format(Locale.US, "Velocità:    %.1f km/h", loc.getSpeed() * 3.6f)
                : "Velocità:    N/D");
        tvBearing.setText(loc.hasBearing()
                ? String.format(Locale.US, "Direzione:   %.1f°", loc.getBearing())
                : "Direzione:   N/D");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.ITALY);
        tvTime.setText("Ora fix:     " + sdf.format(new Date(loc.getTime())));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startGps();
        } else {
            tvStatus.setText("Permesso GPS negato");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
        locationManager.unregisterGnssStatusCallback(gnssStatusCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissionsAndStart();
    }
}
