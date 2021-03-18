package br.com.avanade.fahz.application;

import android.util.Base64;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.model.lgpdModel.EntitieVisible;

public class FahzClaims {
    private JSONObject mJsonObject;
    public ArrayList<EntitieVisible> entitieVisibles;

    public String getCPF() {
        try {
            if (mJsonObject != null)
                return mJsonObject.getString("nameid");
            else
                return "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getName() {
        try {
            if (mJsonObject != null)
                return mJsonObject.getString("fullname");
            else
                return "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getRoles() {
        try {
            if (mJsonObject != null)
                return mJsonObject.getString("roles");
            else
                return "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getEntitiesVisible() {
        try {
            if (mJsonObject != null)
                return mJsonObject.getString("entities_visible");
            else
                return "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getCompany() {
        try {
            if (mJsonObject != null)
                return mJsonObject.getString("company");
            else
                return "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getAdmisionDate() {
        try {
            if (mJsonObject != null)
                return mJsonObject.getString("admissiondate");

            else
                return "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getLifeStatus() {
        try {
            if (mJsonObject != null)
                return mJsonObject.getString("lifestatus");
            else
                return "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    public boolean getPendingApproval() {
        try {
            if (mJsonObject != null)
                return mJsonObject.getBoolean("pendingapproval");
            else
                return false;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getSource() {
        try {
            if (mJsonObject != null)
                return mJsonObject.getString("source");
            else
                return "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getOrganizationalAttribution() {
        try {
            if (mJsonObject != null)
                return mJsonObject.getString("organizationalattribution").replace("\"", "");
            else
                return "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public FahzClaims(String token) {
        //Recebe um token JWT
        String[] parts = token.split("\\.");

        if (parts.length >= 3) {
            String claimsValue = parts[1];

            byte[] decodedValue = Base64.decode(claimsValue, Base64.DEFAULT);
            claimsValue = new String(decodedValue);

            try {
                mJsonObject = new JSONObject(claimsValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
