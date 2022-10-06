package com.imtiaz.githubuserstest.presentation.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.imtiaz.githubuserstest.core.extensions.loadImage
import com.imtiaz.githubuserstest.databinding.ItemUsersBinding
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser

class UsersAdapter(private val onItemClick:(GithubUser) -> Unit) : RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

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
        holder.bind(differ.currentList[position])

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(list: List<GithubUser>) = differ.submitList(list)

    inner class MyViewHolder(binding: ItemUsersBinding) : RecyclerView.ViewHolder(binding.root) {
        private val _binding: ItemUsersBinding = binding

        init {
            itemView.setOnClickListener {
                onItemClick(differ.currentList[adapterPosition])
            }
        }

        fun bind(user: GithubUser) {
            _binding.apply {
                textUserName.text = user.login ?: ""
                imgUser.loadImage(user.avatarUrl)
            }
        }
    }
}
