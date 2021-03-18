package br.com.avanade.fahz.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
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

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.ProfileActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.AdhesionHealthControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.Bank;
import br.com.avanade.fahz.model.BankResponse;
import br.com.avanade.fahz.model.response.BankListResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.ValidateBankDataErrorException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;

public class BankDataFragment extends Fragment {
    private BankResponse mBankData;
    private TextInputEditText mAgency, mAccount, mAgencyDigit, mAccountDigit;
    private Spinner mBank;
    private SessionManager sessionManager;
    private ProgressDialog mProgressDialog;

    private List<Bank> banksList = new ArrayList<>();
    private List<String> banksListStr = new ArrayList<>();

    public static BankDataFragment newInstance(BankResponse bank) {
        BankDataFragment bankDataFragment = new BankDataFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("Bank", bank);
        bankDataFragment.setArguments(bundle);

        return bankDataFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank_data, container, false);

        mBank = view.findViewById(R.id.bank_spinner);
        mAgency = view.findViewById(R.id.agency_edit_text);
        mAgencyDigit = view.findViewById(R.id.agency_digit_edit_text);
        mAccount = view.findViewById(R.id.account_edit_text);
        mAccountDigit = view.findViewById(R.id.account_digit_edit_text);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.bank_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBank.setAdapter(adapter);
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        mProgressDialog = new ProgressDialog(getActivity());

        Bundle bundle = getArguments();
        if (bundle != null) {
            mBankData = bundle.getParcelable("Bank");
            populateListBank();
        }

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
        try {
            updateData();
        } catch (ValidateBankDataErrorException e) {
            LogUtils.error("BankDataFragment", e);
        }

    }

    public BankResponse getBankData() throws ValidateBankDataErrorException {
        updateData();
        return mBankData;
    }

    private void updateData() throws ValidateBankDataErrorException {

        int countValidade = 0;

        if (mBankData == null)
            mBankData = new BankResponse();

        if (banksListStr != null && banksListStr.size() > 0) {
            if (banksListStr.indexOf(mBank.getSelectedItem()) != 0)
                mBankData.bank = banksList.get(banksListStr.indexOf(mBank.getSelectedItem()) - 1).getBankId();
            else if (banksListStr.indexOf(mBank.getSelectedItem()) == 0 && (!mAgency.getText().toString().equals("") || !mAccount.getText().toString().equals(""))) {

                int resID = getResources().getIdentifier("MSG353", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                throw new ValidateBankDataErrorException(message);
            } else
                countValidade++;

            if (!mAgency.getText().toString().equals(""))
                mBankData.agency = mAgency.getText().toString();
            else if (mAgency.getText().toString().equals("") && (banksListStr.indexOf(mBank.getSelectedItem()) != 0 || !mAccount.getText().toString().equals(""))) {

                int resID = getResources().getIdentifier("MSG354", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                throw new ValidateBankDataErrorException(message);
            } else
                countValidade++;

            if (!mAgencyDigit.getText().toString().equals(""))
                mBankData.agencydigit = mAgencyDigit.getText().toString();


            if (!mAccount.getText().toString().equals(""))
                mBankData.account = mAccount.getText().toString();
            else if (mAccount.getText().toString().equals("") && (banksListStr.indexOf(mBank.getSelectedItem()) != 0 || !mAccount.getText().toString().equals(""))) {

                int resID = getResources().getIdentifier("MSG355", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                throw new ValidateBankDataErrorException(message);
            } else
                countValidade++;

            if (!mAccountDigit.getText().toString().equals(""))
                mBankData.accountdigit = mAccountDigit.getText().toString();


            if (countValidade == 3)
                mBankData = null;

        }
    }

    private void updateControls(){
        if (mBankData != null) {
            for (Bank bank: banksList) {
                if(bank.getBankId() == mBankData.bank) {
                    mBank.setSelection(banksListStr.indexOf(bank.getDescription()));
                    break;
                }
            }

            mAgency.setText(String.valueOf(mBankData.agency));
            mAgencyDigit.setText(mBankData.agencydigit);
            mAccount.setText(String.valueOf(mBankData.account));
            mAccountDigit.setText(String.valueOf(mBankData.accountdigit));
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
                    if(t instanceof SocketTimeoutException)
                        showSnackFragment (getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else if(t instanceof UnknownHostException)
                        showSnackFragment (getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else
                        showSnackFragment(t.getMessage(), TYPE_FAILURE);
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
