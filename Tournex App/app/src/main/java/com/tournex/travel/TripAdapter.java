package com.tournex.travel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<MainActivity.Trip> trips;
    private OnTripClickListener listener;

    public interface OnTripClickListener {
        void onTripClick(MainActivity.Trip trip);
    }

    public TripAdapter(List<MainActivity.Trip> trips, OnTripClickListener listener) {
        this.trips = trips;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        MainActivity.Trip trip = trips.get(position);
        holder.bind(trip, listener);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    static class TripViewHolder extends RecyclerView.ViewHolder {
        private CardView cardTrip;
        private ImageView ivTripImage;
        private TextView tvTripName;
        private TextView tvDuration;
        private TextView tvLocation;
        private TextView tvPrice;
        private RatingBar ratingBar;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTrip = itemView.findViewById(R.id.cardTrip);
            ivTripImage = itemView.findViewById(R.id.ivTripImage);
            tvTripName = itemView.findViewById(R.id.tvTripName);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        public void bind(MainActivity.Trip trip, OnTripClickListener listener) {
            tvTripName.setText(trip.name);
            tvDuration.setText(trip.duration);
            tvLocation.setText(trip.location);
            tvPrice.setText(trip.price);
            ratingBar.setRating((float) trip.rating);
            ivTripImage.setImageResource(trip.imageRes);

            cardTrip.setOnClickListener(v -> listener.onTripClick(trip));
        }
    }
}