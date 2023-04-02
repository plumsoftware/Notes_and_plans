package ru.plumsoftware.calendarnotes.baseitems

import android.os.Parcel
import android.os.Parcelable

data class Deal(
    val id: Int,
    val name: String,
    val description: String,
    val dateFrom: Long,
    val dateTo: Long,
    val dealAddTime: Long,
    val rootDealMonth: Int,
    val rootDealYear: Int,
    val rootDay: Int,
    val dealMode: Int) : Parcelable{

    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString().toString(),
        source.readString()!!,
        source.readLong(),
        source.readLong(),
        source.readLong(),
        source.readInt(),
        source.readInt(),
        source.readInt(),
        source.readInt()
    )


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.id)
        dest.writeString(this.name)
        dest.writeString(this.description)
        dest.writeLong(this.dateFrom)
        dest.writeLong(this.dateTo)
        dest.writeLong(this.dealAddTime)
        dest.writeInt(this.rootDealMonth)
        dest.writeInt(this.rootDealYear)
        dest.writeInt(this.rootDay)
        dest.writeInt(this.dealMode)
    }

    companion object{
        @JvmField val CREATOR: Parcelable.Creator<Deal> = object : Parcelable.Creator<Deal> {
            override fun createFromParcel(source: Parcel): Deal {
                return Deal(source)
            }

            override fun newArray(size: Int): Array<Deal?> {
                return arrayOfNulls(size)
            }
        }
    }
}
