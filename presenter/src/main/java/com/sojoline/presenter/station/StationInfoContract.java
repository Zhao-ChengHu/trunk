package com.sojoline.presenter.station;

import com.sojoline.model.bean.StationInfoBean;
import com.sojoline.model.request.StartChargingRequest;
import com.sojoline.model.response.ChargingResponse;
import com.sojoline.presenter.LvIBasePresenter;
import com.sojoline.presenter.LvIBaseView;

/********************************
 * Created by lvshicheng on 2017/4/21.
 ********************************/
public interface StationInfoContract {

  interface View extends LvIBaseView {

    void queryStationInfoSuccess(StationInfoBean mStationInfoBean);

    void queryStationInfoFailed(String msg);

    void callBack(ChargingResponse.Charging charging);

    void startChargingFailed(String msg);
  }

  interface Presenter<R> extends LvIBasePresenter<R> {

    void queryStation(String qrCode);

    void startCharging(StartChargingRequest request);

    boolean isViewActive();

    R getView();
  }
}
