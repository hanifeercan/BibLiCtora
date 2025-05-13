package com.amineaytac.biblictora

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.amineaytac.biblictora.core.network.NetworkConnection
import com.amineaytac.biblictora.databinding.ActivityMainBinding
import com.amineaytac.biblictora.ui.home.HomeFragment
import com.amineaytac.biblictora.ui.splash.SplashFragment
import com.amineaytac.biblictora.util.gone
import com.amineaytac.biblictora.util.visible
import com.amineaytc.biblictora.util.viewBinding
import com.yagmurerdogan.toasticlib.Toastic
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        binding.bottomNavigationView.itemActiveIndicatorColor = null

        val noBottomNavigationIds = listOf(
            R.id.homeFragment,
            R.id.myBooksFragment,
            R.id.favoriteFragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (noBottomNavigationIds.contains(destination.id)) {
                binding.bottomNavigationView.visible()
            } else {
                binding.bottomNavigationView.gone()
            }
        }
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (!isConnected) {
                val currentFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()
                if (currentFragment !is SplashFragment && currentFragment !is HomeFragment) {
                    Toastic.toastic(
                        context = this@MainActivity,
                        message = getString(R.string.check_internet_connection),
                        duration = Toastic.LENGTH_SHORT,
                        type = Toastic.ERROR,
                        isIconAnimated = true
                    ).show()
                }
            }
        }
    }
}