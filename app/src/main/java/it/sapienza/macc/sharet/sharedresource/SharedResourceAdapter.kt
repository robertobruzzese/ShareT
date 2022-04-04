package it.sapienza.macc.sharet.sharedresource

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.sapienza.macc.sharet.R
import it.sapienza.macc.sharet.database.SharedResource
import it.sapienza.macc.sharet.databinding.ListItemSharedResourceBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
class SharedResourceAdapter(val clickListener: SharedResourceListener) : ListAdapter<SharedResource,
        SharedResourceAdapter.ViewHolder>(SharedResourceDiffCallback()) {


    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(clickListener, item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    class ViewHolder private constructor (val binding: ListItemSharedResourceBinding)
        : RecyclerView.ViewHolder(binding.root){

        fun bind(clickListener: SharedResourceListener, item: SharedResource) {
            binding.sharedResource = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }


    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemSharedResourceBinding.inflate(layoutInflater, parent, false)

            return ViewHolder(binding)
        }
    }
}


}

class SharedResourceDiffCallback : DiffUtil.ItemCallback<SharedResource>() {
    override fun areItemsTheSame(oldItem: SharedResource, newItem: SharedResource): Boolean {
        return oldItem.resourceId == newItem.resourceId
    }

    override fun areContentsTheSame(oldItem: SharedResource, newItem: SharedResource): Boolean {
        return oldItem == newItem
    }
}




class SharedResourceListener(val clickListener: (resourceId: Long) -> Unit) {
    fun onClick(resource: SharedResource) = clickListener(resource.resourceId)
}