package com.medis.laboratcall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.medis.laboratcall.Adapter.InputHasilAdapter
import com.medis.laboratcall.Adapter.PesananAdapter
import com.medis.laboratcall.Data.DataInputHasil
import com.medis.laboratcall.Data.DataPesanan
import kotlinx.android.synthetic.main.activity_daftar_pilihan_pasien.*
import org.jetbrains.anko.indeterminateProgressDialog
import java.io.DataInput

class DaftarPilihanPasien : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_pilihan_pasien)

        tb_back.setOnClickListener {
            onBackPressed()
            finish()
        }

        var loading = indeterminateProgressDialog("Silahkan Menunggu")
        loading.apply {
            setCancelable(false)
        }
        loading.show()

        var id_pemeriksaan = intent.getStringExtra("id_pemeriksaan")
        var total_harga =  intent.getStringExtra("total_harga")
        tx_id_pasien.text = intent.getStringExtra("id_pasien")
        tx_nama_pasien.text = intent.getStringExtra("nama_pasien")

        var list = ArrayList<DataInputHasil>()

        var url = Connection.url + "admin/page_pemeriksaan/get_item_pemeriksaan_byid_api/$id_pemeriksaan"
        var rq = Volley.newRequestQueue(this)
        var jar = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                loading.dismiss()
                for (x in 0..response.length() - 1)
                    list.add(
                        DataInputHasil(
                            response.getJSONObject(x).getString("id"),
                            response.getJSONObject(x).getString("item_pemeriksaan")
                        )
                    )


                var adp = InputHasilAdapter(this, list)
                layoutPilihanPemeriksaan.layoutManager = LinearLayoutManager(this)
                layoutPilihanPemeriksaan.adapter = adp

            },
            Response.ErrorListener { error ->
                loading.dismiss()
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            })
        rq.add(jar)

        tb_lanjut.setOnClickListener{
            var i = Intent(this, Pembayaran::class.java)
            i.putExtra("id_pemeriksaan",id_pemeriksaan)
            i.putExtra("total_harga",total_harga)
            startActivity(i)
        }
    }
}
