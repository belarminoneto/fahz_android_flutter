package br.com.avanade.fahz.fragments.message;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.BaseMessageActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.Message;
import br.com.avanade.fahz.model.response.MessagesResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.PicassoImageGetter;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static br.com.avanade.fahz.util.Constants.NUMBER_OF_SHOWN_MESSAGES;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class ListMessageFragment extends Fragment {
    @BindView(R.id.recyclerViewMessage)
    RecyclerView mMessageRecyclerView;
    boolean searchModeReadMessage;
    private ProgressDialog mProgressDialog;
    SessionManager sessionManager;
    private Dialog dialog;
    private Context mContext;

    @BindView(R.id.content_message)
    LinearLayout mMessageContainer;
    @BindView(R.id.btnRead)
    Button btnRead;

    MessageListAdapter adapter;
    private int skip = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_message, container, false);
        ButterKnife.bind(this, view);

        setupUi();
        mContext = getContext();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        searchModeReadMessage = true;

        sessionManager = new SessionManager(getContext());
        mProgressDialog = new ProgressDialog(getContext());

        mMessageRecyclerView.setHasFixedSize(true);
        mMessageRecyclerView.setLayoutManager(layoutManager);

        loadMessageList(false, skip);
        return  view;
    }

    private void setupUi() {
        ((BaseMessageActivity) Objects.requireNonNull(getActivity())).toolbarTitle.setText(getActivity().getString(R.string.list_message));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadMessageList(final Boolean readMessage, int skip) {
        setLoading(true, getString(R.string.search_messages));

        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = apiService.getMessages(readMessage != null ? readMessage : "null", NUMBER_OF_SHOWN_MESSAGES,skip);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    if(response.body().getAsJsonObject().has("messageIdentifier"))
                    {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                    }
                    else
                    {
                        MessagesResponse messages = new Gson().fromJson((response.body().getAsJsonObject()), MessagesResponse.class);
                        showMessageList(messages.getRegisters());
                    }
                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    }
                }

                setLoading(false,"");
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                setLoading(false,"");
                if(t instanceof SocketTimeoutException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                else if(t instanceof UnknownHostException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }

    public void setReadMessage(String idMessage) {
        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<CommitResponse> call = apiService.setReadMessage(idMessage);
        call.enqueue(new Callback<CommitResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    CommitResponse commitResponse = response.body();
                    if (!commitResponse.commited)
                        showSnackBar(commitResponse.messageIdentifier, TYPE_FAILURE);

                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    }
                }

                setLoading(false, "");
            }

            @Override
            public void onFailure(@NonNull Call<CommitResponse> call, @NonNull Throwable t) {
                setLoading(false, "");
                if(t instanceof SocketTimeoutException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                else if(t instanceof UnknownHostException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }

    private void setLoading(Boolean loading, String text){
        if(loading){
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
        else{
            mProgressDialog.dismiss();
        }
    }

    private void showMessageList(final List<Message> menssageList) {
        if(adapter == null) {
            adapter = new MessageListAdapter(menssageList);
            mMessageRecyclerView.setAdapter(adapter);
        }
        else
        {
            adapter.updateData(menssageList);
        }

        adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                if (menssageList.size() > 0) {
                    skip++;
                    loadMessageList(searchModeReadMessage, skip);
                }
            }
        });
    }

    @OnClick(R.id.btnRead)
    public void searchReadMessages(View view) {
        loadMessageList(null, skip);
        btnRead.setVisibility(GONE);
        mMessageRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,10f));
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mMessageContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);

        snackbar.show();
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mMessageContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    private class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {

        private List<Message> mMessageList;
        OnBottomReachedListener onBottomReachedListener;

        MessageListAdapter(List<Message> list) {
            mMessageList = list;
        }

        public void updateData(List<Message> messageList) {
            //mMessageList.clear();
            mMessageList.addAll(messageList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MessageListAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_message_group, parent, false);
            return new MessageListAdapter.MessageViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(@NonNull final MessageListAdapter.MessageViewHolder holder, int position) {
            final Message message = mMessageList.get(position);
            holder.visualize.setTag("closed");

            holder.messageDate.setText(message.getDate());

            //Title
            if (message.getTitle() != null && !message.getTitle().equals("")) {
                holder.messageHeader.setText(message.getTitle());
            }else{
                holder.messageHeader.setText(message.getMessage());
            }

            //Message default
            if (message.getMessage() != null && !message.getMessage().equals("") && message.getContent() != null && !message.getContent().equals("")) {
                message.setMessage(getResources().getString(getResources().getIdentifier("MSG487", "string", getActivity().getPackageName())));
                holder.messageText.setText(message.getMessage());
            }

            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;

            StringBuilder sb = new StringBuilder();

            //Message
            if (message.getMessage() != null && !message.getMessage().equals("")) {
                sb.append(message.getMessage());
            }

            //Content
            if (message.getContent() != null && !message.getContent().equals("")) {
                sb.append(message.getContent());
            }

            PicassoImageGetter imageGetter = new PicassoImageGetter(holder.messageContainer, mContext, width, height);
            Spannable html;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                html = (Spannable) Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
            } else {
                html = (Spannable) Html.fromHtml(sb.toString(), imageGetter, null);
            }

            holder.messageContainer.setText(html);

            if (message.getRead()) {
                holder.imgNotReadMessage.setVisibility(GONE);
                holder.imgReadMessage.setVisibility(View.VISIBLE);
            } else {
                holder.imgNotReadMessage.setVisibility(View.VISIBLE);
                holder.imgReadMessage.setVisibility(GONE);
            }

            holder.visualize.setOnClickListener(view -> {
                if (!message.getRead()) {
                    setReadMessage(message.getId());
                }

                if (view.getTag().equals("open")) {
                    holder.messageContainer.setVisibility(View.GONE);
                    holder.messageText.setVisibility(View.VISIBLE);
                    holder.visualize.setTag("closed");
                } else {
                    holder.messageText.setVisibility(GONE);
                    holder.messageContainer.setVisibility(View.VISIBLE);
                    holder.imgReadMessage.setVisibility(View.VISIBLE);
                    holder.imgNotReadMessage.setVisibility(GONE);
                    holder.visualize.setTag("open");
                }
            });

            if (position == mMessageList.size() - 1 && mMessageList.size()==NUMBER_OF_SHOWN_MESSAGES){
                onBottomReachedListener.onBottomReached(position);
            }
        }

        @Override
        public int getItemCount() {
            return mMessageList.size();
        }

        public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){
            this.onBottomReachedListener = onBottomReachedListener;
        }

        class MessageViewHolder extends RecyclerView.ViewHolder {
            private ImageView imgReadMessage;
            private ImageView imgNotReadMessage;
            private TextView messageHeader;
            private TextView messageDate;
            private TextView messageText;
            private TextView messageContainer;
            private LinearLayout visualize;

            MessageViewHolder(View itemView) {
                super(itemView);
                imgReadMessage = itemView.findViewById(R.id.img_read_message);
                imgNotReadMessage = itemView.findViewById(R.id.img_not_read_message);
                messageHeader = itemView.findViewById(R.id.message_header);
                visualize = itemView.findViewById(R.id.linear_layout_message);
                messageDate = itemView.findViewById(R.id.message_date);
                messageText = itemView.findViewById(R.id.message_text);
                messageContainer = itemView.findViewById(R.id.desc_message_container);

                messageContainer.setVisibility(View.GONE);
            }
        }
    }

    public interface OnBottomReachedListener {
        void onBottomReached(int position);
    }
}
