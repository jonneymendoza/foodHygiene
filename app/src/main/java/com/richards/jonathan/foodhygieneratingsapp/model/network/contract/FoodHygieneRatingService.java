package com.richards.jonathan.foodhygieneratingsapp.model.network.contract;

import com.richards.jonathan.foodhygieneratingsapp.model.objects.Establishments;
import com.richards.jonathan.foodhygieneratingsapp.model.objects.LocalAuthoritiesList;


import io.reactivex.Single;

/**
 * Created by jonathan on 04/12/2017.
 */

public interface FoodHygieneRatingService {

    Single<LocalAuthoritiesList> getLocalAuthorities();

    Single<Establishments> getEstablishments(int localAuthorityId);
}
