import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardedAdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val REWARDED_LOG = "rewardedAd_2836429847092384"
    }

    private var rewardedAd: RewardedAd? = null
    private var isLoading: Boolean = false
    private var isShowing: Boolean = false

    private fun loadAd(adUnitId: String, onLoad: () -> Unit) {
        isLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(REWARDED_LOG, "onAdLoaded: Ad loaded successfully")
                    rewardedAd = ad
                    isLoading = false
                    onLoad()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d(REWARDED_LOG, "onAdLoaded: Ad Failed to load: Error: ${error.message}")
                    isLoading = false
                    rewardedAd = null
                }
            }
        )
    }

    private fun loadAndShowAd(
        activity: AdActivity,
        adUnitId: String,
        callback: (adState: RewardAdCallback) -> Unit
    ) {

        if (rewardedAd != null) {
            Log.d(REWARDED_LOG, "loadAndShowAd: Ad already available")
            showAd(activity, callback)
            return
        }

        if (isLoading) {
            Log.d(REWARDED_LOG, "loadAndShowAd: Ad already loading")
            return
        }
        if (isShowing) {
            Log.d(REWARDED_LOG, "loadAndShowAd: Ad already showing")
            return
        }
        isLoading = true
        loadAd(adUnitId) {
            showAd(activity, callback)
        }
    }

    private fun showAd(
        activity: AdActivity,
        callback: (adState: RewardAdCallback) -> Unit
    ) {
        attachFullScreenCallback(callback)
        isShowing = true
        rewardedAd?.show(activity) {
            Log.d(
                REWARDED_LOG,
                "loadAndShowAd: Ad rewarded: type: ${it.type}, amount: ${it.amount}"
            )
            callback(RewardAdCallback.REWARDED)
        }
    }

    private fun attachFullScreenCallback(callback: (adState: RewardAdCallback) -> Unit) {
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.d(REWARDED_LOG, "onAdShowedFullScreenContent: Ad showed")
                callback(RewardAdCallback.SHOWED)
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(REWARDED_LOG, "onAdDismissedFullScreenContent: Ad dismissed")
                callback(RewardAdCallback.DISMISSED)
                rewardedAd = null
                isShowing = false
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.d(
                    REWARDED_LOG,
                    "onAdFailedToShowFullScreenContent: Ad failed to show: Error: ${error.message}"
                )
                callback(RewardAdCallback.FAILED_TO_SHOW)
                rewardedAd = null
                isShowing = false
            }
        }
    }

    fun preLoadAd(adUnitId: String, onLoaded: () -> Unit) {
        if (rewardedAd != null) {
            Log.d(REWARDED_LOG, "loadAndShowAd: Ad already available")
            return
        }

        if (isLoading) {
            Log.d(REWARDED_LOG, "preLoadAd: Ad already loading")
            return
        }
        isLoading = true
        loadAd(adUnitId) {
            onLoaded()
        }
    }

    enum class RewardAdCallback {
        FAILED_TO_SHOW, SHOWED, REWARDED, DISMISSED
    }

}
