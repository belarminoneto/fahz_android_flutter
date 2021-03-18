package br.com.avanade.fahz.fragments.benefits.scholarship;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.ProfileActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.AdhesionHealthControlActivity;
import br.com.avanade.fahz.activities.benefits.scholarship.RequestScholarshipControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.PhoneEditText;
import br.com.avanade.fahz.model.Bank;
import br.com.avanade.fahz.model.BankResponse;
import br.com.avanade.fahz.model.ScholarshipLifeResponse;
import br.com.avanade.fahz.model.response.BankListResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;

public class ScholarshipPersonalDataFragment extends Fragment {
    private ScholarshipLifeResponse mData;
    private TextInputEditText mAgency, mAccount, mAgencyDigit, mAccountDigit, mEmail, mPhone;
    private Spinner mBank;
    private SessionManager sessionManager;
    private ProgressDialog mProgressDialog;

    private List<Bank> banksList = new ArrayList<>();
    private List<String> banksListStr = new ArrayList<>();

    public static ScholarshipPersonalDataFragment newInstance(BankResponse bank) {
        ScholarshipPersonalDataFragment bankDataFragment = new ScholarshipPersonalDataFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("Bank", bank);
        bankDataFragment.setArguments(bundle);

        return bankDataFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scholarship_personal_data, container, false);

        mBank = view.findViewById(R.id.bank_spinner);
        mAgency = view.findViewById(R.id.agency_edit_text);
        mAgencyDigit = view.findViewById(R.id.agency_digit_edit_text);
        mAccount = view.findViewById(R.id.account_edit_text);
        mAccountDigit = view.findViewById(R.id.account_digit_edit_text);
        mEmail = view.findViewById(R.id.email_text);
        mPhone = view.findViewById(R.id.phone_text);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.bank_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBank.setAdapter(adapter);
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        mProgressDialog = new ProgressDialog(getActivity());

        mData = ((RequestScholarshipControlActivity) getActivity()).getDataInformation();
        populateListBank();


        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser&& getActivity()!=null)
            if(getActivity() instanceof  ProfileActivity)
                ((ProfileActivity)getActivity()).changeHeaderTitle(getString(R.string.text_change_bank_data));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public Pair<ScholarshipLifeResponse,Boolean> getData(){
        Boolean canChange = updateData();
        return new Pair<ScholarshipLifeResponse,Boolean>(mData,canChange);
    }

    private Boolean updateData() {
        Boolean canChange = false;
        if (mData != null) {

            int resID = 0;
            if (mData.getBankData() == null)
                mData.setBankData(new BankResponse());

            if (banksListStr != null && banksListStr.size() > 0) {
                if (banksListStr.indexOf(mBank.getSelectedItem()) == 0)
                    resID = getResources().getIdentifier("MSG291", "string", getActivity().getPackageName());
                else
                    mData.getBankData().bank = banksList.get(banksListStr.indexOf(mBank.getSelectedItem()) - 1).getBankId();

                if (mAgency.getText().toString().equals(""))
                    resID = getResources().getIdentifier("MSG292", "string", getActivity().getPackageName());
                else
                    mData.getBankData().agency = mAgency.getText().toString();

                if (!mAgencyDigit.getText().toString().equals(""))
                    mData.getBankData().agencydigit = mAgencyDigit.getText().toString();

                if (mAccount.getText().toString().equals(""))
                    resID = getResources().getIdentifier("MSG294", "string", getActivity().getPackageName());
                else
                    mData.getBankData().account = mAccount.getText().toString();

                if (mAccountDigit.getText().toString().equals(""))
                    resID = getResources().getIdentifier("MSG293", "string", getActivity().getPackageName());
                else
                    mData.getBankData().accountdigit = mAccountDigit.getText().toString();
            }

            if (mEmail.getText().toString().equals(""))
                resID = getResources().getIdentifier("MSG296", "string", getActivity().getPackageName());
            else
                mData.setEmail(mEmail.getText().toString());

            if (mPhone.getText().toString().equals(""))
                resID = getResources().getIdentifier("MSG295", "string", getActivity().getPackageName());
            else
                mData.setPhone(PhoneEditText.unmaskPhone(mPhone.getText().toString()));


            if (resID != 0) {
                String message = getResources().getString(resID);
                Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
                canChange = false;
            } else {
                ((RequestScholarshipControlActivity) getActivity()).setDataInformation(mData);
                canChange = true;
            }
        }

        return canChange;
    }

    private void updateControls(){
        if (mData != null && mData.getBankData() != null ) {
            for (Bank bank: banksList) {
                if(bank.getBankId() == mData.getBankData().bank) {
                    mBank.setSelection(banksListStr.indexOf(bank.getDescription()));
                    break;
                }
            }

            mAgency.setText(String.valueOf(mData.getBankData().agency));
            mAgencyDigit.setText(mData.getBankData().agencydigit != null ?String.valueOf( mData.getBankData().agencydigit): null);
            mAccount.setText(String.valueOf( mData.getBankData().account));
            mAccountDigit.setText(String.valueOf( mData.getBankData().accountdigit));

            mEmail.setText(mData.getEmail());
            mPhone.setText(PhoneEditText.maskPhone(mData.getPhone()));
        }
    }

    //POPULA O COMBO COM A INFORMACAO DOS BANCOS
    private void populateListBank() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<BankListResponse> call = mAPIService.getBanks();
            call.enqueue(new Callback<BankListResponse>() {
                @Override
                public void onResponse(@NonNull Call<BankListResponse> call, @NonNull Response<BankListResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        BankListResponse bankResponse = response.body();
                        banksList = new ArrayList<>();
                        banksListStr = new ArrayList<>();

                        if (bankResponse.getCount() > 0) {
                            banksList.addAll(bankResponse.getBanks());
                        }

                        banksListStr.add("Selecione");
                        for (Bank bank : banksList) {
                            banksListStr.add(bank.getDescription());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                                R.layout.spinner_layout, banksListStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mBank.setAdapter(adapter);

                        updateControls();
                    } else if (response.code() == 404) {
                        showSnackFragment(getString(R.string.validateSentData), TYPE_FAILURE);
                    } else {

                        showSnackFragment(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                    setLoading(false,"");
                }

                @Override
                public void onFailure(@NonNull Call<BankListResponse> call, @NonNull Throwable t) {
                    setLoading(false,"");
                    if(getActivity()!= null) {
                        if (t instanceof SocketTimeoutException)
                            showSnackFragment(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else if (t instanceof UnknownHostException)
                            showSnackFragment(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else
                            showSnackFragment(t.getMessage(), TYPE_FAILURE);
                    }
                }

            });
        }
    }

    private void setLoading(boolean loading, String text){
        if(loading){
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
        else{
            mProgressDialog.dismiss();
        }
    }

    private void showSnackFragment(String text, int typeFailure )
    {
        if(getActivity() instanceof  ProfileActivity)
            ((ProfileActivity) getActivity()).showSnackBar(text, typeFailure);
        else if(getActivity() instanceof AdhesionHealthControlActivity)
            ((AdhesionHealthControlActivity) getActivity()).showSnackBar(text, typeFailure);
    }
}
