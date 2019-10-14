package com.medis.laboratcall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.activity_pilih_pemeriksaan.*


class PilihPemeriksaan : AppCompatActivity() {

    var item = HashMap<String,Int>()
    var harga:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_pemeriksaan)

            h1.setOnCheckedChangeListener({
                    buttonView, isChecked ->
                if (isChecked){
                    harga += 1000
                    item.put("H1", 1000)
                    updateharga(harga)
                }else{
                    harga -= 1000
                    item.remove("H1")
                    totalHarga.text = harga.toString()
                    updateharga(harga)
                }
            })

            h2.setOnCheckedChangeListener({
                    buttonView, isChecked ->
                if (isChecked){
                    harga += 2000
                    item.put("H2", 2000)
                    updateharga(harga)
                }else{
                    harga -= 2000
                    item.remove("H2")
                    updateharga(harga)
                }
            })

            h3.setOnCheckedChangeListener({
                    buttonView, isChecked ->
                if (isChecked){
                    harga += 2000
                    item.put("H3", 2000)
                    updateharga(harga)

                }else{
                    harga -= 2000
                    item.remove("H3")
                    updateharga(harga)
                }
            })

        tb_lanjut.setOnClickListener{
            //mengambil value dari layanan
            val layananPemeriksaan = intent.getStringExtra("layanan")

            //logika eksekusi tergantung metode layanan
            if(layananPemeriksaan.equals("offlocation"))
            {
                var pembayaran = Intent(this, Pembayaran::class.java)
                pembayaran.putExtra("item",item)
                pembayaran.putExtra("harga",harga.toString())
                pembayaran.putExtra("layanan","offlocation")
                startActivity(pembayaran)

            }else if(layananPemeriksaan.equals("onlocation"))
            {
                var pembayaran = Intent(this, Pembayaran::class.java)
                pembayaran.putExtra("item",item)
                pembayaran.putExtra("harga",harga.toString())
                pembayaran.putExtra("layanan","onlocation")
                startActivity(pembayaran)

            }else{
                Log.d("Error Pilih Pemeriksaan","Metode Pembayaran")
            }

            //Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun updateharga(harga:Int)
    {
        totalHarga.text = harga.toString()
    }

}
