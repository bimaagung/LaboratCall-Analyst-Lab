package com.medis.laboratcall

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_aktif_analis.*
import org.jetbrains.anko.indeterminateProgressDialog

class AktifAnalis : AppCompatActivity(), View.OnClickListener {

//oke
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aktif_analis)


        tb_back.setOnClickListener {
            var intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        check_aktif_laboratoirum()
        tb_lab_open.setOnClickListener(this)
        tb_lab_close.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        var token  = getSharedPreferences("id", Context.MODE_PRIVATE)
        var id_klinik = token.getString("klinik"," ")

        var loading = indeterminateProgressDialog("Silahkan Menunggu")
        loading.apply {
            setCancelable(false)
        }

        when (v.id) {
            R.id.tb_lab_open -> {
                loading.show()
                var url = Connection.url + "admin/page_pemeriksaan/aktif_laboratorium_klinik?id_klinik=$id_klinik&aktif_lab=1"
                var rq= Volley.newRequestQueue(this)
                var sr= JsonObjectRequest(
                    Request.Method.GET,url,null,
                    Response.Listener{ response ->
                        loading.dismiss()
                        var proses = response.getString("proses")

                        if(proses == "open")
                        {

                            tb_lab_open.visibility = View.GONE
                            tb_lab_close.visibility = View.VISIBLE

                        }else if(proses == "open error"){
                            Toast.makeText(this, "Gagal Aktif Laboratorium Klinik",
                                Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this, "Sistem aktf laboratorium klinik error", Toast.LENGTH_SHORT).show()
                        }
                    },
                    Response.ErrorListener{ error ->
                        loading.dismiss()
                        dialogKonfirmasi("Tidak ada koneksi internet")
                    })

                rq.add(sr)
            }

            R.id.tb_lab_close -> {
                loading.show()
                var url = Connection.url + "admin/page_pemeriksaan/aktif_laboratorium_klinik?id_klinik=$id_klinik&aktif_lab=0"
                var rq= Volley.newRequestQueue(this)
                var sr= JsonObjectRequest(
                    Request.Method.GET,url,null,
                    Response.Listener{ response ->
                        loading.dismiss()
                        var proses = response.getString("proses")

                        if(proses == "close")
                        {

                            tb_lab_open.visibility = View.VISIBLE
                            tb_lab_close.visibility = View.GONE

                        }else if(proses == "close error"){
                            Toast.makeText(this, "Gagal Close Laboratorium Klinik",
                                Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this, "Sistem close laboratorium klinik error", Toast.LENGTH_SHORT).show()
                        }
                    },
                    Response.ErrorListener{ error ->
                        loading.dismiss()
                        dialogKonfirmasi("Tidak ada koneksi internet")
                    })

                rq.add(sr)
            }
        }
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
                tb_lab_open.visibility = View.GONE
                tb_lab_close.visibility = View.VISIBLE

            }else if(open == false){

                tb_lab_open.visibility = View.VISIBLE
                tb_lab_close.visibility = View.GONE
            }
            else{
                Toast.makeText(this, "Sistem aktf laboratorium klinik error", Toast.LENGTH_SHORT).show()
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



}
