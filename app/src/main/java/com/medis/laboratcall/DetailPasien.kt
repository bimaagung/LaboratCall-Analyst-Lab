package com.medis.laboratcall

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_detail_pasien.*

class DetailPasien : AppCompatActivity() {

    protected val REQUEST_CHECK_SETTINGS = 0x1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pasien)

        tb_back.setOnClickListener {
            onBackPressed()
            finish()
        }

        var id_pemeriksaan =  intent.getStringExtra("id_pemeriksaan")
        var id_oncall = intent.getStringExtra("id_oncall")
        var total_harga =  intent.getStringExtra("total_harga")
        var id_pasien = intent.getStringExtra("id_pasien")
        var nama_pasien = intent.getStringExtra("nama_pasien")
        var layanan = intent.getStringExtra("layanan")
        var no_wa_pasien = intent.getStringExtra("no_wa_pasien")


        tx_tanggal_pemeriksaan.text = intent.getStringExtra("tanggal_pemeriksaan")
        tx_waktu_pemeriksaan.text = intent.getStringExtra("waktu_pemeriksaan")
        tx_id_pasien.text = id_pasien
        tx_nama_pasien.text = nama_pasien
        tx__jenkel.text = intent.getStringExtra("jenkel_pasien")
        tx_tgl_pasien.text = intent.getStringExtra("tanggal_lahir_pasien")
        tx_umur_pasien.text = intent.getStringExtra("umur_pasien")
        tx_alamat_pasien.text = intent.getStringExtra("alamat_pasien")

        tb_lanjut.setOnClickListener{

            if(layanan.equals("offlocation"))
            {
                var i = Intent(this, DaftarPilihanPasien::class.java)
                i.putExtra("id_pasien",id_pasien)
                i.putExtra("nama_pasien",nama_pasien)
                i.putExtra("id_pemeriksaan",id_pemeriksaan)
                i.putExtra("total_harga",total_harga)
                startActivity(i)
                finish()

            }else if(layanan.equals("onlocation"))
            {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 123)
            }else
            {
                Toast.makeText(this, "Detail Pasien Intent Error", Toast.LENGTH_LONG).show()
            }


        }

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var id_pemeriksaan =  intent.getStringExtra("id_pemeriksaan")
        var id_oncall = intent.getStringExtra("id_oncall")
        var id_pasien = intent.getStringExtra("id_pasien")
        var nama_pasien = intent.getStringExtra("nama_pasien")
        var no_wa_pasien = intent.getStringExtra("no_wa_pasien")

        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                var i = Intent(this, PilihanPasienOncall::class.java)
                i.putExtra("id_pemeriksaan",id_pemeriksaan)
                i.putExtra("id_pasien",id_pasien)
                i.putExtra("nama_pasien",nama_pasien)
                i.putExtra("id_oncall",id_oncall)
                i.putExtra("no_wa_pasien",no_wa_pasien)
                startActivity(i)
                finish()

            } else {
                var intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }



}
