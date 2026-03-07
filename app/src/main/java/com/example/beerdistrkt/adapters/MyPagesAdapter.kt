package com.example.beerdistrkt.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.beerdistrkt.fragPages.statement.presentation.FinanceStatementFragment
import com.example.beerdistrkt.fragPages.statement.presentation.barrels.BarrelsIoFragment

class StatementPagesAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    private val customerID: Int,
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FinanceStatementFragment.newInstance(customerID)
            else -> BarrelsIoFragment.newInstance(customerID)
        }
    }


}

/*
class MyPagesAdapter(
    private val fm: FragmentManager,
    private val customerID: Int
) :
    androidx.fragment.app.FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var titles = arrayOf(" ფინანსები ", " კასრები ")

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FinanceStatementFragment.newInstance(customerID)
            else -> BarrelsIoFragment.newInstance(customerID)
        }
    }

    override fun getCount(): Int {
        return 2
    }

    fun setTitles(titles: Array<String>) {
        this.titles = titles
    }

    val fragmentM: FinanceStatementFragment?
        get() = fm.findFragmentByTag(makeFragmentTag(0))
                as? FinanceStatementFragment

    val fragmentK: BarrelsIoFragment?
        get() = fm.findFragmentByTag(makeFragmentTag(1))
                as? BarrelsIoFragment

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }

    private fun makeFragmentTag(id: Long): String {
        return "android:switcher:${R.id.statement_viewpager}:$id"
    }
}
*/
