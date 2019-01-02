package com.richards.jonathan.foodhygieneratingsapp.presenters;

import android.support.annotation.VisibleForTesting;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.richards.jonathan.foodhygieneratingsapp.model.network.contract.FoodHygieneRatingService;
import com.richards.jonathan.foodhygieneratingsapp.model.network.objects.ErrorCode;
import com.richards.jonathan.foodhygieneratingsapp.model.network.objects.NoNetworkException;
import com.richards.jonathan.foodhygieneratingsapp.model.objects.EstablishmentRating;
import com.richards.jonathan.foodhygieneratingsapp.model.objects.Establishments;
import com.richards.jonathan.foodhygieneratingsapp.model.objects.LocalAuthoritiesList;
import com.richards.jonathan.foodhygieneratingsapp.presenters.contract.FoodHygeneRatingContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jonathan on 04/12/2017.
 * <p>
 * FoodHygeneRatingContract logic class
 */
public class RatingPresenter implements FoodHygeneRatingContract.Presenter {

    private FoodHygeneRatingContract.ViewCallBacks viewPresenterCallBacks;

    private FoodHygieneRatingService foodHygieneRatingService;

    /**
     * Future use: Use Dependency Injection
     *
     * @param presenterCallBacks
     * @param foodHygieneRatingService
     */
    public RatingPresenter(FoodHygeneRatingContract.ViewCallBacks presenterCallBacks,
                           FoodHygieneRatingService foodHygieneRatingService) {
        viewPresenterCallBacks = presenterCallBacks;
        this.foodHygieneRatingService = foodHygieneRatingService;
    }

    @Override
    public void getLocalAuthorities() {
        foodHygieneRatingService.getLocalAuthorities()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<LocalAuthoritiesList>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(LocalAuthoritiesList localAuthorities) {
                        viewPresenterCallBacks.updateLocalAuthoritiesView(localAuthorities.getLocalAuthorityList());
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage().equals(NoNetworkException.NO_NETWORK_AVAILABLE)) {
                            viewPresenterCallBacks.onErrorLocalAuthority(ErrorCode.ERROR_NO_CONNECTION);
                        } else {
                            viewPresenterCallBacks.onErrorLocalAuthority(ErrorCode.ERROR_OTHER);
                        }
                    }
                });

    }

    @Override
    public void getBreakdownRatings(int localAuthorityId, String regionName) {
        foodHygieneRatingService.getEstablishments(localAuthorityId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<Establishments>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(Establishments establishmentRatingList) {
                        HashMap<String, Integer> breakdownRatings = breakDownRatings(establishmentRatingList.getEstablishments(), regionName);
                        viewPresenterCallBacks.updateBreakDownRatingsView(breakdownRatings);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage().equals(NoNetworkException.NO_NETWORK_AVAILABLE)) {
                            viewPresenterCallBacks.onErrorBreakDownRatings(ErrorCode.ERROR_NO_CONNECTION);
                        } else {
                            viewPresenterCallBacks.onErrorBreakDownRatings(ErrorCode.ERROR_OTHER);
                        }
                    }
                });
    }


    @VisibleForTesting
    protected HashMap<String, Integer> breakDownRatings(List<EstablishmentRating> establishmentRatingList, String regionName) {

        HashMap<String, Integer> map = new HashMap<>();

        Stream.of(establishmentRatingList)
                .map(establishment -> {
                    Integer count = map.get(establishment.getRatingValue());
                    map.put(establishment.getRatingValue(), (count == null) ? 1 : count + 1);
                    return map;
                })
                .flatMap(maps -> Stream.of(map.entrySet()))
                .map(entryMap -> {
                    double amountOfRatings = (double) entryMap.getValue() / establishmentRatingList.size() * 100;
                    map.put(entryMap.getKey(), (int) Math.round(amountOfRatings));
                    return map;
                });

//
//
//        //get total amount for each rating
//        for (EstablishmentRating establishment : establishmentRatingList) {
//            Integer count = map.get(establishment.getRatingValue());
//            map.put(establishment.getRatingValue(), (count == null) ? 1 : count + 1);
//        }
//
//        //convert the total amount to percentage
//        for (Map.Entry<String, Integer> entry : map.entrySet()) {
//            double amountOfRatings = (double) entry.getValue() / establishmentRatingList.size() * 100;
//            map.put(entry.getKey(), (int) Math.round(amountOfRatings));
//        }

        return map;
    }
}
