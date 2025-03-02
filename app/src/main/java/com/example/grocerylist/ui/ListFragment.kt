package com.example.grocerylist.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylist.R
import com.example.grocerylist.data.GroceryDatabase
import com.example.grocerylist.data.GroceryItem
import com.example.grocerylist.databinding.FragmentListBinding
import com.example.grocerylist.adapter.GroceryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GroceryAdapter
    private val groceryList: MutableList<GroceryItem> = mutableListOf()
    private val groceryDao by lazy { GroceryDatabase.getDatabase(requireContext()).groceryDao() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        setupRecyclerView()
        fetchGroceryItems()

        binding.fabAddItem.setOnClickListener { showAddItemDialog() }

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = GroceryAdapter(groceryList, ::showUpdateDialog, ::deleteItem)
        binding.recyclerViewList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewList.adapter = adapter

        // Show FAB when scrolling up, hide when scrolling down
        binding.recyclerViewList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.fabAddItem.hide() // Hide FAB when scrolling down
                } else if (dy < 0) {
                    binding.fabAddItem.show() // Show FAB when scrolling up
                }
            }
        })
    }

    private fun fetchGroceryItems() {
        lifecycleScope.launch(Dispatchers.IO) {
            val items = groceryDao.getAllItems()
            withContext(Dispatchers.Main) {
                groceryList.clear()
                groceryList.addAll(items)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun showUpdateDialog(item: GroceryItem) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_update_item, null)
        val editName = dialogView.findViewById<EditText>(R.id.editItemName)
        val editQuantity = dialogView.findViewById<EditText>(R.id.editItemQuantity)
        val editPrice = dialogView.findViewById<EditText>(R.id.editItemPrice)

        editName.setText(item.name)
        editQuantity.setText(item.quantity.toString())
        editPrice.setText(item.price.toString())

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Update Item")
            .setPositiveButton("Update") { _, _ ->
                val newName = editName.text.toString().trim()
                val newQuantity = editQuantity.text.toString().toIntOrNull() ?: item.quantity
                val newPrice = editPrice.text.toString().toDoubleOrNull() ?: item.price

                if (newName.isNotEmpty() && (newName != item.name || newQuantity != item.quantity || newPrice != item.price)) {
                    updateItem(item.copy(name = newName, quantity = newQuantity, price = newPrice))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateItem(updatedItem: GroceryItem) {
        lifecycleScope.launch(Dispatchers.IO) {
            groceryDao.updateItem(updatedItem)
            withContext(Dispatchers.Main) {
                val index = groceryList.indexOfFirst { it.id == updatedItem.id }
                if (index != -1) {
                    groceryList[index] = updatedItem
                    adapter.notifyItemChanged(index)
                }
            }
        }
    }

    private fun deleteItem(item: GroceryItem) {
        lifecycleScope.launch(Dispatchers.IO) {
            groceryDao.deleteItem(item)
            withContext(Dispatchers.Main) {
                val index = groceryList.indexOf(item)
                if (index != -1) {
                    groceryList.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }
            }
        }
    }

    private fun addGroceryItem(item: GroceryItem) {
        lifecycleScope.launch(Dispatchers.IO) {
            val id = groceryDao.insertItem(item)
            val newItem = item.copy(id = id.toString().toIntOrNull() ?: 0)
            withContext(Dispatchers.Main) {
                groceryList.add(newItem)
                adapter.notifyItemInserted(groceryList.size - 1)
            }
        }
    }

    private fun showAddItemDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_item, null)
        val editName = dialogView.findViewById<EditText>(R.id.editItemName)
        val editQuantity = dialogView.findViewById<EditText>(R.id.editItemQuantity)
        val editPrice = dialogView.findViewById<EditText>(R.id.editItemPrice)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Grocery Item")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = editName.text.toString().trim()
                val quantity = editQuantity.text.toString().toIntOrNull() ?: 0
                val price = editPrice.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isNotEmpty()) {
                    addGroceryItem(GroceryItem(name = name, quantity = quantity, price = price))
                } else {
                    Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}