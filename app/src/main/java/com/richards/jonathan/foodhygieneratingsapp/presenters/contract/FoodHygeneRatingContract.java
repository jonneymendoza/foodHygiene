package com.richards.jonathan.foodhygieneratingsapp.presenters.contract;

import com.richards.jonathan.foodhygieneratingsapp.model.network.objects.ErrorCode;
import com.richards.jonathan.foodhygieneratingsapp.model.objects.LocalAuthority;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jonathan on 04/12/2017.
 */

public interface FoodHygeneRatingContract {

    String REGION_NAME_SCOTLAND = "Scotland";

    interface Presenter {
        void getLocalAuthorities();

        void getBreakdownRatings(int localAuthorityId, String regionName);
    }

    interface ViewCallBacks {
        void updateLocalAuthoritiesView(List<LocalAuthority> localAuthorityList);

        void onErrorLocalAuthority(ErrorCode errorCode);

        void updateBreakDownRatingsView(HashMap<String, Integer> breakDownREatings);

        void onErrorBreakDownRatings(ErrorCode errorCode);


    }
}
