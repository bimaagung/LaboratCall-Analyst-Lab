package com.medis.laboratcall.Fragment


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

import com.medis.laboratcall.Adapter.OffLocationAdapter
import com.medis.laboratcall.Connection
import com.medis.laboratcall.Data.DataOffLocation
import kotlinx.android.synthetic.main.fragment_offlocation.*

import kotlinx.android.synthetic.main.fragment_offlocation.view.*
import org.jetbrains.anko.indeterminateProgressDialog





class FragmentOffLocation : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(com.medis.laboratcall.R.layout.fragment_offlocation, container, false)

//    view fab.setOnClickListener { view ->
//
//    }

        getDataOffLocation()

    return view

    }

    fun getDataOffLocation()
    {
//        var loading = activity!!.indeterminateProgressDialog("Silahkan Menunggu")
//        loading.apply {
//            setCancelable(false)
//        }
//        loading.show()

        var list = ArrayList<DataOffLocation>()

        var url = Connection.url + "admin/page_pemeriksaan/get_pemeriksaan_pasien?id_klinik=1"
        var rq = Volley.newRequestQueue(activity)
        var jar = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
//                loading.dismiss()
                for (x in 0..response.length() - 1)
                    list.add(
                        DataOffLocation(
                            response.getJSONObject(x).getString("id_pemeriksaan"),
                            response.getJSONObject(x).getString("id_pasien"),
                            response.getJSONObject(x).getString("tanggal_pemeriksaan"),
                            response.getJSONObject(x).getString("waktu_pemeriksaan"),
                            response.getJSONObject(x).getString("total_harga"),
                            response.getJSONObject(x).getString("nama_pasien"),
                            response.getJSONObject(x).getString("jenkel_pasien"),
                            response.getJSONObject(x).getString("tanggal_lahir_pasien"),
                            response.getJSONObject(x).getString("umur_pasien"),
                            response.getJSONObject(x).getString("alamat_pasien"),
                            response.getJSONObject(x).getBoolean("oncall")
                        )
                    )


                var adp = OffLocationAdapter(requireActivity(), list)
                layoutListOffLocation.layoutManager = LinearLayoutManager(activity)
                layoutListOffLocation.adapter = adp

            },
            Response.ErrorListener { error ->
//                loading.dismiss()
                Toast.makeText(activity, error.message, Toast.LENGTH_LONG).show()
            })
        rq.add(jar)
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            mMessageReceiver, IntentFilter("data_pemeriksaan")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mMessageReceiver)
    }

    var mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            getDataOffLocation()
        }
    }

}

