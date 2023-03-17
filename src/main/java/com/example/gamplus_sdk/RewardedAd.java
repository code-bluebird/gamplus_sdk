package com.example.gamplus_sdk;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

import java.util.Arrays;

public class RewardedAd {
    private static RewardedInterstitialAd rewardedInterstitialAd;
    private static RewardItem rewardedItems;
    static boolean isLoadingAds;
    public static void initializeAd(Context context){
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Toast.makeText(context, "Successfully Initialized", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void loadAd(Activity activity, String adId){
        Toast.makeText(activity, "loadAd()", Toast.LENGTH_SHORT).show();
        if (rewardedInterstitialAd == null) {
            isLoadingAds = true;

            AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
            // Use the test ad unit ID to load an ad.
            RewardedInterstitialAd.load(
                    activity,
                    adId,
                    adRequest,
                    new RewardedInterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(RewardedInterstitialAd ad) {
                            rewardedInterstitialAd = ad;
                            isLoadingAds = false;
                            Toast.makeText(activity, "onAdLoaded", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            // Handle the error.
                            rewardedInterstitialAd = null;
                            isLoadingAds = false;
                            Toast.makeText(activity, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public static void viewAd(Activity activity, String adId) {

        if (rewardedInterstitialAd == null) {
            Toast.makeText(activity, "The rewarded interstitial ad wasn't ready yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        rewardedInterstitialAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    /** Called when ad showed the full screen content. */
                    @Override
                    public void onAdShowedFullScreenContent() {

                        Toast.makeText(activity, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT)
                                .show();
                    }

                    /** Called when the ad failed to show full screen content. */
                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {

                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedInterstitialAd = null;
                        loadAd(activity, adId);

                        Toast.makeText(
                                        activity, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT)
                                .show();
                    }

                    /** Called when full screen content is dismissed. */
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedInterstitialAd = null;
                        Toast.makeText(activity, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT)
                                .show();
                        // Preload the next rewarded interstitial ad.
                        loadAd(activity, adId);
                    }
                });
        rewardedInterstitialAd.show(
                activity,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("Reward", "Value "+rewardItem.getAmount());
                        rewardedItems = rewardItem;
                    }
                });
    }

    public static RewardItem getRewardItems(){
        return rewardedItems;
    }


}
