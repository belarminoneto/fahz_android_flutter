package br.com.avanade.fahz.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.Benefit;
import br.com.avanade.fahz.model.Benefits;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.UserBenefits;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class BenefitsActivity extends NavDrawerBaseActivity {

    public static final String TAG = "BenefitsActivity";
    private View mView;
    private ExpandableListView mExpandableListView;
    private ProgressBar mProgressBarBenefits;
    private List<Benefits> mListBenefits;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_benefits);

        toolbarTitle.setText(getString(R.string.label_benefits));

        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        String name = FahzApplication.getInstance().getFahzClaims().getName();

        mView = findViewById(R.id.benefit_container);
        mExpandableListView = findViewById(R.id.list_benefits);
        mProgressBarBenefits = findViewById(R.id.progressBarBenefits);
        Spinner nameSpinner = findViewById(R.id.spinner_name);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Collections.singletonList(name));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameSpinner.setAdapter(adapter);

        loadUserBenefitsData(cpf);

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                LogUtils.debug(TAG, "onChildClick: TEST 1");
                return false;
            }
        });

        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                LogUtils.debug(TAG, "onGroupClick: TESTE 2");
                return false;
            }
        });

        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                LogUtils.debug(TAG, "onGroupExpand: TEST 3");
            }
        });
    }

    private void loadUserBenefitsData(String cpf) {
        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<UserBenefits> call = apiService.queryUserBenefits(new CPFInBody(cpf));
        call.enqueue(new Callback<UserBenefits>() {
            @Override
            public void onResponse(@NonNull Call<UserBenefits> call, @NonNull Response<UserBenefits> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    UserBenefits userBenefitsResponse = response.body();
                    if (userBenefitsResponse != null) {
                        mListBenefits = userBenefitsResponse.benefits;
                        showExpandableListView();
                        mProgressBarBenefits.setVisibility(View.INVISIBLE);
                    }

                } else {
                    showSnackBar("Você não possui os benefícios", TYPE_FAILURE);
                    mProgressBarBenefits.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserBenefits> call, @NonNull Throwable t) {
                if(t instanceof SocketTimeoutException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if(t instanceof UnknownHostException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
                mProgressBarBenefits.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showExpandableListView() {
        List<HeaderObject> listBenefitsHeaders = popularBenefitsHeaderData();
        HashMap<String, List<String>> listBenefitsChilds = popularBenefitsChildData();
        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(this,
                listBenefitsHeaders, listBenefitsChilds);
        mExpandableListView.setAdapter(expandableListAdapter);
    }

    private List<HeaderObject> popularBenefitsHeaderData() {
        List<HeaderObject> listBenefitsHeader = new ArrayList<>();

        for (Benefits benefits : mListBenefits) {
            Benefit benefit = benefits.benefit;
            HashMap<Boolean, Integer> status = new HashMap<>();
            status.put(benefits.userHasIt, benefits.status);
            HeaderObject headerObject = new HeaderObject(benefit.description, status);
            listBenefitsHeader.add(headerObject);
        }

        return listBenefitsHeader;
    }

    private HashMap<String, List<String>> popularBenefitsChildData() {

        HashMap<String, List<String>> listBenefitsChild = new HashMap<>();

        for (Benefits benefits : mListBenefits) {
            Benefit benefit = benefits.benefit;
            List<String> listMessages = new ArrayList<>();
            listMessages.add(benefits.message);
            listBenefitsChild.put(benefit.description, listMessages);
        }

        return listBenefitsChild;
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context mContext;
        private List<HeaderObject> mListDataHeader;
        private HashMap<String, List<String>> mListDataChild;

        ExpandableListAdapter(Context context, List<HeaderObject> listDataHeader,
                                     HashMap<String, List<String>> listChildData) {
            this.mContext = context;
            this.mListDataHeader = listDataHeader;
            this.mListDataChild = listChildData;
            LogUtils.debug(TAG, "ExpandableListAdapter: list data child size: " + mListDataChild.size());
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this.mListDataChild.get(this.mListDataHeader.get(groupPosition).description).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (layoutInflater != null) {
                    convertView = layoutInflater.inflate(R.layout.list_benefits_item, null);
                    TextView txtListChild = convertView.findViewById(R.id.text_message);
                    txtListChild.setText(childText);
                }
            }

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.mListDataChild.get(this.mListDataHeader.get(groupPosition).description).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.mListDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.mListDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            HeaderObject valuesHeader = (HeaderObject) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (layoutInflater != null) {
                    convertView = layoutInflater.inflate(R.layout.list_benefits_group, null);

                    TextView textBenefitType = convertView.findViewById(R.id.text_benefit_type);
                    textBenefitType.setText(valuesHeader.description);

                    ImageView imgStatus = convertView.findViewById(R.id.imagem_status);
                    if (valuesHeader.status.containsKey(true)) {
                        // futuro identificar qual tipo de status
                        // valuesHeader.status.get(true);
                        Drawable statusOK = getResources().getDrawable(R.drawable.ic_check_green);
                        imgStatus.setImageDrawable(statusOK);
                    }
                }
            }

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private class HeaderObject {
        public String description;
        public HashMap<Boolean, Integer> status;

        public HeaderObject(String description, HashMap<Boolean, Integer> status) {
            this.description = description;
            this.status = status;
        }
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mView, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }
}
