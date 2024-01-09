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
AppOpen ad Implementation:

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

    //In onCreate() method of Activity or Fragment,
    
    ....
    //remaning code
    ....

    //hide your native ad while showing appOpen and restore on close (best practice).
    override fun closeAds() {
        binding.nativeAdView.visibility = View.INVISIBLE
    }

    override fun restoreAds() {
        binding.nativeAdView.visibility = View.VISIBLE
    }
```

### Native Template for Native Ads.
Import Google Native Template as a module.
