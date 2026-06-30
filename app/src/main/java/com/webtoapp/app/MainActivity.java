package com.webtoapp.app;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
    private WebView webView;
    private static final String SITE_URL = "URL_PLACEHOLDER";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setBuiltInZoomControls(false);
        s.setSupportZoom(false);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setGeolocationEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
                String url = req.getUrl().toString();
                if (url.startsWith("whatsapp://") || url.contains("wa.me")) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); }
                    catch (Exception e) { Toast.makeText(MainActivity.this, "WhatsApp no instalado", Toast.LENGTH_SHORT).show(); }
                    return true;
                }
                if (url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("intent:")) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); }
                    catch (Exception ignored) {}
                    return true;
                }
                view.loadUrl(url);
                return true;
            }
        });
        webView.addJavascriptInterface(new Object() {
            @android.webkit.JavascriptInterface
            public void shareToWhatsApp(String text) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain"); i.setPackage("com.whatsapp");
                    i.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(i);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://wa.me/?text=" + Uri.encode(text))));
                }
            }
        }, "AndroidShare");
        webView.loadUrl(SITE_URL);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack(); return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
