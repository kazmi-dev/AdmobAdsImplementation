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
    private var adUnitId: String = "ca-app-pub-3940256099942544/1033173712"
    private var isAdLoading: Boolean = false
    private var isAdShowing: Boolean = false

    fun setAdUnitId(adUnitId: String) {
        this.adUnitId = adUnitId
    }

    fun preloadAd() {

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
        loadAd(adUnitId) { Log.d(INTERSTITIAL_LOG, "preloadAd: Ad loaded") }

    }

    fun showAdIfAvailable(activity: Activity, callback: ((AdState)-> Unit)? = null) {

        //check if ad is not available to show
        if (interstitialAd == null) {
            Log.d(INTERSTITIAL_LOG, "showAdIfAvailable: Ad not available, preloading ad")
            preloadAd()
            callback?.invoke(AdState.FAILED_TO_SHOW)
            return
        }

        //check if ad is already showing
        if (isAdShowing) {
            Log.d(INTERSTITIAL_LOG, "showAdIfAvailable: Ad already showing")
            return
        }

        //show ad
        showAd(activity, isPreloadAfterDismiss = true, callback)

    }

    fun showAdOnDemand(activity: Activity, adUnitId: String, callback: ((AdState) -> Unit)?) {

        //check if ad is already showing
        if (isAdShowing) {
            Log.d(INTERSTITIAL_LOG, "showAdOnDemand: Ad already showing")
            return
        }

        //check if ad is not available
        if (interstitialAd == null) {
            loadAd(adUnitId) {isLoaded->
                if (isLoaded){
                    //show ad
                    attachFullscreenCallback(isPreloadAfterDismiss = false, callback = callback)
                    isAdShowing = true
                    interstitialAd?.show(activity)
                }else{
                    callback?.invoke(AdState.FAILED_TO_LOAD)
                }
            }
            return
        }

        //show ad
        showAd(activity, isPreloadAfterDismiss = false)

    }

    private fun loadAd(adUnitId: String, callback: (Boolean) -> Unit) {
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
                    callback(true)
                }

                override fun onAdFailedToLoad(adLoadError: LoadAdError) {
                    Log.d(INTERSTITIAL_LOG, "onAdFailedToLoad: ${adLoadError.message}")
                    isAdLoading = false
                    interstitialAd = null
                    callback(false)
                }
            }
        )
    }

    private fun showAd(activity: Activity, isPreloadAfterDismiss: Boolean, callback: ((AdState)-> Unit)? = null) {
        attachFullscreenCallback(isPreloadAfterDismiss, callback)
        isAdShowing = true
        interstitialAd?.show(activity)
    }

    private fun attachFullscreenCallback(
        isPreloadAfterDismiss: Boolean,
        callback: ((AdState) -> Unit)?
    ) {
        //callback for ad showing
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdShowedFullScreenContent() {
                Log.d(INTERSTITIAL_LOG, "onAdShowedFullScreenContent: Ad showed")
                callback?.invoke(AdState.SHOWED)
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(
                    INTERSTITIAL_LOG,
                    "onAdDismissedFullScreenContent: Ad dismissed preloading ad"
                )
                interstitialAd = null
                isAdShowing = false
                if (isPreloadAfterDismiss) preloadAd()
                callback?.invoke(AdState.DISMISSED)
            }

            override fun onAdFailedToShowFullScreenContent(adShowError: AdError) {
                Log.d(
                    INTERSTITIAL_LOG,
                    "onAdFailedToShowFullScreenContent: Ad failed to show ${adShowError.message}"
                )
                interstitialAd = null
                isAdShowing = false
                callback?.invoke(AdState.FAILED_TO_SHOW)
            }
        }
    }
}
