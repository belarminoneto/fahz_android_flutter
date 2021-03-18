package br.com.avanade.fahz.fragments.benefits.healthplan;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.MainActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.AdhesionHealthControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.ChangeBenefit;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.OperatorByBenefit;
import br.com.avanade.fahz.model.benefits.OperatorsByBenefitBody;
import br.com.avanade.fahz.model.response.OperatorsResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.BRADESCO;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class ChangeOperatorFragment extends Fragment {

    private APIService mAPIService;
    private ProgressDialog mProgressDialog;
    ExpandableListAdapter listAdapter;
    private static boolean mCanChooseOperator;
    SessionManager sessionManager;
    private static int mIdBenefit;
    private Dialog dialog;
    private Dialog dialogInfo;
    private ChangeBenefit changeBenefit;

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
    @BindView(R.id.change_operator_content)
    RelativeLayout mContent;

    private OperatorByBenefit selectedOperador;
    private OperatorByBenefit currentOperator;
    EditText justification;

    public static ChangeOperatorFragment newInstance(boolean canChooseOperator, int benefit) {
        ChangeOperatorFragment chooseFragment = new ChangeOperatorFragment();
        Bundle bundle = new Bundle();
        mCanChooseOperator = canChooseOperator;
        mIdBenefit = benefit;
        chooseFragment.setArguments(bundle);

        return chooseFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_operator, container, false);
        ButterKnife.bind(this, view);
        mProgressDialog = new ProgressDialog(getActivity());

        sessionManager = new SessionManager(getActivity().getApplicationContext());


        //CASO SEJA UMA EDIÇÃO ESSA PROPRIEDADE DEFINE SE É POSSÍVEL ESCOLHER UMA OUTRA OPERADORA
        if (mCanChooseOperator) {
            mExplainChooseOperator.setVisibility(View.VISIBLE);
            mDescriptionHeader.setVisibility(View.VISIBLE);
            mListOperators.setVisibility(View.VISIBLE);


            //COS DOIS METODOS CONTROLAM A CHAMADA DE UM METODO NA ACTIVITY PARA QUE SEJA SALVO O ID DA OPERADORA, CASO O USUÁRIO TENHA
            //ESCOLHIDO UMA DIFERENTE DO PADRÃO
            expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int previousGroup = -1;

                @Override
                public void onGroupExpand(int groupPosition) {
                    if (groupPosition != previousGroup)
                        expListView.collapseGroup(previousGroup);
                    previousGroup = groupPosition;

                    selectedOperador = (OperatorByBenefit) listAdapter.getGroup(groupPosition);

//                    TextView label = null;
//                    for (int i = 0; i < expListView.getChildCount(); i++) {
//
//                        View v = expListView.getChildAt(i);
//                        label = v.findViewById(R.id.lblListHeader);
//                        if (label != null && label.getTag().equals(selectedOperador.getId()))
//                            break;
//                    }
//
//                    if (label != null) {
//                        switch (selectedOperador.getId()) {
//                            case Constants.CNU:
//                                label.setTextColor(getResources().getColor(R.color.green));
//                                break;
//                            case Constants.SUL:
//                                label.setText("PORQUE");
//                                label.setTextColor(getResources().getColor(R.color.operator_blue));
//                                break;
//                            case Constants.BRADESCO:
//                                label.setTextColor(getResources().getColor(R.color.operator_red));
//                                break;
//                        }
//                    }
                }
            });

            expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                int previousGroup = -1;

                @Override
                public void onGroupCollapse(int groupPosition) {
                    TextView label = null;
                    if(selectedOperador!= null) {
                        for (int i = 0; i < expListView.getChildCount(); i++) {

                            View v = expListView.getChildAt(i);
                            label = v.findViewById(R.id.lblListHeader);
                            if (label != null && label.getTag().equals(selectedOperador.getId()))
                                break;
                        }
                        if (label != null)
                            label.setTextColor(getResources().getColor(R.color.grey_light));
                    }

                    selectedOperador = null;
                }
            });

        } else {
            mExplainChooseOperator.setVisibility(View.GONE);
            mDescriptionHeader.setVisibility(View.GONE);
            mListOperators.setVisibility(View.GONE);
        }


        //BUSCAR OPERADORAS
        getDefaultOperator();

        return view;
    }

    public View getGroupView(ExpandableListView listView, int groupPosition) {
        long packedPosition = ExpandableListView.getPackedPositionForGroup(groupPosition);
        int flatPosition = listView.getFlatListPosition(packedPosition);
        int first = listView.getFirstVisiblePosition();
        return listView.getChildAt(flatPosition - first);
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

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.getOperatorsByBenefit(new OperatorsByBenefitBody(mCpf, mIdBenefit));
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        boolean hasDefault = false;
                        OperatorsResponse operators = new Gson().fromJson((response.body().getAsJsonObject()), OperatorsResponse.class);

                        for (OperatorByBenefit op: operators.getOperatorByBenefits()) {
                            if(op.getIsCurrent()) {
                                hasDefault = true;
                                currentOperator = op;
                                mOperatorName.setText(op.getDescription());

                                //MUDAR A COR DO NOME DA OPERADORA
                                switch (op.getId()) {
                                    case Constants.CNU:
                                        mOperatorName.setTextColor(getResources().getColor(R.color.green));
                                        break;
                                    case Constants.SUL:
                                        mOperatorName.setTextColor(getResources().getColor(R.color.operator_blue));
                                        break;
                                    case BRADESCO:
                                        mOperatorName.setTextColor(getResources().getColor(R.color.operator_red));
                                        break;
                                }
                            }
                        }

                        //METODO QUE MOSTRA AS OPERADORAS EM UM EXPANDABLELIST PARA CASO O USUARIO DESEJE ESCOLHER UMA DIFERENTE DA PADRÃO
                        HashMap<OperatorByBenefit, List<String>> listDataChild = new HashMap<>();

                        List<String> info;

                        for (OperatorByBenefit op: operators.getOperatorByBenefits()) {
                            if (op.getIsAllowed()) {
                                info = new ArrayList<>();
                                info.add("Operadora padrão pela lotação.");
                                listDataChild.put(op, info);
                            } else {
                                info = new ArrayList<>();
                                info.add("Essa operadora não é a padrão da sua unidade de trabalho.");
                                listDataChild.put(op, info);
                            }
                        }

                        listAdapter = new ExpandableListAdapter(getActivity(), operators.getOperatorByBenefits(), listDataChild);

                        expListView.setChildDivider(getResources().getDrawable(R.color.backgroundDefault));
                        expListView.setAdapter(listAdapter);

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

    private void showSnackFragment(String text, int typeFailure )
    {
        if(getActivity() instanceof AdhesionHealthControlActivity)
            ((AdhesionHealthControlActivity) getActivity()).showSnackBar(text, typeFailure);
    }

    @OnClick(R.id.btnContinue)
    public void changeOperator(View view) {
        //Checa se escolheu uma operadora diferente da atual
        if (selectedOperador != null && !selectedOperador.getId().equals(currentOperator.getId())) {
            if (selectedOperador.getIsAllowed()) {
                int resID = getResources().getIdentifier("MSG096", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                Utils.showQuestionDialog(getString(R.string.dialog_title), message, getActivity(), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callChange();
                    }
                });

            } else {
                int resID = getResources().getIdentifier("MSG097", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                dialog = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, getActivity(), onClickOk);

            }

        } else {
            showSnackBar("Não houve seleção de operadora diferente da atual.", TYPE_FAILURE);
        }
    }

    private View.OnClickListener onClickOk = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showJustifyChangeOperatorDialog();
            if(dialog != null) {
                dialog.dismiss();
            }
        }
    };


    //MODAL DE JUSTIFICATIVA
    public void showJustifyChangeOperatorDialog() {

        dialogInfo = new Dialog(getActivity());
        dialogInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInfo.setContentView(R.layout.dialog_choose_exception_operator);

        TextView operatorName = dialogInfo.findViewById(R.id.operator_name);
        operatorName.setText(selectedOperador.getDescription());

         justification = dialogInfo.findViewById(R.id.text_justify);

        final Button confirmButton = dialogInfo.findViewById(R.id.btnDialogSave);
        confirmButton.setEnabled(false);
        confirmButton.setTextColor(getResources().getColor(R.color.grey_text));

        Button cancelButton = dialogInfo.findViewById(R.id.btnDialogCancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInfo.cancel();
            }
        });

        justification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String value = charSequence.toString();
                if(value.equals("")) {
                    confirmButton.setEnabled(false);
                    confirmButton.setTextColor(getResources().getColor(R.color.grey_text));
                }
                else {
                    confirmButton.setEnabled(true);
                    confirmButton.setTextColor(getResources().getColor(R.color.white_text));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //PEGAR INFORMAÇÂO DE DATA E RAZAO DESLIGAMENTO
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (justification.getText() != null && !justification.getText().equals("")) {
                    changeBenefit = new ChangeBenefit();

                    changeBenefit.setOutOfPolicy(true);
                    changeBenefit.setJustification(justification.getText().toString());

                    callChange();
                } else {
                    showSnackBar(getString(R.string.justification_mandatory), TYPE_FAILURE);
                }
            }
        });
        dialogInfo.show();
    }

    public void callChange() {
        setLoading(true, getString(R.string.saving_information));

        if(justification!=null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(justification.getWindowToken(), 0);
        }

        APIService apiService = ServiceGenerator.createService(APIService.class);

        if(changeBenefit==null)
            changeBenefit = new ChangeBenefit();
        changeBenefit.setCPF(FahzApplication.getInstance().getFahzClaims().getCPF());
        changeBenefit.setBenefitId(Integer.toString(mIdBenefit));
        changeBenefit.setOperator(Integer.toString(selectedOperador.getId()));

        Call<CommitResponse> call = apiService.changeBenefit(changeBenefit);

        call.enqueue(new Callback<CommitResponse>() {
            @Override
            public void onResponse(Call<CommitResponse> call, Response<CommitResponse> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    CommitResponse response2 = response.body();
                    if (response2.commited) {
                        if (dialogInfo != null)
                            dialogInfo.cancel();
                        Utils.showSimpleDialogGoBack(getString(R.string.dialog_title), response2.messageIdentifier.replaceAll("\\\\n", "\n"),
                                null, getActivity(), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                });

                    } else {
                        dialogInfo.cancel();
                        showSnackBar(response2.messageIdentifier, TYPE_FAILURE);
                    }
                } else {
                    setLoading(false, "");
                    showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }

                setLoading(false, "");
            }

            @Override
            public void onFailure(Call<CommitResponse> call, Throwable t) {
                if(getActivity()!= null) {
                    if (t instanceof SocketTimeoutException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
                setLoading(false, "");
            }
        });
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mContent, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mContent, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
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
            textGroup.setTag(groupText.getId());

            if(isExpanded) {
                switch (groupText.getId()) {
                    case Constants.CNU:
                        textGroup.setTextColor(getResources().getColor(R.color.green));
                        break;
                    case Constants.SUL:
                        textGroup.setTextColor(getResources().getColor(R.color.operator_blue));
                        break;
                    case BRADESCO:
                        textGroup.setTextColor(getResources().getColor(R.color.operator_red));
                        break;
                }
            }

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
