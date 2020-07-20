package com.example.coordinatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coordinatorapp.ui.fragments.FabFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
                .add(R.id.containerFrag, FabFragment())
                .commit()
    }
}