package com.documentscanner.jetpackcomposepractice.ads

import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.documentscanner.jetpackcomposepractice.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

class BannerAd(private val context: Context) {

    private val adView = AdView(context)

    fun loadAndShowBannerAd(isAnchored: Boolean, bannerAdView: FrameLayout) {
        adView.adUnitId = context.getString(R.string.banner_ad_id)
        if (isAnchored) {
            adView.setAdSize(adSize(bannerAdView))
        } else {
            adView.setAdSize(AdSize.BANNER)
        }

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun adSize(bannerAdView: FrameLayout): AdSize {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            getSizeAboveAndroidApi30(bannerAdView)
        } else {
            getSizeBelowAndroidApi30(bannerAdView)
        }
    }

    @Suppress("DEPRECATION")
    private fun getSizeBelowAndroidApi30(bannerAdView: FrameLayout): AdSize {

        val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)


        var adWidthPixels = bannerAdView.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val density = outMetrics.density
        val adWidth = (adWidthPixels / density).toInt()

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getSizeAboveAndroidApi30(bannerAdView: FrameLayout): AdSize {
        val windowMetrics = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).currentWindowMetrics
        val bounds = windowMetrics.bounds

        var adWidthPixels = bannerAdView.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = bounds.width().toFloat()
        }
        val density = context.resources.displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }

}
