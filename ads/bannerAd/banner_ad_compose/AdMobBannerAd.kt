import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import androidx.compose.ui.platform.LocalResources

@Composable
fun AdMobBannerAd(modifier: Modifier = Modifier) {

    //get Banner Ad Size
    val context: Context = LocalContext.current
    val screenWidthPx = LocalWindowInfo.current.containerSize.width
    val density = LocalResources.current.displayMetrics.density
    val screenWidthDp = (screenWidthPx / density).toInt()
    val activity = context.findActivity()
    var adView: AdView? = null
    val bannerAdSize = remember(screenWidthDp) {
        activity?.let {
            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(it, screenWidthDp)
        }
    }

    //Create Banner Ad View
    AndroidView(
        modifier = modifier,
        factory = { context->
           adView =  AdView(context).apply {
                setAdSize(bannerAdSize ?: AdSize.BANNER)
                adUnitId = "ca-app-pub-3940256099942544/9214589741"
                val adRequest = AdRequest.Builder().build()
                loadAd(adRequest)
            }
            adView
        }
    )

    DisposableEffect(adView) {
        onDispose {
            adView?.destroy()
        }
    }

}

fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
