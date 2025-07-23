import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerAdManager@Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val BANNER_LOG = "BannerAdManager_346298794582"
    }
    private lateinit var adSize: AdSize

    private val adViewList: MutableList<AdView> = mutableListOf()

    private fun getAdSize(activity: Activity, adViewContainer: FrameLayout?): AdSize {
        val windowManager = activity.windowManager
        val displayMetrics = DisplayMetrics()
        var density = activity.resources.displayMetrics.density

        val currentScreenWidthInPx = adViewContainer?.width.takeIf { it != 0 }?.toFloat() ?: run {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                windowManager.currentWindowMetrics.bounds.width().toFloat()
            } else {
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                density = displayMetrics.density
                displayMetrics.widthPixels.toFloat()
            }
        }

        val adWidth = (currentScreenWidthInPx / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    /******************
    load and show
     ********************/
    fun loadAndShowAd(
        activity: Activity,
        adUnitId: String,
        adViewContainer: FrameLayout,
        loadingView: TextView?,
        isCollapsible: Boolean = false,
        isCollapsibleAtBottom: Boolean = true
    ) {

        //initialize AD View
        val adView = AdView(context)

        //Get Ad Size
        adSize = getAdSize(activity, adViewContainer)

        //Set size of banner ad viewContainer
        adViewContainer.apply {
            layoutParams.apply {
                width = adSize.getWidthInPixels(activity)
                height = adSize.getHeightInPixels(activity)
            }
            requestLayout()
        }
        loadBannerAd(
            adView,
            adUnitId,
            adViewContainer,
            loadingView,
            isCollapsible,
            isCollapsibleAtBottom
        )

    }

    private fun loadBannerAd(
        adView: AdView,
        adUnitId: String,
        adViewContainer: FrameLayout,
        loadingView: TextView?,
        isCollapsible: Boolean = false,
        isCollapsibleAtBottom: Boolean = true
    ) {
        //Set Banner Ad Size
        adView.setAdSize(adSize)

        //Set Ad Unit Id
        adView.adUnitId = adUnitId

        //attach view to container
        adViewContainer.removeAllViews()
        adViewContainer.addView(adView)

        //adjust loading view
        loadingView?.let {
            it.layoutParams.apply {
                it.width = adSize.getWidthInPixels(context)
                it.height = adSize.getHeightInPixels(context)
            }
            it.requestLayout()
            it.text = "Loading Ad..."
        }

        //Request Ad
        val adRequest: AdRequest = if (isCollapsible) {
            val extras = Bundle()
            extras.putString("collapsible", if (isCollapsibleAtBottom) "bottom" else "top")
            extras.putString("collapsible_request_id", UUID.randomUUID().toString())
            AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build()
        } else {
            AdRequest.Builder().build()
        }

        //load Ad
        adView.loadAd(adRequest)

        //Ad load and show Listeners
        adView.adListener = object: AdListener(){
            override fun onAdLoaded() {
                Log.d(BANNER_LOG, "onAdLoaded: Ad Loaded.")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.d(BANNER_LOG, "onAdFailedToLoad: Ad Failed to Load, error: ${error.message}")
            }
            override fun onAdImpression() {
                Log.d(BANNER_LOG, "onAdImpression: Ad Impression")
            }
        }
    }


    /******************
    preload ads
     ********************/
    fun showBannerAd(
        activity: Activity,
        adViewContainer: FrameLayout
    ){
        val bannerAd = getBannerAd()
        if (bannerAd == null){
            Log.d(BANNER_LOG, "showBannerAd: Banner Ad is null")
            return
        }

        adViewContainer.apply {
            layoutParams.apply {
                width = adSize.getWidthInPixels(activity)
                height = adSize.getHeightInPixels(activity)
            }
            requestLayout()
        }

        adViewContainer.removeAllViews()
        adViewContainer.addView(bannerAd)
    }

    private fun getBannerAd(): AdView?{
        if (adViewList.isEmpty()){
            Log.d(BANNER_LOG, "getBannerAd: Ad list is empty.")
            return null
        }
        val size = adViewList.size
        Log.d(BANNER_LOG, "getBannerAd: Ad list size: $size")
        return adViewList.removeAt(size-1)
    }

    fun getPreBannerAds(
        ads: Int,
        activity: Activity,
        adUnitId: String
    ){
        for (i in 1..ads){
            val adView = createAdView(activity, adUnitId)
            adViewList.add(adView)
        }
    }

    private fun createAdView(
        activity: Activity,
        adUnitId: String
    ): AdView{
        val adView = AdView(context)
        if (!::adSize.isInitialized){
            adSize = getAdSize(activity, null)
        }
        adView.setAdSize(adSize)
        adView.adUnitId = adUnitId

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        adView.adListener = object: AdListener(){
            override fun onAdLoaded() {
                Log.d(BANNER_LOG, "onAdLoaded: Ad Loaded.")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.d(BANNER_LOG, "onAdFailedToLoad: Ad Failed to Load, error: ${error.message}")
            }
            override fun onAdImpression() {
                Log.d(BANNER_LOG, "onAdImpression: Ad Impression")
            }
        }

        return adView
    }
}
