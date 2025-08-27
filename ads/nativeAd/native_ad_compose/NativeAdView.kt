import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.kazmi.dev.jetpackcompose.databinding.NativeAdMediumLayoutBinding
import com.kazmi.dev.jetpackcompose.databinding.NativeAdSmallLayoutBinding

@Composable
fun NativeAdView(modifier: Modifier = Modifier, isSmall: Boolean = false, adUniId: String) {

    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    LaunchedEffect(Unit) {
        if (nativeAd == null) {
            loadNativeAd(context, adUniId) {
                nativeAd = it
            }
        }
    }

    nativeAd?.let {
        ShowNativeAd(isSmall = isSmall, nativeAd = it)
    }

    DisposableEffect(nativeAd){
        onDispose { 
            nativeAd?.destroy()
            nativeAd = null
        }
    }

}

@Composable
private fun ShowNativeAd(modifier: Modifier = Modifier, isSmall: Boolean, nativeAd: NativeAd) {
    Column(
        modifier = modifier.wrapContentSize()
    ) {
        AndroidView(
            factory = { context ->
                val inflater = LayoutInflater.from(context)
                val binding: ViewBinding = if (isSmall) NativeAdSmallLayoutBinding.inflate(inflater) else NativeAdMediumLayoutBinding.inflate(inflater)

                //populateNativeAd
                populatingNativeAd(isSmall, nativeAd, binding)

                binding.root
            },
            update = {
                val binding = if (isSmall) NativeAdSmallLayoutBinding.bind(it) else NativeAdMediumLayoutBinding.bind(it)

                //populateNativeAd
                populatingNativeAd(isSmall, nativeAd, binding)
            }
        )
    }
}

private fun populatingNativeAd(isSmall: Boolean, nativeAd: NativeAd, binding: ViewBinding) {
    if (isSmall) {
        (binding as NativeAdSmallLayoutBinding).apply {

            populateNativeAd(
                nativeAd,
                binding.nativeAdView,
                binding.mediaView,
                binding.headlineView,
                binding.iconView,
                binding.bodyView,
                binding.headlineTv,
                binding.advertiserStoreView,
                binding.priceView,
                binding.ratingBar,
                binding.callToActionBtn
            )
        }
    } else {
        (binding as NativeAdMediumLayoutBinding).apply {
            populateNativeAd(
                nativeAd,
                binding.nativeAdView,
                binding.mediaView,
                binding.headlineView,
                binding.iconView,
                binding.bodyView,
                binding.headlineTv,
                binding.advertiserStoreView,
                binding.priceView,
                binding.ratingBar,
                binding.callToActionBtn
            )
        }
    }
}

private fun populateNativeAd(
    nativeAd: NativeAd,
    nativeAdView: NativeAdView,
    mediaView: MediaView,
    headlineView: LinearLayout,
    iconView: ImageView,
    bodyView: TextView,
    headlineTv: TextView,
    advertiserStoreView: TextView,
    priceView: TextView,
    ratingBar: RatingBar,
    callToActionBtn: Button
) {
    nativeAdView.apply {
        // media
        this.mediaView = mediaView
        mediaView.mediaContent = nativeAd.mediaContent

        //headline
        this.headlineView = headlineView
        headlineTv.text = nativeAd.headline

        //Ad Icon
        this.iconView = iconView
        iconView.setImageDrawable(nativeAd.icon?.drawable)

        //body
        this.bodyView = bodyView
        bodyView.text = nativeAd.body

        //advertiser and store
        nativeAd.advertiser?.let {
            this.advertiserView = advertiserStoreView
            advertiserStoreView.text = it
        } ?: nativeAd.store?.let {
            this.advertiserView = advertiserStoreView
            advertiserStoreView.text = it
        } ?: {
            advertiserStoreView.isVisible = false
        }

        //price
        nativeAd.price?.let {
            this.priceView = priceView
            priceView.text = it
        } ?: {
            priceView.isVisible = false
        }

        //call to action
        this.callToActionView = callToActionBtn
        callToActionBtn.text = nativeAd.callToAction

        //rating bar
        this.starRatingView = ratingBar
        ratingBar.rating = nativeAd.starRating?.toFloat() ?: 0f

        setNativeAd(nativeAd)

    }
}

private fun loadNativeAd(context: Context, adUniId: String, onAdLoaded: (NativeAd) -> Unit) {
    runCatching {
        AdLoader.Builder(context, adUniId)
            .forNativeAd { ad ->
                onAdLoaded(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    // ad load
                }
            }).build()
    }.onSuccess {
        val adRequest = AdRequest.Builder().build()
        it.loadAd(adRequest)
    }

}
