package com.richards.jonathan.foodhygieneratingsapp.views;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.richards.jonathan.foodhygieneratingsapp.R;
import com.richards.jonathan.foodhygieneratingsapp.model.network.FoodHygieneController;
import com.richards.jonathan.foodhygieneratingsapp.model.network.contract.FoodHygieneRatingService;
import com.richards.jonathan.foodhygieneratingsapp.model.network.objects.ErrorCode;
import com.richards.jonathan.foodhygieneratingsapp.model.objects.LocalAuthority;
import com.richards.jonathan.foodhygieneratingsapp.presenters.RatingPresenter;
import com.richards.jonathan.foodhygieneratingsapp.presenters.contract.FoodHygeneRatingContract;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FoodHygeneRatingContract.ViewCallBacks, AdapterView.OnItemSelectedListener {

    private static final String PERCENTAGE_PREFIX = "%";
    private static final String PREFIX_ZERO = "0";

    @BindView(R.id.local_authority_spinner)
    Spinner localAuthoritySpinner;

    @BindView(R.id.rating_system_scotland_view)
    View scotlandRatingTable;

    @BindView(R.id.rating_system_other_view)
    View otherRatingTable;


    private FoodHygieneRatingService foodHygieneRatingService;

    /**
     * In future production i would use dependency injection to inject the implementation of a presenter here
     */
    private FoodHygeneRatingContract.Presenter ratingPresenter;

    private List<LocalAuthority> localAuthorityList;

    private String regionNameSelected;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        foodHygieneRatingService = new FoodHygieneController();
        ratingPresenter = new RatingPresenter(this, foodHygieneRatingService);
        ratingPresenter.getLocalAuthorities();
        localAuthoritySpinner.setOnItemSelectedListener(this);
        setupPorgressDialog();

        progressDialog.show();

    }

    private void setupPorgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
    }

    @Override
    public void updateLocalAuthoritiesView(List<LocalAuthority> localAuthorityList) {
        progressDialog.dismiss();
        this.localAuthorityList = localAuthorityList;
        String[] localAuthoritiesArray = new String[localAuthorityList.size()];
        for (int i = 0; i < localAuthoritiesArray.length; i++) {
            localAuthoritiesArray[i] = localAuthorityList.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                localAuthoritiesArray);
        localAuthoritySpinner.setAdapter(adapter);
    }

    /**
     * Note: Total percentage count is sometimes not 100% due to some establishments having there ratings
     * set to 'Awaiting Inspection'
     * <p>
     * Use case does not mention this. In a production enviorment this would be flagged and questioned with two proposed solutions:
     * <p>
     * 1. Ignore any establishments that are Awaiting Inspection as part of working out the percentage of each rating
     * 2. Add Awaiting Inspection in the UI .
     *
     * @param breakDownRatings
     */
    @Override
    public void updateBreakDownRatingsView(HashMap<String, Integer> breakDownRatings) {
        progressDialog.dismiss();
        if (regionNameSelected.equals(FoodHygeneRatingContract.REGION_NAME_SCOTLAND)) {
            setupScotlandRatingTable(breakDownRatings);
        } else {
            setupOtherRatingTable(breakDownRatings);
        }
    }

    /**
     * Because we make a network request at launch of this activity, if it fails to get the local authorities
     * We should in future have a Button on the UI to allow the user to retry. Or auto retry once
     * the user has data connection again(assuming this was the reason for the error)
     * <p>
     * currently, the user has to restart the app if it fails to get the local authority
     *
     * @param errorCode
     */
    @Override
    public void onErrorLocalAuthority(ErrorCode errorCode) {
        handleError(errorCode);
    }

    @Override
    public void onErrorBreakDownRatings(ErrorCode errorCode) {
        handleError(errorCode);
        scotlandRatingTable.setVisibility(View.GONE);
        otherRatingTable.setVisibility(View.GONE);
    }

    private void setupOtherRatingTable(HashMap<String, Integer> breakDownRatings) {
        scotlandRatingTable.setVisibility(View.GONE);
        otherRatingTable.setVisibility(View.VISIBLE);

        TextView fiveStar = otherRatingTable.findViewById(R.id.five_star_rating_value);
        TextView fourStar = otherRatingTable.findViewById(R.id.four_star_rating_value);
        TextView threeStar = otherRatingTable.findViewById(R.id.three_star_rating_value);
        TextView twoStar = otherRatingTable.findViewById(R.id.two_star_rating_value);
        TextView oneStar = otherRatingTable.findViewById(R.id.one_star_rating_value);
        TextView exempt = otherRatingTable.findViewById(R.id.exempt_rating_value);

        oneStar.setText(breakDownRatings.get("1") == null
                ? PREFIX_ZERO : breakDownRatings.get("1") + PERCENTAGE_PREFIX);
        twoStar.setText(breakDownRatings.get("2") == null
                ? PREFIX_ZERO : breakDownRatings.get("2") + PERCENTAGE_PREFIX);
        threeStar.setText(breakDownRatings.get("3") == null
                ? PREFIX_ZERO : breakDownRatings.get("3") + PERCENTAGE_PREFIX);
        fourStar.setText(breakDownRatings.get("4") == null
                ? PREFIX_ZERO : breakDownRatings.get("4") + PERCENTAGE_PREFIX);
        fiveStar.setText(breakDownRatings.get("5") == null
                ? PREFIX_ZERO : breakDownRatings.get("5") + PERCENTAGE_PREFIX);
        exempt.setText(breakDownRatings.get("Exempt") == null
                ? PREFIX_ZERO : breakDownRatings.get("Exempt") + PERCENTAGE_PREFIX);

    }

    private void setupScotlandRatingTable(HashMap<String, Integer> breakDownREatings) {
        scotlandRatingTable.setVisibility(View.VISIBLE);
        otherRatingTable.setVisibility(View.GONE);
        TextView passRating = scotlandRatingTable.findViewById(R.id.pass_rating_value);
        TextView needsImprovementRating = scotlandRatingTable.findViewById(R.id.need_improvement_value);
        passRating.setText(breakDownREatings.get(getString(R.string.pass)) + PERCENTAGE_PREFIX);
        needsImprovementRating.setText(breakDownREatings.get(getString(R.string.needs_improvement)) == null
                ? PREFIX_ZERO : breakDownREatings.get(getString(R.string.needs_improvement)) + PERCENTAGE_PREFIX);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LocalAuthority localAuthority = localAuthorityList.get(position);
        regionNameSelected = localAuthority.getRegionName();
        progressDialog.show();
        ratingPresenter.getBreakdownRatings(localAuthority.getId(), regionNameSelected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void handleError(ErrorCode errorCode) {
        progressDialog.dismiss();
        switch (errorCode) {
            case ERROR_OTHER:
                Toast.makeText(this, "Unkonown error has occured. Please try again", Toast.LENGTH_LONG).show();
                break;
            case ERROR_NO_CONNECTION:
                Toast.makeText(this, "No netowork connection, Please check your wifi or data connection", Toast.LENGTH_LONG).show();
                break;
        }
    }

}
