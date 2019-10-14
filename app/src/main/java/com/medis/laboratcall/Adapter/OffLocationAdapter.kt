package com.medis.laboratcall.Adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.getIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.medis.laboratcall.Connection
import com.medis.laboratcall.Data.DataOffLocation
import com.medis.laboratcall.DetailPasien
import kotlinx.android.synthetic.main.list_offlocation.view.*
import org.jetbrains.anko.indeterminateProgressDialog
import android.app.Activity
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.medis.laboratcall.HomeActivity
import com.medis.laboratcall.R


class OffLocationAdapter(var context: Context, var list: ArrayList<DataOffLocation>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.list_offlocation, parent, false)
        return ListOffLocation(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListOffLocation).show(list[position].id_pasien, list[position].nama_pasien, list[position].tanggal_pemeriksaan, list[position].waktu_pemeriksaan, list[position].oncall)

        holder.itemView.DetailPemeriksaanOffCall.setOnClickListener{
            var i = Intent(context, DetailPasien::class.java)
            i.putExtra("tanggal_pemeriksaan", list[position].tanggal_pemeriksaan)
            i.putExtra("waktu_pemeriksaan", list[position].waktu_pemeriksaan)
            i.putExtra("total_harga", list[position].total_harga)
            i.putExtra("id_pemeriksaan", list[position].id_pemeriksaan)
            i.putExtra("id_pasien", list[position].id_pasien)
            i.putExtra("nama_pasien", list[position].nama_pasien)
            i.putExtra("jenkel_pasien", list[position].jenkel_pasien)
            i.putExtra("tanggal_lahir_pasien", list[position].tanggal_lahir_pasien)
            i.putExtra("umur_pasien", list[position].umur_pasien)
            i.putExtra("alamat_pasien", list[position].alamat_pasien)
            i.putExtra("layanan", "offlocation")

            context.startActivity(i)
        }

        holder.itemView.delete_offcall.setOnClickListener{

            //Konfirmasi hapus pemeriksaan
            holder.dialogKonfirmasi(list[position].id_pemeriksaan,"Apakah anda yakin menghapus pemeriksaan ini ?",
                list[position].tanggal_pemeriksaan,list[position].waktu_pemeriksaan,list[position].id_pasien)
        }
    }

    class ListOffLocation(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        fun show(ip:String, np:String, tgl_p:String, wp:String, oc:Boolean)
        {
            if(oc.equals(true))
            {
                itemView.view_offlocation.visibility = View.GONE
                itemView.view_onlocation.visibility = View.VISIBLE
            }else{
                itemView.view_offlocation.visibility = View.VISIBLE
                itemView.view_onlocation.visibility = View.GONE
            }

            itemView.tx_nama_offcall.text = np
            itemView.tx_id_offcall.text = ip
            itemView.tx_tgl_offcall.text = tgl_p+" / "+wp
        }

        fun dialogKonfirmasi(id_pemeriksaan:String,message:String,tanggal:String, waktu:String, id_pasien:String)
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

                        var url= Connection.url + "admin/page_pemeriksaan/delete_pemeriksaan_app?id_pemeriksaan="+id_pemeriksaan+
                                "&id_pasien="+id_pasien+"&tanggal="+tanggal+"&waktu="+waktu
                        var rq= Volley.newRequestQueue(itemView.context)
                        var sr= JsonObjectRequest(
                            Request.Method.GET,url,null,
                            Response.Listener{ response ->
                                loading.dismiss()

                                var proses = response.getBoolean("proses")

                                if(proses.equals(true)){

                                    itemView.DetailPemeriksaanOffCall.visibility = View.GONE
                                    Toast.makeText(itemView.context , "Pemeriksaan berhasil dihapus",Toast.LENGTH_LONG).show()
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