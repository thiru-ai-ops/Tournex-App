package com.tournex.travel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationSelectionActivity extends AppCompatActivity {

    private TextInputEditText etSearch;
    private RecyclerView rvLocations;
    private ImageView ivBack, ivClear;
    private TextView tvSelectedType, tvRecentTitle;
    private MaterialButton btnConfirm;
    private LinearLayout layoutRecent;

    private LocationAdapter adapter;
    private List<LocationItem> allLocations;
    private List<LocationItem> recentLocations;

    private String selectionType; // "from" or "to"
    private String currentSelection = "";

    // Popular Tamil Nadu locations
    private List<LocationItem> popularLocations = Arrays.asList(
            new LocationItem("Chennai", "Capital City", "🏙️", 4.5),
            new LocationItem("Coimbatore", "Manchester of South India", "🏭", 4.3),
            new LocationItem("Madurai", "Temple City", "🛕", 4.7),
            new LocationItem("Ooty", "Queen of Hill Stations", "⛰️", 4.8),
            new LocationItem("Kanyakumari", "Land's End", "🌊", 4.6),
            new LocationItem("Rameswaram", "Sacred Island", "🕌", 4.7),
            new LocationItem("Kodaikanal", "Princess of Hills", "🌲", 4.6),
            new LocationItem("Mahabalipuram", "Shore Temple", "🏛️", 4.4),
            new LocationItem("Tiruchirappalli", "Rockfort Temple", "🛕", 4.3),
            new LocationItem("Thanjavur", "Big Temple", "🏛️", 4.5),
            new LocationItem("Vellore", "Fort City", "🏰", 4.2),
            new LocationItem("Salem", "Steel City", "🏭", 4.1),
            new LocationItem("Tirunelveli", "Halwa City", "🍬", 4.2),
            new LocationItem("Erode", "Turmeric City", "🌾", 4.0),
            new LocationItem("Dindigul", "Lock City", "🔒", 4.1),
            new LocationItem("Puducherry", "French Colony", "🇫🇷", 4.6),
            new LocationItem("Yercaud", "Jewel of the South", "⛰️", 4.4),
            new LocationItem("Valparai", "Tea Estate Paradise", "🍃", 4.5),
            new LocationItem("Hogenakkal", "Niagara of India", "💧", 4.4),
            new LocationItem("Courtallam", "Spa of South India", "💧", 4.3)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection);

        selectionType = getIntent().getStringExtra("type");
        currentSelection = getIntent().getStringExtra("current") != null ?
                getIntent().getStringExtra("current") : "";

        initViews();
        setupData();
        setupListeners();
        loadRecentLocations();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        rvLocations = findViewById(R.id.rvLocations);
        ivBack = findViewById(R.id.ivBack);
        ivClear = findViewById(R.id.ivClear);
        tvSelectedType = findViewById(R.id.tvSelectedType);
        tvRecentTitle = findViewById(R.id.tvRecentTitle);
        btnConfirm = findViewById(R.id.btnConfirm);
        layoutRecent = findViewById(R.id.layoutRecent);

        rvLocations.setLayoutManager(new LinearLayoutManager(this));

        if (selectionType != null && selectionType.equals("from")) {
            tvSelectedType.setText("Select Departure City");
        } else {
            tvSelectedType.setText("Select Destination City");
        }
    }

    private void setupData() {
        allLocations = new ArrayList<>();
        for (LocationItem location : popularLocations) {
            allLocations.add(location);
        }

        adapter = new LocationAdapter(allLocations, new LocationAdapter.OnLocationClickListener() {
            @Override
            public void onLocationClick(LocationItem location) {
                currentSelection = location.name;
                etSearch.setText(location.name);
                updateConfirmButton();
                saveToRecent(location.name);
            }
        });
        rvLocations.setAdapter(adapter);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        ivClear.setOnClickListener(v -> {
            etSearch.setText("");
            adapter.updateList(allLocations);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterLocations(s.toString());
                ivClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnConfirm.setOnClickListener(v -> {
            if (!currentSelection.isEmpty()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_location", currentSelection);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterLocations(String query) {
        List<LocationItem> filtered = new ArrayList<>();
        for (LocationItem location : allLocations) {
            if (location.name.toLowerCase().contains(query.toLowerCase()) ||
                    location.description.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(location);
            }
        }
        adapter.updateList(filtered);
    }

    private void loadRecentLocations() {
        SharedPreferences prefs = getSharedPreferences("TourNex", MODE_PRIVATE);
        String recent = prefs.getString("recent_locations_" + selectionType, "");
        if (!recent.isEmpty()) {
            String[] recentArray = recent.split(",");
            recentLocations = new ArrayList<>();
            for (String loc : recentArray) {
                for (LocationItem item : popularLocations) {
                    if (item.name.equals(loc)) {
                        recentLocations.add(item);
                        break;
                    }
                }
            }
            if (!recentLocations.isEmpty()) {
                tvRecentTitle.setVisibility(View.VISIBLE);
                layoutRecent.setVisibility(View.VISIBLE);
                addRecentChips();
            } else {
                tvRecentTitle.setVisibility(View.GONE);
                layoutRecent.setVisibility(View.GONE);
            }
        } else {
            tvRecentTitle.setVisibility(View.GONE);
            layoutRecent.setVisibility(View.GONE);
        }
    }

    private void addRecentChips() {
        layoutRecent.removeAllViews();
        for (LocationItem location : recentLocations) {
            Chip chip = new Chip(this);
            chip.setText(location.name);
            chip.setChipBackgroundColorResource(android.R.color.white);
            chip.setTextColor(getResources().getColor(R.color.bottom_nav_color));
            chip.setChipStrokeColorResource(R.color.bottom_nav_color);
            chip.setChipStrokeWidth(1f);
            chip.setClickable(true);
            chip.setOnClickListener(v -> {
                currentSelection = location.name;
                etSearch.setText(location.name);
                updateConfirmButton();
            });
            layoutRecent.addView(chip);
        }
    }

    private void saveToRecent(String location) {
        SharedPreferences prefs = getSharedPreferences("TourNex", MODE_PRIVATE);
        String recent = prefs.getString("recent_locations_" + selectionType, "");
        List<String> recentList = new ArrayList<>(Arrays.asList(recent.split(",")));
        recentList.remove(location);
        recentList.add(0, location);
        if (recentList.size() > 5) recentList = recentList.subList(0, 5);

        StringBuilder sb = new StringBuilder();
        for (String loc : recentList) {
            if (!loc.isEmpty()) sb.append(loc).append(",");
        }
        prefs.edit().putString("recent_locations_" + selectionType, sb.toString()).apply();
    }

    private void updateConfirmButton() {
        btnConfirm.setEnabled(!currentSelection.isEmpty());
    }

    // Location Item Class
    static class LocationItem {
        String name;
        String description;
        String icon;
        double rating;

        LocationItem(String name, String description, String icon, double rating) {
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.rating = rating;
        }
    }

    // Location Adapter
    static class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
        private List<LocationItem> locations;
        private OnLocationClickListener listener;

        interface OnLocationClickListener {
            void onLocationClick(LocationItem location);
        }

        LocationAdapter(List<LocationItem> locations, OnLocationClickListener listener) {
            this.locations = locations;
            this.listener = listener;
        }

        void updateList(List<LocationItem> newList) {
            this.locations = newList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_location, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            LocationItem item = locations.get(position);
            holder.tvIcon.setText(item.icon);
            holder.tvName.setText(item.name);
            holder.tvDescription.setText(item.description);
            holder.tvRating.setText(String.valueOf(item.rating));
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLocationClick(item);
                }
            });
        }

        @Override
        public int getItemCount() {
            return locations.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvIcon, tvName, tvDescription, tvRating;
            ViewHolder(View itemView) {
                super(itemView);
                tvIcon = itemView.findViewById(R.id.tvIcon);
                tvName = itemView.findViewById(R.id.tvLocationName);
                tvDescription = itemView.findViewById(R.id.tvLocationDescription);
                tvRating = itemView.findViewById(R.id.tvRating);
            }
        }
    }
}