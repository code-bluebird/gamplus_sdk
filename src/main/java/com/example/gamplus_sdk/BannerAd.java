package com.example.gamplus_sdk;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Arrays;

public class BannerAd {
    private static AdManagerAdView mAdView;

    public static void initializeAd(Context context){
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Toast.makeText(context, "Successfully Initialized", Toast.LENGTH_SHORT).show();
            }
        });

        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345")).build());
    }

    public static AdManagerAdView loadAd(Context context, String adId, AdSize adSize){
        mAdView = new AdManagerAdView((context));
        mAdView.setId(View.generateViewId());
        mAdView.setAdSize(adSize);
        mAdView.setBackgroundColor(1);
        mAdView.setAdUnitId(adId);
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        return mAdView;
    }
}
