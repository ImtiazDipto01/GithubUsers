package com.imtiaz.githubuserstest.data.local.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.imtiaz.githubuserstest.core.extensions.PAGE_KEY

@Entity(tableName = "page")
data class Page(

  @PrimaryKey
  @ColumnInfo(name = "key")
  @NonNull
  val key: String = PAGE_KEY,

  @ColumnInfo(name = "since")
  val since: Int

)
