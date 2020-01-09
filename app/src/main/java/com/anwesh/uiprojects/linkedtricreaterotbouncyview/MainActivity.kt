package com.anwesh.uiprojects.linkedtricreaterotbouncyview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.tricreaterotbouncyview.TriCreateRotBouncyView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TriCreateRotBouncyView.create(this)
    }
}
