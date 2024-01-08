# AdmobAdsImplementation
Google Ad-mob Implementation. Kotlin || dagger-Hilt || Coroutines || MVVM

## Installation
download ads folder and paste in your project.

### 1.
In your application class, add these lines,
```application class
    var appOpenManager: AppOpenManager? = null
    @Inject
    lateinit var interstitialAd: InterstitialAdManager

    fun initializeMobileAdsSdk(){
        MobileAds.initialize(this){}
        appOpenManager = AppOpenManager(this)
        interstitialAd.loadInterstitialAd()
    }

