# AdmobAdsImplementation
Google Ad-mob Implementation. Kotlin || dagger-Hilt || Coroutines || MVVM

## Installation
Below is the implementation of ads.

### Download Ads folder.
download ads folder and paste in your project.

### Google Consent Form
From Jan, 15 2024 you must take user's consent for personalized ads.
In your Application class, add this fun:
```
 fun initializeAdmobSdk(){
    MobileAds.initialize(this){}
}
```

### Taking User's Consent Form
In your Splash Activity or Fragment, add these lines
```
  @Inject
  lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
```
In onCreate() method of your Activity or Fragment
```
googleMobileAdsConsentManager.gatherConsent(this) { consentError ->
  if (consentError != null) {
    Log.d("consentError87654", String.format("%s: %s", consentError.errorCode, consentError.message))
  }

  if (googleMobileAdsConsentManager.canRequestAds) {
    //For Activity
    (applicationContext as BaseApplication).initializeAdmobSdk()

    //For Fragment
    (requireActivity() as BaseApplication).initializeAdmobSdk()
  }
}
```

### AppOpen Ad.

#### Initialization
In your Activity or Fragment, add these lines,
```
//Inject the appOpen impl
@Inject
lateinit var appOpenAdsManager: AppOpenAdsManager
```

##### Usage with both Native and AppOpen ads.
If you use both Native and AppOpen Ads, than use "AppOpen" interface in your fragment or Activity and override methods:
```
  class SomeFragment : Fragment(), AppOpen
                 OR
  class SomeActivity : Activity(), AppOpen
    
  ....
  //remaning code
  ....
    
    //hide your native ad while showing appOpen and restore on close (best practice).

    override fun closeAds() {
        // if using binding
        binding.nativeAdView.visibility = View.INVISIBLE

        //if using view
        nativeAdView.visibility = View.INVISIBLE
    }

    override fun restoreAds() {
        // if using binding
        binding.nativeAdView.visibility = View.VISIBLE

        //if using view
        nativeAdView.visibility = View.VISIBLE
    }
```

### Native Ads.

#### Native Template for Native Ads
Import Google Native Template as a module.

#### Installation
In your Activity's or Fragment's layout file add:
```
 <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/native_ad_container"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:visibility="visible"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent">

        <LinearLayout
            android:id="@+id/ad_template"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent">

            <include layout="@layout/native_ad_loading_small" />
        </LinearLayout>

        <com.google.android.ads.nativetemplates.TemplateView
            android:id="@+id/native_ad_view"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:elevation="3dp"
            android:visibility="invisible"
            app:gnt_template_type="@layout/gnt_small_template_view"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>
```
Inject the native ads implementation depedency,
```
  @Inject
  lateinit var nativeAdManager: NativeAdManager

  ...
  remaining code
  ...
  //if using view
  nativeAdManager.initNativeAd(nativeAdView)

  // if using binding
  nativeAdManager.initNativeAd(binding.nativeAdView)
```

### Interstitial Ads

#### Installation
Inject the interstitial ads depedency implementation,
```
  @Inject
  lateinit var interstitialAdManager: InterstitialAdManager

  ...
  remaining code
  ...

  //for activity
  interstitialAdManager.showInterstitialAdIfAvailable(this@MainActivity)

  //for fragment
  interstitialAdManager.showInterstitialAdIfAvailable(requireActivity())
```
Optional:
```
  //using adPos from AppSettings,
  if (adPos > 2){
    adPos = 0
    interstitialAdManager.showInterstitialAdIfAvailable(this@MainActivity)
  }else{
    adPos++
  }
```

### Banner Ad
In your Activity's or Fragment's layout file add:
```
 <com.google.android.gms.ads.AdView
    xmlns:ads="http://schemas.android.com/apk/res-auto"
    android:id="@+id/adView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_centerHorizontal="true"
    android:layout_alignParentBottom="true"
    ads:adSize="BANNER">
 </com.google.android.gms.ads.AdView>
```
In your Activity or Fragment,
```
 //Inject banner ad depedency
 @Inject
 latinit var bannerAd: BannerAd

 ...
 remaining code
 ...

 // loadAndShowBannerAd takes two parameter, isAnchored : Boolean and bannerAdView: FrameLayout
 bannerAd.loadAndShowBannerAd(isAnchored = false, bannerAdView = binding.bannerAdView))
```


