package com.documentscanner.jetpackcomposepractice.ads

import android.content.Context
import android.util.Log
import android.view.View
import com.documentscanner.jetpackcomposepractice.R
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest

class NativeAdManager(private val context: Context) {

    fun initNativeAd(templateView: TemplateView){
        val adLoader = AdLoader.Builder(
            context,
            context.getString(R.string.native_ad_id)
        ).forNativeAd {ad->
            Log.d("consentError87654", "got native ad")
            templateView.setNativeAd(ad)
            templateView.visibility = View.VISIBLE
        }.build()
        val adRequest = AdRequest.Builder().build()
        adLoader.loadAd(adRequest)
    }

}