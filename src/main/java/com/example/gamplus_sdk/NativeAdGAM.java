package com.example.gamplus_sdk;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.nativead.NativeCustomFormatAd;

import java.util.Locale;

public class NativeAdGAM {
    private static NativeCustomFormatAd nativeCustomFormatAd;
    private static NativeAd nativeAd;
    public static void initializeAd(Context context){
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Toast.makeText(context, "Successfully Initialized", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void requestNativeAd(boolean requestNativeAds, boolean isMuted, Activity activity, FrameLayout frameLayout, TextView videoStatus){
        AdLoader.Builder builder = new AdLoader.Builder(activity, "/6499/example/native");

        if (requestNativeAds) {
            builder.forNativeAd(
                    new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd newNativeAd) {
                            // If this callback occurs after the activity is destroyed, you must call
                            // destroy and return or you may get a memory leak.
                            boolean isDestroyed = false;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                isDestroyed = activity.isDestroyed();
                            }
                            if (isDestroyed || activity.isFinishing() || activity.isChangingConfigurations()) {
                                newNativeAd.destroy();
                                return;
                            }
                            // You must call destroy on old ads when you are done with them,
                            // otherwise you will have a memory leak.
                            if (nativeAd != null) {
                                nativeAd.destroy();
                            }
                            nativeAd = newNativeAd;
                            NativeAdView adView =
                                    (NativeAdView)
                                            activity.getLayoutInflater().inflate(R.layout.activity_bpn, frameLayout, false);
                            populateNativeAdView(nativeAd, adView, videoStatus);
                            frameLayout.removeAllViews();
                            frameLayout.addView(adView);
                        }
                    });
        }

        else{
            builder.forCustomFormatAd(
                    "10104090",
                    new NativeCustomFormatAd.OnCustomFormatAdLoadedListener() {
                        @Override
                        public void onCustomFormatAdLoaded(NativeCustomFormatAd newAd) {
                            // If this callback occurs after the activity is destroyed, you must call
                            // destroy and return or you may get a memory leak.
                            boolean isDestroyed = false;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                isDestroyed = activity.isDestroyed();
                            }
                            if (isDestroyed || activity.isFinishing() || activity.isChangingConfigurations()) {
                                newAd.destroy();
                                return;
                            }
                            // You must call destroy on old ads when you are done with them,
                            // otherwise you will have a memory leak.
                            if (nativeCustomFormatAd != null) {
                                nativeCustomFormatAd.destroy();
                            }
                            nativeCustomFormatAd = newAd;

                            View adView =
                                    activity.getLayoutInflater()
                                            .inflate(R.layout.activity_native_template, frameLayout, false);
                            populateSimpleTemplateAdView(activity, newAd, adView, videoStatus);
                            frameLayout.removeAllViews();
                            frameLayout.addView(adView);
                        }
                    },
                    new NativeCustomFormatAd.OnCustomClickListener() {
                        @Override
                        public void onCustomClick(NativeCustomFormatAd ad, String s) {
                            Toast.makeText(
                                            activity,
                                            "A custom click has occurred in the simple template",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        }

        VideoOptions videoOptions =
                new VideoOptions.Builder().setStartMuted(isMuted).build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader =
                builder
                        .withAdListener(
                                new AdListener() {
                                    @Override
                                    public void onAdFailedToLoad(LoadAdError loadAdError) {
//                                        refresh.setEnabled(true);
                                        String error =
                                                String.format(
                                                        Locale.getDefault(),
                                                        "domain: %s, code: %d, message: %s",
                                                        loadAdError.getDomain(),
                                                        loadAdError.getCode(),
                                                        loadAdError.getMessage());
                                        Toast.makeText(
                                                        activity,
                                                        "Failed to load native ad: " + error,
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                })
                        .build();

        adLoader.loadAd(new AdManagerAdRequest.Builder().build());
        videoStatus.setText("");
    }

    private static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView, TextView videoStatus) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Updates the UI to say whether or not this ad has a video asset.
        if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent().hasVideoContent()) {
            VideoController vc = nativeAd.getMediaContent().getVideoController();
            videoStatus.setText(
                    String.format(
                            Locale.getDefault(),
                            "Video status: Ad contains a %.2f:1 video asset.",
                            nativeAd.getMediaContent().getAspectRatio()));
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
//                    refresh.setEnabled(true);
                    videoStatus.setText("Video status: Video playback has ended.");
                    super.onVideoEnd();
                }
            });
        } else {
            videoStatus.setText("Video status: Ad does not contain a video asset.");
//            refresh.setEnabled(true);
        }
    }

    private static void populateSimpleTemplateAdView(Activity activity,
            final NativeCustomFormatAd nativeCustomFormatAd, View adView, TextView videoStatus) {
        TextView headline = adView.findViewById(R.id.simplecustom_headline);
        TextView caption = adView.findViewById(R.id.simplecustom_caption);

        headline.setText(nativeCustomFormatAd.getText("Headline"));
        caption.setText(nativeCustomFormatAd.getText("Caption"));

        FrameLayout mediaPlaceholder = adView.findViewById(R.id.simplecustom_media_placeholder);

        // Apps can check the MediaContent's hasVideoContent property to determine if the
        // NativeCustomFormatAd has a video asset.
        if (nativeCustomFormatAd.getMediaContent() != null
                && nativeCustomFormatAd.getMediaContent().hasVideoContent()) {
            MediaView mediaView = new MediaView(adView.getContext());
            mediaView.setMediaContent(nativeCustomFormatAd.getMediaContent());
            mediaPlaceholder.addView(mediaView);
            videoStatus.setText(
                    String.format(
                            Locale.getDefault(),
                            "Video status: Ad contains a %.2f:1 video asset.",
                            nativeAd.getMediaContent().getAspectRatio()));
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            VideoController vc = nativeCustomFormatAd.getMediaContent().getVideoController();
            vc.setVideoLifecycleCallbacks(
                    new VideoController.VideoLifecycleCallbacks() {
                        @Override
                        public void onVideoEnd() {
                            // Publishers should allow native ads to complete video playback before
                            // refreshing or replacing them with another ad in the same UI location.
//                            refresh.setEnabled(true);
                            videoStatus.setText("Video status: Video playback has ended.");
                            super.onVideoEnd();
                        }
                    });
        } else {
            ImageView mainImage = new ImageView(activity);
            mainImage.setAdjustViewBounds(true);
            mainImage.setImageDrawable(nativeCustomFormatAd.getImage("MainImage").getDrawable());

            mainImage.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nativeCustomFormatAd.performClick("MainImage");
                        }
                    });
            mediaPlaceholder.addView(mainImage);
//            refresh.setEnabled(true);
            videoStatus.setText("Video status: Ad does not contain a video asset.");
        }
    }

}
