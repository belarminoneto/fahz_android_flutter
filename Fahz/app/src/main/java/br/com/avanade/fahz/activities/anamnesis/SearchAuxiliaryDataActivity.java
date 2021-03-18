package br.com.avanade.fahz.activities.anamnesis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.avanade.fahz.Adapter.AuxDataAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.interfaces.OnAuxDataClickListener;
import br.com.avanade.fahz.interfaces.OnSessionListener;
import br.com.avanade.fahz.model.anamnesisModel.AuxDataBridge;
import br.com.avanade.fahz.model.anamnesisModel.DataRecord;
import br.com.avanade.fahz.model.anamnesisModel.SearchAuxiliaryDataRequest;
import br.com.avanade.fahz.model.anamnesisModel.SearchAuxiliaryDataResponse;
import br.com.avanade.fahz.util.AnamnesisUtils;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.AUX_DATA_INTENT;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.AUX_DATA_TYPE;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.CAN_MULTI_OPTIONS_AUX_DATA;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.UNAUTHORIZED_ACCESS;

public class SearchAuxiliaryDataActivity extends BaseAnamnesisActivity implements OnAuxDataClickListener {

    @BindView(R.id.rvAuxData)
    RecyclerView rvAuxData;

    @BindView(R.id.edtFilter)
    EditText mEdtFilter;

    @BindView(R.id.txtAlert)
    TextView txtAlert;

    @BindView(R.id.linearDataSelected)
    LinearLayout verticalLinearLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Timer searchTimer;
    private List<AuxDataBridge> selectedAuxDataList;
    private int auxDataType;
    private boolean canMultipleOptions;

