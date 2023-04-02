package ru.plumsoftware.calendarnotes.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.plumsoftware.calendarnotes.*
import ru.plumsoftware.calendarnotes.baseitems.Deal
import ru.plumsoftware.calendarnotes.items.DayItem

class ViewPagerCalendarAdapter(fragment: FragmentActivity, private val dates: List<Long>) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = dates.size

    override fun createFragment(position: Int): Fragment {
        val fragment = BlankFragment()
        fragment.arguments = Bundle().apply {
            putLong(ARG_OBJECT_DATE, dates[position])
            putInt(ARG_OBJECT_POSITION, position)
        }
        return fragment
    }
}