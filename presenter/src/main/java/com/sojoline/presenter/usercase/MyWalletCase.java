package com.sojoline.presenter.usercase;

import com.sojoline.model.dagger.ApiComponentHolder;
import com.sojoline.model.response.MyWalletResponse;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/7/4.
 ********************************/
public class MyWalletCase extends UserCase<MyWalletResponse, Void> {

    @Override
    public Observable<MyWalletResponse> interactor(Void params) {
        return ApiComponentHolder.sApiComponent
            .apiService()
            .myWallet()
            .take(1)
            .compose(SchedulersCompat.<MyWalletResponse>applyNewSchedulers());
    }
}
