# AdmobAdsImplementation
Google Ad-mob Implementation. Kotlin || dagger-Hilt || Coroutines || MVVM

## Installation
Below is the implementation of ads.

### Download Ads folder.
download ads folder and paste in your project.

### Initialize Admob 
In your onCreate() method of your Application class add this line:
```
MobileAds.initialize(this){}
```

### AppOpen Ad.

#### Initialization
In your Activity or Fragment, add these lines,
```
//just inject the appOpen impl
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




