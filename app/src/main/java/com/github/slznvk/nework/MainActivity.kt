package com.github.slznvk.nework

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.github.slznvk.nework.databinding.ActivityMainBinding
import com.yandex.mapkit.MapKitFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.navHostFragment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MapKitFactory.setApiKey("fbc1f7ff-db17-4e29-a99d-cf7a33086129")
        MapKitFactory.initialize(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.apply {
            bottomNavigationView.setupWithNavController(navController)
            bottomNavigationView.setOnItemSelectedListener { itemMenu ->
                when (itemMenu.itemId) {
                    R.id.posts -> {
                        navController.navigate(R.id.postsFeedFragment)
                    }

                    R.id.events -> {
                        navController.navigate(R.id.eventsFeedFragment)
                    }

                    else -> {
                        navController.navigate(R.id.usersFragment)
                    }
                }
                true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
    }
}