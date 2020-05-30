package dev.shreyaspatil.ktdroid.ui.ktdroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import dev.shreyaspatil.ktdroid.databinding.ItemRepoBinding
import dev.shreyaspatil.ktdroid.model.Repository

class RepositoryAdapter(private val onClick: (String) -> Unit) :
    ListAdapter<Repository, RepositoryAdapter.RepoViewHolder>(
        DIFF_CALLBACK
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RepoViewHolder(
        ItemRepoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) =
        holder.bind(getItem(position), onClick)


    inner class RepoViewHolder(private val binding: ItemRepoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(repository: Repository, onClick: (String) -> Unit) {
            binding.run {
                repoTitle.text = repository.fullName
                repoDescription.text = repository.description
                repoOwner.text = repository.owner.name
                repoStars.text = repository.stars.toString()
                avatarView.load(repository.owner.avatarUrl)

                // OnClick callback
                root.setOnClickListener {
                    onClick(repository.repoUrl)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Repository>() {
            override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean =
                oldItem.repoUrl == newItem.repoUrl

            override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean =
                oldItem == newItem

        }
    }
}