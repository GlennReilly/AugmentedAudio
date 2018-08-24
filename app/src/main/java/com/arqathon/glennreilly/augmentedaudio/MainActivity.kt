package com.arqathon.glennreilly.augmentedaudio


import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ListView
import com.arqathon.glennreilly.augmentedaudio.audio.SoundManager
import com.arqathon.glennreilly.augmentedaudio.audio.SpeechManager
import com.arqathon.glennreilly.augmentedaudio.data.PointsOfInterestProvider
import com.arqathon.glennreilly.augmentedaudio.service.ActivityRecognitionService
import com.google.android.gms.location.ActivityRecognitionClient


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var mActivityRecognitionClient: ActivityRecognitionClient? = null
    private var mAdapter: ActivitiesAdapter? = null

    private val activityDetectionPendingIntent: PendingIntent
        get() {
            val intent = Intent(this, ActivityRecognitionService::class.java)
            return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val detectedActivitiesListView = findViewById<View>(R.id.activities_listview) as ListView

        val detectedActivities = ActivityRecognitionService.detectedActivitiesFromJson(
            PreferenceManager.getDefaultSharedPreferences(this).getString(
                DETECTED_ACTIVITY, ""
            )
        )

        mAdapter = ActivitiesAdapter(this, detectedActivities)
        detectedActivitiesListView.adapter = mAdapter
        mActivityRecognitionClient = ActivityRecognitionClient(this)
    }

    override fun onResume() {
        super.onResume()
        PointsOfInterestProvider.initialise(this)
        SoundManager.initialise(this)
        SpeechManager.initialise(this)

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
        updateDetectedActivitiesList()

    }

    override fun onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    fun requestUpdatesHandler(view: View) {
        val task = mActivityRecognitionClient!!.requestActivityUpdates(
            3000, activityDetectionPendingIntent
        )
        task.addOnSuccessListener { updateDetectedActivitiesList() }
        SpeechManager.ConvertTextToSpeech("hello ")
    }

    private fun updateDetectedActivitiesList() {

        val mostProbableActivity =
            ActivityRecognitionService.getMostProbableActivityFromJson(
                PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(MOST_PROBABLE_ACTIVITY, "")
            )

        mostProbableActivity?.let {
            //SoundManager.playSoundFor(it, applicationContext)
        }

        val detectedActivities = ActivityRecognitionService.detectedActivitiesFromJson(
            PreferenceManager.getDefaultSharedPreferences(this)
                .getString(DETECTED_ACTIVITY, "")
        )

        mAdapter!!.updateActivities(detectedActivities)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        if (s == DETECTED_ACTIVITY) {
            updateDetectedActivitiesList()
        }
    }

    companion object {
        val DETECTED_ACTIVITY = ".DETECTED_ACTIVITY"
        val MOST_PROBABLE_ACTIVITY = ".MOST_PROBABLE_ACTIVITY"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SpeechManager.ACT_CHECK_TTS_DATA) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // Data exists, so we instantiate the TTS engine
                SpeechManager.tts = TextToSpeech(this, SpeechManager)
            } else {
                // Data is missing, so we start the TTS
                // installation process
                val installIntent = Intent()
                installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                startActivity(installIntent)
            }
        }
    }

    override fun onDestroy() {
        SpeechManager.shutdown()
        super.onDestroy()
    }
}