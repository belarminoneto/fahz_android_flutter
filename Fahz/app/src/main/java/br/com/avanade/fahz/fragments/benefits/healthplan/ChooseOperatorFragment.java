package br.com.avanade.fahz.fragments.benefits.healthplan;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.healthplan.AdhesionHealthControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.OperatorByBenefit;
import br.com.avanade.fahz.model.benefits.OperatorsByBenefitBody;
import br.com.avanade.fahz.model.response.OperatorsResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;

public class ChooseOperatorFragment extends Fragment {

    private APIService mAPIService;
    private ProgressDialog mProgressDialog;
    ExpandableListAdapter listAdapter;
    private static boolean mCanChooseOperator;
    SessionManager sessionManager;
    private List<OperatorByBenefit> exceptionOperators;
    private static int mIdBenefit;

    @BindView(R.id.simpleExpandableListView)
    ExpandableListView expListView;
    @BindView(R.id.explain_choose_operator)
    TextView mExplainChooseOperator;
    @BindView(R.id.header)
    LinearLayout mDescriptionHeader;
    @BindView(R.id.list_operators)
    LinearLayout mListOperators;
    @BindView(R.id.lblOperatorName)
    TextView mOperatorName;

    public static ChooseOperatorFragment newInstance(boolean canChooseOperator, int benefit) {
        ChooseOperatorFragment chooseFragment = new ChooseOperatorFragment();
        Bundle bundle = new Bundle();
        mCanChooseOperator = canChooseOperator;
        mIdBenefit = benefit;
        chooseFragment.setArguments(bundle);

        return chooseFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_operator, container, false);
        ButterKnife.bind(this,view);
        mProgressDialog = new ProgressDialog(getActivity());

        sessionManager = new SessionManager(getActivity().getApplicationContext());


        //CASO SEJA UMA EDIÇÃO ESSA PROPRIEDADE DEFINE SE É POSSÍVEL ESCOLHER UMA OUTRA OPERADORA
        if(mCanChooseOperator) {
            mExplainChooseOperator.setVisibility(View.VISIBLE);
            mDescriptionHeader.setVisibility(View.VISIBLE);
            mListOperators.setVisibility(View.VISIBLE);


            //COS DOIS METODOS CONTROLAM A CHAMADA DE UM METODO NA ACTIVITY PARA QUE SEJA SALVO O ID DA OPERADORA, CASO O USUÁRIO TENHA
            //ESCOLHIDO UMA DIFERENTE DO PADRÃO
            expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int previousGroup = -1;

                @Override
                public void onGroupExpand(int groupPosition) {
                    if(groupPosition != previousGroup)
                        expListView.collapseGroup(previousGroup);
                    previousGroup = groupPosition;

                  checkedChange(listAdapter.getId(groupPosition));
                }
            });

            expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                int previousGroup = -1;

                @Override
                public void onGroupCollapse(int groupPosition) {
                    checkedChange(0);
                }
            });

        }
        else
        {
            mExplainChooseOperator.setVisibility(View.GONE);
            mDescriptionHeader.setVisibility(View.GONE);
            mListOperators.setVisibility(View.GONE);
        }


        //BUSCAR OPERADORAS
        getDefaultOperator();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

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

    //POPULA A INFORMACAO DE OPERADORA PADRAO
    private void getDefaultOperator() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true,getString(R.string.loading_searching));

            String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();

            APIService mAPIService = ServiceGenerator.createService(APIService.class);
            Call<JsonElement> call = mAPIService.getOperatorsByBenefit(new OperatorsByBenefitBody(mCpf, mIdBenefit));
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        boolean hasDefault = false;
                        OperatorsResponse operators = new Gson().fromJson((response.body().getAsJsonObject()), OperatorsResponse.class);
                        exceptionOperators = new ArrayList<>();

                        for (OperatorByBenefit op: operators.getOperatorByBenefits()) {
                            if(op.getIsAllowed()) {
                                hasDefault = true;
                                mOperatorName.setText(op.getDescription());
                            }
                            else
                            {
                                exceptionOperators.add(op);
                            }

                        }

//                        //METODO QUE MOSTRA AS OPERADORAS EM UM EXPANDABLELIST PARA CASO O USUARIO DESEJE ESCOLHER UMA DIFERENTE DA PADRÃO
//                        HashMap<OperatorByBenefit, List<String>> listDataChild = new HashMap<OperatorByBenefit, List<String>>();
//
//                        List<String> top250 = new ArrayList<String>();
//                        top250.add("Esta operadora não é elegível. será necessário a abertura de uma exceção.");
//
//                        List<String> nowShowing = new ArrayList<String>();
//                        nowShowing.add("Esta operadora não é elegível. será necessário a abertura de uma exceção.");
//                        listDataChild.put(exceptionOperators.get(0), top250); // Header, Child data

                        //listAdapter = new ExpandableListAdapter(getActivity(), exceptionOperators, listDataChild);

                        //expListView.setAdapter(listAdapter);
                        mOperatorName.requestFocus();

                        if(!hasDefault) {
                            int resID = getResources().getIdentifier("MSG135", "string", getActivity().getPackageName());
                            String message = getResources().getString(resID);
                            mOperatorName.setText("");
                            showSnackFragment(message,TYPE_FAILURE);
                        }

                        setLoading(false,"");
                    } else {
                        if(response.raw().message()!=null && !response.raw().message().equals(""))
                            showSnackFragment(response.raw().message(), TYPE_FAILURE);
                        else
                            showSnackFragment(getString(R.string.problemServer), TYPE_FAILURE);
                        setLoading(false,"");
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
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

    public void checkedChange(int idOperator)
    {
        if(getActivity()!= null && getActivity() instanceof AdhesionHealthControlActivity)
            ((AdhesionHealthControlActivity)getActivity()).registerExceptionOperator(idOperator);
    }

    private void showSnackFragment(String text, int typeFailure )
    {
        if(getActivity() instanceof AdhesionHealthControlActivity)
            ((AdhesionHealthControlActivity) getActivity()).showSnackBar(text, typeFailure);
    }

    class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<OperatorByBenefit> mListDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<OperatorByBenefit, List<String>> mListDataChild;

        public ExpandableListAdapter(Context context, List<OperatorByBenefit> listDataHeader, HashMap<OperatorByBenefit, List<String>> listDataChild) {
            this.context = context;
            this.mListDataHeader = listDataHeader;
            this.mListDataChild = listDataChild;
        }

        @Override
        public int getGroupCount() {
            return this.mListDataHeader.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            if(this.mListDataChild!= null && this.mListDataChild.get(this.mListDataHeader.get(groupPosition))!= null) {
                return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size();
            }
            else
                return 0;

        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.mListDataHeader.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if(this.mListDataChild!=null)
                return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).get(childPosition);
            else
                return 0;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return mListDataHeader.get(groupPosition).getId();
        }

        public int getId(int groupPosition) {
            return mListDataHeader.get(groupPosition).getId();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent) {
            final OperatorByBenefit groupText = (OperatorByBenefit) getGroup(groupPosition);
            if (convertView == null) {

                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_operator_group, null);
            }

            TextView textGroup = convertView.findViewById(R.id.lblListHeader);
            textGroup.setText(groupText.getDescription());
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            final String child = (String) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_operator_item, parent,false);
            }

            TextView txtListChild = convertView.findViewById(R.id.lblListItem);
            txtListChild.setText(child);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            LogUtils.info("MainActivity", "isChildSelectable - groupPosition: " + groupPosition
                    + " childPosition: " + childPosition);
            return groupPosition != 0;
        }
    }
}
