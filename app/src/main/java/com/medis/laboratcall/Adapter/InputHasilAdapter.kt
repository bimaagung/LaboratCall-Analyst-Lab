package com.medis.laboratcall.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.medis.laboratcall.CollectionDataInputHasil
import com.medis.laboratcall.Data.DataInputHasil
import com.medis.laboratcall.R
import kotlinx.android.synthetic.main.list_pemeriksaan_row.view.*
import kotlinx.android.synthetic.main.list_pilihan_pemeriksaan.view.*

class InputHasilAdapter(var context: Context, var list: ArrayList<DataInputHasil>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.list_pilihan_pemeriksaan, parent, false)
        return ListInputHasil(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListInputHasil).show(list[position].id_item, list[position].item_pemeriksaan)
    }

    class ListInputHasil(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        fun show(ii:String, ip:String)
        {
            itemView.tx_item_pemeriksaan.text = ip
            itemView.tb_open_hasil.visibility = View.INVISIBLE
            itemView.tb_simpan_hasil.visibility = View.VISIBLE

            itemView.tb_simpan_hasil.setOnClickListener{
                //CollectionDataInputHasil.IdItem.put("id_item" , ii)
                var hasil = itemView.input_hasil_pemeriksaan.text.toString().replace(" ","+")

                CollectionDataInputHasil.IdItem.put(ii , ii)
                CollectionDataInputHasil.InputHasil.put(ii , hasil)
                itemView.tb_open_hasil.visibility = View.VISIBLE
                itemView.tb_simpan_hasil.visibility = View.INVISIBLE
            }

            itemView.tb_open_hasil.setOnClickListener{
                itemView.tb_open_hasil.visibility = View.INVISIBLE
                itemView.tb_simpan_hasil.visibility = View.VISIBLE
            }

        }
    }
}
