package com.richards.jonathan.foodhygieneratingsapp.presenters;

import com.richards.jonathan.foodhygieneratingsapp.model.network.FoodHygieneController;
import com.richards.jonathan.foodhygieneratingsapp.model.network.objects.ErrorCode;
import com.richards.jonathan.foodhygieneratingsapp.model.objects.EstablishmentRating;
import com.richards.jonathan.foodhygieneratingsapp.model.objects.Establishments;
import com.richards.jonathan.foodhygieneratingsapp.model.objects.LocalAuthoritiesList;
import com.richards.jonathan.foodhygieneratingsapp.model.objects.LocalAuthority;
import com.richards.jonathan.foodhygieneratingsapp.model.network.objects.NoNetworkException;
import com.richards.jonathan.foodhygieneratingsapp.presenters.contract.FoodHygeneRatingContract;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;

import static org.mockito.Mockito.times;

/**
 * Created by jonathan on 04/12/2017.
 *
 * unit tests that test the business logic of the presenter and checks that the views callbacks
 * are being invoked
 *
 * Future plan:
 *
 * Create UI unit tests to test mainly the appearance of the break down table ratings using espresso
 * or robojuice.
 *
 * Create more in depth testing on the network layer buy storing all template response json files in
 * Assets and then using that as our response source so we can test our parsing from Json to java class Object.
 * This can be done using a custom interceptor in retrofit.
 * With this approach, it means that it is possible to develop a requirement where the server API is not ready. We
 * Just need the json contract spec defined.
 *
 *
 */
public class TestPresenter {

    @Mock
    private FoodHygieneController foodHygieneController;

    @Mock
    private FoodHygeneRatingContract.ViewCallBacks viewCallBacks;

