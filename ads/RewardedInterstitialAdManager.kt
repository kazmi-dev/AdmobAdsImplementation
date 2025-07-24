import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardedInterstitialAdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val REWARDED_INTERSTITIAL_LOG = "rewardedInterstitialAd_2836429847092384"
    }

    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var isLoading: Boolean = false
    private var isShowing: Boolean = false

    private fun loadAd(adUnitId: String, onLoad: () -> Unit) {
        isLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedInterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    Log.d(REWARDED_INTERSTITIAL_LOG, "onAdLoaded: Ad loaded successfully")
                    rewardedInterstitialAd = ad
                    isLoading = false
                    onLoad()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d(
                        REWARDED_INTERSTITIAL_LOG,
                        "onAdLoaded: Ad Failed to load: Error: ${error.message}"
                    )
                    isLoading = false
                    rewardedInterstitialAd = null
                }
            }
        )
    }

    fun loadAndShowAd(
        activity: Activity,
        adUnitId: String,
        callback: (adState: RewardInterstitialAdCallback) -> Unit
    ) {
        if (rewardedInterstitialAd != null) {
            Log.d(REWARDED_INTERSTITIAL_LOG, "loadAndShowAd: Ad already available")
            showAd(activity, callback)
            return
        }
        if (isLoading) {
            Log.d(REWARDED_INTERSTITIAL_LOG, "loadAndShowAd: Ad already loading")
            return
        }
        if (isShowing) {
            Log.d(REWARDED_INTERSTITIAL_LOG, "loadAndShowAd: Ad already showing")
            return
        }
        isLoading = true
        loadAd(adUnitId) {
            isShowing = true
            attachFullScreenCallback(callback)
            showAd(activity, callback)
        }
    }

    private fun attachFullScreenCallback(callback: (adState: RewardInterstitialAdCallback) -> Unit) {
        rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.d(REWARDED_INTERSTITIAL_LOG, "onAdShowedFullScreenContent: Ad showed")
                callback(RewardInterstitialAdCallback.SHOWED)
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(REWARDED_INTERSTITIAL_LOG, "onAdDismissedFullScreenContent: Ad dismissed")
                callback(RewardInterstitialAdCallback.DISMISSED)
                rewardedInterstitialAd = null
                isShowing = false
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.d(
                    REWARDED_INTERSTITIAL_LOG,
                    "onAdFailedToShowFullScreenContent: Ad failed to show: Error: ${error.message}"
                )
                callback(RewardInterstitialAdCallback.FAILED_TO_SHOW)
                rewardedInterstitialAd = null
                isShowing = false
            }
        }
    }

    private fun showAd(
        activity: Activity,
        callback: (adState: RewardInterstitialAdCallback) -> Unit
    ) {
        rewardedInterstitialAd?.show(activity) {
            Log.d(
                REWARDED_INTERSTITIAL_LOG,
                "loadAndShowAd: Ad rewarded: type: ${it.type}, amount: ${it.amount}"
            )
            callback(RewardInterstitialAdCallback.REWARDED)
        }
    }

    fun preLoadAd(adUnitId: String, onLoaded: () -> Unit) {
        if (rewardedInterstitialAd != null) {
            Log.d(REWARDED_INTERSTITIAL_LOG, "preLoadAd: Ad already available")
            return
        }
        if (isLoading) {
            Log.d(REWARDED_INTERSTITIAL_LOG, "preLoadAd: Ad already loading")
            return
        }
        isLoading = true
        loadAd(adUnitId) {
            onLoaded()
        }
    }

    fun showAdIfAvailable(activity: Activity, adUnitId: String, callback: (adState: RewardInterstitialAdCallback) -> Unit){
        if (rewardedInterstitialAd == null){
            Log.d(REWARDED_INTERSTITIAL_LOG, "showAdIfAvailable: Ad not available")
            return
        }
        showAd(activity, callback)
    }
    
    enum class RewardInterstitialAdCallback {
        FAILED_TO_SHOW, SHOWED, REWARDED, DISMISSED
    }

}
