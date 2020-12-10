# Dear-diary

Dear diary is a simple diary (or note) app built with kotlin, the design idea was from this youtube **[playlist](https://www.youtube.com/watch?v=hlkekoPqsis&list=PLam6bY5NszYN6-a1wt7yRISWfmYPdkbMu)** and illustrations from **[OpenDoodles](https://opendoodles.com/)**.

<p align="center">
  <img src="https://raw.githubusercontent.com/ai-null/dear-diary/dev/demo/demo dear-diary.gif" height="400px" />
</p>

___
### Architecture
* min Sdk 23
* [Kotlin](https://kotlinlang.org/), [Courotines](https://developer.android.com/kotlin/coroutines) for threading.
* Jetpack
  * LiveData - data handler
  * lifecycle - data changes observer
  * ViewModel - UI related data holder
  * Room - local Database
* MVVM Architecture (View - DataBinding - ViewModel - Model)

___
### Depedencies
| Library | Description |
| ------  | ----------- |
| [Glide](https://github.com/bumptech/glide) | lightweight image processing library & reducing image size |
| [Material Components](https://github.com/material-components/material-components-android) | UI styling library |
| [Room Database](https://developer.android.com/jetpack/androidx/releases/room) | local database |
