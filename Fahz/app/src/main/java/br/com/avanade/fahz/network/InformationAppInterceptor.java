package br.com.avanade.fahz.network;


import androidx.annotation.NonNull;

import java.io.IOException;

import br.com.avanade.fahz.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class InformationAppInterceptor implements Interceptor {
    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder().header("version-app", BuildConfig.VERSION_NAME);
        builder.header("device-name", android.os.Build.MODEL);
        Request request = builder.build();
        return chain.proceed(request);
    }
}