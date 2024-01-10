package com.documentscanner.jetpackcomposepractice.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.documentscanner.jetpackcomposepractice.R
import com.documentscanner.jetpackcomposepractice.ads.AdsSettings.Companion.isInterstitialShowing
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(private val context: Context) {

    init {
        loadInterstitialAd()
    }

    private val interstitialAdLog = "98765432345678"
    private var interstitialAd: InterstitialAd? = null

    /** load Interstitial Ad */
    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            context.getString(R.string.interstitial_ad_id),
            adRequest,
            object : InterstitialAdLoadCallback(){
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(interstitialAdLog, "Ad Loaded")
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d(interstitialAdLog, "Ad Failed to Load")
                    interstitialAd = null
                }
            }
        )
    }

    /** Show Ad if Available */
    fun showInterstitialAdIfAvailable(activity: Activity) {

        if (isInterstitialShowing) {
            Log.d(interstitialAdLog, "Ad Already Showing")
            return
        }

        if (interstitialAd == null) {
            Log.d(interstitialAdLog, "Ad not Loaded")
            loadInterstitialAd()
            return
        }

        //Show interstitial ad
        interstitialAd?.show(activity)

        //Handle interstitial ad callback
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.d(interstitialAdLog, "ad showed")
                isInterstitialShowing = true
            }

            override fun onAdDismissedFullScreenContent() {
                isInterstitialShowing = false
                interstitialAd = null
                loadInterstitialAd()
            }

            override fun onAdFailedToShowFullScreenContent(adLoadError: AdError) {
                Log.d(interstitialAdLog, "FailedToShowFull: ${adLoadError.message}")
            }
        }

    }

}
