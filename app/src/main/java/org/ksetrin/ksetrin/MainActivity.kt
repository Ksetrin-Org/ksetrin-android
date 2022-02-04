package org.ksetrin.ksetrin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import org.ksetrin.ksetrin.fragments.*

class MainActivity : AppCompatActivity() {

    private lateinit var navigationView : NavigationView
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        modifyViews()
        initListeners()
        setFragment(HomeFragment())
    }

    private fun initViews() {
        navigationView = findViewById(R.id.navigationView)
        drawerLayout = findViewById(R.id.drawerLayout)
        toolbar = findViewById(R.id.materialToolBar)
    }

    private fun modifyViews(){
        navigationView.setCheckedItem(R.id.home_item)
    }
    private fun initListeners() {
        toolbar.setNavigationOnClickListener {
            drawerLayout.open()
        }
        toolbar.setOnMenuItemClickListener {
            navigationView.checkedItem?.isChecked = false
            when(it.itemId) {
                R.id.profile_item -> {
                    setFragment(ProfileFragment())
                    setTitle("Profile")
                }
            }
            true
        }
        navigationView.setNavigationItemSelectedListener {
            drawerLayout.close()
            when(it.itemId) {
                R.id.home_item -> {
                    setFragment(HomeFragment())
                    setTitle("Home")
                }
                R.id.water_item -> {
                    setFragment(WaterBodiesFragment())
                    setTitle("Nearby Water Sources")
                }
                R.id.news_item -> {
                    setFragment(NewsFragment())
                    setTitle("News and Updates")
                }
                R.id.shop_item -> {
                    setFragment(ShopsFragment())
                    setTitle("Nearby Shops")
                }
            }
            true
        }
    }

    private fun setTitle(string : String){
        toolbar.title = string
    }

    private fun setFragment(fragment: Fragment) {
        val manager = supportFragmentManager.beginTransaction()
        manager.replace(R.id.frameLayout, fragment)
        manager.commit()
    }
}