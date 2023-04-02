package ru.plumsoftware.calendarnotes

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import ru.plumsoftware.calendarnotes.adapters.DealAdapter
import ru.plumsoftware.calendarnotes.baseitems.Deal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val ARG_OBJECT_SOURCE = "parcel_deal"
const val ARG_OBJECT_POSITION = "position"

class DealsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_deals, container, false)
    }

    private val calendar: Calendar = Calendar.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT_SOURCE) }?.apply {
            val recyclerViewDeals: RecyclerView =
                view.findViewById<RecyclerView>(R.id.recyclerDeals)
//            val textViewDealDate: TextView = view.findViewById<TextView>(R.id.textViewDealDate)

            val deal: Deal? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getParcelable(ARG_OBJECT_SOURCE, Deal::class.java)
            } else {
                getParcelable(ARG_OBJECT_SOURCE)
            }

            calendar.set(Calendar.MONTH, deal?.rootDealMonth ?: calendar.get(Calendar.MONTH))
//            textViewDealDate.text = SimpleDateFormat(
//                "dd",
//                Locale.getDefault()
//            ).format(calendar.time) + " " + SimpleDateFormat("MMMM", Locale.getDefault()).format(
//                calendar.time
//            ).substring(0, 1).toUpperCase() + SimpleDateFormat("MMMM", Locale.getDefault()).format(
//                calendar.time
//            ).substring(
//                1,
//                SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time).length
//            )

            val deals: kotlin.collections.ArrayList<Deal> = kotlin.collections.ArrayList<Deal>()
            deal?.let { deals.add(it) }

            recyclerViewDeals.setHasFixedSize(true)
            recyclerViewDeals.layoutManager = LinearLayoutManager(context)
            recyclerViewDeals.adapter = DealAdapter(deals, context, activity)
        }
    }
}