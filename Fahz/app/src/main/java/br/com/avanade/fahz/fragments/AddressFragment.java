package br.com.avanade.fahz.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import br.com.avanade.fahz.activities.benefits.dentalplan.AdhesionDentalControlActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.AdhesionHealthControlActivity;
import br.com.avanade.fahz.activities.benefits.pharmacy.AdhesionPharmacyControlActivity;
import br.com.avanade.fahz.controls.ZipEditText;
import br.com.avanade.fahz.model.AddressResponse;
import br.com.avanade.fahz.model.ZipResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;

public class AddressFragment extends Fragment {
    private AddressResponse mAddressData;
    private TextInputEditText mZipCode, mStreet, mNumber, mDistrict, mComplement, mCity, mCountry, mState;
    private APIService mAPIService;
    private ProgressDialog mProgressDialog;



    public static AddressFragment newInstance(AddressResponse address) {
        AddressFragment addressFragment = new AddressFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("Address", address);
        addressFragment.setArguments(bundle);

        return addressFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);

        mProgressDialog = new ProgressDialog(getActivity());

        mZipCode = view.findViewById(R.id.zip_edit_Text);
        mStreet = view.findViewById(R.id.street_edit_text);
        mNumber = view.findViewById(R.id.number_edit_text);
        mDistrict = view.findViewById(R.id.district_edit_text);
        mState = view.findViewById(R.id.uf_edit_text);
        mComplement = view.findViewById(R.id.complement_edit_text);
        mCity = view.findViewById(R.id.city_edit_text);
        mCountry = view.findViewById(R.id.country_edit_text);


        mComplement.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mAddressData == null || !mComplement.getText().toString().equals(mAddressData.complement)) {
                    String message = mComplement.getText().toString();
                    if(message.length() >= 20) {
                        mComplement.setError(getResources().getString(getResources().getIdentifier("MSG483", "string", getActivity().getPackageName())));
                    }

                    if(message.length() > 19) {
                        int maxLength = 20;
                        InputFilter[] fArray = new InputFilter[1];
                        fArray[0] = new InputFilter.LengthFilter(maxLength);
                        mComplement.setFilters(fArray);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        mZipCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    setLoading(true);
                    mAPIService = ServiceGenerator.createService(APIService.class);
                    final String zipCode = ZipEditText.unmaskZip(mZipCode.getText().toString());
                    Call<ZipResponse> call = mAPIService.cep(zipCode);

                    call.enqueue(new Callback<ZipResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<ZipResponse> call, @NonNull Response<ZipResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                ZipResponse zipData = response.body();
                                mStreet.setText(zipData.street);
                                mDistrict.setText(zipData.district);
                                mState.setText(zipData.state);
                                mCity.setText(zipData.city);
                                mCountry.setText("BR");

                            } else if (response.code() == 404) {
                                setLoading(false);
                                showSnackFragment(getString(R.string.validateSentData), TYPE_FAILURE);
                            } else {
                                setLoading(false);
                                showSnackFragment(getString(R.string.problemServer), TYPE_FAILURE);
                            }

                            setLoading(false);
                        }

                        @Override
                        public void onFailure(@NonNull Call<ZipResponse> call, @NonNull Throwable t) {
                            if(getActivity()!= null) {
                                if (t instanceof SocketTimeoutException)
                                    showSnackFragment(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                                else if (t instanceof UnknownHostException)
                                    showSnackFragment(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                                else
                                    showSnackFragment(t.getMessage(), TYPE_FAILURE);
                            }
                            setLoading(false);
                        }
                    });
                }
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            mAddressData = bundle.getParcelable("Address");
            updateControls();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            updateData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AddressResponse getAddressData() throws Exception {
        updateData();
        return mAddressData;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser&& getActivity()!=null)
            if(getActivity() instanceof  ProfileActivity)
                ((ProfileActivity)getActivity()).changeHeaderTitle(getString(R.string.text_change_address));
    }

    private void updateData() throws Exception {
        if(mAddressData==null && !mZipCode.getText().toString().equals(""))
            mAddressData= new AddressResponse();

        if (mAddressData != null) {
            mAddressData.zip = ZipEditText.unmaskZip(mZipCode.getText().toString());
            mAddressData.street = mStreet.getText().toString();
            if(!mNumber.getText().toString().equals(""))
                mAddressData.number = Integer.valueOf(mNumber.getText().toString());
            mAddressData.district = mDistrict.getText().toString();
            mAddressData.state =  mState.getText().toString();
            mAddressData.complement = mComplement.getText().toString();
            mAddressData.city = mCity.getText().toString();
            mAddressData.country = mCountry.getText().toString();
        }
    }

    private void updateControls(){
        if (mAddressData != null) {
            mZipCode.setText(mAddressData.zip);
            mStreet.setText(mAddressData.street);
            mNumber.setText(String.valueOf(mAddressData.number));
            mDistrict.setText(mAddressData.district);
            mState.setText(mAddressData.state);
            mComplement.setText(mAddressData.complement);
            mCity.setText(mAddressData.city);
            mCountry.setText(mAddressData.country);

        }
    }

    public boolean validateRequiredData()
    {
        boolean addressDataIsValid = true;
        List<String> addressDataInvalid = new ArrayList<String>();
        StringBuilder messageNotValid = new StringBuilder(getString(R.string.not_valid_personal_data) + " ");
        try
        {
            updateData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(mAddressData != null)
        {
            if (mAddressData.zip == null || mAddressData.zip.equals(""))
            {
                addressDataInvalid.add(getString(R.string.hint_CEP));
                addressDataIsValid =  false;
            }

            if (mAddressData.number == 0)
            {
                addressDataInvalid.add(getString(R.string.hint_number));
                addressDataIsValid =  false;
            }
        }
        else
        {
            addressDataInvalid.add(getString(R.string.fragment_address));
            addressDataIsValid =  false;
        }

        if(!addressDataIsValid)
        {
            int count = 0;
            for (String word : addressDataInvalid)
            {
                messageNotValid.append(word);

                if(count != addressDataInvalid.size()-1)
                {
                    messageNotValid.append(", ");
                }

                count ++;
            }

            showSnackFragment(messageNotValid.toString(), TYPE_FAILURE);
        }
        else if (mAddressData.complement != null && !mAddressData.complement.equals("") && mAddressData.complement.length() > 20) {
            showSnackFragment(getResources().getString(getResources().getIdentifier("MSG483", "string", getActivity().getPackageName())), TYPE_FAILURE);
            addressDataIsValid = false;
        }

        return addressDataIsValid;
    }

    private void setLoading(boolean loading) {

        if (mProgressDialog != null) {
            if (loading) {
                mProgressDialog.setMessage("Consultando CEP");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            } else if(mProgressDialog.isShowing()) {
                Utils.dismissProgressDialog(mProgressDialog);
            }
        }
    }

    private void showSnackFragment(String text, int typeFailure )
    {
        if(getActivity() instanceof  ProfileActivity)
            ((ProfileActivity) getActivity()).showSnackBar(text, typeFailure);
        else if(getActivity() instanceof AdhesionHealthControlActivity)
            ((AdhesionHealthControlActivity) getActivity()).showSnackBar(text, typeFailure);
        else if(getActivity() instanceof AdhesionDentalControlActivity)
            ((AdhesionDentalControlActivity) getActivity()).showSnackBar(text, typeFailure);
        else if(getActivity() instanceof AdhesionPharmacyControlActivity)
            ((AdhesionPharmacyControlActivity) getActivity()).showSnackBar(text, typeFailure);

    }
}
