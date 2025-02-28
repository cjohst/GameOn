package com.example.gameon.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
@Parcelize
data class GroupMember(
    val group_member_id: Int?, //autogenerated on server side
    val discord_id: String? = null,
    val user: User? = null,
    val group_id: Int?,
    val group: Group?,
    val joined_at: Date? = null, //autogenerated on server side
): Parcelable {
    init {
        require( discord_id != null || user != null ) {
            "Either discord_id or user must not be null"
        }
        require( group_id != null || group != null ) {
            "Either group_id or group must not be null"
        }
    }
}
