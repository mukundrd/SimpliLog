package com.trayis.simplilogapp

import android.os.Parcel
import android.os.Parcelable

data class UserModel(val name: String, val username: String) : Parcelable {

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.let {
            it.writeString(name)
            it.writeString(username)
        }
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel.readString()!!, parcel.readString()!!)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}
