package com.payable.ipg.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.payable.ipg.PAYableIPGClient
import com.payable.ipg.R

internal class IPGActionView : AppCompatActivity() {

    private lateinit var ipgClient: PAYableIPGClient
    private lateinit var ipgFragment: IPGFragment

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.ipg_action_view)

        ipgClient = intent.getSerializableExtra(PAYableIPGClient::class.java.simpleName) as PAYableIPGClient
        title = getString(R.string.payable_mobile_ipg)

        ipgFragment = IPGFragment(ipgClient)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frame_layout, ipgFragment)
        ft.commit()
    }

    override fun onBackPressed() {

    }
}