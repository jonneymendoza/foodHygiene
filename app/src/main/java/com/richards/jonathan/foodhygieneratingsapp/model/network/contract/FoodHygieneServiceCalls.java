package com.richards.jonathan.foodhygieneratingsapp.model.network.contract;

import com.richards.jonathan.foodhygieneratingsapp.model.objects.Establishments;
import com.richards.jonathan.foodhygieneratingsapp.model.objects.LocalAuthoritiesList;


import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by jonathan on 04/12/2017.
 */

public interface FoodHygieneServiceCalls {

    String LOCAL_AUTHORITY_ID_KEY = "localAuthorityId";

    String PAGE_NUMBER_KEY = "pageNumber";

    String PAGE_SIZE_KEY = "pageSize";

    @GET("Authorities")
    Single<LocalAuthoritiesList> getLocalAuthorities();

    @GET("Establishments")
    Single<Establishments> getEstablishmentRatings (
      @Query(LOCAL_AUTHORITY_ID_KEY) int localAuthorityId,
      @Query(PAGE_NUMBER_KEY) int pageNumber,
      @Query(PAGE_SIZE_KEY) int pageSize
    );
}
