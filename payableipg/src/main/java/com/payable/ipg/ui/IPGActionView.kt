package com.payable.ipg.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.payable.ipg.PAYableIPGClient
import com.payable.ipg.R
import com.payable.ipg.databinding.IpgActionViewBinding

internal class IPGActionView : AppCompatActivity() {

    private lateinit var binding: IpgActionViewBinding
    private lateinit var ipgClient: PAYableIPGClient
    private lateinit var ipgFragment: IPGFragment

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.ipg_action_view)

        ipgClient = intent.getSerializableExtra(PAYableIPGClient::class.java.simpleName) as PAYableIPGClient
        title = getString(R.string.payable_mobile_ipg)

        ipgFragment = IPGFragment(ipgClient)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(binding.frameLayout.id, ipgFragment)
        ft.commit()
    }

    override fun onBackPressed() {

    }
}