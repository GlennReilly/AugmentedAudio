package com.arqathon.glennreilly.augmentedaudio.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.ActivityRecognition
import java.util.Locale

object SpeechManager: GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener, TextToSpeech.OnInitListener {

    var tts: TextToSpeech? = null
    val ACT_CHECK_TTS_DATA = 1000
    var mApiClient: GoogleApiClient? = null

    fun init(ctx: Context){
        mApiClient = GoogleApiClient.Builder(ctx)
            .addApi(ActivityRecognition.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()

        (mApiClient as GoogleApiClient).connect()
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result=tts?.setLanguage(Locale.US)
            if(result==TextToSpeech.LANG_MISSING_DATA ||
                result==TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("error", "This Language is not supported")
            }
            else{
                ConvertTextToSpeech("Wow, this actually works!")
            }
        }
        else
            Log.e("error", "Initilization Failed!")
    }

    private fun ConvertTextToSpeech(text: String, qmode: Int = 1) {
        if (qmode == 1)
            tts?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
        else
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    fun shutdown() {
            if (tts != null) {
                (tts as TextToSpeech).apply {
                    stop()
                    shutdown()
                }
            }
    }


}