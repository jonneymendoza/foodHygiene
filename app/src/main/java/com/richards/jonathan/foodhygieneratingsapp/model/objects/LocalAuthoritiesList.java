package com.richards.jonathan.foodhygieneratingsapp.model.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jonathan on 05/12/2017.
 */

public class LocalAuthoritiesList {

    @SerializedName("authorities")
    private List<LocalAuthority> localAuthorityList;

    public List<LocalAuthority> getLocalAuthorityList() {
        return localAuthorityList;
    }

    public void setLocalAuthorityList(List<LocalAuthority> localAuthorityList) {
        this.localAuthorityList = localAuthorityList;
    }
}
