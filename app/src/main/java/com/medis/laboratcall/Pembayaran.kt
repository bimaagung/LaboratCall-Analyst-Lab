package com.medis.laboratcall


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_pembayaran.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.FirebaseDatabase
import com.medis.laboratcall.Adapter.PesananAdapter
import com.medis.laboratcall.Data.DataPesanan
import org.jetbrains.anko.indeterminateProgressDialog


class Pembayaran : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)

        tb_back.setOnClickListener {
            onBackPressed()
            finish()
        }

        //Menampilkan Loading
        var loading = indeterminateProgressDialog("Silahkan Menunggu")
        loading.apply {
            setCancelable(false)
        }
        loading.show()

        //get id analis
        var token  = getSharedPreferences("id", Context.MODE_PRIVATE)
        var id_analis = token.getString("iduser"," ")


        //Get value Intent
        var id_pemeriksaan = intent.getStringExtra("id_pemeriksaan")
        println(id_pemeriksaan)
        //97

        var id_pasien = intent.getStringExtra("id_pasien")
        var id_oncall = intent.getStringExtra("id_oncall")
        var layanan:String? = intent.getStringExtra("layanan")
        var biaya_transportasi = intent.getIntExtra("biaya_transportasi",0)
        var hasilItem = CollectionDataInputHasil.InputHasil



        var list = ArrayList<DataPesanan>()

        var url = Connection.url + "admin/page_pemeriksaan/get_api_harga_dan_total_item/$id_pemeriksaan"
        var rq = Volley.newRequestQueue(this)
        var jar = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                loading.dismiss()
                for (x in 0..response.length() - 1)
                    list.add(
                        DataPesanan(
                            response.getJSONObject(x).getString("item"),
                            response.getJSONObject(x).getString("harga_item")
                        )
                    )


                var adp = PesananAdapter(this, list)
                layout_pembayaran.layoutManager = LinearLayoutManager(this)
                layout_pembayaran.adapter = adp

            },
            Response.ErrorListener { error ->
                loading.dismiss()
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            })
        rq.add(jar)


        //Logika jika pembayaran onlocation
        if(layanan.equals("onlocation"))
        {
            layout_transportasi.visibility = View.VISIBLE
            getBiayaItemPemeriksaan(id_pemeriksaan,biaya_transportasi)
            tx_harga_transportasi.text = biaya_transportasi.toString()
            tb_konfirm.setOnClickListener{
                //Progress OnCall
                changeProgressOnCall(id_oncall)
                //Hapus pemeriksaan oncall firebase
                val firebaseDatabase = FirebaseDatabase.getInstance()
                val reference = firebaseDatabase.getReference()
                reference.child("permintaanOncall").child(id_pasien).removeValue()

                var i = Intent(this, HomeActivity::class.java)
                startActivity(i)
                finish()
                Toast.makeText(this, "Layanan OnCall telah diakhiri silahkan input hasil pemeriksaan laboratorium klinik",Toast.LENGTH_SHORT).show()
            }

            //Jika pembayaran offlocation
        }else{
            layout_transportasi.visibility = View.GONE
            getBiayaItemPemeriksaan(id_pemeriksaan,0)

            println("Error metode layanan")
            tb_konfirm.setOnClickListener{
                loading.show()
                //var url = Connection.url + "admin/page_pemeriksaan/remove_pemeriksaan/$id_pemeriksaan"
                var url = Connection.url + "admin/page_pemeriksaan/insert_hasil_pemeriksaan?id_pemeriksaan=$id_pemeriksaan&id_item="+hasilItem.keys.toString().replace(" ","")+"&hasil="+hasilItem.values.toString().replace(" ","")+"&id_analis="+id_analis


                var rq= Volley.newRequestQueue(this)
                var sr= JsonObjectRequest(Request.Method.GET,url,null,
                    Response.Listener{ response ->
                        loading.dismiss()
                        var remove = response.getString("remove")

                        if(remove.equals("insert"))
                        {
                            //Pindah ke activity home
                            var i = Intent(this, HomeActivity::class.java)
                            startActivity(i)
                            finish()
                            Toast.makeText(this, "Pemeriksaan telah berhasil dikirim pasien",Toast.LENGTH_SHORT).show()

                        }else if(remove.equals("gagal")){
                            Toast.makeText(this, "Delete Pemeriksaan Gagal",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            var i = Intent(this, HomeActivity::class.java)
                            startActivity(i)
                            finish()
                            Toast.makeText(this, "Pemeriksaan telah berhasil dikirim pasien",Toast.LENGTH_SHORT).show()

                        }
                    },
                    Response.ErrorListener{ error ->
                        loading.dismiss()
                        Toast.makeText(this, error.message,Toast.LENGTH_LONG).show()
                    })
                sr.setRetryPolicy(DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                rq.add(sr)
            }

        }
    }

    //Fungsi pembayaran pemeriksaan oncall
    fun getBiayaItemPemeriksaan(id_pemeriksaan:String, biaya_transportasi:Int)
    {

        var url = Connection.url + "admin/page_pemeriksaan/get_total_harga?id_pemeriksaan_pasien=$id_pemeriksaan"

        println(url)

        var rq=Volley.newRequestQueue(this)
        var sr=JsonObjectRequest(Request.Method.GET,url,null,
            Response.Listener{ response ->
                   var harga = response.getString("total_harga").toInt()
                   var totalHargaOncall = (harga+biaya_transportasi)
                   tx_total_harga.text = totalHargaOncall.toString()
            },
            Response.ErrorListener{ error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            })

        rq.add(sr)

    }

    //Menugbag progress oncall di mysql bertanda oncall sudah selsai dan tinggal input hasil
    fun changeProgressOnCall(id_oncall:String)
    {
        var url = Connection.url + "admin/page_pemeriksaan/change_progress_oncall?id_oncall=$id_oncall"

        var rq=Volley.newRequestQueue(this)
        var sr=JsonObjectRequest(Request.Method.GET,url,null,
            Response.Listener{ response ->
                print("Berhasil changeProgressOnCall")
            },
            Response.ErrorListener{ error ->
               // Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            })

        rq.add(sr)

    }

}