    @Override
    @SuppressWarnings("unchecked cast")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            selectedAuxDataList = (List<AuxDataBridge>) getIntent().getExtras()
                    .getSerializable(AUX_DATA_INTENT);
            auxDataType = getIntent().getIntExtra(AUX_DATA_TYPE, 0);
            canMultipleOptions = getIntent().getBooleanExtra(CAN_MULTI_OPTIONS_AUX_DATA, false);
        }
        setupUi();
        setupTxtDataSelected();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_search_auxiliary_data;
    }

    private void setupUi() {
        AuxDataAdapter mAdapter = new AuxDataAdapter(this);
        rvAuxData.setAdapter(mAdapter);
        rvAuxData.setLayoutManager(new LinearLayoutManager(this));

        mEdtFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (s.length() > 2) {
                    restartTimer(s.toString().replace(",", "|"));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        onBack();
        super.onBackPressed();
    }

    @OnClick(R.id.imgBack)
    public void onBack() {
        finish();
    }

    @OnClick(R.id.btnConfirm)
    public void onConfirmClick() {
        Intent intent = new Intent();
        intent.putExtra(AUX_DATA_INTENT, (Serializable) selectedAuxDataList);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.btnClean)
    public void onCleanClick() {
        selectedAuxDataList = new ArrayList<>();
        ((AuxDataAdapter) rvAuxData.getAdapter()).deselectAllAuxData();
        setupTxtDataSelected();
    }

    void changeProgressBarVisibility(final boolean visible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(visible ? VISIBLE : INVISIBLE);
            }
        });
    }

    void restartTimer(final String searchedText) {
        if (searchTimer != null) {
            searchTimer.cancel();
            searchTimer.purge();
        }
        searchTimer = new Timer();
        searchTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                searchAuxData(searchedText);
            }
        }, 500);
    }

    void searchAuxData(final String searchedText) {
        txtAlert.setVisibility(INVISIBLE);
        changeProgressBarVisibility(true);
        Call<SearchAuxiliaryDataResponse> call = mApiService.searchAuxData(new SearchAuxiliaryDataRequest(auxDataType, searchedText));
        call.enqueue(new Callback<SearchAuxiliaryDataResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchAuxiliaryDataResponse> call, @NonNull Response<SearchAuxiliaryDataResponse> response) {
                if (response.raw().code() == UNAUTHORIZED_ACCESS) {
                    getSession(new OnSessionListener() {
                        @Override
                        public void onSessionSuccess() {
                            searchAuxData(searchedText);
                        }
                    });
                } else {
                    SearchAuxiliaryDataResponse body = response.body();
                    if (body == null) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            String error = jObjError.getString("Message");
                            showAlert(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showAlert(getResources().getString(R.string.error_while_searching));
                        }
                    } else {
                        setupSearchAuxDataResponse(body);
                    }
                }
                changeProgressBarVisibility(false);
            }

            @Override
            public void onFailure(@NonNull Call<SearchAuxiliaryDataResponse> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())));
                else if (t instanceof UnknownHostException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())));
                else
                    showAlert(t.getMessage());

                changeProgressBarVisibility(false);
            }
        });
    }

    void setupSearchAuxDataResponse(SearchAuxiliaryDataResponse searchedData) {
        List<AuxDataBridge> auxDataList = setupAuxDataBridge(searchedData);
        for (AuxDataBridge selectedAuxData : selectedAuxDataList) {
            for (AuxDataBridge auxData : auxDataList) {
                if (selectedAuxData.getId().equalsIgnoreCase(auxData.getId())) {
                    auxData.setSelected(true);
                    break;
                }
            }
        }
        ((AuxDataAdapter) rvAuxData.getAdapter()).setItems(auxDataList);
        if (searchedData.getExceededLimit() == 1) {
            txtAlert.setVisibility(VISIBLE);
        } else {
            txtAlert.setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onClickAuxData(@NonNull AuxDataBridge auxData) {
        if (selectedAuxDataList == null) {
            selectedAuxDataList = new ArrayList<>();
        }
        boolean exist = false;
        try {
            for (AuxDataBridge auxDataTemp : selectedAuxDataList) {
                if (auxDataTemp.getId().equalsIgnoreCase(auxData.getId())) {
                    selectedAuxDataList.remove(auxDataTemp);
                    auxData.setSelected(false);
                    rvAuxData.getAdapter().notifyDataSetChanged();
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                if (!canMultipleOptions && selectedAuxDataList.size() > 0) {
                    showAlert(getString(R.string.question_only_allows_one_option));
                } else {
                    selectedAuxDataList.add(auxData);
                    auxData.setSelected(true);
                    rvAuxData.getAdapter().notifyDataSetChanged();
                }
            }
            setupTxtDataSelected();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<AuxDataBridge> setupAuxDataBridge(SearchAuxiliaryDataResponse searchedData) {
        List<AuxDataBridge> auxDataList = new ArrayList<>();

        for (DataRecord dataRecord : searchedData.getDataRecords()) {
            AuxDataBridge auxDataBridge = new AuxDataBridge();
            switch (searchedData.getSend()) {
                case 0:
                    auxDataBridge.setId(String.valueOf(dataRecord.getId()));
                    break;
                case 1:
                    auxDataBridge.setId(String.valueOf(dataRecord.getD1()));
                    break;
                case 2:
                    auxDataBridge.setId(String.valueOf(dataRecord.getD2()));
                    break;
                case 3:
                    auxDataBridge.setId(String.valueOf(dataRecord.getD3()));
                    break;
                case 4:
                    auxDataBridge.setId(String.valueOf(dataRecord.getD4()));
                    break;
            }
            switch (searchedData.getShow()) {
                case 0:
                    auxDataBridge.setTextShown(String.valueOf(dataRecord.getId()));
                    break;
                case 1:
                    auxDataBridge.setTextShown(String.valueOf(dataRecord.getD1()));
                    break;
                case 2:
                    auxDataBridge.setTextShown(String.valueOf(dataRecord.getD2()));
                    break;
                case 3:
                    auxDataBridge.setTextShown(String.valueOf(dataRecord.getD3()));
                    break;
                case 4:
                    auxDataBridge.setTextShown(String.valueOf(dataRecord.getD4()));
                    break;
            }

            for (int i = 1; i < searchedData.getFields().size(); i++) {
                String title = searchedData.getFields().get(i);
                switch (i) {
                    case 1:
                        auxDataBridge.setTitle1(title);
                        auxDataBridge.setDesc1(dataRecord.getD1());
                        break;
                    case 2:
                        auxDataBridge.setTitle2(title);
                        auxDataBridge.setDesc2(dataRecord.getD2());
                        break;
                    case 3:
                        auxDataBridge.setTitle3(title);
                        auxDataBridge.setDesc3(dataRecord.getD3());
                        break;
                    case 4:
                        auxDataBridge.setTitle4(title);
                        auxDataBridge.setDesc4(dataRecord.getD4());
                        break;
                }
            }
            auxDataList.add(auxDataBridge);
        }
        return auxDataList;
    }

    private void setupTxtDataSelected() {
        verticalLinearLayout.removeAllViews();
        if (selectedAuxDataList.isEmpty()) {
            return;
        }
        View view;
        TextView textView;
        for (final AuxDataBridge auxData : selectedAuxDataList) {
            view = getLayoutInflater().inflate(R.layout.textfield_auxdata, verticalLinearLayout, false);
            textView = view.findViewById(R.id.txtDataSelected);
            textView.setText(auxData.getTextShownFirstLetterCapitalized());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAuxData(auxData);
                    ((AuxDataAdapter) rvAuxData.getAdapter()).setLastUnselectedData(auxData);
                }
            });
            verticalLinearLayout.addView(view);
        }

        verticalLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                verticalLinearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                setupDataLinear();
            }
        });
    }

    void setupDataLinear() {
        View[] views = new View[verticalLinearLayout.getChildCount()];
        for (int i = 0; i < verticalLinearLayout.getChildCount(); i++) {
            views[i] = verticalLinearLayout.getChildAt(i);
        }
        verticalLinearLayout.removeAllViews();

        LinearLayout horizontalLinearLayout = new LinearLayout(this);
        setupHorizontalLinearLayout(horizontalLinearLayout);
        verticalLinearLayout.addView(horizontalLinearLayout);

        float xDisplayDpi = getResources().getDisplayMetrics().xdpi;
        float totalViewWidthDP = 0;
        for (View view : views) {
            float viewWidthDP = AnamnesisUtils.convertPixelsToDp(view.getWidth(), this);
            totalViewWidthDP += viewWidthDP;

            if (totalViewWidthDP > xDisplayDpi) {
                horizontalLinearLayout = new LinearLayout(this);
                setupHorizontalLinearLayout(horizontalLinearLayout);
                verticalLinearLayout.addView(horizontalLinearLayout);
                totalViewWidthDP = viewWidthDP;
            }
            horizontalLinearLayout.addView(view);
        }
    }

    private void setupHorizontalLinearLayout(LinearLayout linearLayout) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
    }
}
