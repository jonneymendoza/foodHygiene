package com.richards.jonathan.foodhygieneratingsapp.model.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jonathan on 04/12/2017.
 */
public class EstablishmentRating {

    @SerializedName("FHRSID")
    private String id;

    @SerializedName("BusinessName")
    private String businessName;

    @SerializedName("RatingValue")
    private String ratingValue;

    public EstablishmentRating() {
    }

    public EstablishmentRating(String id, String businessName, String ratingValue) {
        this.id = id;
        this.businessName = businessName;
        this.ratingValue = ratingValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue = ratingValue;
    }
}
