package com.sojoline.model.response;

import com.google.gson.annotations.SerializedName;

/********************************
 * Created by zhaochenghu on 2018/09/11.
 ********************************/
public class AliOrderResponse extends BaseResponse {

    public int errcode = -1;
    public String order_string;

    @Override
    public boolean isSuccess() {
        return errcode == 0;
    }
}
