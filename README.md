# AdmobAdsImplementation
Google Ad-mob Implementation. Kotlin || dagger-Hilt || Coroutines || MVVM

## Installation
download ads folder and paste in your project.

### 1.
Import Google Native Template as a module.

### 2.
In your application class, add these lines,
```
    var appOpenManager: AppOpenManager? = null
    @Inject
    lateinit var interstitialAd: InterstitialAdManager

    //create fun that will be called after getting user's consent
    fun initializeMobileAdsSdk(){
        MobileAds.initialize(this){}
        appOpenManager = AppOpenManager(this)
        interstitialAd.loadInterstitialAd()
    }
```

### 3.
If you use both Native and AppOpen Ads, than use "AppOpen" interface in your fragment or Activity and override methods:
```

    class SomeFragment : Fragment(), AppOpen
                    OR
    class SomeActivity : Activity(), AppOpen
    ....
    //remaning code

    override fun closeAds() {
        binding.nativeAdView.visibility = View.INVISIBLE
    }

    override fun restoreAds() {
        binding.nativeAdView.visibility = View.VISIBLE
    }
```

### 4.

