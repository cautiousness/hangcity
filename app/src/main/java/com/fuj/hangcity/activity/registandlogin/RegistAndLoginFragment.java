package com.fuj.hangcity.activity.registandlogin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fuj.hangcity.R;
import com.fuj.hangcity.base.BaseFragment;
import com.fuj.hangcity.enums.UserOperation;
import com.fuj.hangcity.listener.OnLoginListener;
import com.fuj.hangcity.listener.OnResetPasswordListener;
import com.fuj.hangcity.listener.OnSignUpListener;
import com.fuj.hangcity.tools.UserProxy;
import com.fuj.hangcity.utils.ValidUtils;
import com.fuj.hangcity.widget.DeletableEditText;
import com.fuj.hangcity.widget.SmoothProgressBar;

import static android.app.Activity.RESULT_OK;

/**
 * Created by fuj
 */
public class RegistAndLoginFragment extends BaseFragment implements RegistAndLoginContract.View,
        OnLoginListener, OnSignUpListener, OnResetPasswordListener {
    TextView loginTitle;
    TextView registerTitle;
    TextView resetPassword;

    DeletableEditText userNameInput;
    DeletableEditText userPasswordInput;
    DeletableEditText userEmailInput;

    Button operationBTN;
    SmoothProgressBar progressbar;

    UserProxy userProxy;
    UserOperation operation = UserOperation.LOGIN;

    private RegistAndLoginContract.Presenter mPresenter;

    protected void findViews(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.view_registandlogin, container, false);

        userNameInput = (DeletableEditText)findViewById(R.id.user_name_input);
        userPasswordInput = (DeletableEditText)findViewById(R.id.user_password_input);
        userEmailInput = (DeletableEditText)findViewById(R.id.user_email_input);

        progressbar = (SmoothProgressBar)findViewById(R.id.sm_progressbar);

        loginTitle = (TextView)findViewById(R.id.login_menu);
        loginTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickLoginTitle();
            }
        });

        registerTitle = (TextView)findViewById(R.id.register_menu);
        registerTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRegisterTitle();
            }
        });

        resetPassword = (TextView)findViewById(R.id.reset_password_menu);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickResetPassword();
            }
        });

        operationBTN = (Button)findViewById(R.id.register);
        operationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOperationBTN();
            }
        });

        updateLayout(operation);
        initUserProxy();
    }

    @Override
    public void setPresenter(RegistAndLoginContract.Presenter presenter) {
        mPresenter = presenter;
        mPresenter.start();
    }

    private void clickLoginTitle() {
        operation = UserOperation.LOGIN;
        updateLayout(operation);
    }

    private void clickRegisterTitle() {
        operation = UserOperation.REGISTER;
        updateLayout(operation);
    }

    private void clickResetPassword() {
        operation = UserOperation.RESET_PASSWORD;
        updateLayout(operation);
    }

    private void clickOperationBTN() {
        progressbar.setVisibility(View.VISIBLE);
        if(operation == UserOperation.LOGIN){
            try {
                isEmpty(userNameInput).isEmpty(userPasswordInput);
                userProxy.login(userNameInput.getText().toString().trim(),
                        userPasswordInput.getText().toString().trim());
            } catch (NullPointerException e) {
                showToast(e.getMessage());
            }
        } else if(operation == UserOperation.REGISTER){
            try {
                isEmpty(userNameInput).isEmpty(userPasswordInput)
                        .isEmpty(userEmailInput).isValidEmail();
                userProxy.signUp(userNameInput.getText().toString().trim(),
                        userPasswordInput.getText().toString().trim(),
                        userEmailInput.getText().toString().trim());
            } catch (NullPointerException e) {
                showToast(e.getMessage());
            }
        } else {
            try {
                isEmpty(userEmailInput).isValidEmail();
                userProxy.resetPassword(userEmailInput.getText().toString().trim());
            } catch (NullPointerException e) {
                showToast(e.getMessage());
            }
        }
    }

    private void updateLayout(UserOperation op){
        if(op == UserOperation.LOGIN){
            changeStatus(loginTitle, registerTitle, resetPassword);
            changeVisible(true, true, false, "登录");
        } else if(op == UserOperation.REGISTER) {
            changeStatus(registerTitle, loginTitle, resetPassword);
            changeVisible(true, true, true, "注册");
        } else {
            changeStatus(resetPassword, loginTitle, registerTitle);
            changeVisible(false, false, true, "找回密码");
        }
    }

    private void initUserProxy() {
        userProxy = UserProxy.getInstance();
        userProxy.setOnLoginListener(this);
        userProxy.setOnSignUpListener(this);
        userProxy.setOnResetPasswordListener(this);
    }

    private void changeStatus(TextView view, TextView view1, TextView view2) {
        setTextViewResource(view, R.color.colorPrimary);
        setTextViewDrawable(view1, R.color.gray_white);
        setTextViewDrawable(view2, R.color.gray_white);
    }

    private void setTextViewResource(TextView view, int colorId) {
        setTextView(view, colorId);
        view.setBackgroundResource(R.drawable.bg_login_tab);
    }

    private void setTextViewDrawable(TextView view, int colorId) {
        setTextView(view, colorId);
        view.setBackgroundDrawable(null);
    }

    private void setTextView(TextView view, int colorId) {
        view.setTextColor(getResColor(colorId));
        view.setPadding(16, 16, 16, 16);
        view.setGravity(Gravity.CENTER);
    }

    private void changeVisible(boolean isNameVisible, boolean isPswVisible,
                               boolean isEmailVisible, String msg) {
        setVisible(userNameInput, isNameVisible);
        setVisible(userPasswordInput, isPswVisible);
        setVisible(userEmailInput, isEmailVisible);
        operationBTN.setText(msg);
    }

    private void setVisible(View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private RegistAndLoginFragment isEmpty(DeletableEditText view) {
        if(TextUtils.isEmpty(view.getText())){
            view.setShakeAnimation();
            String msg;
            switch (view.getId()) {
                case R.id.user_name_input:
                    msg = "请输入用户名";
                    break;
                case R.id.user_password_input:
                    msg = "请输入密码";
                    break;
                case R.id.user_email_input:
                    msg = "请输入邮箱";
                    break;
                default:
                    msg = "请输入用户名";
                    break;
            }
            dimissProgressbar();
            throw new NullPointerException(msg);
        }
        return this;
    }

    private RegistAndLoginFragment isValidEmail() {
        if(!ValidUtils.isValidEmail(userEmailInput.getText())){
            userEmailInput.setShakeAnimation();
            dimissProgressbar();
            throw new NullPointerException("邮箱格式不正确");
        }
        return this;
    }

    @Override
    public void onLoginSuccess() {
        dimissProgressbar();
        showToast("登录成功");
        getActivity().setResult(RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void onLoginFailure(String msg) {
        dimissProgressbar();
        showToast(msg);
    }

    @Override
    public void onSignUpSuccess() {
        dimissProgressbar();
        showToast("注册成功");
        operation = UserOperation.LOGIN;
        updateLayout(operation);
    }

    @Override
    public void onSignUpFailure(String msg) {
        dimissProgressbar();
        showToast("注册失败,请确认网络连接后再重试");
    }

    @Override
    public void onResetSuccess() {
        dimissProgressbar();
        showToast("请到邮箱修改密码后再登录");
        operation = UserOperation.LOGIN;
        updateLayout(operation);
    }

    @Override
    public void onResetFailure(String msg) {
        dimissProgressbar();
        showToast("重置密码失败,请确认网络连接后再重试");
    }

    private void dimissProgressbar(){
        if(progressbar != null && progressbar.isShown()){
            progressbar.setVisibility(View.GONE);
        }
    }
}
