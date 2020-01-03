package com.medis.laboratcall

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat

class SplashScreen : AppCompatActivity() {

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 2000 //3 detik

    internal val mRunnable: Runnable = Runnable {
        if(!isFinishing){
            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 123)


    }

    public override fun onDestroy() {
        if(mDelayHandler != null){
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }


    //Ijin akses lokasi
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mDelayHandler = Handler()
                mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
                Log.d("Permission", "Dapat dapat melakukan akses location")

            } else {
                Log.d("Permission", "Tidak dapat melakukan akses location")
                mDelayHandler = Handler()
                mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
            }
        }
    }
}
