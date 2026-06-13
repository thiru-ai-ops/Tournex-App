package com.tournex.travel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.*;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.NumberFormat;
import java.util.*;

public class BudgetPlannerActivity extends BaseActivity {

    private TextInputLayout tilFrom, tilTo;
    private TextInputEditText etFrom, etTo, etManualDays, etBudget;
    private TextView tvDistance, tvTravelDays, tvEstimatedCost, tvTotalBudget, tvCarbonSaved;
    private MaterialButton btnCalculate, btnSaveBudget, btnShareBudget;
    private CardView cardResult, cardTips;
    private Spinner spinnerTransport, spinnerTravelers;
    private Slider sliderBudget;
    private RangeSlider sliderDaysRange;
    private SwitchMaterial switchEcoMode;
    private ProgressBar progressBar;
    private LinearLayout layoutBreakdown;
    private View overlayView;

    private HashMap<String, HashMap<String, Integer>> distanceData = new HashMap<>();
    private SharedPreferences sharedPreferences;
    private NumberFormat currencyFormat;
    private boolean isEcoMode = false;
    private static final int REQUEST_FROM_LOCATION = 1001;
    private static final int REQUEST_TO_LOCATION = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_planner);
        setupBottomNavigation(R.id.nav_budget);

        sharedPreferences = getSharedPreferences("TourNex", MODE_PRIVATE);
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        initViews();
        initDistanceData();
        setupAnimations();
    }

    private void initViews() {
        tilFrom = findViewById(R.id.tilFrom);
        tilTo = findViewById(R.id.tilTo);
        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);
        etManualDays = findViewById(R.id.etManualDays);
        etBudget = findViewById(R.id.etBudget);
        tvDistance = findViewById(R.id.tvDistance);
        tvTravelDays = findViewById(R.id.tvTravelDays);
        tvEstimatedCost = findViewById(R.id.tvEstimatedCost);
        tvTotalBudget = findViewById(R.id.tvTotalBudget);
        tvCarbonSaved = findViewById(R.id.tvCarbonSaved);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnSaveBudget = findViewById(R.id.btnSaveBudget);
        btnShareBudget = findViewById(R.id.btnShareBudget);
        cardResult = findViewById(R.id.cardResult);
        cardTips = findViewById(R.id.cardTips);
        spinnerTransport = findViewById(R.id.spinnerTransport);
        spinnerTravelers = findViewById(R.id.spinnerTravelers);
        sliderBudget = findViewById(R.id.sliderBudget);
        sliderDaysRange = findViewById(R.id.sliderDaysRange);
        switchEcoMode = findViewById(R.id.switchEcoMode);
        progressBar = findViewById(R.id.progressBar);
        layoutBreakdown = findViewById(R.id.layoutBreakdown);
        overlayView = findViewById(R.id.overlayView);

        // Setup travelers spinner
        ArrayAdapter<String> travelersAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"1 Traveler", "2 Travelers", "3 Travelers", "4 Travelers", "5+ Travelers"});
        spinnerTravelers.setAdapter(travelersAdapter);

        // Setup budget slider
        sliderBudget.setValueTo(50000);
        sliderBudget.setValue(10000);
        sliderBudget.addOnChangeListener((slider, value, fromUser) -> {
            etBudget.setText(String.valueOf((int) value));
        });

        // Setup days range slider
        sliderDaysRange.setValues(1f, 7f);
        sliderDaysRange.setValueFrom(1f);
        sliderDaysRange.setValueTo(30f);

        // Eco mode switch
        switchEcoMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isEcoMode = isChecked;
            tvCarbonSaved.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Make from and to fields clickable
        etFrom.setFocusable(false);
        etFrom.setClickable(true);
        etTo.setFocusable(false);
        etTo.setClickable(true);

        etFrom.setOnClickListener(v -> openLocationSelection("from", etFrom.getText().toString()));
        etTo.setOnClickListener(v -> openLocationSelection("to", etTo.getText().toString()));

        cardResult.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        cardTips.setVisibility(View.GONE);
    }

    private void openLocationSelection(String type, String current) {
        Intent intent = new Intent(this, LocationSelectionActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("current", current);
        if (type.equals("from")) {
            startActivityForResult(intent, REQUEST_FROM_LOCATION);
        } else {
            startActivityForResult(intent, REQUEST_TO_LOCATION);
        }
    }

    private void setupAnimations() {
        btnCalculate.setOnClickListener(v -> {
            animateButton(v);
            calculateTrip();
        });

        btnSaveBudget.setOnClickListener(v -> {
            animateButton(v);
            saveBudget();
        });

        btnShareBudget.setOnClickListener(v -> shareBudget());
    }

    private void animateButton(View button) {
        button.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> button.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start())
                .start();
    }

    private void initDistanceData() {
        String[] cities = {"Chennai", "Coimbatore", "Madurai", "Ooty", "Kanyakumari",
                "Rameswaram", "Tiruchirappalli", "Kodaikanal", "Mahabalipuram", "Thanjavur"};

        for (String city : cities) {
            distanceData.put(city, new HashMap<>());
        }

        // Chennai distances
        distanceData.get("Chennai").put("Coimbatore", 510);
        distanceData.get("Chennai").put("Madurai", 460);
        distanceData.get("Chennai").put("Ooty", 550);
        distanceData.get("Chennai").put("Kanyakumari", 700);
        distanceData.get("Chennai").put("Rameswaram", 580);
        distanceData.get("Chennai").put("Tiruchirappalli", 330);
        distanceData.get("Chennai").put("Kodaikanal", 520);
        distanceData.get("Chennai").put("Mahabalipuram", 60);
        distanceData.get("Chennai").put("Thanjavur", 350);

        // Coimbatore distances
        distanceData.get("Coimbatore").put("Chennai", 510);
        distanceData.get("Coimbatore").put("Madurai", 210);
        distanceData.get("Coimbatore").put("Ooty", 86);
        distanceData.get("Coimbatore").put("Kanyakumari", 420);
        distanceData.get("Coimbatore").put("Rameswaram", 310);
        distanceData.get("Coimbatore").put("Tiruchirappalli", 200);
        distanceData.get("Coimbatore").put("Kodaikanal", 180);

        // Madurai distances
        distanceData.get("Madurai").put("Chennai", 460);
        distanceData.get("Madurai").put("Coimbatore", 210);
        distanceData.get("Madurai").put("Ooty", 270);
        distanceData.get("Madurai").put("Kanyakumari", 240);
        distanceData.get("Madurai").put("Rameswaram", 170);
        distanceData.get("Madurai").put("Tiruchirappalli", 140);
        distanceData.get("Madurai").put("Kodaikanal", 120);

        // Ooty distances
        distanceData.get("Ooty").put("Chennai", 550);
        distanceData.get("Ooty").put("Coimbatore", 86);
        distanceData.get("Ooty").put("Madurai", 270);
        distanceData.get("Ooty").put("Kanyakumari", 490);
        distanceData.get("Ooty").put("Kodaikanal", 200);

        // Kanyakumari distances
        distanceData.get("Kanyakumari").put("Chennai", 700);
        distanceData.get("Kanyakumari").put("Madurai", 240);
        distanceData.get("Kanyakumari").put("Rameswaram", 300);

        // Rameswaram distances
        distanceData.get("Rameswaram").put("Chennai", 580);
        distanceData.get("Rameswaram").put("Madurai", 170);
        distanceData.get("Rameswaram").put("Kanyakumari", 300);

        // Kodaikanal distances
        distanceData.get("Kodaikanal").put("Chennai", 520);
        distanceData.get("Kodaikanal").put("Madurai", 120);
        distanceData.get("Kodaikanal").put("Coimbatore", 180);
        distanceData.get("Kodaikanal").put("Ooty", 200);
    }

    private void calculateTrip() {
        String from = etFrom.getText().toString().trim();
        String to = etTo.getText().toString().trim();
        String budgetStr = etBudget.getText().toString().trim();
        String manualDaysStr = etManualDays.getText().toString().trim();

        if (from.isEmpty() || to.isEmpty()) {
            Toast.makeText(this, "Please enter source and destination", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            int distance = getDistance(from, to);
            if (distance == 0) {
                progressBar.setVisibility(View.GONE);
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Oops!")
                        .setMessage("Distance information not available for these cities.\nTry: Chennai to Madurai, Ooty to Coimbatore, etc.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            displayResults(distance, manualDaysStr, budgetStr);
            progressBar.setVisibility(View.GONE);
        }, 500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String location = data.getStringExtra("selected_location");
            if (requestCode == REQUEST_FROM_LOCATION) {
                etFrom.setText(location);
            } else if (requestCode == REQUEST_TO_LOCATION) {
                etTo.setText(location);
            }
        }
    }

    private void displayResults(int distance, String manualDaysStr, String budgetStr) {
        int travelers = 1;
        String travelersText = spinnerTravelers.getSelectedItem().toString();
        if (travelersText.contains("2")) travelers = 2;
        else if (travelersText.contains("3")) travelers = 3;
        else if (travelersText.contains("4")) travelers = 4;
        else if (travelersText.contains("5+")) travelers = 6;

        int travelDays;
        if (!manualDaysStr.isEmpty()) {
            travelDays = Integer.parseInt(manualDaysStr);
            tvTravelDays.setText("📅 " + travelDays + " days (Manual)");
        } else {
            List<Float> range = sliderDaysRange.getValues();
            travelDays = Math.max(1, (int) Math.ceil(range.get(1)));
            tvTravelDays.setText("📅 " + travelDays + " days (Recommended)");
        }

        tvDistance.setText("📍 Distance: " + distance + " KM");

        String transport = spinnerTransport.getSelectedItem().toString();
        int costPerKm;
        String transportIcon;

        if (transport.contains("Bus")) {
            costPerKm = 2;
            transportIcon = "🚌";
        } else if (transport.contains("Train")) {
            costPerKm = 1;
            transportIcon = "🚂";
        } else if (transport.contains("Car")) {
            costPerKm = 8;
            transportIcon = "🚗";
        } else {
            costPerKm = 12;
            transportIcon = "✈️";
        }

        float ecoMultiplier = isEcoMode ? 0.85f : 1f;
        int transportCost = (int)(distance * costPerKm * ecoMultiplier * travelers);
        int stayCost = (int)(travelDays * 1500 * ecoMultiplier * travelers);
        int foodCost = (int)(travelDays * 800 * ecoMultiplier * travelers);
        int sightseeingCost = (int)(travelDays * 1000 * travelers);

        int carbonSaved = isEcoMode ? (int)(distance * 0.5) : 0;
        tvCarbonSaved.setText("🌱 Carbon saved: " + carbonSaved + " kg CO₂");

        int totalCost = transportCost + stayCost + foodCost + sightseeingCost;

        displayCostBreakdown(transportCost, stayCost, foodCost, sightseeingCost, transportIcon);

        tvEstimatedCost.setText("₹" + String.format("%,d", totalCost));

        if (!budgetStr.isEmpty()) {
            int userBudget = Integer.parseInt(budgetStr);
            if (userBudget >= totalCost) {
                int remaining = userBudget - totalCost;
                tvTotalBudget.setText("✅ Within Budget! Remaining: ₹" + String.format("%,d", remaining));
                tvTotalBudget.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            } else {
                int deficit = totalCost - userBudget;
                tvTotalBudget.setText("⚠️ Over Budget by ₹" + String.format("%,d", deficit));
                tvTotalBudget.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                showSavingTips(deficit);
            }
        }

        cardResult.setVisibility(View.VISIBLE);
        cardTips.setVisibility(View.VISIBLE);

        cardResult.setAlpha(0f);
        cardResult.animate()
                .alpha(1f)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void displayCostBreakdown(int transport, int stay, int food, int sightseeing, String transportIcon) {
        layoutBreakdown.removeAllViews();
        addBreakdownItem("🚗 " + transportIcon + " Transport", transport);
        addBreakdownItem("🏨 Accommodation", stay);
        addBreakdownItem("🍽️ Food & Meals", food);
        addBreakdownItem("🎯 Sightseeing", sightseeing);
    }

    private void addBreakdownItem(String title, int amount) {
        View item = getLayoutInflater().inflate(R.layout.item_budget_breakdown, null);
        TextView tvTitle = item.findViewById(R.id.tvBreakdownTitle);
        TextView tvAmount = item.findViewById(R.id.tvBreakdownAmount);

        tvTitle.setText(title);
        tvAmount.setText("₹" + String.format("%,d", amount));

        layoutBreakdown.addView(item);
    }

    private void showSavingTips(int deficit) {
        String[] tips = {
                "💡 Take public transport instead of taxis",
                "💡 Book accommodations in advance for better deals",
                "💡 Travel during off-peak season",
                "💡 Use local food joints instead of luxury restaurants",
                "💡 Consider group travel to share costs"
        };

        TextView tvTips = findViewById(R.id.tvSavingTips);
        tvTips.setText("💪 Saving Tips:\n• " + String.join("\n• ", tips));
        tvTips.setVisibility(View.VISIBLE);
    }

    private int getDistance(String from, String to) {
        if (distanceData.containsKey(from) && distanceData.get(from).containsKey(to)) {
            return distanceData.get(from).get(to);
        }
        if (distanceData.containsKey(to) && distanceData.get(to).containsKey(from)) {
            return distanceData.get(to).get(from);
        }
        return 0;
    }

    private void saveBudget() {
        String from = etFrom.getText().toString().trim();
        String to = etTo.getText().toString().trim();

        if (from.isEmpty() || to.isEmpty()) {
            Toast.makeText(this, "Calculate a trip first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to SharedPreferences
        String savedBudgets = sharedPreferences.getString("saved_budgets", "");
        String newBudget = System.currentTimeMillis() + "|" + from + " to " + to + "|" + tvEstimatedCost.getText().toString() + "\n";
        sharedPreferences.edit().putString("saved_budgets", savedBudgets + newBudget).apply();

        overlayView.setVisibility(View.VISIBLE);
        overlayView.setAlpha(0f);
        overlayView.animate().alpha(1f).setDuration(300).withEndAction(() -> {
            new Handler().postDelayed(() -> {
                overlayView.animate().alpha(0f).setDuration(300).withEndAction(() ->
                        overlayView.setVisibility(View.GONE)).start();
            }, 1500);
        });

        Toast.makeText(this, "✓ Budget plan saved successfully!", Toast.LENGTH_SHORT).show();
    }

    private void shareBudget() {
        String from = etFrom.getText().toString().trim();
        String to = etTo.getText().toString().trim();
        String cost = tvEstimatedCost.getText().toString();

        String shareText = "🌴 TourNex Trip Plan 🌴\n\n" +
                "From: " + from + "\n" +
                "To: " + to + "\n" +
                "Distance: " + tvDistance.getText().toString() + "\n" +
                "Travel Days: " + tvTravelDays.getText().toString() + "\n" +
                "Estimated Cost: " + cost + "\n\n" +
                "Plan your Tamil Nadu trip with TourNex!";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share Trip Plan"));
    }
}