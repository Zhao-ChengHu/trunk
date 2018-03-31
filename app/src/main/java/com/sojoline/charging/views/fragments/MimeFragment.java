package com.sojoline.charging.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sojoline.charging.LvAppUtils;
import com.sojoline.charging.R;
import com.sojoline.charging.services.ServiceUtil;
import com.sojoline.charging.utils.GlideLoader;
import com.sojoline.charging.views.activities.AboutActivity;
import com.sojoline.charging.views.activities.CarActivity;
import com.sojoline.charging.views.activities.CardActivity;
import com.sojoline.charging.views.activities.CollectionActivity;
import com.sojoline.charging.views.activities.LoginActivity;
import com.sojoline.charging.views.activities.MessageActivity;
import com.sojoline.charging.views.activities.MoneyActivity;
import com.sojoline.charging.views.activities.PayRecordActivity;
import com.sojoline.charging.views.activities.SettingActivity;
import com.sojoline.charging.views.activities.UserHeaderImageActivity;
import com.sojoline.charging.views.base.LvBaseFragment;
import com.sojoline.model.bean.ProfileBean;
import com.sojoline.model.storage.AppInfosPreferences;
import com.sojoline.presenter.profile.ProfileContract;
import com.sojoline.presenter.profile.ProfilePresenter;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.util.LvTextUtil;

/********************************
 * Created by lvshicheng on 2017/2/13.
 ********************************/
public class MimeFragment extends LvBaseFragment implements View.OnClickListener,ProfileContract.View{

    @BindView(R.id.mime_ibtn_setting)
    ImageButton  mimeIbtnSetting;
    @BindView(R.id.mime_ibtn_message)
    ImageButton  mimeIbtnMessage;
    @BindView(R.id.mime_ibtn_photo)
    ImageView    mimeIbtnPhoto;
    @BindView(R.id.mime_ll_money)
    LinearLayout mimeLlMoney;
    @BindView(R.id.mime_ll_collection)
    LinearLayout mimeLlCollection;
    @BindView(R.id.mime_ll_card)
    LinearLayout mimeLlCard;
    @BindView(R.id.mime_ll_car)
    LinearLayout mimeLlCar;
//    @BindView(R.id.mime_ll_pile)
//    LinearLayout mimeLlPile;
    @BindView(R.id.mime_ll_about)
    LinearLayout mimeLlAbout;

    @BindView(R.id.mime_tv_login)
    TextView mimeTvLogin;
    @BindView(R.id.mime_tv_name)
    TextView mimeTvName;

    private ProfilePresenter profilePresenter;

    public static MimeFragment newInstance() {

        Bundle args = new Bundle();
        MimeFragment fragment = new MimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frgm_mime, container, false);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        profilePresenter = new ProfilePresenter();
        profilePresenter.attachView(this);
    }

    @Override
    protected void destroyPresenter() {
        super.destroyPresenter();
        profilePresenter.detachView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (LvAppUtils.isLogin()) {
            mimeTvName.setVisibility(View.VISIBLE);
            mimeTvLogin.setVisibility(View.GONE);
            if (LvTextUtil.isEmpty(AppInfosPreferences.get().getHeaderUrl()) ||
                    LvTextUtil.isEmpty(AppInfosPreferences.get().getNick())){
                profilePresenter.getProfile();
            }else {
                GlideLoader.getInstance().displayWithRound(_mActivity.getApplicationContext(),
                        AppInfosPreferences.get().getHeaderUrl(),
                        mimeIbtnPhoto);
                setNickName();
            }

        } else {
            GlideLoader.getInstance().displayResource(_mActivity.getApplicationContext(),
                    R.drawable.ic_photo_default,
                    mimeIbtnPhoto);
            mimeTvName.setVisibility(View.GONE);
            mimeTvLogin.setVisibility(View.VISIBLE);
            mimeIbtnPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_default));
        }
    }

    @Override
    @OnClick({R.id.mime_ibtn_setting, R.id.mime_ibtn_message, R.id.mime_ibtn_photo,
        R.id.mime_tv_login, R.id.mime_ll_money, R.id.mime_ll_collection, R.id.mime_ll_card,
        R.id.mime_ll_car,  R.id.mime_ll_about, R.id.mime_ll_record})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mime_ibtn_message:  //消息
                MessageActivity.navigation();
                break;
            case R.id.mime_ibtn_setting:  //设置
                SettingActivity.navigation();
                break;
            case R.id.mime_ibtn_photo:  //头像
                UserHeaderImageActivity.navigation();
                break;
            case R.id.mime_tv_login:  //登录
                LoginActivity.navigation(false);
                break;
            case R.id.mime_ll_about:  //关于
                AboutActivity.navigation();
                break;
            case R.id.mime_ll_car:  //车辆
                CarActivity.navigation();
                break;
            case R.id.mime_ll_card: //充电卡
                CardActivity.navigation(false);
                break;
            case R.id.mime_ll_money:  //余额
                MoneyActivity.navigation();
                break;
//            case R.id.mime_ll_pile: //建桩
//                PileActivity.navigation();
//                break;
            case R.id.mime_ll_collection: //我的收藏
                CollectionActivity.navigation();
                break;
            case R.id.mime_ll_record: //充电记录
                PayRecordActivity.navigation(null);
                break;
//            case R.id.mime_ll_bill: //发票
//                BillActivity.navigation();
//                break;
            default:
                break;
        }
    }

    @OnClick(R.id.btn_start_query)
    public void clickStartQuery() {
        ServiceUtil.startChargingQuery();
    }

    @OnClick(R.id.btn_stop_query)
    public void clickStopQuery() {
        ServiceUtil.stopChargingQuery();
    }

    /**
     * 如果昵称不为空，name设置为昵称
     * 如果昵称为空，name设置为手机号
     */
    private void setNickName(){
        if (LvTextUtil.isEmpty(AppInfosPreferences.get().getNick())){
            mimeTvName.setText(AppInfosPreferences.get().getUserName());
        }else {
            mimeTvName.setText(AppInfosPreferences.get().getNick());
        }
    }

    @Override
    public void showLoading(String msg) {
        showLoadingDialog();
    }

    @Override
    public void showNormal() {
        dismissLoadingDialog();
    }

    @Override
    public void requestFailed(String msg) {
        if (LvTextUtil.isEmpty(msg)) {
            showToast("网络异常");
        }else {
            showToast(msg);
        }
    }

    @Override
    public void getProfileSuccess(ProfileBean profileBean) {
        AppInfosPreferences.get().setNick(profileBean.nickname);
        AppInfosPreferences.get().setHeaderUrl(profileBean.header);
        setNickName();
        GlideLoader.getInstance().displayWithRound(_mActivity.getApplicationContext(),
                profileBean.header,
                mimeIbtnPhoto);
    }

    @Override
    public void getProfileFailed(String msg) {
        requestFailed(msg);
    }
}
