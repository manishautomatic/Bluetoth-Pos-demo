package com.lapalap_pos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebDemo extends Activity implements  WebinterfaceCallaback{

    private WebView mWebView;
    private final String LAMA_LOGIN_URL="http://www.lapalap.com/posdemo/index.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_demo);
        initUI();
    }


    private void initUI(){
        mWebView=(WebView)findViewById(R.id.webView);
       // WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        android.webkit.CookieManager.getInstance().setAcceptCookie(true);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.addJavascriptInterface(new WebAppInterface(this,this), "AndroidFunction");
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);


        mWebView.loadUrl(LAMA_LOGIN_URL);

    }

    @Override
    public void doMute() {

    }

    @Override
    public void weigh() {
        startActivity(new Intent(WebDemo.this, MainActivity.class));
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

           /**
            if (Uri.parse(url).getHost().equals("helplama.com")) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);

            */
            return false;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}
