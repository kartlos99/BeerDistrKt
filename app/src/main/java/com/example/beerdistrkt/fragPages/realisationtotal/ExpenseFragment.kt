package com.example.beerdistrkt.fragPages.realisationtotal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.common.adapter.SimpleDataAdapter
import com.example.beerdistrkt.common.helper.LinearDividerItemDecoration
import com.example.beerdistrkt.databinding.FragmentExpenseBinding
import com.example.beerdistrkt.fragPages.expense.domain.model.Expense
import com.example.beerdistrkt.fragPages.expense.presentation.view.ExpenseItemView
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.models.DeleteRequest
import com.example.beerdistrkt.utils.Session
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class ExpenseFragment : BaseFragment<SalesViewModel>(), View.OnClickListener {

    override val viewModel: SalesViewModel by viewModels({ requireParentFragment() })

    private val binding by viewBinding(FragmentExpenseBinding::bind)

    var onClose: () -> Unit = {}
    var onTitleClick: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    fun initView() = with(binding) {
        fragExpenseAddItem.setOnClickListener(this@ExpenseFragment)
        fragExpenseClose.setOnClickListener(this@ExpenseFragment)
        fragExpenseTitle.setOnClickListener(this@ExpenseFragment)
        setupRecycler()
    }

    private fun setupRecycler() = with(binding.expenseRecycler) {
        val expenseSimpleDataAdapter = SimpleDataAdapter<Expense>(
            viewCreator = {
                ExpenseItemView(requireContext())
            },
            onBind = { item, view ->
                (view as? ExpenseItemView)?.let { itemView ->
                    itemView.setData(
                        expense = item,
                        author = viewModel.userMap[item.distributorID]!![0].username,
                        canDelete = canDeleteExpense()
                    )
                    itemView.onOptionClick = {
                        openExpenseDetails(it)
                    }
                }
            },
        )
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = expenseSimpleDataAdapter
        addItemDecoration(
            LinearDividerItemDecoration(
                divider = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.list_divider_line
                ),
                showFirstDivider = true,
                showLastDivider = true
            )
        )

        viewModel.expenseLiveData.observe(viewLifecycleOwner) {
            expenseSimpleDataAdapter.submitList(it)
        }
    }

    private fun initViewModel() {
        viewModel.usersLiveData.observe(viewLifecycleOwner) {
            viewModel.formUserMap(it)
        }
    }

    private fun canDeleteExpense() = Session.get().hasPermission(Permission.DeleteExpense) ||
            viewModel.selectedDayLiveData.value == dateFormatDash.format(Date())

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fragExpenseAddItem -> openExpenseDetails()
            R.id.fragExpenseClose -> onClose()
            R.id.fragExpenseTitle -> onTitleClick()
        }
    }

    private fun openExpenseDetails(expense: Expense? = null) {
        if (Session.get().hasPermission(Permission.AddExpenseInPast) || viewModel.isToday())
            findNavController().navigate(
                SalesFragmentDirections
                    .actionSalesFragmentToAddEditExpenseFragment(expense)
            )
        else
            showToast(R.string.cant_add_xarji)
    }

}