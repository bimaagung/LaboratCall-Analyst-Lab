package com.medis.laboratcall

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.medis.laboratcall.Adapter.ViewPagerAdapter
import com.medis.laboratcall.Fragment.FragmentOffLocation
import com.medis.laboratcall.Fragment.FragmentOnLocation
import kotlinx.android.synthetic.main.activity_home.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import org.jetbrains.anko.indeterminateProgressDialog
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*


class HomeActivity : AppCompatActivity() {


    val REQUEST_CHECK_SETTINGS = 0x1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        setSupportActionBar(toolbar)

        toolbar.title = "LaboratCall Analis"
        check_aktif_laboratoirum()
        displayLocationSettingsRequest(this)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.akun) {
            var i = Intent(this, ProfilActivity::class.java)
            startActivity(i)
            finish()
            return true
        }
        if (id == R.id.aktif_lab_klinik) {

            var i = Intent(this, AktifAnalis::class.java)
            startActivity(i)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun check_aktif_laboratoirum()
    {
        var token  = getSharedPreferences("id", Context.MODE_PRIVATE)
        var id_klinik = token.getString("klinik"," ")

        var loading = indeterminateProgressDialog("Silahkan Menunggu")
        loading.apply {
            setCancelable(false)
        }
        loading.show()

        var url = Connection.url + "admin/page_pemeriksaan/get_aktif_laboratorium_klinik/$id_klinik"
        var rq= Volley.newRequestQueue(this)
        var sr= JsonObjectRequest(
            Request.Method.GET,url,null,
            Response.Listener{ response ->
                loading.dismiss()
                var open = response.getBoolean("open")

                if(open == true)
                {
                    adapterFragmentHome()
                    Log.d("message","Laboratorium klinik sudah dibuka")
                }else if(open == false){

                    redirectAktifAnalis("Silahkan buka laboratorium klinik untuk melakukan pelayanan pemeriksaan")
                }
                else{
                    Toast.makeText(this, "Sistem cek laboratorium klinik error", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener{ error ->
                loading.dismiss()
                dialogKonfirmasi("Tidak ada koneksi internet")
            })

        rq.add(sr)

    }

    fun dialogKonfirmasi(message:String)
    {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton("Keluar",
                DialogInterface.OnClickListener { dialog, id ->
                    finish()
                })
            .setNegativeButton("Tunggu",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })
        // Create the AlertDialog object and return it
        val alert = builder.create()
        alert.show()
    }

    fun redirectAktifAnalis(message:String)
    {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton("Buka Laboratorium Klinik",
                DialogInterface.OnClickListener { dialog, id ->
                   var intent = Intent(this,AktifAnalis::class.java)
                    startActivity(intent)
                    finish()
                })
        // Create the AlertDialog object and return it
        val alert = builder.create()
        alert.show()
    }

    fun adapterFragmentHome(){
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(FragmentOffLocation(), "OffLocation")
        adapter.addFragment(FragmentOnLocation(), "OnLocation")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
    }

    fun displayLocationSettingsRequest(context: Context) {

        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()

        val TAG = DetailPasien::class.java!!.getSimpleName()

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback(object : ResultCallback<LocationSettingsResult> {

            override fun onResult(result: LocationSettingsResult) {
                val status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS ->

                        Log.i(

                            "message",
                            "All location settings are satisfied."
                        )
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(
                            "message",
                            "Location settings are not satisfied. Show the user a dialog to upgrade location settings "
                        )

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(this@HomeActivity, REQUEST_CHECK_SETTINGS)
                        } catch (e: IntentSender.SendIntentException) {
                            Log.i("message", "PendingIntent unable to execute request.")
                        }

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                        "message",
                        "Location settings are inadequate, and cannot be fixed here. Dialog not created."
                    )
                }
            }
        })
    }


}
