package com.medis.laboratcall



import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.PolyUtil
import kotlinx.android.synthetic.main.activity_map_analis.*
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.GoogleMap
import com.medis.laboratcall.Data.DataNotifOncall
import org.json.JSONException
import java.text.DecimalFormat

@Suppress("DEPRECATION")
class MapAnalis : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    var lat_analis:Double = 0.0
    var lng_analis:Double = 0.0
    var lat_pasien:Double = 0.0
    var lng_pasien:Double = 0.0
    var lat_origin:Double = 0.0
    var lng_origin:Double = 0.0
    var lat_destination:Double = 0.0
    var lng_destination:Double = 0.0
    lateinit var now: Marker
    var lat_pasien_permanen:Double = 0.0
    var lng_pasien_permanen:Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var id_pasien:String = ""
    var distanceText = ""
    var distanceValue = 0
    var durationText = ""
    var formattedHarga = ""
    var HargaNoFormated = 0
    var id_pemeriksaan_oncall = ""
    var id_analis = ""
    var id_pemeriksaan = ""


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_analis)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        var token = this.getSharedPreferences("id", Context.MODE_PRIVATE)
        id_analis = token.getString("iduser","")
        var nama_analis = token.getString("nama","")


        id_pasien = intent.getStringExtra("id_pasien")
       // id_pasien = "1"
        id_pemeriksaan_oncall = intent.getStringExtra("id_oncall")
        id_pemeriksaan = intent.getStringExtra("id_pemeriksaan")
        var no_wa_pasien = intent.getStringExtra("no_wa_pasien")

//        var item_pemeriksaan = intent.getSerializableExtra("item")
//        var harga_pemeriksaan = intent.getStringExtra("harga")
        tx_analis.text = "Riani Lestari"
        tx_waktu.text = ""
        tx_jarak.text = ""
        tx_peran.text = "Pasien"
        tx_harga_map.text = ""
        aktif_oncall.visibility = View.GONE
        loading.visibility = View.VISIBLE
        konfirmasi_analis.visibility = View.VISIBLE
        tb_sampai_tujuan.visibility = View.INVISIBLE
        var option = 0


        //Firebase on analis or no

        val firebaseDatabase = FirebaseDatabase.getInstance()
        val reference = firebaseDatabase.getReference()
        val query = reference.child("permintaanOncall").child(id_pasien)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    var konfirmlayanan = dataSnapshot.child("konfirmasiOncall").value
                    var id_analis = dataSnapshot.child("id_analis").value

                   // if(konfirmlayanan.toString().equals("1")) {

                        if(id_analis != "0") {
//                        progressDialog.dismiss()

                            //Handling Position Error
                            try
                            {
                                lat_analis = dataSnapshot.child("lat_analis").getValue() as Double
                                lng_analis = dataSnapshot.child("lng_analis").getValue() as Double

                                lat_pasien = dataSnapshot.child("lat_pasien").getValue() as Double
                                lng_pasien = dataSnapshot.child("lng_pasien").getValue() as Double

                                lat_origin = dataSnapshot.child("lat_origin").getValue() as Double
                                lng_origin = dataSnapshot.child("lng_origin").getValue() as Double

                                lat_destination = dataSnapshot.child("lat_destination").getValue() as Double
                                lng_destination = dataSnapshot.child("lng_destination").getValue() as Double
                                //Get direction point origin dan point destination
                                getDirection(lat_origin, lng_origin, lat_destination, lng_destination, lat_analis, lng_analis)
                            }
                            catch (e: JSONException) {
                                option = 1
                            }
                            finally {
                                if(option == 1)
                                {
                                    dialogError("Locasi anda terlalu dekat dengan analis")
                                }
                            }



                            //proses progressbar selesai
                            aktif_oncall.visibility = View.VISIBLE
                            loading.visibility = View.GONE

                            //Pesan debug
                            Log.d("message_debug", konfirmlayanan.toString())
                        }else if(id_analis == 0){
                            Toast.makeText(this@MapAnalis, "Silahkan Menunggu", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this@MapAnalis, "Get Value Error", Toast.LENGTH_LONG).show()
                        }


                }else{
                    Log.d("message_debug","Gagal Firebase oncall")
                   // Toast.makeText(this@MapAnalis, "Gagal Firebase oncall", Toast.LENGTH_LONG).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("message_debug","Error Firebase Konfirm Oncall Analis")
                Toast.makeText(this@MapAnalis, "Error Firebase Konfirm Oncall Analis", Toast.LENGTH_LONG).show()
            }

        })
        //========================

        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 123)

        //Konfirmasi Analis

        tb_terima.setOnClickListener {
            setValueAktifOncall(id_pasien, id_pemeriksaan_oncall,id_analis)
        }
        tb_tolak.setOnClickListener{
            tolakPermintaanOncall(id_pasien, id_pemeriksaan_oncall)
        }

        tb_sampai_tujuan.setOnClickListener{
            dialogKonfirmasi()
        }

        tb_wa_number.setOnClickListener{
            val openURL = Intent(android.content.Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://wa.me/$no_wa_pasien?text=Saya $nama_analis dari LaboratCall")
            startActivity(openURL)
        }



    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==123)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                var manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

                //Call last lat & lng pasien
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation.addOnSuccessListener { location:Location? ->
//                    val latlngAnalis = LatLng( location!!.latitude, location!!.longitude)
//                    lat_pasien_permanen = location.latitude
//                    lng_pasien_permanen = location.longitude
                    //Save Data Notifikasi
                    if(location == null)
                    {
                        Toast.makeText(this@MapAnalis, "Silahkan Nyalakan GPS",Toast.LENGTH_LONG).show()
                    }else{
                        save_notif_konfirm(id_analis, location.latitude, location.longitude,location.latitude, location.longitude)
                 //       now = mMap.addMarker(MarkerOptions().position(latlngAnalis).icon(BitmapDescriptorFactory.fromResource(R.drawable.motor)))

                    }

                }

                var listener = object: LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        val pospasien = LatLng( p0.latitude, p0.longitude)
                        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pospasien,16f))
                        mMap.setMyLocationEnabled(true)
                        mMap.getUiSettings().setMyLocationButtonEnabled(true)
                        mMap.getUiSettings().setZoomGesturesEnabled(true)

                            //Marker Analis
