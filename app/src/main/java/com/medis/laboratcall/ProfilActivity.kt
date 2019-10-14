package com.medis.laboratcall

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_profil.*

class ProfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        datatoolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_green_24dp)

        datatoolbar.setNavigationOnClickListener{
            var i= Intent(this,HomeActivity::class.java)
            startActivity(i)
            finish()
        }

        var token  = getSharedPreferences("id", Context.MODE_PRIVATE)
        tx_nama_analis.text = token.getString("nama"," ")
        tx_tanggal_lahir.text = token.getString("tmp_lahir"," ")+", "+token.getString("tgl_lahir","")
        tx_no_wa_analis.text = "+"+token.getString("no_wa_analis"," ")

        var foto_analis = token.getString("foto_analis"," ")

        //Menampilkan foto
        Glide.with(this)
            .load(Connection.urlFoto+"assets/img/analis/"+foto_analis)
            .fitCenter()
            .apply(RequestOptions().override(130, 150))
            .into(v_foto_analis)

        tb_logout.setOnClickListener{
            //Clear sharedpreference

            var editor_username = token.edit()
            editor_username.clear()
            editor_username.commit()

            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}
