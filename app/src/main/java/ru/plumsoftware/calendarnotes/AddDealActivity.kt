package ru.plumsoftware.calendarnotes

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.widget.*
import androidx.cardview.widget.CardView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.instream.MobileInstreamAds
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import ru.plumsoftware.calendarnotes.database.DealDatabaseEntry
import java.text.SimpleDateFormat
import java.util.*

class AddDealActivity : AppCompatActivity() {
    var databaseHelper: DealDatabaseEntry.DealDatabaseHelper =
        DealDatabaseEntry.DealDatabaseHelper(this)

    private var interstitialAd: InterstitialAd? = null
    private var adRequest: AdRequest? = null

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_deal)

//        Base variables
        val context: Context = this@AddDealActivity as Context
        val activity: Activity = this@AddDealActivity as Activity
        val currentCalendar: Calendar = Calendar.getInstance()
        val mTimePicker1: TimePickerDialog
        val mTimePicker2: TimePickerDialog
        val mCurrentTime = Calendar.getInstance()
        val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mCurrentTime.get(Calendar.MINUTE)
        var pattern: String = ""
        var selectedColor: Int = getColor(R.color.card1)
        var dealStart: Long = System.currentTimeMillis()
        var dealFinish: Long = -1
        val cal1: Calendar = Calendar.getInstance()
        val cal2: Calendar = Calendar.getInstance()
        adRequest = AdRequest.Builder().build()

        currentCalendar.timeInMillis = time

        initSdk(context)

//        Views
        val editTextTextMultiLineName: EditText = findViewById(R.id.editTextTextMultiLineName)
        val editTextTextMultiLineDesc: EditText = findViewById(R.id.editTextTextMultiLineDesc)

        val textSingle: TextView = findViewById(R.id.textSingle)
        val textInterval1: TextView = findViewById(R.id.textInterval1)
        val textInterval2: TextView = findViewById(R.id.textInterval2)
        val textViewTitle: TextView = findViewById(R.id.textViewTitle)

        val radioButtonInterval: RadioButton = findViewById(R.id.radioButtonInterval)
        val radioButtonSingle: RadioButton = findViewById(R.id.radioButtonSingle)

        val imageViewDone: ImageView = findViewById(R.id.imageViewDone)
        val imageViewBack: ImageView = findViewById(R.id.imageViewBack)

        val cardDeal: CardView = findViewById(R.id.cardDeal)
        val card1: CardView = findViewById(R.id.card1)
        val card2: CardView = findViewById(R.id.card2)
        val card3: CardView = findViewById(R.id.card3)
        val card4: CardView = findViewById(R.id.card4)
        val card5: CardView = findViewById(R.id.card5)
        val card6: CardView = findViewById(R.id.card6)

        mTimePicker1 = TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                cal1.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal1.set(Calendar.MINUTE, minute)
                if (radioButtonInterval.isChecked) {
                    textInterval1.text =
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal1.time)
                }
                if (radioButtonSingle.isChecked) {
                    textSingle.text =
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal1.time)
                }
            }
        }, hour, minute, true)
        mTimePicker2 = TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                if (radioButtonInterval.isChecked) {
                    cal2.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    cal2.set(Calendar.MINUTE, minute)
                    textInterval2.text =
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal2.time)
                }
            }
        }, hour, minute, true)

//        Setup data
        pattern = if (currentCalendar.get(Calendar.DAY_OF_MONTH) < 10) {
            "d"
        } else {
            "dd"
        }

        cardDeal.setCardBackgroundColor(getColor(R.color.card1))

        textInterval1.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(dealStart))
        textSingle.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(dealStart))
        val c: Calendar = Calendar.getInstance()
        c.add(Calendar.HOUR_OF_DAY, 1)
        textInterval2.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(c.time)

        textViewTitle.text = SimpleDateFormat(
            pattern,
            Locale.getDefault()
        ).format(currentCalendar.time) + " " + SimpleDateFormat(
            "MMMM",
            Locale.getDefault()
        ).format(
            currentCalendar.time
        ).substring(0, 1).toUpperCase() + SimpleDateFormat("MMMM", Locale.getDefault()).format(
            currentCalendar.time
        ).substring(
            1,
            SimpleDateFormat("MMMM", Locale.getDefault()).format(currentCalendar.time).length
        ) + " " + SimpleDateFormat("yyyy", Locale.getDefault()).format(currentCalendar.time)