    /**
     * Setup the schedulars to run immediatly on main thread for testing as Schedulars.immediate()
     * is  no longer available in rxjava 2
     */
    @BeforeClass
    public static void setUpRxSchedulers() {
        Scheduler immediate = new Scheduler() {
            @Override
            public Disposable scheduleDirect( Runnable run, long delay, TimeUnit unit) {
                return super.scheduleDirect(run, 0, unit);
            }

            @Override
            public Scheduler.Worker createWorker() {
                return new ExecutorScheduler.ExecutorWorker(Runnable::run);
            }
        };

        RxJavaPlugins.setInitIoSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitComputationSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitNewThreadSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitSingleSchedulerHandler(scheduler -> immediate);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getLocalAuthorities(){
        LocalAuthoritiesList localAuthoritiesList = new LocalAuthoritiesList();
        localAuthoritiesList.setLocalAuthorityList(createLocalAuthoritiesList());
        Mockito.when(foodHygieneController.getLocalAuthorities()).thenReturn(Single.just(localAuthoritiesList));

        FoodHygeneRatingContract.Presenter presenter = new RatingPresenter(viewCallBacks,foodHygieneController);

        presenter.getLocalAuthorities();

        InOrder inOrder = Mockito.inOrder(viewCallBacks);

        inOrder.verify(viewCallBacks, times(1)).updateLocalAuthoritiesView(localAuthoritiesList.getLocalAuthorityList());
    }

    @Test
    public void getLocalAuthoritiesError(){

        NoNetworkException noNetworkException = new NoNetworkException();
        Mockito.when(foodHygieneController.getLocalAuthorities()).thenReturn(Single.error(noNetworkException));

        FoodHygeneRatingContract.Presenter presenter = new RatingPresenter(viewCallBacks,foodHygieneController);

        presenter.getLocalAuthorities();

        InOrder inOrder = Mockito.inOrder(viewCallBacks);

        inOrder.verify(viewCallBacks, times(1)).onErrorLocalAuthority(ErrorCode.ERROR_NO_CONNECTION);
    }

    @Test
    public void getBreakDownRatings() {
        Establishments establishments = new Establishments();
        establishments.setEstablishments(createEstablishmentList());

        Mockito.when(foodHygieneController.getEstablishments(123)).thenReturn(Single.just(establishments));

        RatingPresenter presenter = new RatingPresenter(viewCallBacks,foodHygieneController);
        presenter.getBreakdownRatings(123, "Epping");

        InOrder inOrder = Mockito.inOrder(viewCallBacks);

        inOrder.verify(viewCallBacks, times(1)).updateBreakDownRatingsView(presenter.breakDownRatings(establishments.getEstablishments(),"epping"));
    }

    @Test
    public void getBreakDownRatingsError() {
        NoNetworkException noNetworkException = new NoNetworkException();
        Mockito.when(foodHygieneController.getEstablishments(123)).thenReturn(Single.error(noNetworkException));

        FoodHygeneRatingContract.Presenter presenter = new RatingPresenter(viewCallBacks,foodHygieneController);

        presenter.getBreakdownRatings(123,"epping");

        InOrder inOrder = Mockito.inOrder(viewCallBacks);

        inOrder.verify(viewCallBacks, times(1)).onErrorBreakDownRatings(ErrorCode.ERROR_NO_CONNECTION);
    }

    @Test
    public void breakDownRatingsTest() {
        List<EstablishmentRating> establishmentList = createEstablishmentList();

        RatingPresenter presenter = new RatingPresenter(viewCallBacks,foodHygieneController);
        HashMap<String, Integer> breakDownRatings = presenter.breakDownRatings(establishmentList, "England");

        Assert.assertNotNull(breakDownRatings);

        //assert that the 5 star ratings is 33% of the overal
        Assert.assertTrue(breakDownRatings.get("5-star") == 33);

    }

    /**
     * Create mocked establishment list that has 33% of its overal ratings 5 star.
     *
     * @return
     */
    private List<EstablishmentRating> createEstablishmentList() {
        List<EstablishmentRating> establishmentRatingList = new ArrayList<>();

        //Make it so that 33% of the overal ratings are 5 stars
        establishmentRatingList.add(new EstablishmentRating("id1", "name1", "5-star"));
        establishmentRatingList.add(new EstablishmentRating("id2", "name2", "5-star"));
        establishmentRatingList.add(new EstablishmentRating("id3", "name3", "5-star"));
        establishmentRatingList.add(new EstablishmentRating("id4", "name4", "5-star"));
        establishmentRatingList.add(new EstablishmentRating("id5", "name5", "5-star"));

        establishmentRatingList.add(new EstablishmentRating("id6", "name6", "4-star"));
        establishmentRatingList.add(new EstablishmentRating("id7", "name7", "4-star"));
        establishmentRatingList.add(new EstablishmentRating("id8", "name8", "4-star"));
        establishmentRatingList.add(new EstablishmentRating("id9", "name9", "4-star"));

        establishmentRatingList.add(new EstablishmentRating("id10", "name10", "3-star"));
        establishmentRatingList.add(new EstablishmentRating("id11", "name11", "3-star"));
        establishmentRatingList.add(new EstablishmentRating("id12", "name12", "3-star"));

        establishmentRatingList.add(new EstablishmentRating("id13", "name13", "2-star"));

        establishmentRatingList.add(new EstablishmentRating("id14", "name14", "1-star"));
        establishmentRatingList.add(new EstablishmentRating("id15", "name15", "1-star"));

        return establishmentRatingList;

    }

    private List<LocalAuthority> createLocalAuthoritiesList(){
        List<LocalAuthority> authorityList = new ArrayList<>();

        authorityList.add(new LocalAuthority(1, "code1", "Aberdeen", "Scotland"));
        authorityList.add(new LocalAuthority(2, "code1", "Dundee", "Scotland"));
        authorityList.add(new LocalAuthority(3, "code1", "Epping", "England"));
        authorityList.add(new LocalAuthority(4, "code1", "Midlands", "England"));
        authorityList.add(new LocalAuthority(5, "code1", "Devon", "England"));
        authorityList.add(new LocalAuthority(6, "code1", "London", "England"));

        return authorityList;
    }
}
