package ru.plumsoftware.calendarnotes.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import ru.plumsoftware.calendarnotes.*
import ru.plumsoftware.calendarnotes.baseitems.Deal
import ru.plumsoftware.calendarnotes.database.DealDatabaseEntry
import ru.plumsoftware.calendarnotes.items.DayItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DayAdapter(
    private val dayItemList: List<DayItem>,
    private val context: Context,
    private val activity: FragmentActivity
) :
    RecyclerView.Adapter<DayAdapter.DayItemViewHolder>() {
    class DayItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayItemCard: CardView = itemView.findViewById<CardView>(R.id.dayItemCard)
        val textViewDay: TextView = itemView.findViewById<TextView>(R.id.textViewDay)
    }

    private val calendar: Calendar = Calendar.getInstance()
    private var databaseHelperA: DealDatabaseEntry.DealDatabaseHelper? = DealDatabaseEntry.DealDatabaseHelper(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.day_item, parent, false)
        return DayAdapter.DayItemViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DayItemViewHolder, position: Int) {
        val dayItem: DayItem = dayItemList[position]

        if (dayItem.currentDay < 0) {
            when (dayItem.currentDay) {
                -1 -> holder.textViewDay.text = "Пн"
                -2 -> holder.textViewDay.text = "Вт"
                -3 -> holder.textViewDay.text = "Ср"
                -4 -> holder.textViewDay.text = "Чт"
                -5 -> holder.textViewDay.text = "Пт"
                -6 -> holder.textViewDay.text = "Сб"
                -7 -> holder.textViewDay.text = "Вс"
            }
        } else {
            if (dayItem.available)
                holder.textViewDay.text = dayItem.currentDay.toString()
            else {
                holder.textViewDay.setTextColor(Color.parseColor("#787680"))
                holder.textViewDay.text = dayItem.currentDay.toString()
            }
        }


        if (dayItem.indicator != -1) {
            holder.dayItemCard.setCardBackgroundColor(dayItem.indicator)
            holder.textViewDay.setTextColor(Color.BLACK)
        }

        val calendar: Calendar = Calendar.getInstance()
        var pattern: String = ""
        time = calendar.timeInMillis

        holder.dayItemCard.setOnClickListener {
            if (dayItem.available) {
                calendar.set(Calendar.YEAR, dayItem.currentYear)
                calendar.set(Calendar.MONTH, dayItem.currentMonth)
                calendar.set(Calendar.DAY_OF_MONTH, dayItem.currentDay)

                time = calendar.timeInMillis
                pattern = if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
                    "d"
                } else {
                    "dd"
                }
                textView2?.text = SimpleDateFormat(
                    pattern,
                    Locale.getDefault()
                ).format(calendar.time) + " " + SimpleDateFormat(
                    "MMMM",
                    Locale.getDefault()
                ).format(
                    calendar.time
                ).substring(0, 1).toUpperCase() + SimpleDateFormat(
                    "MMMM",
                    Locale.getDefault()
                ).format(
                    calendar.time
                ).substring(
                    1,
                    SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time).length
                )

//                Load deals
                val deals = readValue(
                    databaseHelperA,
                    dayItem.currentMonth,
                    dayItem.currentYear,
                    dayItem.currentDay
                )

//                viewPager2?.adapter = ViewPagerAdapter(activity, deals)
                recyclerViewDeals?.adapter = DealAdapter(deals, context, activity)
            }
        }
    }

    override fun getItemCount(): Int {
        return dayItemList.size
    }

    private fun readValue(
        databaseHelper: SQLiteOpenHelper?,
        rootMonth: Int,
        rootYear: Int,
        rootDay: Int
    ): kotlin.collections.ArrayList<Deal> {
        val db = databaseHelper?.readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_NAME_DEAL,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_DESCRIPTION_DEAL,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_MODE_DEAL,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_DEAL_ADD_TIME,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_DEAL_START_TIME,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_DEAL_FINISH_TIME,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_MONTH,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_YEAR,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_DAY
        )

        val selection =
            "${DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_MONTH} = ? AND " +
                    "${DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_YEAR} = ? AND " +
                    "${DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_DAY} = ?"
        val selectionArgs = arrayOf(rootMonth.toString(), rootYear.toString(), rootDay.toString())

        val sortOrder = "${DealDatabaseEntry.DealDatabaseConstants.COLUMN_DEAL_ADD_TIME} DESC"

        val cursor: Cursor? = db?.query(
            DealDatabaseEntry.DealDatabaseConstants.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            selectionArgs,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )

        val deals = ArrayList<Deal>()

        while (cursor?.moveToNext() == true) {

            val id = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val name = cursor.getString(
                cursor.getColumnIndexOrThrow(
                    DealDatabaseEntry.DealDatabaseConstants.COLUMN_NAME_DEAL
                )
            )
            val desc = cursor.getString(
                cursor.getColumnIndexOrThrow(
                    DealDatabaseEntry.DealDatabaseConstants.COLUMN_DESCRIPTION_DEAL
                )
            )
            val dateFrom = cursor.getLong(
                cursor.getColumnIndexOrThrow(
                    DealDatabaseEntry.DealDatabaseConstants.COLUMN_DEAL_START_TIME
                )
            )
            val dateTo = cursor.getLong(
                cursor.getColumnIndexOrThrow(
                    DealDatabaseEntry.DealDatabaseConstants.COLUMN_DEAL_FINISH_TIME
                )
            )
            val dealAddTime = cursor.getLong(
                cursor.getColumnIndexOrThrow(
                    DealDatabaseEntry.DealDatabaseConstants.COLUMN_DEAL_ADD_TIME
                )
            )
            val rootMonth = cursor.getInt(
                cursor.getColumnIndexOrThrow(
                    DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_MONTH
                )
            )
            val rootYear = cursor.getInt(
                cursor.getColumnIndexOrThrow(
                    DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_YEAR
                )
            )
            val rootDay = cursor.getInt(
                cursor.getColumnIndexOrThrow(
                    DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_DAY
                )
            )
            val dealMode = cursor.getInt(
                cursor.getColumnIndexOrThrow(
                    DealDatabaseEntry.DealDatabaseConstants.COLUMN_MODE_DEAL
                )
            )

            deals.add(
                Deal(
                    id,
                    name,
                    desc,
                    dateFrom,
                    dateTo,
                    dealAddTime,
                    rootMonth,
                    rootYear,
                    rootDay,
                    dealMode
                )
            )
        }
        cursor?.close()
        db?.close()

        return deals
    }
}