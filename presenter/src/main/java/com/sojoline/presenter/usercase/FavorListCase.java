package com.sojoline.presenter.usercase;

import com.sojoline.model.dagger.ApiComponentHolder;
import com.sojoline.model.response.SubstationsResponse;

import java.util.HashMap;

import cn.com.leanvision.baseframe.model.usercase.UserCase;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/4/11.
 ********************************/
public class FavorListCase extends UserCase<SubstationsResponse, HashMap<String, Object>> {

  @Override
  public Observable<SubstationsResponse> interactor(HashMap<String, Object> params) {
    return ApiComponentHolder.sApiComponent.apiService()
        .getFavorList(params)
        .take(1)
        .compose(SchedulersCompat.<SubstationsResponse>applyNewSchedulers());
  }
}
