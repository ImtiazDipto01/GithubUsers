package com.imtiaz.githubuserstest.presentation.user

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.imtiaz.githubuserstest.core.extensions.loadImage
import com.imtiaz.githubuserstest.core.extensions.loadImageAsBitmap
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.databinding.ItemUsersBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UsersAdapter(
    private val onItemClick: (GithubUser) -> Unit,
    private val onScrollUpdateData: (Int) -> Unit
) : RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    private var lastUpdatedSinceId = -1

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GithubUser>() {
        override fun areItemsTheSame(oldItem: GithubUser, newItem: GithubUser): Boolean {
            return oldItem.login == newItem.login
        }

        override fun areContentsTheSame(
            oldItem: GithubUser,
            newItem: GithubUser
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(
            ItemUsersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) =
        holder.bind(differ.currentList[position], position)

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(list: List<GithubUser>) = differ.submitList(list)

    fun clearList() = differ.submitList(null)

    inner class MyViewHolder(binding: ItemUsersBinding) : RecyclerView.ViewHolder(binding.root) {
        private val _binding: ItemUsersBinding = binding

        init {
            itemView.setOnClickListener {
                onItemClick(differ.currentList[adapterPosition])
            }
        }

        fun bind(user: GithubUser, position: Int) {
            _binding.apply {
                textUserName.text = user.login
                imgNote.isVisible = user.note != null

                if ((position + 1) % 4 == 0) {
                    CoroutineScope(Dispatchers.Main).launch {
                        imgUser.loadImageAsBitmap(user.avatarUrl)
                    }
                } else imgUser.loadImage(user.avatarUrl)
            }
            if (lastUpdatedSinceId != user.since) {
                lastUpdatedSinceId = user.since
                onScrollUpdateData(user.since)
            }
        }

    }
}
