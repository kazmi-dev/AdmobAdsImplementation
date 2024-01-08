package com.documentscanner.jetpackcomposepractice.ads

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DependencyInjectionAds {

    @Singleton
    @Provides
    fun providesInterstitialAdsInstance(
        @ApplicationContext context: Context
    ): InterstitialAdManager{
        return InterstitialAdManager(context)
    }

    @Singleton
    @Provides
    fun providesNativeAdsInstance(
        @ApplicationContext context: Context
    ): NativeAdManager{
        return NativeAdManager(context)
    }

    @Singleton
    @Provides
    fun providesGoogleMobileAdsConsentManagerInstance(
        @ApplicationContext context: Context
    ): GoogleMobileAdsConsentManager{
        return GoogleMobileAdsConsentManager(context)
    }

}