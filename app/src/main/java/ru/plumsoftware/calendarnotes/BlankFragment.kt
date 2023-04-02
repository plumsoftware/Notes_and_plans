package ru.plumsoftware.calendarnotes

import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.os.Bundle
import android.provider.BaseColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ru.plumsoftware.calendarnotes.adapters.DayAdapter
import ru.plumsoftware.calendarnotes.database.DealDatabaseEntry
import ru.plumsoftware.calendarnotes.items.DayItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val ARG_OBJECT_DATE = "date"
const val ARG_OBJECT_DAY_ITEM = "day_item"
const val ARG_OBJECT_DEAL = "deal"
var time: Long = 0

class BlankFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT_DATE) }
            ?.apply {

//                Views
                val recyclerViewCalendar: RecyclerView =
                    view.findViewById<RecyclerView>(R.id.recyclerViewCalendar) as RecyclerView
                val textViewMonth = view.findViewById<TextView>(R.id.textViewMonth) as TextView

                val calendar: Calendar = Calendar.getInstance()
                val currentCalendar: Calendar = Calendar.getInstance()
                val databaseHelper: DealDatabaseEntry.ColorDatabaseHelper =
                    DealDatabaseEntry.ColorDatabaseHelper(requireContext())

//                val pos = getLong(ARG_OBJECT_DATE)

//            Add days
                calendar.timeInMillis = getLong(ARG_OBJECT_DATE)

                val dayItemList = ArrayList<DayItem>()
                val maximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                val calendarD: Calendar = Calendar.getInstance()
                val range = 1..maximum

//            Add week names
                for (j in 1..7 step 1) {
                    dayItemList.add(
                        DayItem(
                            (-1 * j),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.YEAR),
                            true,
                            -1
                        )
                    )
                }

                for (j in range step 1) {
                    if (j == 1) {
                        calendar.add(Calendar.MONTH, -1)
                        calendar.set(
                            Calendar.DAY_OF_MONTH,
                            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        )
                        if (SimpleDateFormat("u", Locale.getDefault()).format(calendar.time)
                                .toInt() != 7
                        ) {

                            calendar.timeInMillis = getLong(ARG_OBJECT_DATE)

                            calendar.add(Calendar.MONTH, -1)
                            calendar.set(
                                Calendar.DAY_OF_MONTH,
                                calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                            )

                            //Toast.makeText(context, SimpleDateFormat("dd:MMM", Locale.getDefault()).format(calendar.time), Toast.LENGTH_SHORT).show()

                            val start: Int =
                                SimpleDateFormat("u", Locale.getDefault()).format(calendar.time)
                                    .toInt()
                            val maximum: Int = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

                            for (k in (maximum - start + 1)..maximum step 1) {
                                dayItemList.add(
                                    DayItem(
                                        k,
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.YEAR),
                                        false,
                                        -1
                                    )
                                )
                            }
                        }
                    }

//              Setup current day
                    if (currentCalendar.get(Calendar.DAY_OF_MONTH) == j &&
                        calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                        currentCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    ) {
                        dayItemList.add(
                            DayItem(
                                j,
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.YEAR),
                                true,
                                Color.parseColor("#8CF7C3")
                            )
                        )
                    } else {
                        val color = readValue(
                            databaseHelper,
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.YEAR),
                            j
                        )


//                        Setup all days
                        dayItemList.add(
                            DayItem(
                                j,
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.YEAR),
                                true,
                                indicator = color
                            )
                        )
                    }

                    calendar.timeInMillis = getLong(ARG_OBJECT_DATE)

//            Get previous days
                    if (j == maximum && SimpleDateFormat(
                            "u",
                            Locale.getDefault()
                        ).format(calendar.time).toInt() != 1
                    ) {
                        calendar.timeInMillis = getLong(ARG_OBJECT_DATE)

                        calendar.add(Calendar.MONTH, +1)
                        calendar.set(Calendar.DAY_OF_MONTH, 1)
                        val start: Int =
                            SimpleDateFormat("u", Locale.getDefault()).format(calendar.time).toInt()

                        for (k in 1..(8 - start) step 1) {
                            dayItemList.add(
                                DayItem(
                                    k,
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.YEAR),
                                    false,
                                    -1
                                )
                            )
                        }
                    }

                    calendar.timeInMillis = getLong(ARG_OBJECT_DATE)
                }

//            Add item to stack list
                val name: String =
                    SimpleDateFormat("LLLL", Locale.getDefault()).format(calendar.time)

//        Setup data
                recyclerViewCalendar.setHasFixedSize(true)
                recyclerViewCalendar.layoutManager =
                    StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL)
                recyclerViewCalendar.adapter =
                    DayAdapter(dayItemList, requireContext(), requireActivity())

                textViewMonth.text = name
            }
    }

    private fun readValue(
        databaseHelper: SQLiteOpenHelper?,
        rootMonth: Int,
        rootYear: Int,
        rootDay: Int
    ): Int {
        val db = databaseHelper?.readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_NAME_COLOR,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_MONTH,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_YEAR,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_DAY
        )

        val selection =
            "${DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_MONTH} = ? AND " +
                    "${DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_YEAR} = ? AND " +
                    "${DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_DAY} = ?"
        val selectionArgs = arrayOf(rootMonth.toString(), rootYear.toString(), rootDay.toString())

        //val sortOrder = "${DealDatabaseEntry.DealDatabaseConstants.COLUMN_DEAL_ADD_TIME} DESC"

        val cursor: Cursor? = db?.query(
            DealDatabaseEntry.DealDatabaseConstants.TABLE_NAME_2,   // The table to query
            null,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            selectionArgs,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            null              // The sort order
        )

        var color: Int = 0

        while (cursor?.moveToNext() == true) {
            color =
                cursor.getInt(cursor.getColumnIndexOrThrow(DealDatabaseEntry.DealDatabaseConstants.COLUMN_NAME_COLOR))
            return color
        }
        cursor?.close()
        db?.close()

        return color
    }
}