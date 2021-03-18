package br.com.avanade.fahz.fragments.lpgd;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.com.avanade.fahz.Adapter.lgpd.CardViewTransparencyAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.model.lgpdModel.TransparencyPanel;

public class TransparencyFragment extends Fragment {
    private TransparencyPanel mTransparency;
    private ProgressDialog mProgressDialog;
    private RecyclerView mRecycleViewTransparency;

    CardViewTransparencyAdapter mCardViewTransparencyAdapter;

    public static TransparencyFragment newInstance(TransparencyPanel transparency) {
        TransparencyFragment transparencyFragment = new TransparencyFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("transparency", transparency);
        transparencyFragment.setArguments(bundle);

        return transparencyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transparency, container, false);

        mProgressDialog = new ProgressDialog(getActivity());

        mRecycleViewTransparency = view.findViewById(R.id.recycleViewTransparency);

        mRecycleViewTransparency.setHasFixedSize(true);
        RecyclerView.LayoutManager myRecycle = new LinearLayoutManager(view.getContext());
        mRecycleViewTransparency.setLayoutManager(myRecycle);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTransparency = bundle.getParcelable("transparency");

            if(mTransparency.familyGroup.size() > 0){
                mCardViewTransparencyAdapter = new CardViewTransparencyAdapter(view.getContext(), mTransparency.familyGroup);
                mRecycleViewTransparency.setAdapter(mCardViewTransparencyAdapter);
                mCardViewTransparencyAdapter.notifyDataSetChanged();
            }
        }

        return view;
    }

}
