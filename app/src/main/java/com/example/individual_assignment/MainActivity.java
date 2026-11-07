package com.example.individual_assignment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etBaggageWeight, etRatePerKg;
    private Spinner spinnerTicketClass;
    private Button btnCalculate, btnReset, btnBackToMain;
    private TextView tvExcessWeight, tvExcessCharge, tvTotalPayable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind UI Components
        etBaggageWeight = findViewById(R.id.etBaggageWeight);
        etRatePerKg = findViewById(R.id.etRatePerKg);
        spinnerTicketClass = findViewById(R.id.spinnerTicketClass);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnReset = findViewById(R.id.btnReset);
        tvExcessWeight = findViewById(R.id.tvExcessWeight);
        tvExcessCharge = findViewById(R.id.tvExcessCharge);
        tvTotalPayable = findViewById(R.id.tvTotalPayable);

        // Bind Butang Back to Main Menu
        btnBackToMain = findViewById(R.id.btnBackToMain);

        // Setup Spinner Adapter manually
        String[] ticketClasses = {"Economy Class (20 kg)", "Business Class (40 kg)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ticketClasses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTicketClass.setAdapter(adapter);

        // Calculate Button Listener
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBaggageFee();
            }
        });

        // Clear Button Listener
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFields();
            }
        });

        // Listener BACK TO MAIN MENU button
        if (btnBackToMain != null) {
            btnBackToMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void calculateBaggageFee() {
        String weightStr = etBaggageWeight.getText().toString().trim();
        String rateStr = etRatePerKg.getText().toString().trim();

        // 1: if weight input 0 (Instruction & Helpful Notice)
        if (weightStr.isEmpty()) {
            etBaggageWeight.setError("Please enter baggage weight!");
            Toast.makeText(this, "Notice: Baggage weight is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2: if input rate 0
        if (rateStr.isEmpty()) {
            etRatePerKg.setError("Please enter excess rate per kg!");
            Toast.makeText(this, "Notice: Excess rate per kg is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        double baggageWeight = Double.parseDouble(weightStr);
        double ratePerKg = Double.parseDouble(rateStr);

        int selectedPosition = spinnerTicketClass.getSelectedItemPosition();
        double freeWeightAllowance = (selectedPosition == 0) ? 20.0 : 40.0;

        // 3: Logik Had & Mesej Notice/Within Limit
        if (baggageWeight <= freeWeightAllowance) {
            // if (Within Limit)
            tvExcessWeight.setTextColor(Color.parseColor("#2E7D32")); // Warna hijau
            tvExcessWeight.setText(String.format(Locale.getDefault(), "Status: Within Limit (Max %.0f kg)", freeWeightAllowance));
            tvExcessCharge.setText("Excess Baggage Charge: RM 0.00");
            tvTotalPayable.setText("Total Amount Payable (Inc. 8% Tax): RM 0.00");

            Toast.makeText(this, "Notice: Baggage is within limit. No extra fee!", Toast.LENGTH_LONG).show();
        } else {
            // if (Excess Baggage)
            double excessWeight = baggageWeight - freeWeightAllowance;
            double excessCharge = excessWeight * ratePerKg;

            double totalPayable = excessCharge + (0.08 * excessCharge);

            tvExcessWeight.setTextColor(Color.RED); // Warna merah
            tvExcessWeight.setText(String.format(Locale.getDefault(), "Excess Baggage Weight: %.2f kg", excessWeight));
            tvExcessCharge.setText(String.format(Locale.getDefault(), "Excess Baggage Charge: RM %.2f", excessCharge));
            tvTotalPayable.setText(String.format(Locale.getDefault(), "Total Amount Payable (Inc. 8%% Tax): RM %.2f", totalPayable));
        }
    }

    private void resetFields() {
        etBaggageWeight.setText("");
        etRatePerKg.setText("");
        spinnerTicketClass.setSelection(0);
        tvExcessWeight.setTextColor(Color.BLACK);
        tvExcessWeight.setText("Excess Baggage Weight: 0 kg");
        tvExcessCharge.setText("Excess Baggage Charge: RM 0.00");
        tvTotalPayable.setText("Total Amount Payable (Inc. 8% Tax): RM 0.00");
        Toast.makeText(this, "Fields have been cleared", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my Excess Baggage Fee Calculator app at: https://github.com/AisyaSofea30/ExcessBaggageCalculator");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}