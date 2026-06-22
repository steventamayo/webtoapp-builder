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

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(false);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setGeolocationEnabled(true);
        settings.setUserAgentString(
            "Mozilla/5.0 (Linux; Android 11; Mobile) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
        );

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                // WhatsApp: wa.me, whatsapp://, api.whatsapp.com
                if (url.startsWith("whatsapp://") || url.contains("wa.me")
                        || url.contains("api.whatsapp.com")) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this,
                            "WhatsApp no instalado", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }

                // intent://
                if (url.startsWith("intent://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if (intent != null) startActivity(intent);
                    } catch (Exception ignored) {}
                    return true;
                }

                // tel, mailto, sms, market
                if (url.startsWith("tel:") || url.startsWith("mailto:")
                        || url.startsWith("sms:") || url.startsWith("market:")) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception ignored) {}
                    return true;
                }

                view.loadUrl(url);
                return true;
            }
        });

        webView.addJavascriptInterface(new ShareInterface(this), "AndroidShare");
        webView.loadUrl(SITE_URL);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static class ShareInterface {
        private final Activity activity;
        ShareInterface(Activity a) { this.activity = a; }

        @android.webkit.JavascriptInterface
        public void shareText(String text, String url) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, text + (url != null ? "\n" + url : ""));
            activity.startActivity(Intent.createChooser(i, "Compartir via..."));
        }

        @android.webkit.JavascriptInterface
        public void shareToWhatsApp(String text) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.setPackage("com.whatsapp");
                i.putExtra(Intent.EXTRA_TEXT, text);
                activity.startActivity(i);
            } catch (Exception e) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://wa.me/?text=" + Uri.encode(text))));
            }
        }
    }
}
