import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.ai.fusion.character.merge.video.generator.databinding.NativeAdMediumLayoutBinding
import com.ai.fusion.character.merge.video.generator.databinding.NativeAdSmallLayoutBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeAdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val NATIVE_LOG = "nativeAd_2836429847092384"
    }

    private var nativeAd: NativeAd? = null
    private var isAdLoading: Boolean = false

    private fun loadNativeAd(adUnitId: String, onLoad: () -> Unit) {
        //create ad loader
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd {
                nativeAd = it
            }
            .withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    Log.d(NATIVE_LOG, "loadNativeAd: Ad Loaded successfully")
                    isAdLoading = false
                    onLoad()
                }

                override fun onAdFailedToLoad(loadError: LoadAdError) {
                    Log.d(NATIVE_LOG, "loadNativeAd: Ad Failed to load: Error: ${loadError.message}")
                    isAdLoading = false
                    nativeAd = null
                }

                override fun onAdImpression() {
                    Log.d(NATIVE_LOG, "loadNativeAd: Ad Impression, shown.")
                }
            }).build()

        //Request ad
        val adRequest = AdRequest.Builder().build()
        adLoader.loadAd(adRequest)
    }

    fun showNativeAdOnDemand(adUnitId: String, nativeAdContainer: FrameLayout, adSize: NativeTemplate = NativeTemplate.MEDIUM){
        //check is ad is already available
        if (nativeAd != null) {
            Log.d(NATIVE_LOG, "loadNativeAd: Ad already available")
            showNativeAd(nativeAd!!, adSize, nativeAdContainer)
            return
        }
        //check if already loading
        if (isAdLoading) {
            Log.d(NATIVE_LOG, "loadNativeAd: Ad already loading")
            return
        }

        loadNativeAd(adUnitId){
            nativeAd?.let {ad->
                showNativeAd(ad, adSize, nativeAdContainer)
            }?: run {
                Log.d(NATIVE_LOG, "loadNativeAd: Ad not Available to populate")
                nativeAd = null
                return@run
            }
        }
    }

    private fun showNativeAd(ad: NativeAd, adSize: NativeTemplate, nativeAdContainer: FrameLayout) {
        when(adSize){
            NativeTemplate.SMALL->{
                populateNativeSmallAd(ad, nativeAdContainer)
            }
            else-> {
                populateNativeMediumAd(ad, nativeAdContainer)
            }
        }
    }

    //Destroy native ad wherever you want to
    fun destroyNativeAd(){
        nativeAd?.destroy()
        nativeAd = null
        Log.d(NATIVE_LOG, "DestroyNativeAd: Destroyed")
    }

    private fun populateNativeMediumAd(ad: NativeAd, nativeAdContainer: FrameLayout) {
        val binding = NativeAdMediumLayoutBinding.inflate(LayoutInflater.from(context))
        binding.apply {
            populateNativeAd(
                nativeAdView,
                headlineView,
                headlineTv,
                mediaView,
                bodyView,
                callToActionBtn,
                iconView,
                ratingBar,
                advertiserStoreView,
                ad
            )
        }
        //add view to container
        nativeAdContainer.apply {
            removeAllViews()
            addView(binding.root)
        }
    }

    private fun populateNativeSmallAd(ad: NativeAd, nativeAdContainer: FrameLayout) {
        val binding = NativeAdSmallLayoutBinding.inflate(LayoutInflater.from(context))

        binding.apply {
            populateNativeAd(
                nativeAdView,
                headlineView,
                headlineTv,
                mediaView,
                bodyView,
                callToActionBtn,
                iconView,
                ratingBar,
                advertiserStoreView,
                ad
            )
        }

        //add view to container
        nativeAdContainer.apply {
            removeAllViews()
            addView(binding.root)
        }

    }

    private fun populateNativeAd(
        nativeAdView: NativeAdView,
        headlineView: LinearLayout,
        headlineTv: TextView,
        mediaView: MediaView,
        bodyView: TextView,
        callToActionBtn: Button,
        iconView: ImageView,
        ratingBar: RatingBar,
        advertiserStoreView: TextView,
        ad: NativeAd,
    ){
        //populate headline view
        nativeAdView.headlineView = headlineView
        headlineTv.text = ad.headline

        //populate media view
        nativeAdView.mediaView = mediaView
        mediaView.mediaContent = ad.mediaContent

        //populate body view
        nativeAdView.bodyView = bodyView
        bodyView.text = ad.body

        //populate call to action view
        nativeAdView.callToActionView = callToActionBtn
        callToActionBtn.text = ad.callToAction

        //populate icon view
        nativeAdView.iconView = iconView
        iconView.setImageDrawable(ad.icon?.drawable)

        //populate rating, advertiser and store view
        when{
            hasRating(ad)->{
                nativeAdView.starRatingView = ratingBar
                ratingBar.rating = ad.starRating?.toFloat() ?: 0f
                ratingBar.isVisible = true
            }
            hasOnlyStore(ad)->{
                nativeAdView.storeView = advertiserStoreView
                advertiserStoreView.text = ad.store
            }
            else->{
                nativeAdView.advertiserView = advertiserStoreView
                advertiserStoreView.text = ad.advertiser
            }
        }

        //set Native ad view
        nativeAdView.setNativeAd(ad)
    }

    private fun hasRating(ad: NativeAd): Boolean {
        return ad.starRating?.toFloat()?.let {rating->
            rating > 0
        }?: false
    }
    private fun hasOnlyStore(ad: NativeAd): Boolean {
        return ad.store!= null && ad.advertiser == null
    }

    enum class NativeTemplate{
        MEDIUM, SMALL, LARGE
    }

}
