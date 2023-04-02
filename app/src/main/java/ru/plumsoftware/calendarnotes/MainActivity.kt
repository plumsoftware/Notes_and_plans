package ru.plumsoftware.calendarnotes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.provider.BaseColumns
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.instream.MobileInstreamAds
import ru.plumsoftware.calendarnotes.adapters.DealAdapter
import ru.plumsoftware.calendarnotes.adapters.ViewPagerCalendarAdapter
import ru.plumsoftware.calendarnotes.baseitems.Deal
import ru.plumsoftware.calendarnotes.database.DealDatabaseEntry
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("StaticFieldLeak")
var textView2: TextView? = null
var viewPager2: ViewPager2? = null
var databaseHelper: DealDatabaseEntry.DealDatabaseHelper? = null
var recyclerViewDeals: RecyclerView? = null
var ads: Boolean = false
var viewPager2Calendar: ViewPager2? = null

class MainActivity : FragmentActivity() {

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        setContentView(R.layout.activity_main)

//        Base variables
        val context: Context = MainActivity@ this as Context
        val activity: FragmentActivity = MainActivity@ this as FragmentActivity
        val calendar: Calendar = Calendar.getInstance()
        val currentCalendar: Calendar = Calendar.getInstance()
        val dates = ArrayList<Long>()
        val colors = ArrayList<Int>()
        val sharedPreferences: SharedPreferences = getSharedPreferences("_ads", MODE_PRIVATE)
        databaseHelper = DealDatabaseEntry.DealDatabaseHelper(this)

//        Ads
        initSdk(context = context)

//        Views
//        val stackView: StackView = findViewById<StackView>(R.id.stackView)
        viewPager2 = findViewById<ViewPager2>(R.id.viewPager2)
        viewPager2Calendar = findViewById<ViewPager2>(R.id.viewPager2Calendar)

        var changeYear: LinearLayout = findViewById<LinearLayout>(R.id.changeYear)
        val textViewYear: TextView = findViewById<TextView>(R.id.textViewYear)
        val textViewAdd: TextView = findViewById<TextView>(R.id.textViewAdd)
        val imageViewAdd: ImageView = findViewById<ImageView>(R.id.imageViewAdd)
        recyclerViewDeals = findViewById<RecyclerView>(R.id.recyclerDeals)

        textView2 = findViewById<TextView>(R.id.textView2)

//        Setup base data
        textViewYear.text =
            SimpleDateFormat("yyyy", Locale.getDefault()).format(currentCalendar.time)
        val year: Int = currentCalendar.get(Calendar.YEAR)
        setTime(currentCalendar, textView2)
        val boolean = sharedPreferences.getBoolean("is_first", true)

        if (boolean) {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putLong("time", System.currentTimeMillis())
            editor.putBoolean("is_first", false)
            editor.apply()
        }

        if (!boolean) {
            val cal: Calendar = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis() - sharedPreferences.getLong(
                "time",
                System.currentTimeMillis()
            )

            if (cal.get(Calendar.DAY_OF_MONTH) >= 2) {
                ads = true
            }
        }

//        Setup calendar on stack view
//        val stackItemList = ArrayList<StackItem>()
        for (i in currentCalendar.get(Calendar.MONTH)..11 step 1) {
            calendar.set(Calendar.MONTH, i)
            calendar.set(Calendar.YEAR, year)
            dates.add(calendar.timeInMillis)

//            val dayItemList = ArrayList<DayItem>()
//            val maximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

//            val range = 1..maximum

//            Add week names
//            for (j in 1..7 step 1) {
//                dayItemList.add(DayItem((-1 * j), true, -1))
//            }

//            Get previous days
//            for (j in range step 1) {
//                if (j == 1 && SimpleDateFormat("u", Locale.getDefault()).format(calendar.time)
//                        .toInt() != 1
//                ) {
//                    val o = SimpleDateFormat("u", Locale.getDefault()).format(calendar.time).toInt()
//                    calendar.set(Calendar.MONTH, -i)
//                    val m = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
//                    for (k in (m - (o - 3))..m step 1) {
//                        dayItemList.add(DayItem(k, false, -1))
//                    }
//                }
//
//                calendar.set(Calendar.MONTH, i)
//                calendar.set(Calendar.YEAR, year)
//
////              Setup current day
//                if (currentCalendar.get(Calendar.DAY_OF_MONTH) == j &&
//                    calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
//                    currentCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
//                ) {
//                    dayItemList.add(DayItem(j, true, Color.parseColor("#8CF7C3")))
//                } else
//                    dayItemList.add(DayItem(j, true, 0))

//                calendar.set(Calendar.MONTH, i)
//                calendar.set(Calendar.YEAR, year)
//                dates.add(calendar.timeInMillis)
//            }

//            Add item to stack list
//            val name: String = SimpleDateFormat("LLLL", Locale.getDefault()).format(calendar.time)
//            if (i >= calendar.get(Calendar.MONTH))
//                stackItemList.add(StackItem(dayItemList = dayItemList, month = Month(name, i)))
        }
//        stackView.adapter = StackAdapter(stackItemList, context, activity)

//        Clickers
//        stackView.onChangeListener = object : OnChangeListener {
//            override fun onChange(remainingCardsCount: Int, totalCardsCount: Int) {
//                if (remainingCardsCount == 0) {
//                    stackView.visibility = View.INVISIBLE
//                }
//            }
//        }

