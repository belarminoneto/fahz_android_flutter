package br.com.avanade.fahz.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class AuthenticationInterceptor implements Interceptor {
    private String authToken;

    public AuthenticationInterceptor(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder().header("Authorization", "Bearer " + authToken);

        Request request = builder.build();
        return chain.proceed(request);
    }
}
