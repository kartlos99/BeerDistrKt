package com.example.beerdistrkt.fragPages.realisationtotal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
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

    override val viewModel: SalesViewModel by viewModels({  requireParentFragment() })

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
        with(binding) {
            fragExpenseAddItem.setOnClickListener(this@ExpenseFragment)
            fragExpenseClose.setOnClickListener(this@ExpenseFragment)
            fragExpenseTitle.setOnClickListener(this@ExpenseFragment)
        }
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.usersLiveData.observe(viewLifecycleOwner) {
            viewModel.formUserMap(it)
        }
        viewModel.expenseLiveData.observe(viewLifecycleOwner) {
            onUpdate(it)
        }
    }

    private fun onUpdate(expenseList: List<Expense>) {
        if (expenseList == null) return
        binding.fragExpenseList.removeAllViews()
        val canDel = Session.get().hasPermission(Permission.DeleteExpense) ||
                viewModel.selectedDayLiveData.value == dateFormatDash.format(Date())

        Log.d("XARJEBI", expenseList.toString())
        expenseList.forEach { expense ->
            binding.fragExpenseList.addView(
                ExpenseItemView(requireContext()).apply {
                    setData(
                        expense,
                        viewModel.userMap[expense.distributorID]!![0].username,
                        canDel
                    )
                    onRemoveClick = { recID ->
                        viewModel.deleteExpense(
                            DeleteRequest(
                                recID,
                                "xarjebi",
                                Session.get().userID!!
                            )
                        )
                    }
                }
            )
        }
//            fragExpenseScroll.post {
//                fragExpenseScroll.smoothScrollTo(
//                    0,
//                    fragExpenseScroll.bottom
//                )
//            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fragExpenseAddItem -> addNewExpense()
            R.id.fragExpenseClose -> onClose()
            R.id.fragExpenseTitle -> onTitleClick()
        }
    }

    private fun addNewExpense() {
        if (Session.get().hasPermission(Permission.AddExpenseInPast) || viewModel.isToday())
            findNavController().navigate(R.id.action_salesFragment_to_addEditExpenseFragment)
        else
            showToast(R.string.cant_add_xarji)
    }
}