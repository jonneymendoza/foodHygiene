package com.richards.jonathan.foodhygieneratingsapp.model.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jonathan on 04/12/2017.
 */

public class LocalAuthority {

    @SerializedName("LocalAuthorityId")
    private int id;

    @SerializedName("LocalAuthorityIdCode")
    private String code;

    @SerializedName("Name")
    private String name;

    @SerializedName("RegionName")
    private String regionName;

    public LocalAuthority(int id, String code, String name, String regionName) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.regionName = regionName;
    }

    public LocalAuthority() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
}
