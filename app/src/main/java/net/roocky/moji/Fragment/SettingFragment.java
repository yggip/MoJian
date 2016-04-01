package net.roocky.moji.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.umeng.update.UmengUpdateAgent;

import net.roocky.moji.R;
import net.roocky.moji.Util.FileCopy;
import net.roocky.moji.Util.Permission;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by roocky on 03/16.
 * 设置Fragment
 */
public class SettingFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.ll_backup)
    LinearLayout llBackup;
    @Bind(R.id.ll_restore)
    LinearLayout llRestore;
    @Bind(R.id.ll_update)
    LinearLayout llUpdate;
    @Bind(R.id.ll_feedback)
    LinearLayout llFeedback;

    private final int EXTERNAL_STORAGE = 0;
    private int idClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);

        setOnClickListener();

        return view;
    }

    private void setOnClickListener() {
        llBackup.setOnClickListener(this);
        llRestore.setOnClickListener(this);
        llUpdate.setOnClickListener(this);
        llFeedback.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_update:
                UmengUpdateAgent.forceUpdate(getActivity());
                break;
            case R.id.ll_feedback:

                break;
            default:
                /**
                 * check()方法判断是否已经拥有该权限，若已拥有则直接进行备份&恢复操作，否则向用户发出请求
                 * 请求被处理后会回调onRequestPermissionsResult()方法
                 */
                idClick = v.getId();
                if (Permission.check(this, Manifest.permission.READ_EXTERNAL_STORAGE, EXTERNAL_STORAGE)) {
                    backStore(idClick);   //备份 & 恢复
                }

                break;
        }
    }

    //处理Android 6.0中permission请求完成事件
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE:  //存储空间权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    backStore(idClick);     //若获取成功权限则进行备份&恢复操作
                } else {
                    Snackbar.make(llBackup, getString(R.string.per_fail), Snackbar.LENGTH_SHORT).show();
                }
                return;

        }
    }

    //备份 & 恢复
    private void backStore(int id) {
        String result;
        if (id == R.id.ll_backup) {
            if (FileCopy.copy(getString(R.string.path_databases), getString(R.string.path_sdcard))) {
                result = "备份成功！";
            } else {
                result = "备份失败！";
            }
            Snackbar.make(llBackup, result, Snackbar.LENGTH_SHORT).show();
        } else {
            if (FileCopy.copy(getString(R.string.path_sdcard), getString(R.string.path_databases))) {
                result = "恢复成功！";
            } else {
                result = "恢复失败！";
            }
            Snackbar.make(llRestore, result, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}