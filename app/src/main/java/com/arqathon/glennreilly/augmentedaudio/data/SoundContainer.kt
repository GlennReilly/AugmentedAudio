package com.arqathon.glennreilly.augmentedaudio.data

import com.google.android.gms.location.DetectedActivity

data class SoundSet(val soundId: Int, val loopCount: Int = 0, val leftVolume: Float, val rightVolume: Float)

data class ActivityNotificationEvent(val activity: DetectedActivity, val volume: Float, val loop:Int )
