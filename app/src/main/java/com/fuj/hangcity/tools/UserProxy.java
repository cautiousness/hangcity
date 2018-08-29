package com.fuj.hangcity.tools;

import com.fuj.hangcity.listener.OnLoginListener;
import com.fuj.hangcity.listener.OnResetPasswordListener;
import com.fuj.hangcity.listener.OnSignUpListener;
import com.fuj.hangcity.listener.OnUpdateListener;
import com.fuj.hangcity.model.user.User;
import com.fuj.hangcity.utils.LogUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class UserProxy {
	private OnSignUpListener onSignUpListener;
	private OnLoginListener onLoginListener;
	private OnUpdateListener onUpdateListener;
	private OnResetPasswordListener onResetPasswordListener;

	private static UserProxy instance;

	private UserProxy() {}

	public static UserProxy getInstance() {
		if(instance == null) {
			synchronized (UserProxy.class) {
				if(instance == null) {
					instance = new UserProxy();
				}
			}
		}
		return instance;
	}

	public void signUp(String userName, String password, String email){
		User user = new User();
		user.setUsername(userName);
		user.setPassword(password);
		user.setEmail(email);
		user.setSex(Constant.SEX_FEMALE);
		user.setSignature("这个家伙很懒,什么也没说");
		user.signUp(new SaveListener<User>() {
			@Override
			public void done(User user, BmobException e) {
				if(onSignUpListener!= null && e != null) {
					onSignUpListener.onSignUpFailure(e.getMessage());
					return;
				}

				if(onSignUpListener!= null) {
					onSignUpListener.onSignUpSuccess();
				}
			}
		});
	}
	
	public User getCurrentUser(){
		User user = BmobUser.getCurrentUser(User.class);
		if(user != null){
			LogUtils.i("本地用户信息 " + user.getObjectId() + "-"
					+ user.getUsername() + "-"
					+ user.getSessionToken() + "-"
					+ user.getCreatedAt() + "-"
					+ user.getUpdatedAt() + "-"
					+ user.getSignature() + "-"
					+ user.getSex());
			return user;
		} else {
			LogUtils.i("本地用户为null");
		}
		return null;
	}
	
	public void login(String userName, String password){
		final BmobUser user = new BmobUser();
		user.setUsername(userName);
		user.setPassword(password);
		user.login(new SaveListener<User>() {
			@Override
			public void done(User user, BmobException e) {
				if(onLoginListener!= null && e != null) {
					onLoginListener.onLoginFailure(e.getMessage());
					return;
				}

				if(onLoginListener!= null) {
					onLoginListener.onLoginSuccess();
				}
			}
		});
	}

	public void logout(){
		BmobUser.logOut();
		LogUtils.i("logout result:" + (null == getCurrentUser()));
	}
	
	public void update(String... args){
		User user = getCurrentUser();
		user.setUsername(args[0]);
		user.setEmail(args[1]);
		user.setPassword(args[2]);
		user.setSex(args[3]);
		user.setSignature(args[4]);
		user.update(new UpdateListener() {
			@Override
			public void done(BmobException e) {
				if(onUpdateListener!= null && e != null) {
					onUpdateListener.onUpdateFailure(e.getMessage());
					return;
				}

				if(onUpdateListener!= null) {
					onUpdateListener.onUpdateSuccess();
				}
			}
		});
	}

	public void resetPassword(String email){
		BmobUser.resetPasswordByEmail(email, new UpdateListener() {
			@Override
			public void done(BmobException e) {
				if(onResetPasswordListener!= null && e != null) {
					onResetPasswordListener.onResetFailure(e.getMessage());
					return;
				}

				if(onResetPasswordListener!= null) {
					onResetPasswordListener.onResetSuccess();
				}
			}
		});
	}

	public void setOnSignUpListener(OnSignUpListener onSignUpListener) {
		this.onSignUpListener = onSignUpListener;
	}

	public void setOnLoginListener(OnLoginListener onLoginListener) {
		this.onLoginListener = onLoginListener;
	}

	public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
		this.onUpdateListener = onUpdateListener;
	}

	public void setOnResetPasswordListener(OnResetPasswordListener onResetPasswordListener) {
		this.onResetPasswordListener = onResetPasswordListener;
	}
}
