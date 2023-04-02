package ru.plumsoftware.calendarnotes.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.nativeads.*
import ru.plumsoftware.calendarnotes.*
import ru.plumsoftware.calendarnotes.baseitems.Deal
import ru.plumsoftware.calendarnotes.database.DealDatabaseEntry
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DealAdapter(
    private val dealList: ArrayList<Deal>?,
    private val context: Context?,
    private val activity: FragmentActivity?
) : RecyclerView.Adapter<DealAdapter.DealViewHolder>() {
    class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardDeal: CardView = itemView.findViewById<CardView>(R.id.cardNote)
        val textViewNoteName: TextView = itemView.findViewById<TextView>(R.id.textViewNoteName)
        val textViewDescription: TextView =
            itemView.findViewById<TextView>(R.id.textViewDescription)
        val textViewDealTime: TextView = itemView.findViewById<TextView>(R.id.textViewDealTime)

//        Native ads
        val mNativeAdView =
            itemView.findViewById<android.view.View?>(R.id.nativeAdView) as NativeAdView?
        val mediaView = itemView.findViewById<android.view.View?>(R.id.media) as MediaView?
        val age = itemView.findViewById<android.view.View?>(R.id.age) as TextView?
        val bodyView = itemView.findViewById<android.view.View?>(R.id.tvAdvertiser) as TextView?
        val call_to_action =
            itemView.findViewById<android.view.View?>(R.id.btnVisitSite) as TextView?
        val domain = itemView.findViewById<android.view.View?>(R.id.textViewDomain) as TextView?
        val favicon =
            itemView.findViewById<android.view.View?>(R.id.adsPromo) as android.widget.ImageView?
        val imageViewFeedback =
            itemView.findViewById<android.view.View?>(R.id.imageViewFeedback) as android.widget.ImageView?
        val priceView = itemView.findViewById<android.view.View?>(R.id.priceView) as TextView?
        val storeView = itemView.findViewById<android.view.View?>(R.id.storeView) as TextView?
        val tvHeadline = itemView.findViewById<android.view.View?>(R.id.tvHeadline) as TextView?
        val warning = itemView.findViewById<android.view.View?>(R.id.textViewWarning) as TextView?
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.deal_item, parent, false)

        return DealViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val deal: Deal = dealList!![position]

