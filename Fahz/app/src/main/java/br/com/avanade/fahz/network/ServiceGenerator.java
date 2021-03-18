package br.com.avanade.fahz.network;

import android.os.Build;
import android.text.TextUtils;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import br.com.avanade.fahz.BuildConfig;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.ConstantsBaseURL;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, FahzApplication.getInstance().getToken());
    }

    public static <S> S createService(Class<S> serviceClass, String cpf, String password) {
        if (!TextUtils.isEmpty(cpf)) {
            String authToken = Credentials.basic(cpf, password);
            return createService(serviceClass, authToken);
        }

        return createService(serviceClass, FahzApplication.getInstance().getToken());
    }

    public static <S> S changePassword(Class<S> serviceClass, String cpf, String currentPassword, String newPassword) {
        if (!TextUtils.isEmpty(cpf) && !TextUtils.isEmpty(currentPassword) && !TextUtils.isEmpty(newPassword)) {
            String authToken = Credentials.basic(cpf, newPassword);
            return createService(serviceClass, authToken);
        }
        return createService(serviceClass, null);
    }

    private static <S> S createService(Class<S> serviceClass, final String authToken) {
        if (!TextUtils.isEmpty(authToken)) {

            try {
                TLSSocketFactory tlsTocketFactory = new TLSSocketFactory();

                httpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(160, TimeUnit.SECONDS)
                        .sslSocketFactory(tlsTocketFactory, tlsTocketFactory.getTrustManager())
                        .readTimeout(160, TimeUnit.SECONDS)
                        .writeTimeout(160, TimeUnit.SECONDS);

                builder.client(httpClient.build());
                retrofit = builder.build();
                
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            AuthenticationInterceptor authInterceptor = new AuthenticationInterceptor(authToken);
            if (!httpClient.interceptors().contains(authInterceptor)) {
                httpClient.addInterceptor(authInterceptor);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }

            InformationAppInterceptor informationInterceptor = new InformationAppInterceptor();
            if (!httpClient.interceptors().contains(informationInterceptor)) {
                httpClient.addInterceptor(informationInterceptor);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }

        }
        return retrofit.create(serviceClass);
    }

    public static <S> S createServiceNoToken(Class<S> serviceClass) {

        try {
            TLSSocketFactory tlsTocketFactory = new TLSSocketFactory();

            httpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .sslSocketFactory(tlsTocketFactory, tlsTocketFactory.getTrustManager())
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS);

            builder.client(httpClient.build());
            retrofit = builder.build();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        InformationAppInterceptor informationInterceptor = new InformationAppInterceptor();
        if (!httpClient.interceptors().contains(informationInterceptor)) {
            httpClient.addInterceptor(informationInterceptor);

            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }
}
