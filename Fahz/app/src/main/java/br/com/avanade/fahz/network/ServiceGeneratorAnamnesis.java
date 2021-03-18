package br.com.avanade.fahz.network;

import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import br.com.avanade.fahz.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static br.com.avanade.fahz.util.AnamnesisSession.userAnamnesisSession;

public class ServiceGeneratorAnamnesis {

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BuildConfig.ANAMNESIS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static <S> S createAnamnesisService(Class<S> serviceClass) {
        return createService(serviceClass, userAnamnesisSession.getToken());
    }

    private static <S> S createService(Class<S> serviceClass, final String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }
        return retrofit.create(serviceClass);
    }

    public static <S> S createAnamnesisServiceNoToken(Class<S> serviceClass) {
        httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        builder.client(httpClient.build());
        retrofit = builder.build();

        return retrofit.create(serviceClass);
    }
}
