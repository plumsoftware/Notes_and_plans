package ru.plumsoftware.calendarnotes.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ru.plumsoftware.calendarnotes.R
import ru.plumsoftware.calendarnotes.items.StackItem
import java.util.*

class StackAdapter(
    private val stackList: List<StackItem>,
    private val context: Context,
    private val activity: FragmentActivity
) : BaseAdapter() {

    override fun getCount(): Int {
        return stackList.size
    }

    override fun getItem(position: Int): Any {
        return stackList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.stack_item, parent, false)
        val stackItem: StackItem = stackList[position]

        val recyclerViewCalendar =
            view.findViewById<RecyclerView>(R.id.recyclerViewCalendar) as RecyclerView
        val textViewMonth = view.findViewById<TextView>(R.id.textViewMonth) as TextView

        val dayItemList = stackItem.dayItemList

//        Setup data
        recyclerViewCalendar.setHasFixedSize(true)
        recyclerViewCalendar.layoutManager =
            StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL)
        recyclerViewCalendar.adapter = DayAdapter(dayItemList, context, activity)

        textViewMonth.text = stackItem.month.monthName
        return view
    }
}