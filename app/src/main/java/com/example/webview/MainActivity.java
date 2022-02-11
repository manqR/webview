package com.example.webview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    WebView wv;
    double longitude = 0, latitude = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wv = new WebView(this);
        wv.loadUrl("http://52.221.199.238/webview/index.html");
        setContentView(wv);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, 0);

        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setGeolocationEnabled(true);
        wv.addJavascriptInterface(new WebAppInterface(this), "Android");


    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface   // must be added for API 17 or higher
        public void showToast(String toast) {
            getCurrentLocation();
            if(longitude == 0){
                Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_SHORT).show();
            }else{
                String longLoat = "longitude "+ longitude +"\nLatitude "+latitude;
                Toast.makeText(mContext, longLoat, Toast.LENGTH_SHORT).show();
                try {
                    wv.post(new Runnable() {
                        @Override
                        public void run() {
                            wv.loadUrl("javascript:setLongLat('"+ longLoat +"')");
                        }
                    });

                }catch (Exception ex){
                    Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

//                [wv stringByEvaluatingJavaScriptFromString:@"document.getElementById('textFieldID').value = 'Hello World'"]
            }

        }
    }

    public void getCurrentLocation(){
        FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Pop the browser back stack or exit the activity
        if (wv.canGoBack()) {
            wv.goBack();
        }
        else {
            super.onBackPressed();
        }
    }
}