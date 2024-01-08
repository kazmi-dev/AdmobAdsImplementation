package com.documentscanner.jetpackcomposepractice.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.documentscanner.jetpackcomposepractice.BaseApplication
import com.documentscanner.jetpackcomposepractice.R
import com.documentscanner.jetpackcomposepractice.ads.AdsSettings.Companion.isInterstitialShowing
import com.documentscanner.jetpackcomposepractice.ads.AdsSettings.Companion.isSplashScreen
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.Date

class AppOpenAdsManager(
    private val mBaseApp: BaseApplication
): Application.ActivityLifecycleCallbacks, LifecycleEventObserver {

    private val appOpenLog = "appOpenLog12345"
    private var appOpenAd: AppOpenAd? = null
    private var appOpen: AppOpen? = null
    private var isAdLoading = false
    private var isAdShowing = false
    private var loadTime: Long = 0
    private var currentActivity: Activity? = null

    init {
        mBaseApp.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /** Set AppOpen */
    fun setAppOpen(appOpen: AppOpen){
        this.appOpen = appOpen
    }

    /** Load AppOpenAd */
    private fun loadAd(){
        if (isAdLoading){
            Log.d(appOpenLog, "Ad Already available or loading")
            return
        }
        isAdLoading = true
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            mBaseApp.applicationContext,
            mBaseApp.applicationContext.getString(R.string.app_open_ad_id),
            adRequest,
            appOpenLoadCallback
        )
    }

    private val appOpenLoadCallback = object : AppOpenAdLoadCallback(){
        override fun onAdLoaded(ad: AppOpenAd) {
            appOpenAd = ad
            loadTime = Date().time
            isAdLoading = false
            Log.d(appOpenLog, "Ad Loaded")
        }

        override fun onAdFailedToLoad(error: LoadAdError) {
            isAdLoading = false
            appOpenAd = null
            Log.d(appOpenLog, "Ad Failed to load: $error")
        }
    }

    /** Show AppOpenAd If Available */
    private fun showAdIfAvailable(activity: Activity){

        if (isAdShowing) {
            Log.d(appOpenLog, "Ad already Showing")
            return
        }

        if (isAdAvailable()){
            Log.d(appOpenLog, "Ad not available")
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback(){

            override fun onAdShowedFullScreenContent() {
                Log.d(appOpenLog, "Ad Showed")
                isAdShowing = true
                appOpen?.closeAds()
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(appOpenLog, "Ad Dismissed")
                isAdShowing = false
                appOpenAd = null
                appOpen?.restoreAds()
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adLoadError: AdError) {
                Log.d(appOpenLog, adLoadError.message)
                isAdShowing = false
                appOpenAd = null
            }
        }

        appOpenAd?.show(activity)

    }

    private fun isAdAvailable(): Boolean {
        return appOpen != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Int): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /** Lifecycle callbacks */
    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {

        if (event == Lifecycle.Event.ON_START){

            /** if you are using SplashActivity use "currentActivity == SplashActivity()".
             * if you are using SplashFragment use companion Object member "isSplashScreen: Boolean" */

            if (currentActivity == SplashActivity() && !isSplashScreen && !isInterstitialShowing){
                showAdIfAvailable(currentActivity!!)
            }else{
                Log.d(appOpenLog, "Cant Show Ad Right Now")
            }
        }

    }

}
