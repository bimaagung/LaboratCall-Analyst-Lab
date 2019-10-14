package com.medis.laboratcall.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medis.laboratcall.CollectionDataInputHasil
import com.medis.laboratcall.Data.DataInputHasil
import com.medis.laboratcall.Data.DataPesanan
import com.medis.laboratcall.Data.DataPilihanPasienOncall
import com.medis.laboratcall.R
import kotlinx.android.synthetic.main.list_pemeriksaan_row.view.*
import kotlinx.android.synthetic.main.list_pilihan_pemeriksaan.view.*

class PilihanPasienOncallAdapter(var context: Context, var list: ArrayList<DataPilihanPasienOncall>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.list_pilihan_pasien_oncall, parent, false)
        return ListPilihanPasienOncall(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListPilihanPasienOncall).show(list[position].item)

    }

    class ListPilihanPasienOncall(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        fun show(it:String)
        {
            itemView.tx_item.text = it
        }
    }
}
