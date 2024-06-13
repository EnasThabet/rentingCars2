package com.example.project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddCar extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextModel, editTextDescription, editTextPrice, editTextNumCars;
    private ImageButton buttonRegister;
    private Spinner yearSpinner;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        editTextModel = findViewById(R.id.car_model);
        editTextDescription = findViewById(R.id.car_description);
        editTextPrice = findViewById(R.id.car_price);
        editTextNumCars = findViewById(R.id.car_number);
        buttonRegister = findViewById(R.id.subment_button);
        yearSpinner = findViewById(R.id.year_spinner);

        progressDialog = new ProgressDialog(this);

        buttonRegister.setOnClickListener(this);

        // Populate the year spinner
        populateYearSpinner();
    }

    private void populateYearSpinner() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = new Integer[currentYear - 1969];
        for (int i = 0; i < years.length; i++) {
            years[i] = 1970 + i;
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);
    }

    private void registerCar() {
        final String model = editTextModel.getText().toString().trim();
        final String description = editTextDescription.getText().toString().trim();
        final String price = editTextPrice.getText().toString().trim();
        final String numCars = editTextNumCars.getText().toString().trim();
        final String year = yearSpinner.getSelectedItem().toString().trim();

        progressDialog.setMessage("Registering Car...");
        progressDialog.show();

        String url = "http://192.168.0.111/Android/includes/AddCar.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d("VolleyResponse", "Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String message = jsonObject.getString("message");

                            AlertDialog.Builder builder = new AlertDialog.Builder(AddCar.this);
                            builder.setTitle(error ? "Car Registration Failed" : "Car Registration Successful");
                            builder.setMessage(message);
                            builder.setPositiveButton("OK", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                            try {
                                errorMessage += "\nResponse Data: " + new String(error.networkResponse.data, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e("VolleyError", errorMessage);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("model", model);
                params.put("make_date", year);
                params.put("price_per_day", price);
                params.put("description", description);
                params.put("cars_number", numCars);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonRegister) {
            registerCar();
        }
    }
}
