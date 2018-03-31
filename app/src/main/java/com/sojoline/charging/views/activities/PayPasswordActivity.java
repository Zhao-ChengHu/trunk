package com.sojoline.charging.views.activities;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sojoline.charging.R;
import com.sojoline.charging.custom.OnPayPasswordListener;
import com.sojoline.charging.custom.PsdInputView;
import com.sojoline.charging.event.GetDataEvent;
import com.sojoline.charging.views.base.LvBaseAppCompatActivity;
import com.sojoline.model.dagger.ApiComponentHolder;
import com.sojoline.model.request.PayPwdRequest;
import com.sojoline.model.response.SimpleResponse;

import butterknife.BindView;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;

/**
 * Created by Administrator on 2017/3/2 0002.
 */
@Route(path = "/mime/pay_password")
public class PayPasswordActivity extends LvBaseAppCompatActivity {

  @BindView(R.id.payset_psdinput)
  PsdInputView psdInput;

  public static void navigation() {
    ARouter.getInstance().build("/mime/pay_password").navigation();
  }

  @Override
  protected void setContentView(Bundle savedInstanceState) {
    setContentView(R.layout.aty_paysetting_psd);
  }

  @Override
  protected void initView() {
    initToolbarNav("输入支付密码");

    psdInput.setOnPayPasswordListener(new OnPayPasswordListener() {
      @Override
      public void onPasswordInputFinish(String psd) {
		  checkPayPwd(psd);
      }

      @Override
      public void onForgetPassword() {
        PaySettingVerify.navigation();

      }
    });

  }
	/**
	 * 检查旧密码
	 */
	private void checkPayPwd(final String pwd) {
		showLoadingDialog();
		ApiComponentHolder.sApiComponent.apiService()
				.checkPayPwd(new PayPwdRequest(pwd))
				.take(1)
				.compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
				.subscribe(new SimpleSubscriber<SimpleResponse>() {
					@Override
					public void onError(Throwable e) {
						super.onError(e);
						dismissLoadingDialog();
						psdInput.clearAllPsd();
						showToast(R.string.network_not_available);
					}

					@Override
					public void onNext(SimpleResponse response) {
						dismissLoadingDialog();
						psdInput.clearAllPsd();
						if (response.isSuccess()) {
							RxBus.getInstance().postEvent(new GetDataEvent("true"));
							finish();
						} else {
							showToast(response.msg);
						}
					}
				});
	}
}
