package com.fuj.hangcity.activity.main.user;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fuj.hangcity.R;
import com.fuj.hangcity.activity.chooseplugin.ChoosePluginActivity;
import com.fuj.hangcity.activity.main.MainActivity;
import com.fuj.hangcity.activity.registandlogin.RegistAndLoginActivity;
import com.fuj.hangcity.base.BaseFragment;
import com.fuj.hangcity.model.user.UserSettings;
import com.fuj.hangcity.tools.UserProxy;
import com.fuj.hangcity.utils.LogUtils;

import java.io.FileNotFoundException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.app.Activity.RESULT_OK;
import static com.fuj.hangcity.R.id.user_userTV;

/**
 * Created by fuj
 */
public class UserFragment extends BaseFragment implements UserContract.View {
    private TextView userTV;
    private TextView quitTV;
    private ImageView avatarIV;

    private static final int LOGIN = 1;
    private static final int AVATAR = 2;

    private UserSettings userSettings;
    private UserProxy userProxy;

    private UserContract.Presenter mPresenter;

    @Override
    protected void findViews(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.view_user, container, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setViewMargins(toolbar, 0, getStatusBarHeight(), 0, 0);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitleEnabled(false);

        TextView gameTV = (TextView) findViewById(R.id.user_option_gameTV);
        gameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChoosePluginActivity.class));
            }
        });

        quitTV = (TextView) findViewById(R.id.user_quit);
        quitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickQuit();
            }
        });

        avatarIV = (ImageView) findViewById(R.id.user_avatarIV);
        avatarIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAvatar();
            }
        });

        userTV = (TextView) findViewById(user_userTV);
        userTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAvatar();
            }
        });

        userProxy = UserProxy.getInstance();
        setUser();
        loadBackdrop();
    }

    private void loadBackdrop() {
        ImageView imageView = (ImageView) findViewById(R.id.user_backdropIV);
        Glide.with(this).load(R.mipmap.bg_user).centerCrop().into(imageView);
    }

    @Override
    public void setPresenter(UserContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void clickAvatar() {
        if(userProxy.getCurrentUser() != null) {
            changeAvatar();
        } else {
            showToast("请登录");
            showActivityResult(RegistAndLoginActivity.class, LOGIN);
        }
    }

    private void changeAvatar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOGIN:
                loginResult(resultCode);
                break;
            case AVATAR:
                avatarResult(resultCode, data);
                break;
            default:
                break;
        }
    }

    private void loginResult(int resultCode) {
        if(resultCode == RESULT_OK) {
            setUser();
        }
    }

    private void setUser() {
        if(userProxy.getCurrentUser() != null) {
            userTV.setText(userProxy.getCurrentUser().getUsername());
            setAvatar();
            showQuit();
            return;
        }
        userTV.setText("请登录");
        hideQuit();
    }

    private void avatarResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            final Uri uri = data.getData();
            LogUtils.i("[uri] " + uri);
            final ContentResolver cr = getActivity().getContentResolver();

            final BmobFile file = new BmobFile(userProxy.getCurrentUser().getUsername(), null, uri.getPath());
            UserSettings settings = new UserSettings();
            settings.setAvatar(file);
            settings.setObjectId(userSettings.getObjectId());
            LogUtils.i("[objectId] " + userProxy.getCurrentUser().getObjectId());
            settings.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e == null){
                        showToast("头像上传成功");
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                            avatarIV.setImageBitmap(bitmap);
                        } catch (FileNotFoundException exception) {
                            LogUtils.e(exception.getMessage());
                        }
                    } else {
                        showToast("头像上传失败");
                        LogUtils.i("[error] " + e.getMessage());
                    }
                }
            });
        }
    }

    private void setAvatar() {
        BmobQuery<UserSettings> query = new BmobQuery<>();
        query.addWhereEqualTo(UserSettings.USER_ID, userProxy.getCurrentUser().getObjectId());
        query.findObjects(new FindListener<UserSettings>() {
            @Override
            public void done(List<UserSettings> list, BmobException e) {
                if(e == null && list != null){
                    userSettings = list.get(0);
                    if(userSettings == null) {
                        showToast("读取头像失败");
                        return;
                    }
                    BmobFile file = userSettings.getAvatar();
                    LogUtils.i(" url = " + file.getUrl() + ", " + file.getFileUrl());
                    Glide.with(getActivity()).load(file.getFileUrl()).into(avatarIV);
                    return;
                }
                showToast("未找到头像");
            }
        });
    }

    private void clickQuit() {
        if (userProxy.getCurrentUser() != null) {
            userProxy.logout();
            setUser();
        }
    }

    private void hideQuit() {
        quitTV.setVisibility(View.GONE);
    }

    private void showQuit() {
        quitTV.setVisibility(View.VISIBLE);
    }
}
