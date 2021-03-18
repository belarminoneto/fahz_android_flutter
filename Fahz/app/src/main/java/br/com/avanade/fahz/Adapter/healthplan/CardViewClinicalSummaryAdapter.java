package br.com.avanade.fahz.Adapter.healthplan;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.response.SmallLife;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class CardViewClinicalSummaryAdapter extends RecyclerView.Adapter<CardViewClinicalSummaryAdapter.CustomViewHolder> {

    private Context context;
    public List<String> stringList;
    public SmallLife smallLife;
    private ProgressDialog mProgressDialog;
    View view;
    StringBuilder sb = new StringBuilder();

    public CardViewClinicalSummaryAdapter(Context context, List<String> list, SmallLife smallLife){
        this.context = context;
        this.stringList = list;
        this.smallLife = smallLife;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_clinical_summary,  parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        mProgressDialog = new ProgressDialog(context);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewClinicalSummaryAdapter.CustomViewHolder holder, final int position) {
        holder.textViewTitle.setText(this.stringList.get(position));
        holder.textViewDetails.setText("");
    }

    @Override
    public int getItemCount() {
        return (stringList !=null ? stringList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView textViewTitle;
        TextView textViewDetails;
        ImageView imageViewDetails;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        CustomViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            textViewTitle = (TextView) itemView.findViewById(R.id.textview_title);
            textViewDetails = (TextView) itemView.findViewById(R.id.textViewDetails);
            imageViewDetails = (ImageView) itemView.findViewById(R.id.imageViewDetails);
            imageViewDetails.setImageResource(R.drawable.ic_arrow_drop_down);

            imageViewDetails.setOnClickListener(v -> {

                if (imageViewDetails.getDrawable().getConstantState() == context.getResources().getDrawable( R.drawable.ic_arrow_drop_down).getConstantState()) {

                    imageViewDetails.setImageResource(R.drawable.ic_arrow_drop_up);

                    int clickedPosition = getAdapterPosition();

                    if (clickedPosition == 0)
                        loadDiagnosedDiseases(textViewDetails, stringList.get(clickedPosition));
                    else if (clickedPosition == 1)
                        loadMedicalTreatment(textViewDetails, stringList.get(clickedPosition));
                    else if (clickedPosition == 2)
                        loadAllergies(textViewDetails, stringList.get(clickedPosition));
                    else if (clickedPosition == 3)
                        loadFamilyHistory(textViewDetails, stringList.get(clickedPosition));
                    else if (clickedPosition == 4)
                        loadSurgeries(textViewDetails, stringList.get(clickedPosition));

                }else{
                    imageViewDetails.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_drop_down));
                    textViewDetails.setText(null);
                }
            });
        }

        @Override
        public void onClick(View v) {
            //int clickedPosition = getAdapterPosition();
            //Toast.makeText(context, stringList.get(clickedPosition) , Toast.LENGTH_LONG).show();
        }
    }

    private void setLoading(boolean loading) {
        if (loading) {
            mProgressDialog.setMessage(context.getResources().getString(R.string.wait_moment));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else
            mProgressDialog.dismiss();
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? context.getResources().getColor(R.color.green) : context.getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    private void loadDiagnosedDiseases(TextView textViewDetails, String msg) {

        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
        Call<JsonElement> call = mAPIService.getDiagnosedDiseases(new CPFInBody(smallLife.getCpf()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    JsonElement value = response.body().getAsJsonObject().get("commited");

                    if (value.getAsBoolean()) {

                        JsonArray json = ((JsonObject) response.body()).getAsJsonArray("Data");

                        sb = new StringBuilder();
                        sb.append("<br>");
                        if(json.size() > 0) {
                            for (int j = 0; j < json.size(); j++) {
                                String desc = json.get(j).getAsJsonObject().get("Disease").getAsString();
                                sb.append(desc);
                                sb.append("<br><br>");
                            }
                        }else{
                            sb.append(context.getString(R.string.no_found_record_of_clinical_summary));
                            sb.append(msg);
                        }
                        textViewDetails.setText(Html.fromHtml(sb.toString()));

                    } else {

                        int resID = context.getResources().getIdentifier("MSG361", "string", context.getPackageName());
                        String message = context.getResources().getString(resID);
                        showSnackBar(message,TYPE_FAILURE);

                    }
                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    }
                }

                setLoading(false);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                setLoading(false);
                if (context != null) {
                    if (t instanceof SocketTimeoutException)
                        showSnackBar(context.getResources().getString(context.getResources().getIdentifier("MSG362", "string", context.getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        showSnackBar(context.getResources().getString(context.getResources().getIdentifier("MSG361", "string", context.getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
            }
        });
    }

    private void loadMedicalTreatment(TextView textViewDetails, String msg) {

        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
        Call<JsonElement> call = mAPIService.getMedicalTreatment(new CPFInBody(smallLife.getCpf()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    JsonElement value = response.body().getAsJsonObject().get("commited");

                    if (value.getAsBoolean()) {

                        JsonArray json = ((JsonObject) response.body()).getAsJsonArray("Data");

                        sb = new StringBuilder();
                        sb.append("<br>");
                        if (json.size() > 0) {
                            for (int j = 0; j < json.size(); j++) {
                                String desc = json.get(j).getAsJsonObject().get("Medication").getAsString();
                                sb.append(desc);
                                sb.append("<br><br>");
                            }
                        }else{
                            sb.append(context.getString(R.string.no_found_record_of_clinical_summary));
                            sb.append(msg);
                        }


                        textViewDetails.setText(Html.fromHtml(sb.toString()));

                    } else {

                        int resID = context.getResources().getIdentifier("MSG361", "string", context.getPackageName());
                        String message = context.getResources().getString(resID);
                        showSnackBar(message,TYPE_FAILURE);

                    }
                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                setLoading(false);
                if (context != null) {
                    if (t instanceof SocketTimeoutException)
                        showSnackBar(context.getResources().getString(context.getResources().getIdentifier("MSG362", "string", context.getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        showSnackBar(context.getResources().getString(context.getResources().getIdentifier("MSG361", "string", context.getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
            }
        });
    }

    private void loadAllergies(TextView textViewDetails, String msg) {

        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
        Call<JsonElement> call = mAPIService.getAllergies(new CPFInBody(smallLife.getCpf()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    JsonElement value = response.body().getAsJsonObject().get("commited");

                    if (value.getAsBoolean()) {

                        JsonArray json = ((JsonObject) response.body()).getAsJsonArray("Data");

                        sb = new StringBuilder();
                        sb.append("<br>");
                        if (json.size() > 0) {
                            for (int j = 0; j < json.size(); j++){
                                String desc = json.get(j).getAsJsonObject().get("Name").getAsString();
                                sb.append(desc);
                                sb.append("<br><br>");
                            }
                        }else{
                            sb.append(context.getString(R.string.no_found_record_of_clinical_summary));
                            sb.append(msg);
                        }


                        textViewDetails.setText(Html.fromHtml(sb.toString()));

                    } else {

                        int resID = context.getResources().getIdentifier("MSG361", "string", context.getPackageName());
                        String message = context.getResources().getString(resID);
                        showSnackBar(message,TYPE_FAILURE);

                    }
                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                setLoading(false);
                if (context != null) {
                    if (t instanceof SocketTimeoutException)
                        showSnackBar(context.getResources().getString(context.getResources().getIdentifier("MSG362", "string", context.getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        showSnackBar(context.getResources().getString(context.getResources().getIdentifier("MSG361", "string", context.getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
            }
        });
    }

    private void loadFamilyHistory(TextView textViewDetails, String msg) {

        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
        Call<JsonElement> call = mAPIService.getFamilyHistory(new CPFInBody(smallLife.getCpf()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    JsonElement value = response.body().getAsJsonObject().get("commited");

                    if (value.getAsBoolean()) {

                        JsonArray json = ((JsonObject) response.body()).getAsJsonArray("Data");

                        sb = new StringBuilder();
                        sb.append("<br>");
                        if (json.size() > 0) {
                            for (int j = 0; j < json.size(); j++){
                                String desc = json.get(j).getAsJsonObject().get("Disease").getAsString();
                                sb.append(desc);
                                sb.append("<br><br>");
                            }
                        }else{
                            sb.append(context.getString(R.string.no_found_record_of_clinical_summary));
                            sb.append(msg);
                        }

                        textViewDetails.setText(Html.fromHtml(sb.toString()));

                    } else {

                        int resID = context.getResources().getIdentifier("MSG361", "string", context.getPackageName());
                        String message = context.getResources().getString(resID);
                        showSnackBar(message,TYPE_FAILURE);

                    }
                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                setLoading(false);
                if (context != null) {
                    if (t instanceof SocketTimeoutException)
                        showSnackBar(context.getResources().getString(context.getResources().getIdentifier("MSG362", "string", context.getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        showSnackBar(context.getResources().getString(context.getResources().getIdentifier("MSG361", "string", context.getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
            }
        });
    }

    private void loadSurgeries(TextView textViewDetails, String msg) {

        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
        Call<JsonElement> call = mAPIService.getSurgeries(new CPFInBody(smallLife.getCpf()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    JsonElement value = response.body().getAsJsonObject().get("commited");

                    if (value.getAsBoolean()) {

                        JsonArray json = ((JsonObject) response.body()).getAsJsonArray("Data");

                        sb = new StringBuilder();
                        sb.append("<br>");
                        if (json.size() > 0) {
                            for (int j = 0; j < json.size(); j++){
                                String desc = json.get(j).getAsJsonObject().get("Name").getAsString();
                                sb.append(desc);
                                sb.append("<br><br>");
                            }
                        }else{
                            sb.append(context.getString(R.string.no_found_record_of_clinical_summary));
                            sb.append(msg);
                        }

                        textViewDetails.setText(Html.fromHtml(sb.toString()));

                    } else {

                        int resID = context.getResources().getIdentifier("MSG361", "string", context.getPackageName());
                        String message = context.getResources().getString(resID);
                        showSnackBar(message,TYPE_FAILURE);

                    }
                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                setLoading(false);
                if (context != null) {
                    if (t instanceof SocketTimeoutException)
                        showSnackBar(context.getResources().getString(context.getResources().getIdentifier("MSG362", "string", context.getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        showSnackBar(context.getResources().getString(context.getResources().getIdentifier("MSG361", "string", context.getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
            }
        });
    }
}
