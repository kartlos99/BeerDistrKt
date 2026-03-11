package com.example.beerdistrkt.adapters

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.beerdistrkt.fragPages.statement.presentation.FinanceStatementFragment
import com.example.beerdistrkt.fragPages.statement.presentation.barrels.BarrelsIoFragment

class StatementPagesAdapter(
    private val fm: FragmentManager,
    lifecycle: Lifecycle,
    private val customerID: Int,
) : FragmentStateAdapter(fm, lifecycle) {

    val fragments = SparseArray<Fragment>()

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FinanceStatementFragment.newInstance(customerID).also {
                fragments.set(position, it)
            }

            else -> BarrelsIoFragment.newInstance(customerID).also {
                fragments.set(position, it)
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getBarrelStatementFragment(): BarrelsIoFragment? {
        return fragments.get(1) as? BarrelsIoFragment
    }

    fun getFinanceFragment(): FinanceStatementFragment? {
        return fragments.get(0) as? FinanceStatementFragment
    }
}
