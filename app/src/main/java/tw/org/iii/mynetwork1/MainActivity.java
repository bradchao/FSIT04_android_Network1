package tw.org.iii.mynetwork1;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ConnectivityManager connectivityManager;
    private MyNetworkBroadcast receiver;
    private WebView webView;

    private ProgressDialog progressDialog;

    private LocationManager lmgr;
    private MyLocationListener listener;
    private UIHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        }else{
            init();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            init();
        }else{
            finish();
        }

    }

    private void init(){

        handler = new UIHandler();

        lmgr = (LocationManager)getSystemService(LOCATION_SERVICE);
        listener = new MyLocationListener();
        lmgr.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,0,0,listener);

        connectivityManager =
                (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        Log.v("brad", "isconnect: " + isConnectNetwork());
        Log.v("brad", "wifi: " + isConnectWifi());

        receiver = new MyNetworkBroadcast();
        IntentFilter filter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);


        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        webView = findViewById(R.id.webview);
        initWebView();
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            //Log.v("brad", lat + " : " + lng);

            webView.loadUrl(
                "javascript:gotoKD("+ lat + "," + lng + ")");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }



    private void initWebView(){

        webView.setWebViewClient(new MyWebViewClient());

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);

        webView.addJavascriptInterface(new MyJSBrad(),
                "brad");

        //webView.loadUrl("file:///android_asset/brad.html");
        //webView.loadUrl("http://www.iii.org.tw");

        webView.loadUrl("file:///android_asset/brad.html");



    }

    public class MyJSBrad {
        @JavascriptInterface
        public void callFromJS(String name){
            Log.v("brad", "from JS:" + name);

            Message mesg = new Message();
            Bundle data = new Bundle();
            data.putString("name", name);
            mesg.setData(data);
            handler.sendMessage(mesg);

//            Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT)
//                    .show();

        }
    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String name = msg.getData().getString("name");
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Message")
                    .setMessage(name)
                    .show();


        }
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, WebResourceRequest request) {
            //Log.v("brad", "shouldOverrideUrlLoading1" );




            return super.shouldOverrideUrlLoading(view, request);
        }

//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            Log.v("brad", "shouldOverrideUrlLoading1:" + url );
//
//            url = "file:///android_asset/map.html";
//            return super.shouldOverrideUrlLoading(view, url);
//        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressDialog.show();
            Log.v("brad", "onPageStarted");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressDialog.dismiss();
            Log.v("brad", "onPageFinished");
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v("brad", "keyCode = " + keyCode);

        if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK){
            webView.goBack();
            return false;
        }else{
            return super.onKeyDown(keyCode, event);
        }

        //return true; //super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        Log.v("brad", "finish");
        if (receiver != null){
            unregisterReceiver(receiver);
        }

        lmgr.removeUpdates(listener);

        super.finish();
    }

    private boolean isConnectNetwork(){
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = info != null && info.isConnected();
        return isConnected;
    }

    private boolean isConnectWifi(){
        NetworkInfo info =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return info.isConnected();
    }

    public void test1(View view) {
//        webView.loadUrl(
//                "javascript:gotoKD("+ lat + "," + lng + ")");

    }

    private class MyNetworkBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("brad", "OK");
        }
    }


}
