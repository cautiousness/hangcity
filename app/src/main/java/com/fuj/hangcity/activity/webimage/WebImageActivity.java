package com.fuj.hangcity.activity.webimage;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.fuj.hangcity.R;
import com.fuj.hangcity.base.BaseActivity;
import com.fuj.hangcity.tools.Constant;
import com.fuj.hangcity.utils.ToastUtils;
import com.fuj.hangcity.widget.ZoomableImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by fuj
 */
public class WebImageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webimage);
        String imagePath = getIntent().getStringExtra(Constant.BUNDLE_INFO_DETAIL_EXTRA_IMG);

        TextView saveTV = (TextView) findViewById(R.id.webimage_saveTV);
        saveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(WebImageActivity.this, getString(R.string.toast_download_success));
            }
        });

        final ZoomableImageView imageView = (ZoomableImageView) findViewById(R.id.webimageIV);
        new AsyncTask<String, Void, BitmapDrawable>() {
            @Override
            protected BitmapDrawable doInBackground(String... params) {
                BitmapDrawable bitmapDrawable = null;
                try {
                    bitmapDrawable = (BitmapDrawable) loadImageFromUrl(params[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmapDrawable;
            }

            @Override
            protected void onPostExecute(@NonNull BitmapDrawable bitmapDrawable) {
                imageView.setImageBitmap(bitmapDrawable.getBitmap());
            }
        }.execute(imagePath);
    }

    public Drawable loadImageFromUrl(String url) throws IOException {
        InputStream i = (InputStream) new URL(url).getContent();
        return Drawable.createFromStream(i, Constant.EXTERNAL_STORAGE_DIR_SRC);
    }
}
