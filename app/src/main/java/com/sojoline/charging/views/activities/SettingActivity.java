package com.sojoline.charging.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sojoline.charging.R;
import com.sojoline.charging.event.RefreshUserCollectionEvent;
import com.sojoline.charging.event.StopQueryStatusEvent;
import com.sojoline.charging.utils.LvSpfEncryption;
import com.sojoline.charging.views.base.LvBaseAppCompatActivity;
import com.sojoline.charging.views.dialog.NiftyDialogBuilder;
import com.sojoline.model.LvRepository;
import com.sojoline.model.dagger.ApiComponentHolder;
import com.sojoline.model.response.SimpleResponse;
import com.sojoline.model.response.SubstationsResponse;
import com.sojoline.model.storage.AppInfosPreferences;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import rx.functions.Action1;

/**
 * Created by admin on 2017-02-16.
 */
@Route(path = "/login/mime/setting")
public class SettingActivity extends LvBaseAppCompatActivity {

    @BindView(R.id.setting_bt_exit)
    Button btExit;

    public static void navigation() {
        ARouter.getInstance().build("/login/mime/setting").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_setting);
    }

    @Override
    protected void initView() {
        initToolbarNav("设置");
    }

    @OnClick({R.id.setting_psd, R.id.setting_bt_exit, R.id.setting_nick})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_nick:
                nickInputDialog();
                break;
            case R.id.setting_psd://修改密码
                ChangePswActivity.navigation();
                break;
            case R.id.setting_bt_exit://退出登录
                // Clear all login info.
                logout();
                finish();
                break;
        }
    }

    public static void logout() {
        AppInfosPreferences.get().setHeaderUrl("");
        AppInfosPreferences.get().setToken("");
        AppInfosPreferences.get().setUid("");
        AppInfosPreferences.get().setUserName("");
        AppInfosPreferences.get().setNick("");
        LvSpfEncryption.setSecretKey("");
        RxBus.getInstance().postEvent(new StopQueryStatusEvent());
        getSubstations();
    }

    /**
     * 昵称输入对话框
     */
    private void nickInputDialog() {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
        View view = View.inflate(this, R.layout.dialog_edit, null);
        final EditText editText = (EditText) view.findViewById(R.id.et_nick);
        editText.setHint("请输入昵称（1-11个字符）");
        builder.withTitle("修改昵称")
            .withDuration(500)
            .setCustomView(view, this)
            .setButtonOkClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LvTextUtil.isEmpty(editText.getText().toString())) {
                        showToast("昵称不能为空");
                    } else {
                        updateNick(editText.getText().toString());
                    }
                    builder.dismiss();
                }
            })
            .setButtonCancelClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                }
            })
            .show();
    }

    /**
     * 修改昵称
     *
     * @param nick
     */
    private void updateNick(final String nick) {
        HashMap<String, String> map = new HashMap<>();
        map.put("nickname", nick);
        ApiComponentHolder.sApiComponent
            .apiService()
            .updateNick(map)
            .take(1)
            .compose(this.<SimpleResponse>bindUntilEvent(ActivityEvent.DESTROY))
            .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
            .subscribe(new SimpleSubscriber<SimpleResponse>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    showNormal();
                    requestFailed(null);
                }

                @Override
                public void onNext(SimpleResponse simpleResponse) {
                    showNormal();
                    if (simpleResponse.isSuccess()) {
                        showToast("修改成功");
                        AppInfosPreferences.get().setNick(nick);
                    } else {
                        requestFailed(simpleResponse.msg);
                    }
                }

                @Override
                public void onStart() {
                    super.onStart();
                    showLoadingDialog();
                }
            });
    }

    public void showLoading(String msg) {
        showLoadingDialog();
    }

    public void showNormal() {
        dismissLoadingDialog();
    }

    public void requestFailed(String msg) {
        if (LvTextUtil.isEmpty(msg)) {
            showToast(R.string.network_not_available);
        } else {
            showToast(msg);
        }
    }

    /**
     * 重新获取充电站数据
     */
    private static void getSubstations() {
        ApiComponentHolder.sApiComponent
                .apiService()
                .getSubstation()
                .compose(SchedulersCompat.<SubstationsResponse>applyNewSchedulers())
                .doOnNext(new Action1<SubstationsResponse>() {
                    @Override
                    public void call(SubstationsResponse response) {

                        LvRepository.getInstance().refreshSubstations(response.substations);
                        RxBus.getInstance().postEvent(new RefreshUserCollectionEvent());
                    }
                })
                .subscribe(new SimpleSubscriber<SubstationsResponse>() {
                });
    }
}
