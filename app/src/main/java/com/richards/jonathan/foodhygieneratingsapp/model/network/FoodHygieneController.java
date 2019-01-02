package com.richards.jonathan.foodhygieneratingsapp.model.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.richards.jonathan.foodhygieneratingsapp.FoodHygieneRatingApp;
import com.richards.jonathan.foodhygieneratingsapp.model.network.contract.FoodHygieneRatingService;
import com.richards.jonathan.foodhygieneratingsapp.model.network.contract.FoodHygieneServiceCalls;
import com.richards.jonathan.foodhygieneratingsapp.model.objects.Establishments;

import com.richards.jonathan.foodhygieneratingsapp.model.network.objects.NoNetworkException;
import com.richards.jonathan.foodhygieneratingsapp.model.objects.LocalAuthoritiesList;

import io.reactivex.Single;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jonathan on 04/12/2017.
 */

public class FoodHygieneController implements FoodHygieneRatingService {

    private static final String BASE_URL = "http://api.ratings.food.gov.uk/";

    private static final String API_VERSION_HEADER_KEY = "x-api-version";

    private static final String API_VERSION_HEADER_VALUE = "2";

    /*
     Set one page and max 1000 items to retrieve list of establishments as some local authorities
     contain a large list that cannot fit in one page response. 1000 items was chosen as it represents
     more variation in the results. Tested results up to 500 and many establishments first 500 items that get returned are 5 stars or pass rated


     Here is my solution to getting all establishments in a live app.

     Find out how many establishments there is in a local authority and divide it into page numbers
     and sizes. eg: total establishments = 100, divide into 4 pages equalling to 25 items per page and use an observer
     to poll each page and update the table dynamically on each page.

     We can then cache and save these rating details in the phone storage so a user does not have to poll the data each time but instead,
     give them the option to update the data in the UI.

     note:      Api call establishments/basic does not give us the ability to filter the results based
     on local authority id. This call would be ideal to use as we do not need full details of each establishment.
     Just the name and ratings. Ideally  i would make a suggestion to the server side team to make the API more flexible to specify what data
     a client wants to be returned to make the response lighter.
     */
    private static final int PAGE_NUMBER_LIMIT = 1;

    private static final int PAGE_SIZE = 1000;

    private Interceptor interceptor = chain -> {
        if (isNetworkActive()) {
            Request request = chain.request();
            Request.Builder newRequest = request.newBuilder().addHeader(API_VERSION_HEADER_KEY, API_VERSION_HEADER_VALUE);
            return chain.proceed(newRequest.build());
        } else {
            throw new NoNetworkException();
        }
    };

    @Override
    public Single<LocalAuthoritiesList> getLocalAuthorities() {
        return apiCall().getLocalAuthorities();
    }

    private FoodHygieneServiceCalls apiCall() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();

        FoodHygieneServiceCalls foodHygieneServiceCalls = retrofit.create(FoodHygieneServiceCalls.class);
        return foodHygieneServiceCalls;
    }

    @Override
    public Single<Establishments> getEstablishments(int localAuthorityId) {
        return apiCall().getEstablishmentRatings(localAuthorityId, PAGE_NUMBER_LIMIT, PAGE_SIZE);
    }

    private boolean isNetworkActive() {
        ConnectivityManager cm =
                (ConnectivityManager) FoodHygieneRatingApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

}
