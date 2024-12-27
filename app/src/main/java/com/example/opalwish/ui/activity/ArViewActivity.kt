package com.example.opalwish.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import com.example.opalwish.R
import com.example.opalwish.databinding.ActivityArViewBinding
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode

class ArViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArViewBinding
    private lateinit var modelNode: ArModelNode

                                                                                        
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityArViewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.placeButton.setOnClickListener {
            placeModel()
        }

        modelNode = ArModelNode(binding.sceneView.engine, PlacementMode.BEST_AVAILABLE).apply {
            loadModelGlbAsync(
                glbFileLocation = "models/new.glb",
            )
            {
                binding.sceneView.planeRenderer.isVisible = true
            }

            onAnchorChanged = {
                binding.placeButton.isGone
            }
        }
        binding.sceneView.addChild(modelNode)
    }

    private fun placeModel(){
        modelNode.anchor()
        binding.sceneView.planeRenderer.isVisible = false

    }

}