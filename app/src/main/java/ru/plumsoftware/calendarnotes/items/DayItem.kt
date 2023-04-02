package ru.plumsoftware.calendarnotes.items

import android.os.Parcel
import android.os.Parcelable
import ru.plumsoftware.calendarnotes.baseitems.Day

class DayItem(
    var currentDay: Int,
    var currentMonth: Int,
    var currentYear: Int,
    var available: Boolean = true,
    var indicator: Int = -1
) : Day(currentDay, available), Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readInt(),
        source.readInt(),
        source.readBoolean(),
        source.readInt()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.currentDay)
        dest.writeInt(this.currentMonth)
        dest.writeInt(this.currentYear)
        dest.writeBoolean(this.available)
        dest.writeInt(this.indicator)
    }

    companion object CREATOR : Parcelable.Creator<DayItem> {
        override fun createFromParcel(parcel: Parcel): DayItem {
            return DayItem(parcel)
        }

        override fun newArray(size: Int): Array<DayItem?> {
            return arrayOfNulls(size)
        }
    }
}