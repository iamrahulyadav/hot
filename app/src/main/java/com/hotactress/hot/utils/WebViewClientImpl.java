package com.hotactress.hot.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URI;
import java.net.URISyntaxException;

public class WebViewClientImpl extends WebViewClient {

    private Activity activity = null;
    private AlertDialog alertDialog;


    public WebViewClientImpl(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host.equals("lolmenow.com"))
                return false;   // open link in app itself
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
        return true;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            String host = request.getUrl().getHost();
            if (host.equals("lolmenow.com"))
                return false;   // open link in app itself

            Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
            activity.startActivity(intent);
            return true;
        } else
            return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }
}