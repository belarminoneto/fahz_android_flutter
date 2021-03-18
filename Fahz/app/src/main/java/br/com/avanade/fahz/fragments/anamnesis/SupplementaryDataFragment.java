package br.com.avanade.fahz.fragments.anamnesis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.interfaces.QuestionnaireActivityListener;
import br.com.avanade.fahz.model.anamnesisModel.SupplementaryDataAnamnesis;
import br.com.avanade.fahz.util.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.com.avanade.fahz.util.AnamnesisSession.userAnamnesisSession;

public class SupplementaryDataFragment extends Fragment {

    @BindView(R.id.edtHeightData)
    EditText edtHeightData;

    @BindView(R.id.edtWeightData)
    EditText edtWeightData;

    @BindView(R.id.txtBMIData)
    TextView txtBMIData;

    @BindView(R.id.txtWeight)
    TextView txtWeight;

    @BindView(R.id.txtHeight)
    TextView txtHeight;

    @BindView(R.id.txtBMI)
    TextView txtBMI;

    @BindView(R.id.txtText)
    TextView txtText;

    SupplementaryDataAnamnesis supplementaryData;
    private QuestionnaireActivityListener listener;
    private boolean onScreen;

    public static SupplementaryDataFragment newInstance(SupplementaryDataAnamnesis supplementaryData, QuestionnaireActivityListener listener) {
        SupplementaryDataFragment fragment = new SupplementaryDataFragment();
        fragment.supplementaryData = supplementaryData;
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supplementary_data, container, false);
        ButterKnife.bind(this, view);
        setupUI();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupUI() {

        try {

            float weight = supplementaryData.getWeight();
            float height = supplementaryData.getHeight();
            if (weight < 0 || weight > 300) {
                weight = 0;
                supplementaryData.setWeight(0);
            }
            if (height < 0 || height > 2.5) {
                height = 0;
                supplementaryData.setHeight(0);
            }
            edtWeightData.setText(String.valueOf((weight == 0 ? "": weight)));
            edtHeightData.setText(String.valueOf(height == 0 ? "": height));

            calculateBMI(weight, height);

            if(weight == 0 || height == 0){
                onScreen = false;
                listener.enableNextButton(false);
            }else{
                onScreen = true;
                listener.enableNextButton(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        edtHeightData.addTextChangedListener(new TextWatcher() {
            boolean isUpdating;
            String oldString = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String str = unmask(s.toString());
                StringBuilder mask = new StringBuilder();
                if (isUpdating) {
                    oldString = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;

                for (final char m : Constants.MASK_HEIGHT.toCharArray()) {
                    if (m != '#' && str.length() > oldString.length()) {
                        mask.append(m);
                        continue;
                    }
                    try {
                        mask.append(str.charAt(i));
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                edtHeightData.setText(mask.toString());
                edtHeightData.setSelection(mask.length());

                try {
                    float height = 0;
                    if (s.length() > 0) {
                        height = Float.parseFloat(mask.toString().replace(",","."));
                    }
                    restartTimer(supplementaryData.getWeight(), height);
                } catch (Exception e) {
                    edtHeightData.setText("0.0");
                    edtHeightData.setError(getResources().getString(R.string.invalid_value));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edtWeightData.addTextChangedListener(new TextWatcher() {
            boolean isUpdating;
            String oldString = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String str = unmask(s.toString());
                StringBuilder mask = new StringBuilder();
                if (isUpdating) {
                    oldString = str;
                    isUpdating = false;
                    return;
                }

                String mascara = "";
                if(str.length() == 6)
                    mascara = Constants.MASK_WEIGHT_3D;
                else if(str.length() == 5)
                    mascara = Constants.MASK_WEIGHT_2D;
                else
                    mascara = Constants.MASK_WEIGHT_1D;

                int i = 0;
                for (final char m : mascara.toCharArray()) {
                    if (m != '#' && str.length() > oldString.length()) {
                        mask.append(m);
                        continue;
                    }
                    try {
                        mask.append(str.charAt(i));
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                edtWeightData.setText(mask.toString());
                edtWeightData.setSelection(mask.length());

                try {
                    float weight = 0;
                    if (s.length() > 0) {
                        weight = Float.parseFloat(mask.toString().replace(",","."));
                    }
                    restartTimer(weight, supplementaryData.getHeight());
                } catch (Exception e) {
                    edtWeightData.setText("0.0");
                    edtWeightData.setError(getResources().getString(R.string.invalid_value));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    void restartTimer(final float weight, final float height) {
        checkValues(weight, height);
    }

    private void checkValues(float weight, float height) {

        boolean error = false;
        if (weight > 0 && weight < 300) {
            supplementaryData.setWeight(weight);
        } else {
            supplementaryData.setWeight(0);
            edtWeightData.setError(getResources().getString(R.string.invalid_value));
            error = true;
        }
        if (height > 0 && height < 2.5) {
            supplementaryData.setHeight(height);
        } else {
            supplementaryData.setHeight(0);
            edtHeightData.setError(getResources().getString(R.string.invalid_value));
            error = true;
        }
        if (error) {
            txtBMIData.setText(getResources().getString(R.string.invalid_value));
        } else {
            calculateBMI(weight, height);
            listener.onDataChanged(supplementaryData);
        }
        if (onScreen) {
            listener.enableNextButton(!error);
        } else {
            listener.enableNextButton(true);
        }
    }

    @SuppressLint("StringFormatMatches")
    private void calculateBMI(float weight, float height) {

        if (weight == 0 || height == 0) {
            return;
        }
        String range;
        float index = weight / (height * height);

        if (userAnamnesisSession.getAge() < 60) {
            if (index < 18.5) {
                range = getString(R.string.low_weight);
            } else if (index < 25) {
                range = getString(R.string.normal_weight);
            } else if (index < 30) {
                range = getString(R.string.overweight);
            } else if (index < 35) {
                range = getString(R.string.obesity_class_1);
            } else if (index < 40) {
                range = getString(R.string.obesity_class_2);
            } else {
                range = getString(R.string.obesity_class_3);
            }
        } else {
            if (index < 22) {
                range = getString(R.string.low_weight);
            } else if (index < 27) {
                range = getString(R.string.normal_weight);
            } else if (index < 30) {
                range = getString(R.string.overweight);
            } else if (index < 35) {
                range = getString(R.string.obesity_class_1);
            } else if (index < 40) {
                range = getString(R.string.obesity_class_2);
            } else {
                range = getString(R.string.obesity_class_3);
            }
        }

        txtBMIData.setText(Html.fromHtml(getResources().getString(R.string.bmi, index , range)));
        supplementaryData.setBodyMassIndex((float) index);
    }

    private static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }
}