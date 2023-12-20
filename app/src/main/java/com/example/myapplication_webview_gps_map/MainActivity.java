package com.example.myapplication_webview_gps_map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;

import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener {
    WebView web;
    MainActivity mactivity;
    Button bok;
    EditText x,y;

    TextView address;
    LocationManager locationManager;
    Location location;
    double mLatitude = 0;
    double mLongitude = 0;
    String provider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mactivity=this;
        //
        x=(EditText)findViewById(R.id.editText);
        y=(EditText)findViewById(R.id.editText2);
        bok=(Button)findViewById(R.id.button);
        bok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String xv=x.getText().toString();
                String yv=y.getText().toString();
                web.loadUrl("javascript: initialize2("+xv+","+yv+")");

            }
        });
        //
        address=(TextView)findViewById(R.id.t1);

        web=(WebView)findViewById(R.id.webView);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setDomStorageEnabled(true);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient()
                             {
                                 public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                     return false;                    }
                             }
        );
        //
        //


        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .addPathHandler("/res/", new WebViewAssetLoader.ResourcesPathHandler(this))
                .build();
        //
        web.setWebViewClient(new WebViewClientCompat() {
            @Override
            @RequiresApi(21)
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            @SuppressWarnings("deprecation") // for API < 21
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return assetLoader.shouldInterceptRequest(Uri.parse(url));
            }
        });

        web.loadUrl("https://appassets.androidplatform.net/assets/k_9.html");

        //
        if (ActivityCompat.checkSelfPermission(mactivity, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(mactivity, new String[]{ACCESS_FINE_LOCATION}, 12);

        }
        else
            initlocation();
        //
    }
//
//
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 12) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initlocation();
        } else
            return;
    }
}
    //
    void initlocation() {
        try {
            // Getting LocationManager object from System Service LOCATION_SERVICE
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            provider = locationManager.getBestProvider(criteria, true);
            // Enabling  get My Location
            // Getting Current Location From GPS
            location = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider, 12000, 0, mactivity);
//            location.setLatitude(25.087195);
//            location.setLongitude(121.565531);
//            onLocationChanged(location);
        }
        catch(SecurityException e)
        {
            ActivityCompat.requestPermissions(mactivity, new String[]{ACCESS_FINE_LOCATION}, 12);
        }

    }
    //
    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location==null)
        {
            address.setText("尚未定位成功!");
        }
        else {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            Log.e("x=",""+mLatitude);
            Log.e("y=",""+mLongitude);
            x.setText(""+mLatitude);
            y.setText(""+mLongitude);
            web.loadUrl("javascript: initialize2("+mLatitude+","+mLongitude+")");
        }
    }
    //
    public class JavaScriptInterface {
        MainActivity mContext;
        /** Instantiate the interface and set the context */
        JavaScriptInterface(MainActivity c)
        {
            mContext = c;
        }
        /** Show a toast from the web page */
        @JavascriptInterface
        public void showaddress(String adr) {
            mContext.address.setText(adr);
        }

    }
    //
}