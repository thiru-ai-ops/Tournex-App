package com.tournex.travel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private SharedPreferences sharedPreferences;
    private TextView tvWelcome, tvUserName;
    private RecyclerView rvPopularTrips;
    private BottomNavigationView bottomNavigationView;
    private TripAdapter tripAdapter;
    private List<Trip> tripList;

    // Category Views
    private LinearLayout categoryAdventure, categoryRelax, categorySpiritual, categoryHeritage, categoryNature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigation(R.id.nav_home);

        sharedPreferences = getSharedPreferences("TourNex", MODE_PRIVATE);

        initViews();
        setupWelcomeMessage();
        setupPopularTrips();
        setupCategories();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserName = findViewById(R.id.tvUserName);
        rvPopularTrips = findViewById(R.id.rvPopularTrips);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Initialize category views
        categoryAdventure = findViewById(R.id.categoryAdventure);
        categoryRelax = findViewById(R.id.categoryRelax);
        categorySpiritual = findViewById(R.id.categorySpiritual);
        categoryHeritage = findViewById(R.id.categoryHeritage);
        categoryNature = findViewById(R.id.categoryNature);
    }

    private void setupWelcomeMessage() {
        String username = sharedPreferences.getString("username", "Explorer");
        tvUserName.setText(username);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour < 12) {
            greeting = "Good Morning";
        } else if (hour < 16) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }

        tvWelcome.setText(greeting + "!");
    }

    private void setupCategories() {
        // Adventure Category Click
        categoryAdventure.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MoodActivity.class);
            intent.putExtra("selected_mood", "Adventure");
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Relax Category Click
        categoryRelax.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MoodActivity.class);
            intent.putExtra("selected_mood", "Relax");
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Spiritual Category Click
        categorySpiritual.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MoodActivity.class);
            intent.putExtra("selected_mood", "Spiritual");
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Heritage Category Click
        categoryHeritage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MoodActivity.class);
            intent.putExtra("selected_mood", "Heritage");
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Nature Category Click
        categoryNature.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MoodActivity.class);
            intent.putExtra("selected_mood", "Nature");
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Add ripple effect animation
        addRippleEffect(categoryAdventure);
        addRippleEffect(categoryRelax);
        addRippleEffect(categorySpiritual);
        addRippleEffect(categoryHeritage);
        addRippleEffect(categoryNature);
    }

    private void addRippleEffect(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });
    }

    private void setupPopularTrips() {
        tripList = new ArrayList<>();

        tripList.add(new Trip(
                "Ooty - Queen of Hill Stations",
                "3 Days • 2 Nights",
                "Nilgiris, Tamil Nadu",
                4.5,
                R.drawable.ic_ooty_trip,
                new String[]{"Toy Train Ride", "Botanical Garden", "Ooty Lake", "Doddabetta Peak"},
                "₹4,999"
        ));

        tripList.add(new Trip(
                "Kodaikanal - Princess of Hills",
                "4 Days • 3 Nights",
                "Dindigul, Tamil Nadu",
                4.7,
                R.drawable.ic_kodaikanal_trip,
                new String[]{"Coaker's Walk", "Kodaikanal Lake", "Bryant Park", "Silver Cascade"},
                "₹5,999"
        ));

        tripList.add(new Trip(
                "Madurai - Temple City",
                "2 Days • 1 Night",
                "Madurai, Tamil Nadu",
                4.6,
                R.drawable.ic_madurai_trip,
                new String[]{"Meenakshi Temple", "Thirumalai Nayakkar Palace", "Gandhi Museum", "Vandiyur Mariamman Teppakulam"},
                "₹3,499"
        ));

        tripList.add(new Trip(
                "Rameswaram - Sacred Island",
                "2 Days • 1 Night",
                "Rameswaram, Tamil Nadu",
                4.8,
                R.drawable.ic_rameswaram_trip,
                new String[]{"Ramanathaswamy Temple", "Dhanushkodi", "Pamban Bridge", "Agni Theertham"},
                "₹3,999"
        ));

        tripList.add(new Trip(
                "Kanyakumari - Land's End",
                "2 Days • 1 Night",
                "Kanyakumari, Tamil Nadu",
                4.6,
                R.drawable.ic_kanyakumari_trip,
                new String[]{"Vivekananda Rock", "Thiruvalluvar Statue", "Sunset View", "Kumari Amman Temple"},
                "₹3,499"
        ));

        tripList.add(new Trip(
                "Mahabalipuram - Shore Temple",
                "1 Day",
                "Chennai, Tamil Nadu",
                4.4,
                R.drawable.ic_mahabalipuram_trip,
                new String[]{"Shore Temple", "Pancha Rathas", "Arjuna's Penance", "Krishna's Butterball"},
                "₹1,999"
        ));

        tripAdapter = new TripAdapter(tripList, this::onTripClicked);
        rvPopularTrips.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvPopularTrips.setAdapter(tripAdapter);
    }

    private void onTripClicked(Trip trip) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_trip_details, null);

        TextView tvTripName = dialogView.findViewById(R.id.tvTripName);
        TextView tvDuration = dialogView.findViewById(R.id.tvDuration);
        TextView tvLocation = dialogView.findViewById(R.id.tvLocation);
        TextView tvPrice = dialogView.findViewById(R.id.tvPrice);
        TextView tvHighlights = dialogView.findViewById(R.id.tvHighlights);
        com.google.android.material.button.MaterialButton btnBookNow = dialogView.findViewById(R.id.btnBookNow);

        tvTripName.setText(trip.name);
        tvDuration.setText(trip.duration);
        tvLocation.setText("📍 " + trip.location);
        tvPrice.setText(trip.price);

        StringBuilder highlights = new StringBuilder();
        for (String highlight : trip.highlights) {
            highlights.append("• ").append(highlight).append("\n");
        }
        tvHighlights.setText(highlights.toString());

        builder.setView(dialogView);
        builder.setCancelable(true);
        android.app.AlertDialog dialog = builder.create();

        btnBookNow.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
            intent.putExtra("place_name", trip.name);
            intent.putExtra("place_price", trip.price); // Now passing proper price like "₹4,999"
            startActivity(intent);
        });

        dialog.show();
    }

    // Trip Model Class
    public static class Trip {
        String name;
        String duration;
        String location;
        double rating;
        int imageRes;
        String[] highlights;
        String price;

        public Trip(String name, String duration, String location, double rating, int imageRes, String[] highlights, String price) {
            this.name = name;
            this.duration = duration;
            this.location = location;
            this.rating = rating;
            this.imageRes = imageRes;
            this.highlights = highlights;
            this.price = price;
        }

        public String getPrice() {
            return price;
        }
    }
}