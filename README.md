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

### 1. Initialization:

```Initialization
@Inject lateinit var bannerAdManager: BannerAdManager
```
### 2. Single Banner Ad:
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

### 3. Preloading Banner Ads:
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

### 1. Initialization:

```Initialization
@Inject lateinit var interstitialADManager: InterstitialAdManager
```

### 2. Show Ads on demand:
This function loads and immediately shows an interstitial ad at runtime. It's designed for on-the-spot ad display without preloading.

```show on demand ad
fun showAdOnDemand(
    activity: Activity,
    adUnitId: String,
    callback: (AdState) -> Unit
)
```

| Parameter  | Type                | Required | Description                                                                                    |
| ---------- | ------------------- | -------- | ---------------------------------------------------------------------------------------------- |
| `activity` | `Activity`          | ✅ Yes    | The current activity context used to display the ad.                                           |
| `adUnitId` | `String`            | ✅ Yes    | Your **Interstitial Ad Unit ID** from AdMob.                                                   |
| `callback` | `(AdState) -> Unit` | ✅ Yes    | Lambda callback that provides ad state results. It must handle one of the `AdState` responses. |

### 3. Show Ads by preloading:
This function shows a previously preloaded interstitial ad if it's ready. It’s ideal when you’ve already loaded an ad and want to display it only if available, avoiding unnecessary reloads or errors.

```preloaded ads
fun showAdIfAvailable(
    activity: Activity,
    callback: (AdState) -> Unit
)
```

| Parameter  | Type                | Required | Description                                                                    |
| ---------- | ------------------- | -------- | ------------------------------------------------------------------------------ |
| `activity` | `Activity`          | ✅ Yes    | The current activity used to show the interstitial ad.                         |
| `callback` | `(AdState) -> Unit` | ✅ Yes    | Lambda function that returns the ad display state (Loaded, Closed, or Failed). |


## 🔧 App Open Ads

### 1. Initialization:

```Initialization
@Inject lateinit var appOpenAdManager: AppOpenAdManager
```
### 2. Set before showing ads:
You can set these in your Application class or in Activty class.

#### 1. Set if preloading:
Sets whether the interstitial ad should be shown on demand or only if preloaded.

```set preload
setShowAdOnDemand(isShowAdOnDemand: Boolean)
```

| Name               | Type      | Description                                                                                                                                           |
| ------------------ | --------- | ----------------------------------------------------------------------------------------------------------------------------------------------------- |
| `isShowAdOnDemand` | `Boolean` | If `true`, the ad will be loaded and shown immediately using `showAdOnDemand()`. If `false`, only preloaded ads will be used (`showAdIfAvailable()`). |

#### 2. Set ad unit id:
Sets a global interstitial ad unit ID, so you don’t need to pass it every time to loadAd() or showAdOnDemand().

``` adunit id
setAdUnitId(adUnitId: String)
```

| Name       | Type     | Description                         |
| ---------- | -------- | ----------------------------------- |
| `adUnitId` | `String` | Your AdMob interstitial ad unit ID. |

#### 3. Set ad visibility controller:
Sets a controller to hide or restore your app’s UI elements when the ad appears or disappears. Helpful for removing sensitive or interactive UI during ads.

```set visibility
setAdVisibilityController(adVisibilityController: AdVisibilityController)
```

| Name                     | Type                     | Description                                                        |
| ------------------------ | ------------------------ | ------------------------------------------------------------------ |
| `adVisibilityController` | `AdVisibilityController` | Interface instance to control your app's UI visibility during ads. |

### 3. Show Ads
If on demand is set `true` than Ad will be loaded and than showd otherwise ad will be preloaded and will be shown instantly

## Native ads

### 1. Initialization:

``` initialize
@Inject lateinit var nativeAdmanager: NativeAdManager
```

### 2. Copy required `layouts` and `drawables` in your project:
Copy the layouts and drawables in your projects resource folder so that it will be avaialble to the `NativeAdManager` class.

### 3. Show Native Ad:
This function loads and immediately displays a native ad inside the given container without requiring any preload setup. It’s perfect for screens where you need to show a native ad on-the-fly with minimal code.

```show ad
fun showNativeAdOnDemand(
    adUnitId: String,
    nativeAdContainer: FrameLayout,
    adSize: NativeTemplate = NativeTemplate.MEDIUM
)
```
| Parameter           | Type             | Required | Description                                                                |
| ------------------- | ---------------- | -------- | -------------------------------------------------------------------------- |
| `adUnitId`          | `String`         | ✅ Yes    | Your **Native Ad Unit ID** from AdMob (test or production).                |
| `nativeAdContainer` | `FrameLayout`    | ✅ Yes    | The container where the native ad view will be displayed.                  |
| `adSize`            | `NativeTemplate` | ❌ No     | Enum defining the layout size/style of the native ad. Default is `MEDIUM`. |