//        Clickers
        imageViewBack.setOnClickListener {
            onBackPressed()
        }

        imageViewDone.setOnClickListener {
            dealStart = cal1.timeInMillis
            dealFinish = if (radioButtonInterval.isChecked)
                cal2.timeInMillis
            else
                -1
            putValue(
                editTextTextMultiLineName.text.toString(),
                editTextTextMultiLineDesc.text.toString(),
                selectedColor,
                System.currentTimeMillis(),
                dealStart,
                dealFinish,
                currentCalendar.get(Calendar.MONTH),
                currentCalendar.get(Calendar.DAY_OF_MONTH),
                currentCalendar.get(Calendar.YEAR)
            )
            putValue(
                selectedColor,
                currentCalendar.get(Calendar.MONTH),
                currentCalendar.get(Calendar.DAY_OF_MONTH),
                currentCalendar.get(Calendar.YEAR),
            )
            onBackPressed()
        }
        textInterval1.setOnClickListener {
            if (radioButtonInterval.isChecked)
                mTimePicker1.show()
        }
        textInterval2.setOnClickListener {
            if (radioButtonInterval.isChecked)
                mTimePicker2.show()
        }
        textSingle.setOnClickListener {
            if (radioButtonSingle.isChecked)
                mTimePicker1.show()
        }
        card1.setOnClickListener {
            cardDeal.setCardBackgroundColor(getColor(R.color.card1))
            selectedColor = getColor(R.color.card1)
        }
        card2.setOnClickListener {
            cardDeal.setCardBackgroundColor(getColor(R.color.card2))
            selectedColor = getColor(R.color.card2)
        }
        card3.setOnClickListener {
            cardDeal.setCardBackgroundColor(getColor(R.color.card3))
            selectedColor = getColor(R.color.card3)
        }
        card4.setOnClickListener {
            cardDeal.setCardBackgroundColor(getColor(R.color.card4))
            selectedColor = getColor(R.color.card4)
        }
        card5.setOnClickListener {
            cardDeal.setCardBackgroundColor(getColor(R.color.card5))
            selectedColor = getColor(R.color.card5)
        }
        card6.setOnClickListener {
            cardDeal.setCardBackgroundColor(getColor(R.color.card6))
            selectedColor = getColor(R.color.card6)
        }

        radioButtonSingle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) dealFinish = -1
        }
        radioButtonInterval.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) dealFinish = cal2.timeInMillis
        }
    }

    private fun putValue(
        name: String,
        desc: String,
        selectedColor: Int,
        dealAddTime: Long,
        dealStart: Long,
        dealFinish: Long,
        rootMonth: Int,
        rootDay: Int,
        rootYear: Int
    ) {
        val db = databaseHelper.readableDatabase

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

        val values = ContentValues()

        values.put(DealDatabaseEntry.DealDatabaseConstants.COLUMN_NAME_DEAL, name)
        values.put(
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_DESCRIPTION_DEAL,
            desc
        )
        values.put(
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_MODE_DEAL,
            selectedColor
        )
        values.put(
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_DEAL_ADD_TIME,
            dealAddTime
        )
        values.put(
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_DAY,
            rootDay
        )
        values.put(
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_MONTH,
            rootMonth
        )
        values.put(
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_YEAR,
            rootYear
        )
        values.put(
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_DEAL_START_TIME,
            dealStart
        )
        values.put(
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_DEAL_FINISH_TIME,
            dealFinish
        )

        val newRowId =
            db.insert(DealDatabaseEntry.DealDatabaseConstants.TABLE_NAME, null, values)

        db.close()
    }

    private fun putValue(
        color: Int,
        rootMonth: Int,
        rootDay: Int,
        rootYear: Int
    ) {
        val db = DealDatabaseEntry.ColorDatabaseHelper(this).readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_NAME_COLOR,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_MONTH,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_YEAR,
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_DAY
        )

        val values = ContentValues()


        values.put(
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_DAY,
            rootDay
        )
        values.put(
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_MONTH,
            rootMonth
        )
        values.put(
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_YEAR,
            rootYear
        )
        values.put(
            DealDatabaseEntry.DealDatabaseConstants.COLUMN_NAME_COLOR,
            color
        )

        val newRowId =
            db.insert(DealDatabaseEntry.DealDatabaseConstants.TABLE_NAME_2, null, values)

        db.close()
    }

    private fun initSdk(context: Context) {
        MobileInstreamAds.setAdGroupPreloading(MainActivity.INSTREAM_AD_GROUP_PRELOADING_ENABLED)
        MobileAds.initialize(context) {

        }
        MobileAds.enableLogging(true)
    }

    override fun onBackPressed() {
        val progressDialog = ProgressDialog(this)
        progressDialog.showDialog()
        interstitialAd = InterstitialAd(this)
        interstitialAd?.setAdUnitId("R-M-2180539-2")
//        interstitialAd?.setAdUnitId("demo-interstitial-yandex")
        interstitialAd?.setInterstitialAdEventListener(object : InterstitialAdEventListener{
            override fun onAdLoaded() {
                progressDialog.dismissDialog()
                interstitialAd?.show()
            }

            override fun onAdFailedToLoad(error: AdRequestError) {
                progressDialog.dismissDialog()
                start(this@AddDealActivity)
            }

            override fun onAdShown() {

            }

            override fun onAdDismissed() {
                progressDialog.dismissDialog()
                start(this@AddDealActivity)
            }

            override fun onAdClicked() {

            }

            override fun onLeftApplication() {

            }

            override fun onReturnedToApplication() {

            }

            override fun onImpression(data: ImpressionData?) {

            }
        })
        interstitialAd?.loadAd(adRequest!!)
    }

    override fun onDestroy() {
        databaseHelper.close()
        super.onDestroy()
    }

    fun start(context: Context){
        val i: Intent = Intent(context, MainActivity::class.java)
        startActivity(i)
        overridePendingTransition(0, 0)
        finish()
    }
}