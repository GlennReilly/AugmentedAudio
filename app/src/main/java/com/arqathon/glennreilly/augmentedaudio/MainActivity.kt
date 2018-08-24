package com.arqathon.glennreilly.augmentedaudio


import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.arqathon.glennreilly.augmentedaudio.R.drawable
import com.arqathon.glennreilly.augmentedaudio.audio.SoundManager
import com.arqathon.glennreilly.augmentedaudio.audio.SpeechManager
import com.arqathon.glennreilly.augmentedaudio.data.PointsOfInterestProvider
import com.arqathon.glennreilly.augmentedaudio.gps.MainActivity
import com.arqathon.glennreilly.augmentedaudio.gps.services.LocationMonitoringService
import com.arqathon.glennreilly.augmentedaudio.model.PointOfInterest
import com.arqathon.glennreilly.augmentedaudio.service.ActivityRecognitionService
import com.arqathon.glennreilly.home.NextAdapter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.ActivityRecognitionClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.next_activity.*

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var mActivityRecognitionClient: ActivityRecognitionClient? = null
    private var mAdapter: ActivitiesAdapter? = null

    private val activityDetectionPendingIntent: PendingIntent
        get() {
            val intent = Intent(this, ActivityRecognitionService::class.java)
            return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        }


    private var mAlreadyStartedService = false

    /**
     * Return the availability of GooglePlayServices
     */
    val isGooglePlayServicesAvailable: Boolean
        get() {
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val status = googleApiAvailability.isGooglePlayServicesAvailable(this)
            if (status != ConnectionResult.SUCCESS) {
                if (googleApiAvailability.isUserResolvableError(status)) {
                    googleApiAvailability.getErrorDialog(this, status, 2404).show()
                }
                return false
            }
            return true
        }

    private val adapter = NextAdapter(this)

    private var buttonPlay = true


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.next_activity)

        setupToolbar()

        initPlayButton(!buttonPlay)

        play_button.setOnClickListener{
            initPlayButton(buttonPlay)
        }

        places_list.adapter = adapter
        places_list.layoutManager = LinearLayoutManager(this)

        mActivityRecognitionClient = ActivityRecognitionClient(this)

        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE)
                    val longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE)

                    if (latitude != null && longitude != null) {
                        (mMsgView as TextView).text = getString(R.string.msg_location_service_started) + "\n Latitude : " + latitude + "\n Longitude: " + longitude

                        var results = arrayOf<Float>().toFloatArray()

                        PointsOfInterestProvider.pointsOfInterestCollection?.pointsOfInterestCollection?.forEach {
                            Location.distanceBetween(
                                latitude.toDouble(),
                                longitude.toDouble(),
                                it.lat.toDouble(),
                                it.lon.toDouble(),
                                results
                            )
                        }
                    }
                }
            }, IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        )
    }

    private fun initPlayButton(play: Boolean) {
        if (!play) {
            play_button.setImageResource(drawable.ic_pause_circle_outline_black_24dp)
            buttonPlay = true
        } else {
            play_button.setImageResource(drawable.ic_play_circle_outline_black_24dp)
            buttonPlay = false
        }
    }

    private fun setupToolbar() {
        next_toolbar.setTitle(R.string.next_activity_title)
        this.setSupportActionBar(next_toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        }
    }

    override fun onResume() {
        super.onResume()
        PointsOfInterestProvider.initialise(this)
        SoundManager.initialise(this)
        SpeechManager.initialise(this)

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)

        val pointsOfInterestCollection: List<PointOfInterest>?
            = PointsOfInterestProvider.pointsOfInterestCollection?.pointsOfInterestCollection

        pointsOfInterestCollection?.let {
            adapter.addAll(it)
        }

        SpeechManager.initialise(this)

        startStep1()
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
        //task.addOnSuccessListener { updateDetectedActivitiesList() }
        SpeechManager.ConvertTextToSpeech("hello ")
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        if (s == DETECTED_ACTIVITY) {
            //updateDetectedActivitiesList()
        }
    }

    companion object {
        val DETECTED_ACTIVITY = ".DETECTED_ACTIVITY"
        val MOST_PROBABLE_ACTIVITY = ".MOST_PROBABLE_ACTIVITY"
        private val TAG = MainActivity::class.java.simpleName

        /**
         * Code used in requesting runtime permissions.
         */
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    /**
     * Step 1: Check Google Play services
     */
    private fun startStep1() {
        Log.d("ASD", "STEP 1")
        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable) {

            //Passing null to indicate that it is executing for the first time.
            startStep2(null)

        } else {
            Toast.makeText(applicationContext, R.string.no_google_playservice_available, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Step 2: Check & Prompt Internet connection
     */
    private fun startStep2(dialog: DialogInterface?): Boolean {
        Log.d("ASD", "STEP 2")
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected) {
            promptInternetConnect()
            return false
        }


        dialog?.dismiss()

        //Yes there is active internet connection. Next check Location is granted by user or not.

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3()
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions()
        }
        return true
    }

    /**
     * Show A Dialog with button to refresh the internet state.
     */
    private fun promptInternetConnect() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(R.string.title_alert_no_intenet)
        builder.setMessage(R.string.msg_alert_no_internet)

        val positiveText = getString(R.string.btn_label_refresh)
        builder.setPositiveButton(
            positiveText
        ) { dialog, which ->
            //Block the Application Execution until user grants the permissions
            if (startStep2(dialog)) {

                //Now make sure about location permission.
                if (checkPermissions()) {

                    //Step 2: Start the Location Monitor Service
                    //Everything is there to start the service.
                    startStep3()
                } else if (!checkPermissions()) {
                    requestPermissions()
                }

            }
        }


        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Step 3: Start the Location Monitor Service
     */
    private fun startStep3() {
        Log.d("ASD", "STEP 3")
        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.

        if (!mAlreadyStartedService && mMsgView != null) {

            (mMsgView as TextView).setText(R.string.msg_location_service_started)

            //Start location sharing service to app server.........
            val intent = Intent(this, LocationMonitoringService::class.java)
            startService(intent)

            mAlreadyStartedService = true
            //Ends................................................
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState1 = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val permissionState2 = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Shows a [Snackbar].
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private fun showSnackbar(
        mainTextStringId: Int, actionStringId: Int,
        listener: View.OnClickListener
    ) {
        Snackbar.make(
            findViewById<View>(android.R.id.content),
            getString(mainTextStringId),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(getString(actionStringId), listener).show()
    }

    /**
     * Start permissions requests.
     */
    private fun requestPermissions() {

        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val shouldProvideRationale2 = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar(R.string.permission_rationale,
                android.R.string.ok, View.OnClickListener {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                })
        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
                // If img_user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "Permission granted, updates requested, starting location updates")
                startStep3()

            } else {
                // Permission denied.

                // Notify the img_user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the img_user for permission (device policy or "Never ask
                // again" prompts). Therefore, a img_user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                    R.string.settings, View.OnClickListener {
                        // Build intent that displays the App settings screen.
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID, null
                        )
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
            }
        }
    }


    public override fun onDestroy() {


        //Stop location sharing service to app server.........

        stopService(Intent(this, LocationMonitoringService::class.java))
        mAlreadyStartedService = false
        //Ends................................................


        super.onDestroy()
    }


}
