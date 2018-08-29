package com.fuj.hangcity.tools;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuj.hangcity.R;
import com.fuj.hangcity.adapter.base.RVAdapter;
import com.fuj.hangcity.cache.ConCache;
import com.fuj.hangcity.utils.DensityUtils;


/**
 * @author fuj
 */
public class LoadingDialog {
    private static Dialog loadingDialog;
	private static LoadingDialog instance;
	private static Dialog optionDialog;

	public static LoadingDialog getInstance() {
		if(instance == null) {
			instance = new LoadingDialog();
		}
		return instance;
	}

	private LoadingDialog() {}
	
    public void initLoadingDialog(Context context, String loadingMsg) {
        View view = View.inflate(context, R.layout.dialog_loading, null);
        ImageView loadingIV = (ImageView) view.findViewById(R.id.dialog_loadingIV);
        TextView loadingTV = (TextView) view.findViewById(R.id.dialog_loadingTV);
        Animation loadingAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_loading);
        loadingIV.startAnimation(loadingAnim);  
        loadingTV.setText(loadingMsg);
        
        loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setContentView(view);
        loadingDialog.setCancelable(false);
        Window window = loadingDialog.getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.gravity = Gravity.CENTER;
		layoutParams.width = ConCache.getInstance().getInt(context, ConCache.LOGIN_HEAD_WIDTH);
		layoutParams.height = layoutParams.width;
		layoutParams.dimAmount = 0.4f;
		loadingDialog.onWindowAttributesChanged(layoutParams);
		loadingDialog.setCanceledOnTouchOutside(false);
    }

	public void showOptionDialog(Context context, RecyclerView optionLV, int height,
				 RVAdapter adapter, int gravity, int paramsX) {
		optionLV.setAdapter(adapter);
		optionDialog = new Dialog(context, R.style.titleDialog);
		optionDialog.setContentView(optionLV);
		Window window = optionDialog.getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.gravity = gravity;
		layoutParams.x = DensityUtils.dp2px(context, paramsX);
		layoutParams.y = height;
		layoutParams.width = ConCache.getInstance().getInt(context, ConCache.LOGIN_HEAD_WIDTH);
		layoutParams.dimAmount = 0.2f;
		optionDialog.onWindowAttributesChanged(layoutParams);
		optionDialog.setCanceledOnTouchOutside(true);
		optionDialog.show();
	}

	public void showLoadingDialog(Context context, String loadingMsg) {
		synchronized(this) {
			instance.initLoadingDialog(context, loadingMsg);
			if(!loadingDialog.isShowing()) {
				loadingDialog.show();
			}
        }
	}
	
	public void hideLoadingDialog() {
		if(loadingDialog != null) {
			loadingDialog.dismiss();
		}
	}
	
	public void hideOptionDialog() {
		if(optionDialog != null) {
			optionDialog.dismiss();
		}
	}
}