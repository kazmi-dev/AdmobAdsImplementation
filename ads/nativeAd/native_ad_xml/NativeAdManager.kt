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
    private var isAdShown: Boolean? = false

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
            NativeTemplate.SMALL ->{
                populateNativeSmallAd(ad, nativeAdContainer)
            }
            else-> {
                populateNativeMediumAd(ad, nativeAdContainer)
            }
        }
    }

    //Destroy native ad wherever you want to
    fun destroyNativeAd(){
        nativeAd?.let {
            if (isAdShown == true) {
                isAdShown = false
                it.destroy()
                nativeAd = null
                Log.d(NATIVE_LOG, "DestroyNativeAd: Destroyed")
            }
        }
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
                priceView,
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
                priceView,
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
        priceView: TextView,
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

        //populate rating bar
        ad.starRating?.let {
            ratingBar.isVisible = true
            ratingBar.rating = it.toFloat()
        }

        //populate advertiser and store view
        ad.advertiser?.let {
            nativeAdView.advertiserView = advertiserStoreView
            advertiserStoreView.text = it
        }?: run {
            ad.store?.let {
                nativeAdView.storeView = advertiserStoreView
                advertiserStoreView.text = it
            }?: { advertiserStoreView.isVisible = false }
        }

        //populate price
        ad.price?.let {
            nativeAdView.priceView = advertiserStoreView
            advertiserStoreView.text = it
        }?:{
            priceView.isVisible = false
        }

        //set Native ad view
        nativeAdView.setNativeAd(ad)
    }

}