        imageViewAdd.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(context, AddDealActivity::class.java)
                intent.putExtra("date", currentCalendar.timeInMillis)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
        })

        textViewAdd.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(context, AddDealActivity::class.java)
                intent.putExtra("date", currentCalendar.timeInMillis)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
        })

//        changeYear.setOnClickListener {
//
//        }

//        Read database
        val deals = readValue(
            databaseHelper,
            currentCalendar.get(Calendar.MONTH),
            currentCalendar.get(Calendar.YEAR),
            currentCalendar.get(Calendar.DAY_OF_MONTH)
        )

//        ViewPager
        viewPager2Calendar?.adapter = ViewPagerCalendarAdapter(this, dates)
//        viewPager2?.adapter = ViewPagerAdapter(this, deals)

        recyclerViewDeals?.setHasFixedSize(true)
        recyclerViewDeals?.layoutManager = LinearLayoutManager(context)
        recyclerViewDeals?.adapter = DealAdapter(deals, context, activity)

        deleteRecentMonth(currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.YEAR))
    }

    private fun initSdk(context: Context) {
        MobileInstreamAds.setAdGroupPreloading(INSTREAM_AD_GROUP_PRELOADING_ENABLED)
        MobileAds.initialize(context) {

        }
        MobileAds.enableLogging(true)
    }

    companion object {
        const val INSTREAM_AD_GROUP_PRELOADING_ENABLED = true
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

    @SuppressLint("SetTextI18n")
    fun setTime(currentCalendar: Calendar, textView2: TextView?) {
        var pattern: String = ""

        pattern = if (currentCalendar.get(Calendar.DAY_OF_MONTH) < 10) {
            "d"
        } else {
            "dd"
        }

        textView2?.text = SimpleDateFormat(
            pattern,
            Locale.getDefault()
        ).format(currentCalendar.time) + " " + SimpleDateFormat("MMMM", Locale.getDefault()).format(
            currentCalendar.time
        ).substring(0, 1).toUpperCase() + SimpleDateFormat("MMMM", Locale.getDefault()).format(
            currentCalendar.time
        ).substring(
            1,
            SimpleDateFormat("MMMM", Locale.getDefault()).format(currentCalendar.time).length
        )
    }

    fun deleteRecentMonth(rootMonth: Int, rootYear: Int) {

        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.YEAR, rootYear)
        cal.set(Calendar.MONTH, rootMonth)
        cal.add(Calendar.MONTH, -1)

        val db1 = databaseHelper?.readableDatabase
        val db2 = DealDatabaseEntry.ColorDatabaseHelper(this).readableDatabase

        val where1 = DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_MONTH + " = ? AND " +
                DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_YEAR + " = ?"
        val args1 = arrayOf(cal.get(Calendar.MONTH).toString(), cal.get(Calendar.YEAR).toString())

        val where2 = DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_MONTH + " = ? AND " +
                DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_YEAR + " = ?"
        val args2 = arrayOf(cal.get(Calendar.MONTH).toString(), cal.get(Calendar.YEAR).toString())

        db1?.delete(DealDatabaseEntry.DealDatabaseConstants.TABLE_NAME, where1, args1)
        db2?.delete(DealDatabaseEntry.DealDatabaseConstants.TABLE_NAME_2, where2, args2)
    }
}