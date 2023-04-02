package ru.plumsoftware.calendarnotes.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.plumsoftware.calendarnotes.ARG_OBJECT_POSITION
import ru.plumsoftware.calendarnotes.ARG_OBJECT_SOURCE
import ru.plumsoftware.calendarnotes.DealsFragment
import ru.plumsoftware.calendarnotes.baseitems.Deal

class ViewPagerAdapter(private val fragment: FragmentActivity, private val deals: List<Deal>) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int  = deals.size

    override fun createFragment(position: Int): Fragment {
        val fragment = DealsFragment()
        fragment.arguments = Bundle().apply {
            putParcelable(ARG_OBJECT_SOURCE, deals[position])
        }
        return fragment
    }
}