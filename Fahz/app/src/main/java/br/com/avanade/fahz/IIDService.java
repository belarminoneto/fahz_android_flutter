package br.com.avanade.fahz;

import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import br.com.avanade.fahz.application.FahzApplication;

public class IIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String ALL_FAHZ_GROUP = "allfahz";
        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        if (!TextUtils.isEmpty(cpf)) {
            FirebaseMessaging.getInstance().subscribeToTopic(cpf);
        }
        FirebaseMessaging.getInstance().subscribeToTopic(ALL_FAHZ_GROUP);
    }
}
