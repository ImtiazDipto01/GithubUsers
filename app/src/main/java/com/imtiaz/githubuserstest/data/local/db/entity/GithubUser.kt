package com.imtiaz.githubuserstest.data.local.db.entity

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "user")
data class GithubUser(

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id") val id: Int,

    @ColumnInfo(name = "login") val login: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String? = null,
    @ColumnInfo(name = "node_id") val nodeId: String? = null,
    @ColumnInfo(name = "url") val url: String? = null,
    @ColumnInfo(name = "since") val since: Int = 0,

    @ColumnInfo(name = "name") val name: String? = null,
    @ColumnInfo(name = "location") val location: String? = null,
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "public_repos") val publicRepos: Int = 0,
    @ColumnInfo(name = "public_gists") val publicGists: Int = 0,
    @ColumnInfo(name = "followers") val followers: Int = 0,
    @ColumnInfo(name = "following") val following: Int = 0,
) : Parcelable
