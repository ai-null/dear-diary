<h1 align="center">Dear-diary</h1>

<p align="center">
Dear diary is a simple diary (or note) app built with kotlin,
  <br />the design idea was from this youtube <b><a href="https://www.youtube.com/watch?v=hlkekoPqsis&list=PLam6bY5NszYN6-a1wt7yRISWfmYPdkbMu">playlist</a></b> and the illustrations from <b><a href="https://opendoodles.com/">OpenDoodles</a></b>.
</p>

<p align="center">
<img src="https://raw.githubusercontent.com/ai-null/dear-diary/dev/demo/homescreen.png" width="30%" />
<img src="https://raw.githubusercontent.com/ai-null/dear-diary/dev/demo/add-note-screen.png" width="30%" />
<img src="https://raw.githubusercontent.com/ai-null/dear-diary/dev/demo/dialog-delete.png" width="30%" />

<br />
<br />
<img align="center" src="https://raw.githubusercontent.com/ai-null/dear-diary/dev/demo/demo dear-diary.gif" height="400px" />
</p>

___
## Architecture

* min Sdk 23
* [Kotlin](https://kotlinlang.org/), [Courotines](https://developer.android.com/kotlin/coroutines) for threading.
* Jetpack
  * LiveData - data handler
  * lifecycle - data changes observer
  * ViewModel - UI related data holder
  * Room - local Database
* MVVM Architecture (Model - View - ViewModel)

___
## Depedencies
| Library | Description |
| ------  | ----------- |
| [Glide](https://github.com/bumptech/glide) | lightweight image processing library |
| [Material Components](https://github.com/material-components/material-components-android) | UI styling library |
| [Room Database](https://developer.android.com/jetpack/androidx/releases/room) | local database |

___
## License
```
MIT License

Copyright (c) 2020 ai-null (ainul)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
