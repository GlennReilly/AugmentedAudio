package com.arqathon.glennreilly.augmentedaudio.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import com.arqathon.glennreilly.augmentedaudio.R
import com.google.android.gms.location.DetectedActivity

object SoundManager {
    private lateinit var soundPool: SoundPool
    var soundsLoaded: Boolean = false

    val situationToSoundMap = mapOf(
        DetectedActivity.STILL to R.raw.frogs,
        DetectedActivity.ON_FOOT to R.raw.waddle,
        DetectedActivity.WALKING to R.raw.waddle,
        DetectedActivity.RUNNING to R.raw.tunnel_run,
        DetectedActivity.IN_VEHICLE to R.raw.funky_car_chase,
        DetectedActivity.ON_BICYCLE to R.raw.bicycle_bell,
        DetectedActivity.TILTING to R.raw.beep_a_major,
        DetectedActivity.UNKNOWN to R.raw.jaws
    ) //TODO thinking that the loop count should be specific to the sound/activity..

    val situationToSoundPoolResource = mutableMapOf<Int, Int>()

    var beepInAMajor: Int = 0

    fun init(context: Context) {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .build()


        soundPool.setOnLoadCompleteListener(object : SoundPool.OnLoadCompleteListener {
            override fun onLoadComplete(
                soundPool: SoundPool, sampleId: Int,
                status: Int
            ) {
                soundsLoaded = true
            }
        })

        beepInAMajor = soundPool.load(context, R.raw.beep_a_major, 1)
        situationToSoundMap.forEach{
            situationToSoundPoolResource[it.key] = soundPool.load(context, it.value, 1)
        }
    }

    fun playSoundFor(detectedActivity: DetectedActivity, applicationContext: Context){

        if (SoundManager.soundsLoaded) {
            //TODO instead of directly playing the sound, perhaps we should insert it into a queue
            //TODO How long does a sample loop for? until something else happens? (could get annoying)

            val soundForSituation = situationToSoundPoolResource.get(detectedActivity.type)
            val volumesForSituation = getVolumesForSituation(applicationContext)
            val loopCountForSituation = getLoopCountForSituation()

            soundForSituation?.let{soundPool.play(
                it,
                volumesForSituation.first,
                volumesForSituation.second,
                1,
                loopCountForSituation,
                detectedActivity.confidence.toFloat()/100
            )}
        }
    }

    private fun getLoopCountForSituation() = 4

    fun getVolumesForSituation(ctx: Context):Pair<Float, Float> {
        return Pair(getCurrentVolume(ctx), getCurrentVolume(ctx))
    }

    fun getCurrentVolume(ctx: Context): Float {
        val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val actVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val volume = actVolume / maxVolume //TODO need to factor in how our volume relates to system volume. Percentage?
        return volume
    }

}
