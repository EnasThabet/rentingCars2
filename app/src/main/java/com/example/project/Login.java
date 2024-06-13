package com.example.project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class Login extends AppCompatActivity implements View.OnClickListener{

    EditText editTextID , editTextPassword ;
    private ProgressDialog progressDialog;
    private Button buttonLogin ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        editTextID = (EditText) findViewById(R.id.adminLoginID);
        editTextPassword = (EditText) findViewById(R.id.adminLoginPassword);
        buttonLogin = (Button) findViewById(R.id.AdminButtnLogin);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait.....");
        buttonLogin.setOnClickListener(this);

    }

    private void showMessageDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void adminLogin() {
        final String IDString = editTextID.getText().toString().trim();
        final String Password = editTextPassword.getText().toString().trim();

        if (IDString.isEmpty() || Password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
            return;
        }

        int ID = Integer.parseInt(IDString);
        String url = "http://192.168.0.111/Android/includes/LoginAdmins.php";
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d("LoginResponse", "Response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        int adminID = obj.getInt("ID");
                        String adminName = obj.getString("userName");
                        String adminEmail = obj.getString("Email");
                        String adminAddress = obj.getString("Address");
                        String adminPassword = Password;

                        SharedPrefAdminManager.getInstance(getApplicationContext()).adminLogin(
                                adminID,
                                adminName,
                                adminEmail,
                                adminAddress,
                                adminPassword
                        );

                        Toast.makeText(getApplicationContext(), "Admin login successful", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Login.this, UpdateAdmin.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("LoginError", "JSON Parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "JSON Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        String errorMessage = "Login Error: " + volleyError.toString();
                        if (volleyError.networkResponse != null) {
                            errorMessage += "\nStatus Code: " + volleyError.networkResponse.statusCode;
                            try {
                                errorMessage += "\nResponse Data: " + new String(volleyError.networkResponse.data, "UTF-8");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        volleyError.printStackTrace();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ID", IDString);
                params.put("Password", Password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onClick(View v) {
        if (v == buttonLogin) {
            adminLogin();
        }

    }
}