package com.example.shopii.config;

import android.content.Context;
import com.example.shopii.R;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import javax.net.ssl.*;
import java.security.cert.CertificateException;

public class RetrofitConfig {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(Context context, String endPoint) {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(getSSLSocketFactory(), getTrustManager())
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(endPoint)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static SSLSocketFactory getSSLSocketFactory() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{getTrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static X509TrustManager getTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        };
    }
} 