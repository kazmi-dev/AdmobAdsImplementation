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

### Initialization:

```Initialization
@Inject lateinit var bannerAdManager: BannerAdManager
```
### Single Banner Ad:
Use this function to dynamically load and display a Banner Ad inside a container (e.g., FrameLayout), with optional loading text and collapsibility settings.

```Single ad show
bannerAdManager.loadAndShowAd(
    activity = requireActivity(),
    adUnitId = "Your Ad Unit ID",
    adViewContainer = yourViewContainer,         
    loadingView = yourLoadingTextView,           
    isCollapsible = false,                        
    isCollapsibleAtBottom = true                  
)
```
| Parameter               | Type        | Required | Description                                                                                                 |
| ----------------------- | ----------- | -------- | ----------------------------------------------------------------------------------------------------------- |
| `activity`              | `Activity`  | ✅ Yes    | Your current `Activity` (or `requireActivity()` inside Fragment). Used for context.                         |
| `adUnitId`              | `String`    | ✅ Yes    | Your **Banner Ad Unit ID** from AdMob (test or production).                                                 |
| `adViewContainer`       | `ViewGroup` | ✅ Yes    | The layout container where the banner ad will be injected. Usually a `FrameLayout`.                         |
| `loadingView`           | `TextView?` | ❌ No     | Optional loading text (e.g., "Loading Ad..."). It will be shown until the ad is loaded.                     |
| `isCollapsible`         | `Boolean`   | ❌ No     | If true, the banner will be collapsible (expand/collapse animation). Default is `false`.                    |
| `isCollapsibleAtBottom` | `Boolean`   | ❌ No     | If `isCollapsible` is `true`, setting this to `true` will collapse from the **bottom**. Default is `true`. |

### Preloading Banner Ads:
This function preloads multiple banner ads into memory so they’re ready to display when needed (useful for ViewPagers, RecyclerViews, or dynamic sections in your app).

```preloading ads
bannerAdManager.getPreBannerAds(
    count = your required ads coun        
    activity = requireActivity(),          
    adUnitId = "Your Ad Unit ID"           
)
bannerAdManager.showBannerAd(
    requireActivity(),
    binding.bannerAdViewContainer
)
```
| Parameter  | Type       | Required | Description                                                               |
| ---------- | ---------- | -------- | ------------------------------------------------------------------------- |
| `count`    | `Int`      | ✅ Yes    | Number of banner ads to preload.                                          |
| `activity` | `Activity` | ✅ Yes    | Activity context used by AdMob SDK. Use `requireActivity()` in fragments. |
| `adUnitId` | `String`   | ✅ Yes    | Your **Banner Ad Unit ID** (from AdMob) used to request ads.              |

## 🔧 Interstitial Ads

