package com.sojoline.presenter.usercase;

import com.sojoline.model.dagger.ApiComponentHolder;
import com.sojoline.model.request.CardRequest;
import com.sojoline.model.response.SimpleResponse;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/4/11.
 ********************************/
public class UnbindCardCase extends UserCase<SimpleResponse, CardRequest> {

  @Override
  public Observable<SimpleResponse> interactor(CardRequest params) {
    return ApiComponentHolder.sApiComponent.apiService()
        .unbindCard(params)
        .take(1)
        .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers());
  }
}
