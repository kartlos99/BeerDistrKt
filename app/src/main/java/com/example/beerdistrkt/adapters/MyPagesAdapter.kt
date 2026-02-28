package com.example.beerdistrkt.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.statement.presentation.FinanceStatementFragment
import com.example.beerdistrkt.fragPages.statement.presentation.StatementSubPageFragment


class MyPagesAdapter(
    private val fm: FragmentManager,
    private val customerID: Int
) :
    androidx.fragment.app.FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var titles = arrayOf(" ფინანსები ", " კასრები ")

    private val mPagerFragments: MutableList<StatementSubPageFragment> = ArrayList()

    fun getDataFromAdapter(): List<StatementSubPageFragment> {
        return mPagerFragments
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FinanceStatementFragment.newInstance(customerID)
            else -> StatementSubPageFragment.newInstance(position, customerID)
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

    val fragmentK: StatementSubPageFragment?
        get() = fm.findFragmentByTag(makeFragmentTag(1))
                as? StatementSubPageFragment

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }

    private fun makeFragmentTag(id: Long): String {
        return "android:switcher:${R.id.statement_viewpager}:$id"
    }
}
