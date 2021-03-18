package br.com.avanade.fahz.fragments.benefits;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.healthplan.BaseHealthControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.Card;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.response.PlanCardsResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class CardsPlanFragment extends Fragment {

    @BindView(R.id.content_cards)
    LinearLayout mContentCards;
    SessionManager sessionManager;
    @BindView(R.id.recyclerViewCards)
    RecyclerView mCardsRecyclerView;

    @BindView(R.id.benefit_edit_text)
    TextView mBenefitText;

    private ProgressDialog mProgressDialog;
    private int idBenefit= 0;
    private String actualDependentCpf;

    private String benefitDescriptionValue= "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan_card, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getContext());
        setLoading(false,"");
        sessionManager = new SessionManager(getActivity().getApplicationContext());


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        mCardsRecyclerView.setHasFixedSize(true);
        mCardsRecyclerView.setLayoutManager(layoutManager);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            idBenefit = bundle.getInt(Constants.BENEFIT_EXTRA, 0);
            benefitDescriptionValue = bundle.getString(Constants.BENEFIT_DESCRIPTION_EXTRA);
        }

        setupUi();
        getDependentes();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setLoading(Boolean loading, String text){
        if(loading){
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
        else{
            mProgressDialog.dismiss();
        }
    }

    private void setupUi()
    {
        if(getActivity() instanceof BaseHealthControlActivity)
            ((BaseHealthControlActivity)getActivity()).toolbarTitle.setText(getActivity().getString(R.string.label_plan_card));

        mBenefitText.setText(benefitDescriptionValue);
    }

    //BUSCA CARTEIRAS
    private void getDependentes() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.getPlanCards(idBenefit);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));

                    if (response.isSuccessful()) {

                        if(response.body().getAsJsonObject().has("messageIdentifier"))
                        {
                            CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                            showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                        }
                        else
                        {
                            PlanCardsResponse requests = new Gson().fromJson((response.body().getAsJsonObject()), PlanCardsResponse.class);
                            CardsAdapter adapter = new CardsAdapter(requests.getCards());
                            mCardsRecyclerView.setAdapter(adapter);
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

                    setLoading(false,"");
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    setLoading(false, "");
                    if(getActivity()!= null) {
                        if (t instanceof SocketTimeoutException)
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else if (t instanceof UnknownHostException)
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else
                            showSnackBar(t.getMessage(), TYPE_FAILURE);
                    }
                }

            });
        }
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mContentCards, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mContentCards, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    private class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardViewHolder> {

        private List<Card> mCardList;

        CardsAdapter(List<Card> list) {
            mCardList = list;
        }

        void updateData(List<Card> dependentList) {
            mCardList.clear();
            mCardList.addAll(dependentList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_plan_card_item,
                    parent, false);
            return new CardViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
            final Card card = mCardList.get(position);
            holder.txtTypeDesc.setText(card.getIsHolder()? "Titular": "Dependente");
            holder.txtName.setText(Utils.AbbreviateName(card.getFullName(),26,false));
            holder.txtOperator.setText(card.getOperator().getDescription());
            holder.txtPlan.setText(card.getPlan().getDescription());
            holder.txtNumber.setText(card.getNumber() == null ? "0" : card.getNumber());
        }

        @Override
        public int getItemCount() {
            return mCardList.size();
        }

        class CardViewHolder extends RecyclerView.ViewHolder {
            private TextView txtTypeDesc;
            private TextView txtName;
            private TextView txtOperator;
            private TextView txtPlan;
            private TextView txtNumber;


            CardViewHolder(View itemView) {
                super(itemView);
                txtTypeDesc = itemView.findViewById(R.id.type_text);
                txtName = itemView.findViewById(R.id.name_edit_text);
                txtOperator = itemView.findViewById(R.id.operator_text);
                txtPlan = itemView.findViewById(R.id.type_plan_text);
                txtNumber = itemView.findViewById(R.id.number_card_text);

            }
        }
    }

}