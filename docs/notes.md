### Rounded Corners

- one of the ways how to create rounded corners: https://stackoverflow.com/questions/9334618/rounded-button-in-android
- "Другим вариантом решения может быть атрибут app:cornerRadius, Если ты используешь Button в качестве основного компонента" - from yp mentor repsonse

### Why button is purple and how to fix it

- MaterialButton is default Button and it ignore background, we can use android.widget.Button instead or backgroundTint = null
- https://stackoverflow.com/questions/64722733/android-background-drawable-not-working-in-button-since-android-studio-4-1

### YS fonts

https://bestfonts.pro/font/yandex-sans

## Data Binding

- https://developer.android.com/topic/libraries/data-binding
- do not forget to add `val binding = DataBindingUtil.setContentView<SettingsBinding>(this, R.layout.settings)` to Activity

Of course I get 
weird fail: https://github.com/Kotlin/kotlinx.serialization/issues/3092
fix in the kotlin 2.1.20: https://youtrack.jetbrains.com/issue/KT-75035/java.lang.IllegalArgumentException-source-must-not-be-null#focus=Comments-27-12867700.0-0

## When data binding state will be changed later

Android official docs have a reference to this: https://medium.com/androiddevelopers/data-binding-lessons-learnt-4fd16576b719
Basically it recommends to pass the component state as one data input instead of bunch of separate variables if component suppose to be changed from code.

## Adaptive Design

https://developer.android.com/develop/ui/views/layout/edge-to-edge
https://developer.android.com/develop/ui/views/layout/responsive-adaptive-design-with-views

To Do:
- [x] buttons looks similar, can I use some common "parent" xml tag for them?
- [x] double check if it can be used (see: https://habr.com/ru/companies/yandex/articles/282534/)

## Emulator

```
adb shell "cmd uimode night yes"
```

```
adb shell "cmd uimode night no"
```