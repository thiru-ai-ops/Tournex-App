package com.tournex.travel;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.cardview.widget.CardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class BookingActivity extends BaseActivity {

    private LinearLayout layoutCurrentBookings, layoutUpcomingBookings, layoutBookingHistory;
    private TextView tvNoCurrent, tvNoUpcoming, tvNoHistory;
    private SharedPreferences sharedPreferences;
    private String currentUsername;
    private TabLayout tabLayoutBookings;
    private ChipGroup chipFilterGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        setupBottomNavigation(R.id.nav_bookings);  // Fixed: Changed from nav_budget to nav_bookings

        sharedPreferences = getSharedPreferences("TourNex", MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", "User");

        initViews();
        setupTabs();
        setupFilters();
        loadBookings();
    }

    private void initViews() {
        layoutCurrentBookings = findViewById(R.id.layoutCurrentBookings);
        layoutUpcomingBookings = findViewById(R.id.layoutUpcomingBookings);
        layoutBookingHistory = findViewById(R.id.layoutBookingHistory);
        tvNoCurrent = findViewById(R.id.tvNoCurrent);
        tvNoUpcoming = findViewById(R.id.tvNoUpcoming);
        tvNoHistory = findViewById(R.id.tvNoHistory);
        tabLayoutBookings = findViewById(R.id.tabLayoutBookings);
        chipFilterGroup = findViewById(R.id.chipFilterGroup);
    }

    private void setupTabs() {
        if (tabLayoutBookings != null) {
            tabLayoutBookings.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int position = tab.getPosition();
                    if (position == 0) {
                        scrollToSection(layoutCurrentBookings);
                    } else if (position == 1) {
                        scrollToSection(layoutUpcomingBookings);
                    } else {
                        scrollToSection(layoutBookingHistory);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
        }
    }

    private void setupFilters() {
        if (chipFilterGroup != null) {
            chipFilterGroup.setOnCheckedChangeListener((group, checkedId) -> {
                Chip chip = findViewById(checkedId);
                if (chip != null) {
                    filterBookingsByCategory(chip.getText().toString());
                }
            });
        }
    }

    private void filterBookingsByCategory(String category) {
        Toast.makeText(this, "Filter: " + category, Toast.LENGTH_SHORT).show();
    }

    private void scrollToSection(LinearLayout layout) {
        if (layout != null) {
            layout.requestFocusFromTouch();
            View scrollView = findViewById(R.id.scrollViewBookings);
            if (scrollView instanceof ScrollView) {
                ((ScrollView) scrollView).smoothScrollTo(0, layout.getTop());
            }
        }
    }

    private void loadBookings() {
        String bookingsStr = sharedPreferences.getString("bookings", "");

        List<Booking> currentBookings = new ArrayList<>();
        List<Booking> upcomingBookings = new ArrayList<>();
        List<Booking> historyBookings = new ArrayList<>();

        if (!bookingsStr.isEmpty()) {
            String[] bookings = bookingsStr.split(";");
            for (String booking : bookings) {
                if (!booking.isEmpty()) {
                    String[] details = booking.split("\\|");
                    if (details.length >= 6) {
                        Booking b = new Booking();
                        b.bookingId = details[0];
                        b.placeName = details[1];
                        b.amount = details[2];
                        b.date = details[3];
                        b.status = details[4];
                        b.username = details[5];

                        if (b.username.equals(currentUsername)) {
                            if (b.status.equals("Pending")) {
                                currentBookings.add(b);
                            } else if (b.status.equals("Confirmed")) {
                                upcomingBookings.add(b);
                            } else {
                                historyBookings.add(b);
                            }
                        }
                    }
                }
            }
        }

        if (currentBookings.isEmpty() && upcomingBookings.isEmpty() && historyBookings.isEmpty()) {
            addDemoBooking();
            loadBookings();
            return;
        }

        displayBookings(currentBookings, layoutCurrentBookings, tvNoCurrent);
        displayBookings(upcomingBookings, layoutUpcomingBookings, tvNoUpcoming);
        displayBookings(historyBookings, layoutBookingHistory, tvNoHistory);
    }

    private void addDemoBooking() {
        String demoBooking = "TNDEMO001|Ooty Hill Station|₹2,500|15/12/2024|Confirmed|" + currentUsername + ";";
        String existing = sharedPreferences.getString("bookings", "");
        sharedPreferences.edit().putString("bookings", existing + demoBooking).apply();
    }

    private void displayBookings(List<Booking> bookings, LinearLayout layout, TextView noDataText) {
        layout.removeAllViews();

        if (bookings.isEmpty()) {
            noDataText.setVisibility(View.VISIBLE);
            return;
        }

        noDataText.setVisibility(View.GONE);

        for (Booking booking : bookings) {
            View cardView = getLayoutInflater().inflate(R.layout.item_booking, null);

            TextView tvBookingId = cardView.findViewById(R.id.tvBookingId);
            TextView tvPlaceName = cardView.findViewById(R.id.tvPlaceName);
            TextView tvAmount = cardView.findViewById(R.id.tvAmount);
            TextView tvDate = cardView.findViewById(R.id.tvDate);
            TextView tvStatus = cardView.findViewById(R.id.tvStatus);
            Button btnCancel = cardView.findViewById(R.id.btnCancel);

            tvBookingId.setText("ID: " + booking.bookingId);
            tvPlaceName.setText(booking.placeName);
            tvAmount.setText(booking.amount);
            tvDate.setText("Booked on: " + booking.date);
            tvStatus.setText(booking.status);

            if (booking.status.equals("Confirmed")) {
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else if (booking.status.equals("Pending")) {
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            } else {
                tvStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }

            btnCancel.setOnClickListener(v -> cancelBooking(booking.bookingId));
            layout.addView(cardView);
        }
    }

    private void cancelBooking(String bookingId) {
        String bookingsStr = sharedPreferences.getString("bookings", "");
        String[] bookings = bookingsStr.split(";");
        StringBuilder newBookings = new StringBuilder();

        for (String booking : bookings) {
            if (!booking.isEmpty() && !booking.startsWith(bookingId)) {
                newBookings.append(booking).append(";");
            }
        }

        sharedPreferences.edit().putString("bookings", newBookings.toString()).apply();
        Toast.makeText(this, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
        loadBookings();
    }

    private class Booking {
        String bookingId;
        String placeName;
        String amount;
        String date;
        String status;
        String username;
    }
}