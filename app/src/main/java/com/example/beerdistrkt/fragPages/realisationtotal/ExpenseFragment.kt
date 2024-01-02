package com.example.beerdistrkt.fragPages.realisationtotal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.customView.XarjiRowView
import com.example.beerdistrkt.databinding.FragmentExpenseBinding
import com.example.beerdistrkt.dialogs.XarjebiDialog
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.getActCtxViewModel
import com.example.beerdistrkt.models.DeleteRequest
import com.example.beerdistrkt.models.Xarji
import com.example.beerdistrkt.utils.Session
import java.util.*

class ExpenseFragment : BaseFragment<SalesViewModel>(), View.OnClickListener {

    override val viewModel: SalesViewModel by lazy { getActCtxViewModel() }

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

    private fun onUpdate(expenseList: List<Xarji>?) {
        if (expenseList == null) return
        binding.fragExpenseList.removeAllViews()
        val canDel = Session.get().hasPermission(Permission.DeleteExpense) ||
                viewModel.selectedDayLiveData.value == dateFormatDash.format(Date())

        Log.d("XARJEBI", expenseList.toString())
        expenseList.forEach {
            binding.fragExpenseList.addView(XarjiRowView(
                requireContext(),
                it,
                viewModel.userMap[it.distrID]!![0].username,
                canDel
            ) { recID ->
                viewModel.deleteExpense(
                    DeleteRequest(
                        recID,
                        "xarjebi",
                        Session.get().userID!!
                    )
                )
            })
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
            XarjebiDialog(requireContext()) { comment, amount ->
                if (amount.isEmpty()) {
                    showToast(getString(R.string.msg_empty_not_saved))
                } else {
                    try {
                        val am = amount.toFloat()
                        viewModel.addExpense(comment, am.toString())
                    } catch (e: NumberFormatException) {
                        Log.d(SalesFragment.TAG, e.toString())
                        showToast(getString(R.string.msg_invalid_format))
                    }
                }
            }.show(childFragmentManager, "expenseDialogTag")
        else
            showToast(R.string.cant_add_xarji)
    }
}