package com.sojoline.model.response;

import com.google.gson.annotations.SerializedName;
import com.sojoline.model.bean.StationInfoBean;

/********************************
 * Created by lvshicheng on 2017/4/25.
 ********************************/
public class StationInfoResponse extends BaseResponse {

  @SerializedName("content")
  public StationInfoBean stationInfo;
}
