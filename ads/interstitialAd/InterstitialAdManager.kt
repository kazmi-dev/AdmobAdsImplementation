import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterstitialAdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val INTERSTITIAL_LOG = "InterstitialAdManager_346298794582"
    }

    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading: Boolean = false
    private var isAdShowing: Boolean = false
    private var activityRef : WeakReference<Activity>? = null

    fun preloadAd(adUnitId: String) {

        //check if ad already available
        if (interstitialAd != null) {
            Log.d(INTERSTITIAL_LOG, "preloadAd: Ad already available")
            return
        }

        //check if ad already loading
        if (isAdLoading) {
            Log.d(INTERSTITIAL_LOG, "preloadAd: Ad already loading")
            return
        }

        //loadAd
        loadAd(adUnitId) {
            Log.d(INTERSTITIAL_LOG, "preloadAd: Ad loaded")
        }

    }

    fun showAdIfAvailable(activity: Activity, adUnitId: String,  callback: (InterstitialAdCallback)-> Unit) {
        activityRef = WeakReference(activity)

        //check if ad is not available to show
        if (interstitialAd == null) {
            Log.d(INTERSTITIAL_LOG, "showAdIfAvailable: Ad not available, preloading ad")
            preloadAd(adUnitId)
            callback.invoke(InterstitialAdCallback.FAILED_TO_SHOW)
            return
        }

        //check if ad is already showing
        if (isAdShowing) {
            Log.d(INTERSTITIAL_LOG, "showAdIfAvailable: Ad already showing")
            releaseResources()
            return
        }

        //show ad
        showAd(adUnitId, isPreloadAfterDismiss = true, callback)

    }

    private fun releaseResources() {
        activityRef?.clear()
        activityRef = null
    }

    fun showAdOnDemand(activity: Activity, adUnitId: String, callback: (InterstitialAdCallback) -> Unit) {
        activityRef = WeakReference(activity)
        
        //check if ad is already showing
        if (isAdShowing) {
            Log.d(INTERSTITIAL_LOG, "showAdOnDemand: Ad already showing")
            return
        }

        //check if ad is not available
        if (interstitialAd == null) {
            loadAd(adUnitId) {
                //show ad
                attachFullscreenCallback(adUnitId, isPreloadAfterDismiss = false, callback = callback)
                isAdShowing = true
                interstitialAd?.show(activity)
            }
            return
        }

        //show ad
        showAd(adUnitId, isPreloadAfterDismiss = false, callback)

    }

    private fun loadAd(adUnitId: String, onLoaded: () -> Unit) {
        isAdLoading = true

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(INTERSTITIAL_LOG, "onAdLoaded: Ad loaded")
                    isAdLoading = false
                    interstitialAd = ad
                    onLoaded()
                }

                override fun onAdFailedToLoad(adLoadError: LoadAdError) {
                    Log.d(INTERSTITIAL_LOG, "onAdFailedToLoad: ${adLoadError.message}")
                    isAdLoading = false
                    interstitialAd = null
                }
            }
        )
    }

    private fun showAd(adUnitId: String, isPreloadAfterDismiss: Boolean, callback: (InterstitialAdCallback)-> Unit) {
        attachFullscreenCallback(adUnitId, isPreloadAfterDismiss, callback)
        isAdShowing = true
       activityRef?.get()?.let {
           interstitialAd?.show(it)
       }
    }

    private fun attachFullscreenCallback(
        adUnitId: String,
        isPreloadAfterDismiss: Boolean,
        callback: (InterstitialAdCallback) -> Unit
    ) {
        //callback for ad showing
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdShowedFullScreenContent() {
                Log.d(INTERSTITIAL_LOG, "onAdShowedFullScreenContent: Ad showed")
                callback.invoke(InterstitialAdCallback.SHOWED)
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(
                    INTERSTITIAL_LOG,
                    "onAdDismissedFullScreenContent: Ad dismissed preloading ad"
                )
                interstitialAd = null
                isAdShowing = false
                if (isPreloadAfterDismiss) preloadAd(adUnitId)
                callback.invoke(InterstitialAdCallback.DISMISSED)
            }

            override fun onAdFailedToShowFullScreenContent(adShowError: AdError) {
                Log.d(
                    INTERSTITIAL_LOG,
                    "onAdFailedToShowFullScreenContent: Ad failed to show ${adShowError.message}"
                )
                interstitialAd = null
                isAdShowing = false
                callback.invoke(InterstitialAdCallback.FAILED_TO_SHOW)
            }
        }
    }

}
