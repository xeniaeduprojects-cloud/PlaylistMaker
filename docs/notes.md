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

### on rotation problem with navigation

- singleliveevent vs livedata - https://medium.com/huawei-developers/comparison-of-livedata-singleliveevent-and-mediatorlivedata-in-android-6ff1a24a98e1
- receiveAsFlow: https://slack-chats.kotlinlang.org/t/490166/how-can-a-singleliveevent-android-pattern-when-using-view-mo

### Sound doesn't play on emulator

- https://stackoverflow.com/questions/67385075/sound-not-working-in-android-emulator-on-macos

### Architecture sample 

- https://github.com/android/nowinandroid

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

### Todo

- CoroutineExceptionHandler - add in the future

### Make visualization

`jdeps -dotoutput app/build/jdeps app/build/tmp/kotlin-classes/debug/`

```
python3 - <<'EOF'
import re

PKG = "playlistmaker"
MERGED_APP = "com.praktikum.playlistmaker.app"
TOP_PKGS = {'com.praktikum.playlistmaker.app'}
IGNORE = ('.di', '.databinding', '.medialibrary', '.util')
ROOT_PKG = 'com.praktikum.playlistmaker'

with open("app/build/jdeps/debug.dot") as f:
    lines = f.readlines()

def clean(s):
    s = re.sub(r'\s*\([^)]+\)\s*$', '', s).strip()
    if s in TOP_PKGS:
        return MERGED_APP
    s = re.sub(r'(\.data)\..+$', r'\1', s)
    s = re.sub(r'(\.domain)\..+$', r'\1', s)
    return s

def rank_block(rank, node_list):
    if not node_list:
        return ""
    names = "; ".join('"' + n + '"' for n in node_list)
    return '  { rank=' + rank + '; ' + names + '; }\n'

edges = set()
nodes = set()

for line in lines:
    if '->' not in line:
        continue
    parts = re.split(r'\s*->\s*', line, maxsplit=1)
    if len(parts) < 2:
        continue
    src_m = re.search(r'"([^"]+)"', parts[0])
    dst_m = re.search(r'"([^"]+)"', parts[1])
    if not src_m or not dst_m:
        continue
    src = clean(src_m.group(1))
    dst = clean(dst_m.group(1))
    if PKG in src and PKG in dst and src != dst:
        if src == ROOT_PKG or dst == ROOT_PKG:
            continue
        if not any(ig in src or ig in dst for ig in IGNORE):
            edges.add((src, dst))
            nodes.add(src)
            nodes.add(dst)

top_nodes    = [MERGED_APP] if MERGED_APP in nodes else []
ui_nodes     = sorted(n for n in nodes if '.ui'     in n)
domain_nodes = sorted(n for n in nodes if '.domain' in n)
data_nodes   = sorted(n for n in nodes if '.data'   in n)
util_nodes   = sorted(n for n in nodes if '.util'   in n)

out = 'digraph "app" {\n  rankdir=BT;\n  edge [dir=back];\n'
out += rank_block("min", util_nodes)
out += rank_block("same", data_nodes)
out += rank_block("same", domain_nodes)
out += rank_block("same", ui_nodes)
out += rank_block("max", top_nodes)

layers = [util_nodes, data_nodes, domain_nodes, ui_nodes, top_nodes]
for i in range(len(layers) - 1):
    if layers[i] and layers[i+1]:
        out += '  "' + layers[i][0] + '" -> "' + layers[i+1][0] + '" [style=invis];\n'

for src, dst in sorted(edges):
    out += '  "' + src + '" -> "' + dst + '";\n'
out += "}\n"

with open("app/build/jdeps/full.dot", "w") as f:
    f.write(out)
EOF

dot -Tsvg app/build/jdeps/full.dot -o app/build/jdeps/full.svg && open app/build/jdeps/full.svg
```

How it works:

Build — ./gradlew assembleDebug compiles .class files to app/build/tmp/kotlin-classes/debug/
jdeps — scans compiled classes, outputs package-level dependency graph as .dot file
Python filter — parses the dot file, strips (debug)/(not found) suffixes, collapses data.* and domain.* subpackages into parent, merges app + root package, drops ignored packages (.di, .databinding, .medialibrary), deduplicates edges
Layout — assigns rank groups to enforce layers: util(bottom) → data → domain → ui → app(top); invisible edges enforce vertical order
Graphviz dot — renders the filtered .dot to SVG