//        Setup deal data
        holder.textViewNoteName.text = deal.name
        holder.textViewDescription.text = deal.description

        if (deal.dateTo != -1L) {
            holder.textViewDealTime.text = "С " +
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(deal.dateFrom) +
                    " до " +
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(deal.dateTo)
        } else {
            holder.textViewDealTime.text =
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(deal.dateFrom)
        }

        holder.cardDeal.setCardBackgroundColor(deal.dealMode)

        if (ads) {
            if (position == 1) {
                val progressDialog: ProgressDialog = ProgressDialog(context!!)
                progressDialog.showDialog()
                holder.mNativeAdView?.visibility = View.VISIBLE

                val nativeAdLoader: NativeAdLoader = NativeAdLoader(context)
                nativeAdLoader.setNativeAdLoadListener(object : NativeAdLoadListener,
                    NativeAdEventListener {
                    override fun onAdLoaded(nativeAd: NativeAd) {

                        val nativeAdViewBinder = nativeAd.run {
                            NativeAdViewBinder.Builder(holder.mNativeAdView!!)
                                .setAgeView(holder.age)
                                .setBodyView(holder.bodyView)
                                .setCallToActionView(holder.call_to_action)
                                .setDomainView(holder.domain)
                                .setFaviconView(holder.favicon)
                                .setFeedbackView(holder.imageViewFeedback)
//                                .setIconView(holder.i)
                                .setMediaView(holder.mediaView)
                                .setPriceView(holder.priceView)
//                                .setRatingView(holder.)
//                                .setReviewCountView(holder.re)
                                .setSponsoredView(holder.storeView)
                                .setTitleView(holder.tvHeadline)
                                .setWarningView(holder.warning)
                                .build()
                        }
                        progressDialog.dismissDialog()
                        try {
                            nativeAd.bindNativeAd(nativeAdViewBinder)
                            nativeAd.setNativeAdEventListener(this)
                            holder.mNativeAdView?.isVisible = true
                        } catch (exception: NativeAdException) {
                            exception.printStackTrace()
                        }
                    }

                    override fun onAdFailedToLoad(p0: AdRequestError) {
                        holder.mNativeAdView?.visibility = View.GONE
                        progressDialog.dismissDialog()
//                        Toast.makeText(context, p0.toString(), Toast.LENGTH_LONG).show()
                    }

                    override fun onAdClicked() {

                    }

                    override fun onLeftApplication() {

                    }

                    override fun onReturnedToApplication() {

                    }

                    override fun onImpression(p0: ImpressionData?) {

                    }

                })

                try {
                    nativeAdLoader.loadAd(
                        NativeAdRequestConfiguration
                            .Builder("R-M-2180539-3")
                            .setShouldLoadImagesAutomatically(true)
                            .build()
                    )
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    progressDialog.dismissDialog()
                }
            } else
                holder.mNativeAdView?.visibility = View.GONE
        } else
            holder.mNativeAdView?.visibility = View.GONE

        holder.cardDeal.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(view: View?): Boolean {
                showPopup(view, context!!, deal)
                return true
            }

        })
    }

    private fun showPopup(view: View?, context: Context, deal: Deal) {
        val wrapper: Context = ContextThemeWrapper(context, R.style.style_PopupMenu);
        val popup = PopupMenu(wrapper, view!!)
        popup.inflate(R.menu.deal_popup_menu)
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.remove -> {
                    val databaseHelper = DealDatabaseEntry.DealDatabaseHelper(context)
                    val databaseHelper2 = DealDatabaseEntry.ColorDatabaseHelper(context)
                    val db = databaseHelper.readableDatabase
                    val db2 = databaseHelper2.readableDatabase
                    val where: String = BaseColumns._ID + " = ?"
                    val args = arrayOf<String>(deal.id.toString())
                    db.delete(DealDatabaseEntry.DealDatabaseConstants.TABLE_NAME, where, args)
                    val where2: String =
                        DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_DAY + " = ? AND " +
                                DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_MONTH + " = ? AND " +
                                DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_YEAR + " = ? "

                    val args2 = arrayOf<String>(deal.rootDay.toString(), deal.rootDealMonth.toString(), deal.rootDealYear.toString())
                    db2.delete(DealDatabaseEntry.DealDatabaseConstants.TABLE_NAME_2, where2, args2)
                    dealList?.remove(deal)
                    recyclerViewDeals?.adapter = DealAdapter(dealList, context, activity)

                    val calendar: Calendar = Calendar.getInstance()
                    val currentCalendar: Calendar = Calendar.getInstance()
                    val dates = ArrayList<Long>()

                    for (i in currentCalendar.get(Calendar.MONTH)..11 step 1) {
                        calendar.set(Calendar.MONTH, i)
                        calendar.set(Calendar.YEAR, deal.rootDealYear)
                        dates.add(calendar.timeInMillis)
                    }

                    viewPager2Calendar?.adapter = ViewPagerCalendarAdapter(activity!!, dates)

                    db.close()
                    db2.close()
                }
                R.id.doneDeal -> {
                    val databaseHelper = DealDatabaseEntry.DealDatabaseHelper(context)
                    val databaseHelper2 = DealDatabaseEntry.ColorDatabaseHelper(context)
                    val db = databaseHelper.readableDatabase
                    val db2 = databaseHelper2.readableDatabase
                    val where: String = BaseColumns._ID + " = ?"
                    val args = arrayOf<String>(deal.id.toString())
                    db.delete(DealDatabaseEntry.DealDatabaseConstants.TABLE_NAME, where, args)
                    val where2: String =
                        DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_DAY + " = ? AND " +
                                DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_MONTH + " = ? AND " +
                                DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_YEAR + " = ? "

                    val args2 = arrayOf<String>(deal.rootDay.toString(), deal.rootDealMonth.toString(), deal.rootDealYear.toString())
                    db2.delete(DealDatabaseEntry.DealDatabaseConstants.TABLE_NAME_2, where2, args2)
                    dealList?.remove(deal)
                    recyclerViewDeals?.adapter = DealAdapter(dealList, context, activity)

                    val calendar: Calendar = Calendar.getInstance()
                    val currentCalendar: Calendar = Calendar.getInstance()
                    val dates = ArrayList<Long>()

                    for (i in currentCalendar.get(Calendar.MONTH)..11 step 1) {
                        calendar.set(Calendar.MONTH, i)
                        calendar.set(Calendar.YEAR, deal.rootDealYear)
                        dates.add(calendar.timeInMillis)
                    }

                    viewPager2Calendar?.adapter = ViewPagerCalendarAdapter(activity!!, dates)

                    db.close()
                    db2.close()
                }
            }

            true
        })
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        popup.show()
    }

    override fun getItemCount() = dealList?.size!!
}