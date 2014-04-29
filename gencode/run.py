#!/usr/bin/python
import config

if config.android:
    import android
    android.parseandroid(config.indexurl).start()
if config.iOS:
    import ios
    ios.parseios(config.indexurl).start()