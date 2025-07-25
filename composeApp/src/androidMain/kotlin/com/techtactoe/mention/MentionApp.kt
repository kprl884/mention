package com.techtactoe.mention

import android.app.Application
import com.techtactoe.mention.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MentionApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@MentionApp)
        }
    }
}
