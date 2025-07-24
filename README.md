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

## 🛠️ Prerequisites

Requirements:
- ✅ Dagger/Hilt
- ✅ Google Ads services (play_services_ads_)
- ✅ Internet 🤪

## Setup Prerequisites:

### 1. 🧱 Hilt Setup (Required)
This library uses Dagger Hilt for dependency injection. Please complete the following setup before using the Ads SDK.

#### 📌 1. Add Hilt Dependencies

Add following depedencies and plugins to `libs.versions.toml`

Dependencies:
```depdencies
dagger-hilt = { group = "com.google.dagger", name = "hilt-android", version.ref = "daggerHilt_version" }
dagger-ksp = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "daggerHilt_version" }
```
Plugin:
```plugin
hilt = { id = "com.google.dagger.hilt.android", version.ref = "daggerHilt_version" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp_version" }
```
Versions:
```versions
daggerHilt_version = "2.55"
ksp_version = "2.1.0-1.0.29"
```

#### Now add add dependencies and plugins in `build.gradle` for both app and project level as below:
#### App Level:

Plugins:
```plugin
alias(libs.plugins.ksp)
alias(libs.plugins.hilt)
```
Dependencies:
```depdencies
implementation(libs.dagger.hilt)
ksp(libs.dagger.ksp)
```
#### Project Level:
Plugin:
```plugin
alias(libs.plugins.hilt) apply  false
alias(libs.plugins.ksp) apply  false
```

#### 📌 2. Annotate Your Application Class

```anotate
@HiltAndroidApp
class MyApp : Application()
```

#### 📌 3. Update Manifest file

```update
<application
    android:name=".MyApp"
    ... >
</application>
```

### 2. 🔌 Google Services Setup (Required for AdMob)
To use AdMob (Google Ads), your project must include the Google Services plugin and the Google Mobile Ads SDK.

#### 1. Add depdendency to `libs.versions.toml`:

Dependency:
```depdency
google-ads = {group = "com.google.android.gms", name = "play-services-ads", version.ref = "google_services_ads_version"}
```
Versions:
```version
google_services_ads_version = "24.4.0"
```
#### 2. Add to `build.gradle`

```depdency
implementation(libs.google.ads)
```

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
This function is used to load and show an Interstitial Ad on demand — all in one call. It is best used when you're okay with a short delay while the ad loads and shows immediately afterward.

```show on demand ad
fun showAdOnDemand(
    activity: Activity,
    adUnitId: String,
    callback: (InterstitialAdCallback) -> Unit
)
```

| Parameter  | Type                               | Required | Description                                               |
| ---------- | ---------------------------------- | -------- | --------------------------------------------------------- |
| `activity` | `Activity`                         | ✅ Yes    | The current activity used to present the ad.              |
| `adUnitId` | `String`                           | ✅ Yes    | Your **Interstitial Ad Unit ID** from AdMob.              |
| `callback` | `(InterstitialAdCallback) -> Unit` | ✅ Yes    | Lambda that returns ad status results via a sealed class. |

#### Example:

```show ad on demand
interstitialAdManager.showAdOnDemand(
    activity = this,
    adUnitId = getString(R.string.interstitial_ad_unit_id)
) { adState ->
    when (adState) {
        is InterstitialAdCallback.Loaded -> {
            Log.d("Ad", "Interstitial Ad Shown")
        }
        is InterstitialAdCallback.Closed -> {
            Log.d("Ad", "Interstitial Ad Closed by user")
        }
        is InterstitialAdCallback.Failed -> {
            Log.e("Ad", "Interstitial Ad Failed to load/show")
        }
    }
}
```

### 3. Show Ads by preloading:
This function is used to load an interstitial ad in advance, so it can be shown instantly later using showAdIfAvailable(). This improves user experience by eliminating loading delays when showing the ad.

#### 1. Pre load ad:

```preloaded ads
fun preloadAd(adUnitId: String)
```

| Parameter  | Type     | Required | Description                                                                                      |
| ---------- | -------- | -------- | ------------------------------------------------------------------------------------------------ |
| `adUnitId` | `String` | ✅ Yes    | Your **Interstitial Ad Unit ID** from AdMob. Use the same ID when calling `showAdIfAvailable()`. |

