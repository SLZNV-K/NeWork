package com.github.slznvk.nework.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.github.slznvk.nework.R
import com.github.slznvk.nework.auth.AppAuth
import com.github.slznvk.nework.databinding.ActivityMainBinding
import com.github.slznvk.nework.viewModel.AuthViewModel
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var appAuth: AppAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            setSupportActionBar(toolbar)

            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.auth_main_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {

                        R.id.signIn -> {
                            if (authViewModel.authenticated) {
                                navController.navigate(
                                    R.id.userDetailsFragment
                                )
                            } else {
                                navController.navigate(R.id.loginFragment)
                            }
                            true
                        }

                        else -> false
                    }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}