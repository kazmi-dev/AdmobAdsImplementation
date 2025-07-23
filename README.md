# 📢 Android Google Ads Library

A lightweight Kotlin-based library to easily integrate **Google AdMob Ads** into any Android project with minimal setup.

Supports:
- ✅ Banner and Collapsible Banner Ads  
- ✅ Interstitial Ads
- ✅ Interstitial Rewarded Ads
- ✅ Rewarded Ads  
- ✅ Native Ads
- ✅ App Open Ads

---

## 🔧 Banner and Collapsible Banner Ads

### Copy `BannerAdManager.kt` class in your code and initialize it as below:

```Initialization
@Inject lateinit var bannerAdManager: BannerAdManager
```
### Single Banner Ad:

```Single ad show
bannerAdManager.loadAndShowAd(
                activity = requireActivity(),
                adUnitId = "Your Ad Unit ID",
                adViewContainer = Your ViewContainer(FrameLayout),
                loadingView = Lyour Loading View(textView) nullable,
                isCollapsible = false (default),
                isCollapsibleAtBottom = false (default)
            )
```


