package br.com.avanade.fahz.activities.request;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.model.Request;

import static br.com.avanade.fahz.util.Constants.NUMBER_OF_SHOWN_REQUESTS;

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.RequestViewHolder> {

    ListRequestActivity.OnBottomReachedListener onBottomReachedListener;
    private List<Request> mRequestList;
    private OnRowClickListener rowListener;
    private Context context;
    private String selectedType;


    public RequestListAdapter(List<Request> list, Context context, String selectedType) {
        this.mRequestList = list;
        this.context = context;
        this.selectedType = selectedType;
    }

    public void updateData(List<Request> list) {
        //mMessageList.clear();
        mRequestList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestListAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_requests_group,
                parent, false);
        return new RequestListAdapter.RequestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestListAdapter.RequestViewHolder holder, final int position) {
        final Request request = mRequestList.get(position);

        holder.requestDate.setText(DateEditText.parseTODate(request.getDate()));
        holder.requestHeader.setText(request.getBehaviorDescription());

        if (request.getStatus().description.equals(context.getString(R.string.analysis))) {
            holder.statusDisapproved.setVisibility(View.GONE);
            holder.statusPending.setVisibility(View.VISIBLE);
            holder.statusApproved.setVisibility(View.GONE);

            holder.statusPending.setText(context.getString(R.string.analysis));
        }

        if (position % 2 == 1) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white04));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white01));
        }

        if (position == mRequestList.size() - 1 && mRequestList.size() == NUMBER_OF_SHOWN_REQUESTS) {

            onBottomReachedListener.onBottomReached(position);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rowListener != null) {
                    rowListener.onRowClicked(request, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRequestList.size();
    }

    public void setOnBottomReachedListener(ListRequestActivity.OnBottomReachedListener onBottomReachedListener) {

        this.onBottomReachedListener = onBottomReachedListener;
    }

    public void setRowListener(OnRowClickListener rowListener) {
        this.rowListener = rowListener;
    }

    public interface OnRowClickListener {
        void onRowClicked(Request request, int position);
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        private TextView requestHeader;
        private TextView requestDate;

        private TextView statusDisapproved;
        private TextView statusPending;
        private TextView statusApproved;


        RequestViewHolder(View itemView) {
            super(itemView);
            requestHeader = itemView.findViewById(R.id.request_header);
            requestDate = itemView.findViewById(R.id.request_date);
            statusDisapproved = itemView.findViewById(R.id.status_request_disapproved);
            statusPending = itemView.findViewById(R.id.status_request_pending);
            statusApproved = itemView.findViewById(R.id.status_request_approved);

            if (selectedType.equals(context.getString(R.string.approved_request))) {
                statusDisapproved.setVisibility(View.GONE);
                statusPending.setVisibility(View.GONE);
                statusApproved.setVisibility(View.VISIBLE);
            } else if (selectedType.equals(context.getString(R.string.disapproved_request))) {
                statusDisapproved.setVisibility(View.VISIBLE);
                statusPending.setVisibility(View.GONE);
                statusApproved.setVisibility(View.GONE);
            } else {
                statusDisapproved.setVisibility(View.GONE);
                statusPending.setVisibility(View.VISIBLE);
                statusApproved.setVisibility(View.GONE);
            }
        }

    }

}