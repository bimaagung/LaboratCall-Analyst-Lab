package com.medis.laboratcall.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medis.laboratcall.CollectionDataInputHasil
import com.medis.laboratcall.Data.DataInputHasil
import com.medis.laboratcall.Data.DataPesanan
import com.medis.laboratcall.R
import kotlinx.android.synthetic.main.list_pemeriksaan_row.view.*
import kotlinx.android.synthetic.main.list_pilihan_pemeriksaan.view.*

class PesananAdapter(var context: Context, var list: ArrayList<DataPesanan>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.list_pemeriksaan_row, parent, false)
        return ListPesanan(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListPesanan).show(list[position].item, list[position].harga_item)

    }

    class ListPesanan(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        fun show(it:String, hi:String)
        {
            itemView.tx_item.text = it
            itemView.tx_harga.text = hi
        }
    }
}
