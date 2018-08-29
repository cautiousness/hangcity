package com.fuj.hangcity.activity.infodetail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fuj.hangcity.R;
import com.fuj.hangcity.base.BaseActivity;
import com.fuj.hangcity.tools.Constant;

/**
 * Created by fuj
 */
public class InfoDetailActivity extends BaseActivity {
    private ProgressBar progressBar;
    private ImageView backIV;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_detail);

        backIV = (ImageView) findViewById(R.id.backIV);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initWebView();
    }

    private void initWebView() {
        if(getIntent() != null && getIntent().hasExtra(Constant.BUNDLE_INFO_EXTRA_URL)) {
            String url = getIntent().getStringExtra(Constant.BUNDLE_INFO_EXTRA_URL);
            webView = (WebView) findViewById(R.id.webview);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);

            setViewMargins(webView, 0, getStatusBarHeight(this), 0, 0);
            webView.getSettings().setJavaScriptEnabled(false);
            webView.loadUrl(url);
            //webView.addJavascriptInterface(new JavaScriptInterface(this), "imagelistner");
            webView.setWebViewClient(new MyWebViewClient());
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            /*String fun="javascript:function getClass(parent,sClass) { var aEle=parent.getElementsByTagName('div'); var aResult=[]; var i=0; for(i<0;i<aEle.length;i++) { if(aEle[i].className==sClass) { aResult.push(aEle[i]); } }; return aResult; } ";

            view.loadUrl(fun);

            String fun2="javascript:function hideOther() {getClass(document,'nav-sides')[0].style.display='none'; getClass(document,'side-bar')[0].style.display='none'; getClass(document,'area-main')[0].style.display='none'; getClass(document,'home-foot')[0].style.display='none'; getClass(document,'enter')[0].style.display='none'; getClass(document,'crumb')[0].style.display='none';getClass(document,'date-tab clearfix')[0].style.display='none'; document.getElementById('id_sidebar').style.display='none'; document.getElementById('top_nav').style.display='none'; document.getElementById('fix-personal').style.display='none'; document.getElementById('waterlogo').style.display='none';getClass(document,'wrap')[0].style.minWidth=0;getClass(document,'game')[0].style.paddingTop=0;}";

            view.loadUrl(fun2);

            view.loadUrl("javascript:hideOther();");

            super.onPageFinished(view, url);*/
            progressBar.setVisibility(android.view.View.GONE);
            //addImageClickListner();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(android.view.View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    /*private void addImageClickListner() {
        webView.loadUrl("javascript:(function(){var objs = document.getElementsByTagName(\"img\"); for(var i=0;i<objs.length;i++) {   objs[i].onclick=function()  " +
            "{    window.imagelistner.openImage(this.src);  } }})()");
    }

    public static class JavaScriptInterface {
        private Context context;

        public JavaScriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void openImage(String img) {
            Intent intent = new Intent();
            intent.putExtra(Constant.BUNDLE_INFO_DETAIL_EXTRA_IMG, img);
            intent.setClass(context, WebImageActivity.class);
            context.startActivity(intent);
        }
    }*/
}
