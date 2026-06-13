package com.tournex.travel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class MoodActivity extends BaseActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ChipGroup chipCategoryGroup;
    private TextView tvSelectedMood, tvMoodDescription;
    private ImageView ivMoodHeader;
    private LinearLayout layoutMoodInfo;

    private MoodPagerAdapter pagerAdapter;
    private String currentMood = "Adventure";

    // Mood data with colors and descriptions
    private List<MoodCategory> moodCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_new);

        setupBottomNavigation(R.id.nav_mood);
        initViews();
        setupMoodCategories();
        setupViewPager();
        setupChipGroup();
        setupAnimations();
        // Check if a specific mood was selected from MainActivity
        String selectedMood = getIntent().getStringExtra("selected_mood");
        if (selectedMood != null) {
            selectMood(selectedMood);
        }
    }
    private void selectMood(String mood) {
        int position = 0;
        switch (mood) {
            case "Adventure":
                position = 0;
                break;
            case "Relax":
                position = 1;
                break;
            case "Spiritual":
                position = 2;
                break;
            case "Heritage":
                position = 3;
                break;
            case "Nature":
                position = 4;
                break;
        }
        viewPager.setCurrentItem(position, true);
    }
    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        chipCategoryGroup = findViewById(R.id.chipCategoryGroup);
        tvSelectedMood = findViewById(R.id.tvSelectedMood);
        tvMoodDescription = findViewById(R.id.tvMoodDescription);
        ivMoodHeader = findViewById(R.id.ivMoodHeader);
        layoutMoodInfo = findViewById(R.id.layoutMoodInfo);
    }

    private void setupMoodCategories() {
        moodCategories.add(new MoodCategory(
                "Adventure",
                "Get your adrenaline pumping with thrilling activities",
                "#FF6B6B",
                R.drawable.ic_adventure_header,
                new Place[]{
                        new Place("Trekking in Ooty", "Beautiful Nilgiri hills trekking", "₹2,500", R.drawable.ic_adventure, 4.7),
                        new Place("River Rafting - Hogenakkal", "Exciting river rafting experience", "₹3,000", R.drawable.ic_adventure, 4.5),
                        new Place("Rock Climbing - Yercaud", "Challenging rock climbing spots", "₹1,800", R.drawable.ic_adventure, 4.6),
                        new Place("Camping - Kodaikanal", "Night camping in pine forests", "₹2,200", R.drawable.ic_adventure, 4.8),
                        new Place("Paragliding - Kullu", "Fly high with scenic views", "₹4,500", R.drawable.ic_adventure, 4.9),
                        new Place("Mountain Biking", "Rough terrain cycling adventure", "₹1,500", R.drawable.ic_adventure, 4.4)
                }
        ));

        moodCategories.add(new MoodCategory(
                "Relaxation",
                "Unwind and rejuvenate in peaceful surroundings",
                "#4ECDC4",
                R.drawable.ic_relax_header,
                new Place[]{
                        new Place("Pondicherry Beach", "Peaceful French colony beaches", "₹1,500", R.drawable.ic_pon, 4.6),
                        new Place("Munnar Hills", "Tea gardens and cool breeze", "₹2,000", R.drawable.ic_mu, 4.8),
                        new Place("Vattakanal", "Offbeat peaceful location", "₹1,200", R.drawable.ic_vat, 4.5),
                        new Place("Courtallam Falls", "Natural water spa", "₹1,000", R.drawable.ic_cout, 4.4),
                        new Place("Ayurveda Retreat", "Traditional wellness center", "₹3,500", R.drawable.ic_relax, 4.7),
                        new Place("Boating - Ooty Lake", "Serene lake experience", "₹800", R.drawable.placeholder_ooty, 4.3)
                }
        ));

        moodCategories.add(new MoodCategory(
                "Spiritual",
                "Discover divine experiences and ancient temples",
                "#9B59B6",
                R.drawable.ic_spiritual_header,
                new Place[]{
                        new Place("Rameswaram Temple", "Sacred Jyotirlinga temple", "₹800", R.drawable.ic_ra, 4.9),
                        new Place("Palani Murugan Temple", "Famous hill temple", "₹600", R.drawable.ic_pa, 4.7),
                        new Place("Velankanni Church", "Holy Christian shrine", "₹700", R.drawable.ic_vel, 4.8),
                        new Place("Tiruvannamalai", "Sacred Annamalaiyar Temple", "₹500", R.drawable.ic_tir, 4.6),
                        new Place("Madurai Meenakshi", "Historic temple architecture", "₹600", R.drawable.placeholder_madurai, 4.8),
                        new Place("Thanjavur Big Temple", "UNESCO world heritage", "₹400", R.drawable.ic_tan, 4.7)
                }
        ));

        moodCategories.add(new MoodCategory(
                "Heritage",
                "Explore rich history and ancient architecture",
                "#F39C12",
                R.drawable.ic_heritage_header,
                new Place[]{
                        new Place("Mahabalipuram", "Ancient rock-cut temples", "₹500", R.drawable.ic_ma, 4.7),
                        new Place("Thanjavur Big Temple", "UNESCO world heritage", "₹400", R.drawable.ic_tan, 4.8),
                        new Place("Kanchipuram", "City of thousand temples", "₹400", R.drawable.ic_kan, 4.5),
                        new Place("Fort St. George", "Historic British fort", "₹300", R.drawable.ic_heritage, 4.4),
                        new Place("Darasuram Temple", "Ancient Chola architecture", "₹350", R.drawable.ic_heritage, 4.6),
                        new Place("Gingee Fort", "Majestic hill fortress", "₹450", R.drawable.ic_heritage, 4.5)
                }
        ));

        moodCategories.add(new MoodCategory(
                "Nature",
                "Immerse yourself in breathtaking natural beauty",
                "#27AE60",
                R.drawable.ic_nature_header,
                new Place[]{
                        new Place("Ooty Lake", "Scenic boating location", "₹800", R.drawable.placeholder_ooty, 4.6),
                        new Place("Kodaikanal", "Princess of hill stations", "₹1,500", R.drawable.ic_kod, 4.8),
                        new Place("Yercaud", "Jewel of the South", "₹1,000", R.drawable.ic_y, 4.5),
                        new Place("Valparai", "Wildlife and tea estates", "₹1,300", R.drawable.ic_v, 4.7),
                        new Place("Hogenakkal Falls", "Niagara of India", "₹900", R.drawable.ic_nature, 4.6),
                        new Place("Mudumalai Wildlife", "Jungle safari experience", "₹1,800", R.drawable.ic_nature, 4.8)
                }
        ));
    }

    private void setupViewPager() {
        pagerAdapter = new MoodPagerAdapter(this, moodCategories);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    MoodCategory category = moodCategories.get(position);
                    tab.setText(category.name);
                    tab.setIcon(getTabIcon(position));
                }
        ).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentMood = moodCategories.get(position).name;
                updateMoodHeader(position);
                updateSelectedChip(position);
            }
        });

        // Set initial header
        updateMoodHeader(0);
    }

    private void updateMoodHeader(int position) {
        MoodCategory category = moodCategories.get(position);
        tvSelectedMood.setText(category.name);
        tvMoodDescription.setText(category.description);
        ivMoodHeader.setImageResource(category.headerImage);

        // Change header background color with animation
        int color = android.graphics.Color.parseColor(category.color);
        layoutMoodInfo.setBackgroundColor(color);

        // Animate header change
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        layoutMoodInfo.startAnimation(fadeIn);
    }

    private void setupChipGroup() {
        // Remove default chips
        chipCategoryGroup.removeAllViews();

        String[] categories = {"All", "Trending", "Nearby", "Top Rated", "Budget Friendly"};
        for (String category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(android.R.color.white);
            chip.setTextColor(getResources().getColor(R.color.bottom_nav_color));
            chip.setChipStrokeColorResource(R.color.bottom_nav_color);
            chip.setChipStrokeWidth(1f);
            chipCategoryGroup.addView(chip);
        }

        chipCategoryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = findViewById(checkedId);
            if (chip != null) {
                filterPlacesByCategory(chip.getText().toString());
            }
        });
    }

    private void filterPlacesByCategory(String filter) {
        // Get current page adapter and filter places
        if (pagerAdapter != null) {
            pagerAdapter.filterPlaces(filter);
            Toast.makeText(this, "Filter: " + filter, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSelectedChip(int position) {
        // Update tab selection in chip group if needed
    }

    private int getTabIcon(int position) {
        switch(position) {
            case 0: return R.drawable.ic_adventure;
            case 1: return R.drawable.ic_relax;
            case 2: return R.drawable.ic_spiritual;
            case 3: return R.drawable.ic_heritage;
            case 4: return R.drawable.ic_nature;
            default: return R.drawable.ic_adventure;
        }
    }

    private void setupAnimations() {
        Animation slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        viewPager.startAnimation(slideUp);
    }

    public void showPlaceDetails(Place place) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_place_details, null);

        TextView tvName = dialogView.findViewById(R.id.tvPlaceName);
        TextView tvDesc = dialogView.findViewById(R.id.tvPlaceDescription);
        TextView tvPrice = dialogView.findViewById(R.id.tvPlacePriceDetail);
        TextView tvRating = dialogView.findViewById(R.id.tvRating);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        Button btnBook = dialogView.findViewById(R.id.btnBookNowDetail);

        tvName.setText(place.name);
        tvDesc.setText(place.description);
        tvPrice.setText(place.price);
        ratingBar.setRating((float) place.rating);
        tvRating.setText(String.format("%.1f ★ (128 reviews)", place.rating));

        builder.setView(dialogView);
        builder.setCancelable(true);
        androidx.appcompat.app.AlertDialog dialog = builder.create();

        btnBook.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(MoodActivity.this, PaymentActivity.class);
            intent.putExtra("place_name", place.name);
            intent.putExtra("place_price", place.price);
            startActivity(intent);
        });

        dialog.show();
    }

    // Data Classes
    static class MoodCategory {
        String name;
        String description;
        String color;
        int headerImage;
        Place[] places;

        MoodCategory(String name, String description, String color, int headerImage, Place[] places) {
            this.name = name;
            this.description = description;
            this.color = color;
            this.headerImage = headerImage;
            this.places = places;
        }
    }

    static class Place {
        String name;
        String description;
        String price;
        int iconRes;
        double rating;

        Place(String name, String description, String price, int iconRes, double rating) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.iconRes = iconRes;
            this.rating = rating;
        }
    }
}