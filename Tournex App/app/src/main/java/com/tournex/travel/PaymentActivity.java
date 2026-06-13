package com.tournex.travel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvPlaceName, tvAmount, tvTotalAmount;
    private TextInputEditText etCardNumber, etExpiry, etCvv, etCardHolder;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCard, rbUpi;
    private CardView cardCardDetails;
    private Button btnPayNow;
    private String placeName;
    private String placePrice;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        sharedPreferences = getSharedPreferences("TourNex", MODE_PRIVATE);

        // Get data from intent
        placeName = getIntent().getStringExtra("place_name");
        placePrice = getIntent().getStringExtra("place_price");

        if (placeName == null) placeName = "Custom Trip";
        if (placePrice == null) placePrice = "₹2,000";

        initViews();
        setupPaymentOptions();

        btnPayNow.setOnClickListener(v -> processPayment());
    }

    private void initViews() {
        tvPlaceName = findViewById(R.id.tvPlaceName);
        tvAmount = findViewById(R.id.tvAmount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etCvv = findViewById(R.id.etCvv);
        etCardHolder = findViewById(R.id.etCardHolder);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rbCard = findViewById(R.id.rbCard);
        rbUpi = findViewById(R.id.rbUpi);
        cardCardDetails = findViewById(R.id.cardCardDetails);
        btnPayNow = findViewById(R.id.btnPayNow);

        tvPlaceName.setText(placeName);
        tvAmount.setText("Amount: " + placePrice);

        // Extract numeric value from price string (e.g., "₹4,999" -> 4999)
        String priceStr = placePrice.replace("₹", "").replace(",", "").trim();
        int baseAmount;
        try {
            baseAmount = Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            baseAmount = 2000; // Default value if parsing fails
        }

        int gst = (int)(baseAmount * 0.05);
        int total = baseAmount + gst;
        tvTotalAmount.setText("Total (incl. GST): ₹" + total);
    }

    private void setupPaymentOptions() {
        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCard) {
                cardCardDetails.setVisibility(View.VISIBLE);
            } else {
                cardCardDetails.setVisibility(View.GONE);
            }
        });
    }

    private void processPayment() {
        boolean isValid = false;

        if (rbCard.isChecked()) {
            // Validate card details
            String cardNum = etCardNumber.getText().toString();
            String expiry = etExpiry.getText().toString();
            String cvv = etCvv.getText().toString();
            String holder = etCardHolder.getText().toString();

            if (cardNum.isEmpty() || expiry.isEmpty() || cvv.isEmpty() || holder.isEmpty()) {
                Toast.makeText(this, "Please fill all card details", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cardNum.length() < 16) {
                Toast.makeText(this, "Invalid card number", Toast.LENGTH_SHORT).show();
                return;
            }

            isValid = true;
        } else {
            // UPI payment
            isValid = true;
        }

        if (isValid) {
            // Generate booking ID
            String bookingId = "TN" + System.currentTimeMillis() + new Random().nextInt(999);
            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            String username = sharedPreferences.getString("username", "User");

            // Save booking to SharedPreferences
            String bookings = sharedPreferences.getString("bookings", "");
            String totalAmount = tvTotalAmount.getText().toString().replace("Total (incl. GST): ", "");
            String newBooking = bookingId + "|" + placeName + "|" + totalAmount + "|" + currentDate + "|Pending|" + username + ";";

            sharedPreferences.edit().putString("bookings", bookings + newBooking).apply();

            // Show success dialog
            showSuccessDialog(bookingId);
        }
    }

    private void showSuccessDialog(String bookingId) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_payment_success, null);

        TextView tvBookingId = view.findViewById(R.id.tvBookingId);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        Button btnOk = view.findViewById(R.id.btnOk);

        tvBookingId.setText("Booking ID: " + bookingId);
        tvMessage.setText("Payment Successful! Your trip to " + placeName + " is confirmed.");

        builder.setView(view);
        builder.setCancelable(false);
        android.app.AlertDialog dialog = builder.create();

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(PaymentActivity.this, BookingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }
}