package org.ksetrin.ksetrin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import org.ksetrin.ksetrin.R
import org.ksetrin.ksetrin.fragments.*

class MainActivity : AppCompatActivity() {

    private lateinit var navigationView : NavigationView
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var toolbar: MaterialToolbar
    private var selectedFragment : Fragment? = null

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
                    setFragment(WaterFragment())
                    setTitle("Nearby Water Sources")
                }
                R.id.reminder_item -> {
                    setFragment(RemindersFragment())
                    setTitle("Reminders")
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
            it.isChecked = true
            true
        }
    }

    private fun setTitle(string : String){
        toolbar.title = string
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentInFrame = supportFragmentManager.findFragmentById(R.id.frameLayout)
        if (fragmentInFrame != null && fragmentInFrame::class == fragment::class) return
        selectedFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        val fragmentInFrame = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when {
            fragmentInFrame!! :: class.java == AddReminderFragment::class.java -> {
                setFragment(RemindersFragment())
                setTitle("Reminders")
            }
            fragmentInFrame:: class.java != HomeFragment::class.java -> {
                setFragment(HomeFragment())
                setTitle("Home")
                navigationView.setCheckedItem(R.id.home_item)
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

}