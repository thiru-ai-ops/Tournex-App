package com.tournex.travel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class MoodPagerAdapter extends RecyclerView.Adapter<MoodPagerAdapter.MoodViewHolder> {

    private List<MoodActivity.MoodCategory> categories;
    private MoodActivity activity;
    private String currentFilter = "All";

    public MoodPagerAdapter(MoodActivity activity, List<MoodActivity.MoodCategory> categories) {
        this.activity = activity;
        this.categories = categories;
    }

    public void filterPlaces(String filter) {
        this.currentFilter = filter;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mood_page, parent, false);
        return new MoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        MoodActivity.MoodCategory category = categories.get(position);
        holder.bind(category, currentFilter);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class MoodViewHolder extends RecyclerView.ViewHolder {
        private GridLayout gridPlaces;

        MoodViewHolder(@NonNull View itemView) {
            super(itemView);
            gridPlaces = itemView.findViewById(R.id.gridPlaces);
        }

        void bind(MoodActivity.MoodCategory category, String filter) {
            gridPlaces.removeAllViews();

            // Filter places based on filter
            List<MoodActivity.Place> filteredPlaces = new ArrayList<>();
            for (MoodActivity.Place place : category.places) {
                if (filter.equals("All")) {
                    filteredPlaces.add(place);
                } else if (filter.equals("Top Rated") && place.rating >= 4.7) {
                    filteredPlaces.add(place);
                } else if (filter.equals("Budget Friendly") &&
                        Integer.parseInt(place.price.replace("₹", "").replace(",", "")) < 1500) {
                    filteredPlaces.add(place);
                } else if (filter.equals("Trending") && getAdapterPosition() == 0) {
                    filteredPlaces.add(place);
                } else if (filter.equals("Nearby") && getAdapterPosition() < 2) {
                    filteredPlaces.add(place);
                } else if (!filter.equals("All") && !filter.equals("Top Rated") &&
                        !filter.equals("Budget Friendly") && !filter.equals("Trending") &&
                        !filter.equals("Nearby")) {
                    filteredPlaces.add(place);
                }
            }

            if (filteredPlaces.isEmpty()) {
                // Show empty state
                View emptyView = LayoutInflater.from(activity)
                        .inflate(R.layout.item_empty_places, null);
                GridLayout.Spec rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                GridLayout.Spec colSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(0, 2, 1f);
                emptyView.setLayoutParams(params);
                gridPlaces.addView(emptyView);
                return;
            }

            // Calculate number of rows needed
            int columnCount = 2;
            int rowCount = (int) Math.ceil((double) filteredPlaces.size() / columnCount);
            gridPlaces.setRowCount(rowCount);
            gridPlaces.setColumnCount(columnCount);

            // Add places to grid
            for (int i = 0; i < filteredPlaces.size(); i++) {
                MoodActivity.Place place = filteredPlaces.get(i);
                View placeCard = LayoutInflater.from(activity)
                        .inflate(R.layout.item_mood_place, null);

                ImageView ivIcon = placeCard.findViewById(R.id.ivPlaceIcon);
                TextView tvName = placeCard.findViewById(R.id.tvPlaceName);
                TextView tvDesc = placeCard.findViewById(R.id.tvPlaceDesc);
                TextView tvPrice = placeCard.findViewById(R.id.tvPlacePrice);
                TextView tvRating = placeCard.findViewById(R.id.tvRating);
                Button btnBook = placeCard.findViewById(R.id.btnBookNow);
                MaterialCardView card = placeCard.findViewById(R.id.cardPlace);

                tvName.setText(place.name);
                tvDesc.setText(place.description);
                tvPrice.setText(place.price);
                tvRating.setText(String.format("%.1f ★", place.rating));
                ivIcon.setImageResource(place.iconRes);

                // Calculate row and column
                int row = i / columnCount;
                int col = i % columnCount;

                // Set layout parameters for GridLayout
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row, 1, 1f);
                params.columnSpec = GridLayout.spec(col, 1, 1f);
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.setMargins(8, 8, 8, 8);
                placeCard.setLayoutParams(params);

                // Set card click listener
                card.setOnClickListener(v -> {
                    activity.showPlaceDetails(place);
                });

                btnBook.setOnClickListener(v -> {
                    activity.showPlaceDetails(place);
                });

                gridPlaces.addView(placeCard);
            }
        }
    }
}