#### Example:

```example
interstitialAdManager.preloadAd(
    adUnitId = getString(R.string.interstitial_ad_unit_id)
)
```

#### 2. Show Ad:

```show ad
fun showAdIfAvailable(
    activity: Activity,
    adUnitId: String,
    callback: (InterstitialAdCallback) -> Unit
)
```

| Parameter  | Type                               | Required | Description                                                                 |
| ---------- | ---------------------------------- | -------- | --------------------------------------------------------------------------- |
| `activity` | `Activity`                         | ✅ Yes    | The current activity context used to display the ad.                        |
| `adUnitId` | `String`                           | ✅ Yes    | Your **Interstitial Ad Unit ID**. Must match the one used in `preloadAd()`. |
| `callback` | `(InterstitialAdCallback) -> Unit` | ✅ Yes    | Callback for handling the ad's load/show state and events.                  |

#### Example:

```example
interstitialAdManager.showAdIfAvailable(
    activity = this,
    adUnitId = getString(R.string.interstitial_ad_unit_id)
) { adState ->
    when (adState) {
        is InterstitialAdCallback.Loaded -> {
            Log.d("Ad", "Interstitial ad shown.")
        }
        is InterstitialAdCallback.Closed -> {
            Log.d("Ad", "User closed the interstitial ad.")
        }
        is InterstitialAdCallback.Failed -> {
            Log.e("Ad", "No ad available or failed to show.")
        }
    }
}
```

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

## 🔧 Native ads

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

## 🔧 Rewarded Ads

### 1. Initialization:

``` initialize
@Inject lateinit var rewardedAdManager: RewardedAdManager
```

### 2. Show Ads on demand:
This function loads and immediately shows a rewarded ad. It’s a one-liner solution for when you want to give users a reward after watching a video ad, without preloading.

```load and show ad
private fun loadAndShowAd(
    activity: Activity,
    adUnitId: String,
    callback: (adState: RewardAdCallback) -> Unit
)
```

| Parameter  | Type                         | Required | Description                                                                          |
| ---------- | ---------------------------- | -------- | ------------------------------------------------------------------------------------ |
| `activity` | `Activity`                   | ✅ Yes    | The activity used to show the rewarded ad.                                           |
| `adUnitId` | `String`                     | ✅ Yes    | Your **Rewarded Ad Unit ID** from AdMob.                                             |
| `callback` | `(RewardAdCallback) -> Unit` | ✅ Yes    | Lambda function that returns the ad status, including reward info and failure state. |

### 3. Show pre loaded ads:
This function allows you to load an interstitial ad in advance without showing it immediately. You can later display it using showAdIfAvailable().

#### 1. Preload Ad:

```preload ad
fun preLoadAd(
    adUnitId: String,
    onLoaded: () -> Unit
)
```

| Parameter  | Type         | Required | Description                                                     |
| ---------- | ------------ | -------- | --------------------------------------------------------------- |
| `adUnitId` | `String`     | ✅ Yes    | Your **Interstitial Ad Unit ID** from AdMob.                    |
| `onLoaded` | `() -> Unit` | ✅ Yes    | Callback triggered **only when the ad is successfully loaded**. |

#### Example:

```example
rewardInterstitialManager.preLoadAd(
    adUnitId = getString(R.string.reward_interstitial_ad_unit_id)
){
    // on ad loaded
}
```

#### 2. Show Ad:

```show ad
fun showAdIfAvailable(
    activity: Activity,
    adUnitId: String,
    callback: (adState: RewardAdCallback) -> Unit
)
```

| Parameter  | Type                         | Required | Description                                               |
| ---------- | ---------------------------- | -------- | --------------------------------------------------------- |
| `activity` | `Activity`                   | ✅ Yes    | The current activity used to display the ad.              |
| `adUnitId` | `String`                     | ✅ Yes    | Your **Rewarded Ad Unit ID** from AdMob.                  |
| `callback` | `(RewardAdCallback) -> Unit` | ✅ Yes    | Lambda callback for handling ad states and reward events. |

#### Example:

