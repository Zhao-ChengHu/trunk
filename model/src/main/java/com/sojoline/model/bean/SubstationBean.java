package com.sojoline.model.bean;

import java.util.Locale;

import cn.com.leanvision.baseframe.util.LvTextUtil;

/********************************
 * Created by lvshicheng on 2017/3/8.
 ********************************/
public class SubstationBean {

  public transient String distance; // 距离当前位置

  public String updateTime;
 //  public String name;
  public String areaName;
  // public String substationId;
  public String areaId;
  public String telenum;
  public String parkingFeeTplId;
  public String createTime;
  public String companyId;
  public String companyName;
  public String remark;
  public String chargingFeeTplId;
  public String email;
  public String s_id;
  public String disable;
  public String stationType;
  public String b_actived;
  public String joinTime;
  public String serviceCall;
  public String serviceTime;
  public String servicer;
  public String id;
  public String address;
  public String longitude;
  public String latitude;
  public int hasRestDC;
  public int hasTotalDC;
  public int hasRestAC;
  public int hasTotalAC;
  public int     hasRest;  // 某个子站处于rest状态的充电桩数量
  public String  hasRestMIX;
  public boolean isFavorites;
  /*public String serviceCall;
  public String serviceTime;
  public String address;
  public boolean isFavorites;
  public String updateTime;
   public String remark;
   public int hasRest;
   public String companyName;
   public String companyCode;
   public String hasRestMIX;
   public int hasTotalDC;
   public int hasRestDC;
   public String areaId;
   public String hasRestAC;
   public String createTime;
   public int   hasTotalAC;

   public String hasRatedPower;
   public String hasTotal;
   public String templateId;
   public String hasTotalMIX;
   public String joinTime;
   public String id;
   public String areaName;*/
  public String areaLatitude;
  public String areaLongitude;

  public double getLat() {
    if (LvTextUtil.isEmpty(areaLatitude)) {
      areaLatitude = "40.071857";
    }
    return Double.parseDouble(areaLatitude);
  }

  public double getLng() {
    if (LvTextUtil.isEmpty(areaLongitude)) {
      areaLongitude = "116.360286";
    }
    return Double.parseDouble(areaLongitude);
  }

   /* public double getLat() {
        if (LvTextUtil.isEmpty(latitude)) {
            latitude = "40.071857";
        }
        return Double.parseDouble(latitude);
    }

    public double getLng() {
        if (LvTextUtil.isEmpty(longitude)) {
            longitude = "116.360286";
        }
        return Double.parseDouble(longitude);
    }*/

  public void setDistance(float distance) {
    if (distance < 1000) {
      this.distance = String.format(Locale.getDefault(), "%.1f米", distance);
    } else {
      this.distance = String.format(Locale.getDefault(), "%.1f千米", distance / 1000.0f);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SubstationBean that = (SubstationBean) o;

    return id != null ? id.equals(that.id) : that.id == null;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
