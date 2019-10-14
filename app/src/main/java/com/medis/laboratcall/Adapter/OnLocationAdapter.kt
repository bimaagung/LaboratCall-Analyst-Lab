package com.medis.laboratcall.Adapter

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import com.medis.laboratcall.Connection
import com.medis.laboratcall.Data.DataOnLocation
import com.medis.laboratcall.DetailPasien
import com.medis.laboratcall.HomeActivity
import com.medis.laboratcall.R
import kotlinx.android.synthetic.main.list_onlocation.view.*
import org.jetbrains.anko.indeterminateProgressDialog

class OnLocationAdapter(var context: Context, var list: ArrayList<DataOnLocation>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.list_onlocation, parent, false)
        return ListOnLocation(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListOnLocation).show(list[position].id_pasien, list[position].nama_pasien, list[position].tanggal_pemeriksaan, list[position].waktu_pemeriksaan)

        holder.itemView.DetailPemeriksaanOnCall.setOnClickListener{



            var i = Intent(context, DetailPasien::class.java)
            i.putExtra("tanggal_pemeriksaan", list[position].tanggal_pemeriksaan)
            i.putExtra("waktu_pemeriksaan", list[position].waktu_pemeriksaan)
            i.putExtra("id_oncall", list[position].id_oncall)
            i.putExtra("id_pemeriksaan", list[position].id_pemeriksaan)
            i.putExtra("id_pasien", list[position].id_pasien)
            i.putExtra("nama_pasien", list[position].nama_pasien)
            i.putExtra("jenkel_pasien", list[position].jenkel_pasien)
            i.putExtra("tanggal_lahir_pasien", list[position].tanggal_lahir_pasien)
            i.putExtra("umur_pasien", list[position].umur_pasien)
            i.putExtra("alamat_pasien", list[position].alamat_pasien)
            i.putExtra("no_wa_pasien", list[position].no_wa_pasien)
            i.putExtra("layanan", "onlocation")
            context.startActivity(i)
        }

        holder.itemView.delete_oncall.setOnClickListener{

            //Konfirmasi hapus pemeriksaan
            holder.dialogKonfirmasi(list[position].id_oncall,"Apakah anda yakin menghapus oncall ini ?")
        }


    }





    class ListOnLocation(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        fun show(ip:String, np:String, tgl_p:String, wp:String)
        {
            itemView.tx_nama_oncall.text = np
            itemView.tx_id_oncall.text = ip
            itemView.tx_tgl_oncall.text = tgl_p+" / "+wp
        }

        fun dialogKonfirmasi(id_oncall:String,message:String)
        {

            val builder = AlertDialog.Builder(itemView.context)
            builder.setMessage(message)
                .setPositiveButton("Ya",
                    DialogInterface.OnClickListener { dialog, id ->

                        //Progres Menunggu
                        var loading = itemView.context.indeterminateProgressDialog("Silahkan Menunggu")
                        loading.apply {
                            setCancelable(false)
                        }
                        loading.show()

                        var url= Connection.url + "admin/page_pemeriksaan/delete_oncall_app?id_oncall=$id_oncall"
                        var rq= Volley.newRequestQueue(itemView.context)
                        var sr= JsonObjectRequest(
                            Request.Method.GET,url,null,
                            Response.Listener{ response ->
                                loading.dismiss()

                                var proses = response.getBoolean("proses")

                                if(proses.equals(true)){

                                    itemView.DetailPemeriksaanOnCall.visibility = View.GONE
                                    Toast.makeText(itemView.context , "Pemeriksaan OnCall berhasil dihapus",Toast.LENGTH_LONG).show()
                                }else if(proses.equals(false)){
                                    konfirmasiKoneksi("Pemeriksaan gagal dihapus")
                                }else{
                                    konfirmasiKoneksi("Sistem Error")
                                }
                            },
                            Response.ErrorListener{ error ->
                                loading.dismiss()
                                konfirmasiKoneksi("Tidak ada koneksi internet")
                            })
                        rq.add(sr)
                    })
                .setNegativeButton("Tidak",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
            // Create the AlertDialog object and return it
            val alert = builder.create()
            alert.show()
        }

        fun konfirmasiKoneksi(message: String) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setMessage(message)
                .setPositiveButton("Mengerti",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
            // Create the AlertDialog object and return it
            val alert = builder.create()
            alert.show()
        }

    }
}