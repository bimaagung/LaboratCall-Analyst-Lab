package com.medis.laboratcall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.medis.laboratcall.Adapter.PilihanPasienOncallAdapter
import com.medis.laboratcall.Data.DataPilihanPasienOncall
import kotlinx.android.synthetic.main.activity_pilihan_pasien_oncall.*
import org.jetbrains.anko.indeterminateProgressDialog

class PilihanPasienOncall : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilihan_pasien_oncall)

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
        println(id_pemeriksaan)
        var id_pasien = intent.getStringExtra("id_pasien")
        var id_oncall = intent.getStringExtra("id_oncall")
        var nama_pasien = intent.getStringExtra("nama_pasien")
        var no_wa_pasien = intent.getStringExtra("no_wa_pasien")

        tx_id_pasien_oncall.text = id_pasien
        tx_nama_pasien_oncall.text = nama_pasien

        var list = ArrayList<DataPilihanPasienOncall>()

        var url = Connection.url + "admin/page_pemeriksaan/get_api_harga_dan_total_item/$id_pemeriksaan"
        var rq = Volley.newRequestQueue(this)
        var jar = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                loading.dismiss()
                for (x in 0..response.length() - 1)
                    list.add(
                        DataPilihanPasienOncall(
                            response.getJSONObject(x).getString("item"),
                            response.getJSONObject(x).getString("harga_item")
                        )
                    )


                var adp = PilihanPasienOncallAdapter(this, list)
                layoutPilihanPasienOncall.layoutManager = LinearLayoutManager(this)
                layoutPilihanPasienOncall.adapter = adp

            },
            Response.ErrorListener { error ->
                loading.dismiss()
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            })
        rq.add(jar)

        tb_lanjut.setOnClickListener{
            var i= Intent(this,MapAnalis::class.java)
            i.putExtra("id_pasien", id_pasien)
            i.putExtra("id_oncall", id_oncall)
            i.putExtra("no_wa_pasien", no_wa_pasien)
            i.putExtra("id_pemeriksaan", id_pemeriksaan)
            startActivity(i)
            finish()
        }

    }
}
