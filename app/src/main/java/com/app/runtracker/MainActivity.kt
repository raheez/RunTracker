package com.app.runtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.runtracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var mBinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding?.centerButton?.setOnClickListener {
            mBinding?.titleTv?.setText("clicked")
        }
    }
}