```example
rewardedAdManager.showAdIfAvailable(
    activity = this,
    adUnitId = getString(R.string.rewarded_ad_unit_id)
) { adState ->
    when (adState) {
        is RewardAdCallback.Loaded -> {
            Log.d("Ad", "Rewarded ad shown.")
        }
        is RewardAdCallback.Closed -> {
            Log.d("Ad", "User closed the ad.")
        }
        is RewardAdCallback.Failed -> {
            Log.e("Ad", "No ad available to show.")
        }
        is RewardAdCallback.RewardEarned -> {
            Log.d("Ad", "User earned ${adState.amount} ${adState.type}")
            // Grant reward here
        }
    }
}
```

## 🔧 Rewarded Interstitial Ads

### 1. Initialization:

``` initialize
@Inject lateinit var rewardedInterstitialAdManager: RewardedInterstitialAdManager
```

### 2. Show Ad on demand:
This function loads and immediately shows a Rewarded Interstitial Ad, which is a type of rewarded ad that doesn’t require explicit user opt-in (e.g., clicking a button). It combines the reach of interstitial ads with the reward mechanics of rewarded ads.

```load and show
fun loadAndShowAd(
    activity: Activity,
    adUnitId: String,
    callback: (adState: RewardInterstitialAdCallback) -> Unit
)
```

| Parameter  | Type                                     | Required | Description                                            |
| ---------- | ---------------------------------------- | -------- | ------------------------------------------------------ |
| `activity` | `Activity`                               | ✅ Yes    | The current activity where the ad should be displayed. |
| `adUnitId` | `String`                                 | ✅ Yes    | Your **Rewarded Interstitial Ad Unit ID** from AdMob.  |
| `callback` | `(RewardInterstitialAdCallback) -> Unit` | ✅ Yes    | Callback to handle ad events and reward state.         |

### 3. Pre load ad:
This function allows you to load a Rewarded Interstitial Ad in advance. Once preloaded, you can later display it using showAdIfAvailable() for a smooth, delay-free user experience.

#### 1. Preload Ad:

```preload ad
fun preLoadAd(
    adUnitId: String,
    onLoaded: () -> Unit
)
```

| Parameter  | Type         | Required | Description                                                       |
| ---------- | ------------ | -------- | ----------------------------------------------------------------- |
| `adUnitId` | `String`     | ✅ Yes    | Your **Rewarded Interstitial Ad Unit ID** from AdMob.             |
| `onLoaded` | `() -> Unit` | ✅ Yes    | Lambda called only when the ad is successfully loaded and cached. |

#### Example:

```example
rewardInterstitialManager.preLoadAd(
    adUnitId = getString(R.string.reward_interstitial_ad_unit_id)
){
    // on ad loaded
}
```

#### 2. Show Ad:

```show ad
fun showAdIfAvailable(
    activity: Activity,
    adUnitId: String,
    callback: (adState: RewardInterstitialAdCallback) -> Unit
)
```

| Parameter  | Type                                     | Required | Description                                                           |
| ---------- | ---------------------------------------- | -------- | --------------------------------------------------------------------- |
| `activity` | `Activity`                               | ✅ Yes    | The current activity used to show the ad.                             |
| `adUnitId` | `String`                                 | ✅ Yes    | The same **Rewarded Interstitial Ad Unit ID** used during preloading. |
| `callback` | `(RewardInterstitialAdCallback) -> Unit` | ✅ Yes    | Callback to listen for ad status and rewards.                         |

#### Example:

```example
rewardInterstitialManager.showAdIfAvailable(
    activity = this,
    adUnitId = getString(R.string.reward_interstitial_ad_unit_id)
) { adState ->
    when (adState) {
        is RewardInterstitialAdCallback.Loaded -> {
            Log.d("Ad", "Ad shown.")
        }
        is RewardInterstitialAdCallback.Closed -> {
            Log.d("Ad", "User closed the ad.")
        }
        is RewardInterstitialAdCallback.Failed -> {
            Log.e("Ad", "No ad available.")
        }
        is RewardInterstitialAdCallback.RewardEarned -> {
            Log.d("Ad", "User earned: ${adState.amount} ${adState.type}")
            // Grant the reward here
        }
    }
}
```







