
package com.example.grocerylist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylist.data.GroceryItem
import com.example.grocerylist.databinding.ItemGroceryBinding

class GroceryAdapter(
    private var items: MutableList<GroceryItem>,
    private val onItemClick: (GroceryItem) -> Unit,  // For update
    private val onDeleteClick: (GroceryItem) -> Unit // For delete
) : RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder>() {

    inner class GroceryViewHolder(private val binding: ItemGroceryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroceryItem) {
            binding.itemId.text = item.id.toString()
            binding.itemName.text = item.name
            binding.itemQuantity.text = item.quantity.toString()
            binding.itemPrice.text = "₹${String.format("%.2f", item.quantity * item.price)}"

            // Click to update
            binding.root.setOnClickListener {
                onItemClick(item)  // Calls update function
            }

            // Long click to delete
            binding.root.setOnLongClickListener {
                onDeleteClick(item)  // Calls delete function
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val binding = ItemGroceryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroceryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<GroceryItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}