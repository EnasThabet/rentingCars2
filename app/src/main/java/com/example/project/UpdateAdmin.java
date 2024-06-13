package com.example.project;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateAdmin extends AppCompatActivity {

    private EditText editTextID, editTextUsername, editTextEmail, editTextAddress, editTextPassword;
    private ImageButton buttonUpdate;
    private ProgressDialog progressDialog ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_admin);

        editTextID = findViewById(R.id.ID);
        editTextUsername = findViewById(R.id.UserName);
        editTextEmail = findViewById(R.id.Email);
        editTextAddress = findViewById(R.id.Address);
        editTextPassword = findViewById(R.id.Password);
        buttonUpdate = findViewById(R.id.update_button);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Admin...");

        populateUserInfo();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });
    }

    private void populateUserInfo() {
        SharedPrefAdminManager sharedPrefAdminManager = SharedPrefAdminManager.getInstance(this);

        int userId = sharedPrefAdminManager.getAdminID();
        String username = sharedPrefAdminManager.getAdminName();
        String email = sharedPrefAdminManager.getEmail();
        String address = sharedPrefAdminManager.getAddress();
        String password = sharedPrefAdminManager.getPassword();

        editTextID.setText(userId);
        editTextUsername.setText(username);
        editTextEmail.setText(email);
        editTextAddress.setText(address);
        editTextPassword.setText(password);
    }

    private void updateUser() {
        String updatedID = editTextID.getText().toString().trim();
        String updatedUsername = editTextUsername.getText().toString().trim();
        String updatedEmail = editTextEmail.getText().toString().trim();
        String updatedAddress = editTextAddress.getText().toString().trim();
        String updatedPassword = editTextPassword.getText().toString().trim();

        progressDialog.show();
        String url = "http://192.168.0.111/Android/includes/UpdateAdmin.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String message = jsonObject.getString("message");
                            if (!error) {
                                // Update successful
                                showDialog("Success", message);
                            } else {
                                // Update failed
                                showDialog("Error", message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showDialog("Error", "JSON Parsing Error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        showDialog("Error", "Error: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ID", updatedID);
                params.put("userName", updatedUsername);
                params.put("Email", updatedEmail);
                params.put("Address", updatedAddress);
                params.put("Password", updatedPassword);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
