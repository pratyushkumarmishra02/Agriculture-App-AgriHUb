package com.example.Agri_Hub;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PaymentActivity extends AppCompatActivity {

    private TextView totalAmount;
    private Button creditCard, netBanking, upi, cashOnDelivery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Payment");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        totalAmount = findViewById(R.id.totalAmount);
        creditCard = findViewById(R.id.creditCard);
        netBanking = findViewById(R.id.netBanking);
        upi = findViewById(R.id.upi);
        cashOnDelivery = findViewById(R.id.cashOnDelivery);

        // Retrieve total amount from intent or set manually
        String amount = getIntent().getStringExtra("TOTAL_AMOUNT");
        if (amount != null) {
            totalAmount.setText(amount);
        } else {
            totalAmount.setText("₹0");
        }

        creditCard.setOnClickListener(view -> openPaymentGateway("Credit Card"));
        netBanking.setOnClickListener(view -> openPaymentGateway("Net Banking"));
        upi.setOnClickListener(view -> openPaymentGateway("UPI"));
        cashOnDelivery.setOnClickListener(view -> openPaymentGateway("Cash on Delivery"));
    }

    private void openPaymentGateway(String method) {
//        Intent intent = new Intent(PaymentActivity.this, PaymentProcessingActivity.class);
//        intent.putExtra("PAYMENT_METHOD", method);
//        intent.putExtra("TOTAL_AMOUNT", totalAmount.getText().toString());
//        startActivity(intent);
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
