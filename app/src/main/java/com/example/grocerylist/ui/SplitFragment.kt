package com.example.grocerylist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.grocerylist.databinding.FragmentSplitBinding
import com.example.grocerylist.data.GroceryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplitFragment : Fragment() {

    private var _binding: FragmentSplitBinding? = null
    private val binding get() = _binding!!

    private var peopleCount = 1
    private var totalPrice = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSplitBinding.inflate(inflater, container, false)

        // Fetch total price from Room Database
        fetchTotalPrice()

        // Set initial values
        updateUI()

        // Increase button click
        binding.btnIncrease.setOnClickListener {
            peopleCount++
            updateUI()
        }

        // Decrease button click
        binding.btnDecrease.setOnClickListener {
            if (peopleCount > 1) {
                peopleCount--
                updateUI()
            }
        }

        return binding.root
    }

    private fun updateUI() {
        binding.txtPeopleCount.text = peopleCount.toString()
        binding.txtTotalPrice.text = "Total: ₹${String.format("%.2f", totalPrice)}"
        binding.txtSplitAmount.text = "Each Person Pays: ₹${String.format("%.2f", totalPrice / peopleCount)}"
    }

    private fun fetchTotalPrice() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = GroceryDatabase.getDatabase(requireContext())
            val total = db.groceryDao().getTotalPrice()
            withContext(Dispatchers.Main) {
                totalPrice = total // Removed ?: 0.0
                updateUI()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
