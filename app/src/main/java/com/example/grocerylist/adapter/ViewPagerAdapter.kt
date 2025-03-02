package com.example.grocerylist.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.grocerylist.ui.ListFragment
import com.example.grocerylist.ui.SplitFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ListFragment()
        1 -> SplitFragment()
        else -> throw IllegalArgumentException("Invalid position")
    }
}
