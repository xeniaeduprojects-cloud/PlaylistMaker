### Rounded Corners

- one of the ways how to create rounded corners: https://stackoverflow.com/questions/9334618/rounded-button-in-android
- "Другим вариантом решения может быть атрибут app:cornerRadius, Если ты используешь Button в качестве основного компонента" - from yp mentor

### Links: 
- Edge to Edge: https://developer.android.com/develop/ui/views/layout/edge-to-edge
- Responsible Design with Views: https://developer.android.com/develop/ui/views/layout/responsive-adaptive-design-with-views
- How to Save State: https://developer.android.com/topic/libraries/architecture/saving-states
- Guide to App Architecture: https://developer.android.com/topic/architecture
- UI layer: https://developer.android.com/topic/architecture/ui-layer

### ViewModel Notes

- https://developer.android.com/topic/architecture/ui-layer#naming-conventions

## Commands

```bash
adb shell "cmd uimode night yes"
```

```bash
adb shell "cmd uimode night no"
```

```bash
./gradlew installGitHooks
```

### Retrofit3

https://samsetdev.medium.com/retrofit-3-0-tutorial-key-differences-from-retrofit-2-682f9fd07a9a

### Koin

https://insert-koin.io/docs/setup/compiler-plugin
https://insert-koin.io/docs/quickstart/android/
https://insert-koin.io/docs/reference/koin-android/instrumented-testing/

### Github Actions
https://github.com/ReactiveCircus/android-emulator-runner

### Questions

```bash
2026-04-04 23:26:42.831  6701-6708  StrictMode              com.praktikum.playlistmaker          D  StrictMode policy violation: android.os.strictmode.LeakedClosableViolation: A resource was acquired at attached stack trace but never released. See java.io.Closeable for information on avoiding resource leaks. Callsite: RemoteAnimationTarget[leash]
                                                                                                    	at android.os.StrictMode$AndroidCloseGuardReporter.report(StrictMode.java:2061)
                                                                                                    	at dalvik.system.CloseGuard.warnIfOpen(CloseGuard.java:338)
                                                                                                    	at android.view.SurfaceControl.finalize(SurfaceControl.java:1731)
                                                                                                    	at java.lang.Daemons$FinalizerDaemon.doFinalize(Daemons.java:387)
                                                                                                    	at java.lang.Daemons$FinalizerDaemon.processReference(Daemons.java:367)
                                                                                                    	at java.lang.Daemons$FinalizerDaemon.runInternal(Daemons.java:339)
                                                                                                    	at java.lang.Daemons$Daemon.run(Daemons.java:132)
                                                                                                    	at java.lang.Thread.run(Thread.java:1119)
```