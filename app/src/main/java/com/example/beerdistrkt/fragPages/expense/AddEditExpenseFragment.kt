package com.example.beerdistrkt.fragPages.expense

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.AddEditExpenseFragmentBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditExpenseFragment : BaseFragment<AddEditExpenseViewModel>() {

    override val viewModel: AddEditExpenseViewModel by viewModels()

    override var frLayout: Int? = R.layout.add_edit_expense_fragment

    private val binding by viewBinding(AddEditExpenseFragmentBinding::bind)

    private val categories = listOf("asd", "dfk dkgh", "more it", "3456", "kide rame", "da","ase mere")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showToast("ASAS")
        categories.forEach {

            val chip = Chip(context).apply {
                text = it
                checkedIcon = resources.getDrawable(R.drawable.check_24, null)
                isCheckable = true
                isChecked = it == "da"
            }
            chip.text = it

            binding.categoryChipsGroup.addView(chip)
        }
    }
}