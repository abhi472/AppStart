package com.example.abhishek.appstart;

import android.accessibilityservice.AccessibilityService;
import android.app.Fragment;
import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.abhishek.appstart.ICallBack;

/**
 * Created by abhishek on 21/3/16.
 */
public class ApiManager {


    ICallBack iCallBack;
    Fragment context;
    AccessibilityService context2;

    private static ApiManager instance;

    public static ApiManager getInstance() {
        if (instance == null) {
            instance = new ApiManager();
        }
        return instance;
    }

    public static ApiManager getInstance(Fragment context) {
        //if (instance == null) {
        instance = new ApiManager(context);
        //}
        return instance;
    }

    ApiManager() {

    }

    ApiManager(Fragment c) {
        this.context = c;
    }

    /**
     * Call from Fragment only
     *
     * @param requestUrl
     */
    public void sendReq(String requestUrl) {
        iCallBack = (ICallBack) context;
        StringRequest stringRequest = new StringRequest(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                iCallBack.onResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                iCallBack.onResult(error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest, "stringReq");
    }

    /**
     * Call from Activity only
     *
     * @param context
     * @param requestUrl
     */
    public void sendReq(Context context, String requestUrl) {
        iCallBack = (ICallBack) context;

        StringRequest stringRequest = new StringRequest(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                iCallBack.onResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                iCallBack.onResult(error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
    public void sendReq(AccessibilityService service, String requestUrl) {
        iCallBack = (ICallBack) service;

        StringRequest stringRequest = new StringRequest(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                iCallBack.onResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                iCallBack.onResult(error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}