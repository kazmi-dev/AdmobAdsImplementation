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
        nativeView.visibility = View.INVISIBLE
    }

    override fun restoreAds() {
        // if using binding
        binding.nativeAdView.visibility = View.VISIBLE

        //if using view
        nativeView.visibility = View.VISIBLE
    }
```

### Native Ads.

#### Native Template for Native Ads
Import Google Native Template as a module.

#### Installation
Inject the native ads depedency implementation,
```
  @Inject
  lateinit var nativeAdManager: NativeAdManager

  ...
  remaining code
  ...
  //if using view
  nativeAdManager.initNativeAd(templateView)

  // if using binding
  nativeAdManager.initNativeAd(binding.templateView)
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