//                        if(now != null){
//                            now.remove()
//                        }
//                        var latLng = LatLng(lat_analis, lng_analis);
//                        now = mMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.motor)))
                        val firebaseDatabase = FirebaseDatabase.getInstance()
                        val reference = firebaseDatabase.getReference()
                        val setVal = reference.child("permintaanOncall").child(id_pasien)
                        setVal.child("lat_analis").setValue(p0.latitude)
                        setVal.child("lng_analis").setValue(p0.longitude)
                    }

                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

                    }

                    override fun onProviderEnabled(p0: String?) {

                    }

                    override fun onProviderDisabled(p0: String?) {

                    }
                }

                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0f, listener)

            }
        }
    }

    fun getDirection(lat_origin:Double, lng_origin:Double, lat_destination:Double, lng_destination:Double, lat_analis:Double, lng_analis:Double)
    {
        val latAnalis =  LatLng(lat_analis, lng_analis)
        val latLngOrigin = LatLng(lat_origin, lng_origin) //pasien
        val latLngDestination = LatLng(lat_destination, lng_destination) //analis
//        mMap.addMarker(MarkerOptions().position(latLngOrigin).title("Pasien"))
        mMap.addMarker(MarkerOptions().position(latLngOrigin).icon(BitmapDescriptorFactory.fromResource(R.drawable.housepasien)).title("Pasien"))
//        mMap.addMarker(MarkerOptions().position(latLngDestination).title("Analis"))
        mMap.addMarker(MarkerOptions().position(latLngDestination).icon(BitmapDescriptorFactory.fromResource(R.drawable.labanalis)).title("Analis"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latAnalis, 17.0f))


        val path: MutableList<List<LatLng>> = ArrayList()
        val urlDirections = "https://maps.googleapis.com/maps/api/directions/json?origin="+lat_origin+","+lng_origin+"&destination="+lat_destination+","+lng_destination+"&key=AIzaSyDs91q4g9okkhZweA86z-QbQIOp1Xdil6g"

        //Log.d("serverGoogleMap",urlDirections)

        var rq=Volley.newRequestQueue(this@MapAnalis)
        val sr = JsonObjectRequest(Request.Method.GET,urlDirections,null,Response.Listener {
                response ->
            //val jsonResponse = response
            // Get routes
            val routes = response.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")
            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                path.add(PolyUtil.decode(points))
            }
            for (i in 0 until path.size) {
                mMap.addPolyline(PolylineOptions().addAll(path[i]).color(Color.BLUE))
            }

             distanceText = legs.getJSONObject(0).getJSONObject("distance").getString("text")
             distanceValue = legs.getJSONObject(0).getJSONObject("distance").getInt("value")
             durationText = legs.getJSONObject(0).getJSONObject("duration").getString("text")
             //durationValue = legs.getJSONObject(0).getJSONObject("duration").getString("value")

            //Kalkulasi Harga
            var kalkulasi_harga = distanceValue*6

            if(kalkulasi_harga.equals(0)){
                kalkulasi_harga = 50
            }else{
                kalkulasi_harga = distanceValue*6
            }

            var formatterHarga = DecimalFormat("#,###")
            formattedHarga = formatterHarga.format(kalkulasi_harga)
            HargaNoFormated = kalkulasi_harga

            tx_jarak.text = distanceText
            tx_waktu.text = durationText
            tx_harga_map.text = "Rp. $formattedHarga,00"

            konfirmasi_dialog_pemeriksaan_oncall(distanceText,durationText,"Rp. $formattedHarga,00",id_pasien,id_pemeriksaan_oncall)

        },
            Response.ErrorListener{ error ->
                //Toast.makeText(this, error.message,Toast.LENGTH_LONG).show()
              //  Log.d("error query", error.message)
            })

        rq.add(sr)
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var latLng = LatLng(lat_analis, lng_analis);
       // now = mMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.motor)))
    }

    fun save_notif_konfirm(id_analis:String, lat_analis:Double, lng_analis:Double, lat_origin:Double, lng_origin:Double)
    {
            val firebaseDatabase = FirebaseDatabase.getInstance()
            val reference = firebaseDatabase.getReference()
            val setVal = reference.child("permintaanOncall").child(id_pasien)
                setVal.child("id_analis").setValue(id_analis)
                setVal.child("lat_analis").setValue(lat_analis)
                setVal.child("lng_analis").setValue(lng_analis)
                setVal.child("lat_origin").setValue(lat_origin)
                setVal.child("lng_origin").setValue(lng_origin)


    }


    //nb : nilai di database mysql harus 1 pada kolum kesediaan_analis agar muncul alert dialog
    fun konfirmasi_dialog_pemeriksaan_oncall(distanceText:String, durationText:String, harga:String, id_pasien:String, id_pemeriksaan_oncall:String)
    {
        var url_pemeriksaan_oncall = Connection.url+"admin/page_pemeriksaan/konfirmasi_dialog_pemeriksaan_oncall/$id_pemeriksaan_oncall"
        var rq_pemeriksaan_oncall = Volley.newRequestQueue(this)
        var sr_pemeriksaan_oncall = JsonObjectRequest(Request.Method.GET, url_pemeriksaan_oncall,null,
            Response.Listener{ response ->
                var kesediaan_analis = response.getString("kesediaan_analis")

                if(kesediaan_analis.equals("0")) //menunggu konfirmasi
                {
                    Log.d("Proses_debug","proses konfirmasi analis")
                }
                else if(kesediaan_analis.equals("1")) //proses konfirmasi
                {
                 //   dialog_confirm_harga(distanceText, durationText, harga, id_pasien)
                }
                else if(kesediaan_analis.equals("2")) //konfirmasi diterima
                {
                    Log.d("Proses_debug","proses konfirmasi analis")
                }
                else{
                    Log.d("Proses_debug_error","proses konfirmasi analis")
                }

            },Response.ErrorListener {error ->
               // Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            })
        rq_pemeriksaan_oncall.add(sr_pemeriksaan_oncall)
    }

    fun setValueAktifOncall(id_pasien: String, id_pemeriksaan_oncall: String, id_analis: String)
    {
        var url_update_pemeriksaan_oncall = Connection.url+"admin/page_pemeriksaan/terima_konfirm_oncall?id_pemeriksaan_oncall=$id_pemeriksaan_oncall&id_analis=$id_analis"
            var rq_update_pemeriksaan_oncall = Volley.newRequestQueue(this)
            var sr_update_pemeriksaan_oncall = JsonObjectRequest(Request.Method.GET, url_update_pemeriksaan_oncall,null,
                Response.Listener{ response ->
                    var proses = response.getBoolean("proses")

                    if(proses == true)
                    {
                        val firebaseDatabase = FirebaseDatabase.getInstance()
                        val reference = firebaseDatabase.getReference()
                        reference.child("permintaanOncall").child(id_pasien).child("konfirmasiOncall").setValue("1")
                        konfirmasi_analis.visibility = View.GONE
                        tb_sampai_tujuan.visibility = View.VISIBLE
                    }else if(proses == false){
                        println("Gagal Set Vakue Aktif Oncall Mysql")
                    }else{
                        println("Gagal Set Vakue Aktif Oncall Mysql")
                    }
                },Response.ErrorListener {error -> Log.d("debug","error update konfirm oncall") })
            rq_update_pemeriksaan_oncall.add(sr_update_pemeriksaan_oncall)
    }

    fun tolakPermintaanOncall(id_pasien:String, id_pemeriksaan_oncall:String){

    }

    fun konfirmasiSampaiTujuan(id_pemeriksaan_oncall:String){

        val firebaseDatabase = FirebaseDatabase.getInstance()
        val reference = firebaseDatabase.getReference()
        reference.child("permintaanOncall").child(id_pasien).child("aktif_oncall").setValue("2")
        var i = Intent(this@MapAnalis, Pembayaran::class.java)
        i.putExtra("id_pemeriksaan", id_pemeriksaan)
        i.putExtra("id_oncall", id_pemeriksaan_oncall)
        i.putExtra("id_pasien", id_pasien)
        i.putExtra("biaya_transportasi", HargaNoFormated)
        i.putExtra("layanan", "onlocation")
        startActivity(i)
        finish()

    }

    fun dialogKonfirmasi()
    {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Apakah anda yakin kalau sudah sampai tujuan ?")
            .setPositiveButton("Ya",
                DialogInterface.OnClickListener { dialog, id ->
                    konfirmasiSampaiTujuan(id_pemeriksaan_oncall)
                })
            .setNegativeButton("Tidak",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })
        // Create the AlertDialog object and return it
        val alert = builder.create()
        alert.show()
    }

    fun dialogError(message:String)
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


}
