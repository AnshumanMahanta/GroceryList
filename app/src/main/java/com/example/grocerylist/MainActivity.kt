package com.example.grocerylist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grocerylist.adapter.ViewPagerAdapter
import com.example.grocerylist.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val tabTitles = arrayOf("List", "Split")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewPager2 with the adapter
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Link the TabLayout with the ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}
