package br.com.avanade.fahz.fragments.benefits.healthplan.prontmed;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.List;

import br.com.avanade.fahz.Adapter.prontmed.ProntMedScheduleAdapter;
import br.com.avanade.fahz.R;

public class CancelScheduleDialogFragment extends DialogFragment {

    int itemPosition;
    ProntMedScheduleAdapter.ScheduleItemViewHolder viewHolder;

    public void setItemPosition(int position) {
        this.itemPosition = position;
    }

    public int getItemPosition() {
        return this.itemPosition;
    }

    public void setViewHolder(ProntMedScheduleAdapter.ScheduleItemViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public ProntMedScheduleAdapter.ScheduleItemViewHolder getViewHolder() {
        return this.viewHolder;
    }

    public interface CancelScheduleDialogListener {
        void onDialogPositiveClick(CancelScheduleDialogFragment dialog);
        void onDialogNegativeClick(CancelScheduleDialogFragment dialog);
    }

    CancelScheduleDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            List<Fragment> fragmentList = getFragmentManager().getFragments();
            mListener = (CancelScheduleDialogListener) fragmentList.get(fragmentList.size() - 2);
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CancelScheduleDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.cancel_schedule_prontmed_dialog_title))
                .setMessage(getString(R.string.cancel_schedule_prontmed_dialog_content))
                .setPositiveButton(R.string.confirm_modal, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClick(CancelScheduleDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(CancelScheduleDialogFragment.this);
                    }
                });
        return builder.create();
    }
}
