package tw.org.iii.mynetwork1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {
    private ConnectivityManager connectivityManager;
    private MyNetworkBroadcast receiver;
    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectivityManager =
                (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        Log.v("brad", "isconnect: " + isConnectNetwork());
        Log.v("brad", "wifi: " + isConnectWifi());

        receiver = new MyNetworkBroadcast();
        IntentFilter filter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);


        webView = findViewById(R.id.webview);
        initWebView();
    }


    private void initWebView(){
        webView.loadUrl("file:///android_asset/brad.html");





    }




    @Override
    public void finish() {
        if (receiver != null){
            unregisterReceiver(receiver);
        }
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

    private class MyNetworkBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("brad", "OK");
        }
    }


}
