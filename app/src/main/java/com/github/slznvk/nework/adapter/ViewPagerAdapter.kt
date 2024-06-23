package com.github.slznvk.nework.adapter

import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.github.slznvk.nework.ui.JobsFragment
import com.github.slznvk.nework.ui.UsersFragment.Companion.USER_ID
import com.github.slznvk.nework.ui.WallFragment

class ViewPagerAdapter(
    private val userId: Long,
    fragment: Fragment
) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> WallFragment.newInstance().apply {
            arguments = bundleOf(USER_ID to userId)
        }

        1 -> JobsFragment.newInstance().apply {
            arguments = bundleOf(USER_ID to userId)
        }

        else -> error("Wrong fragment position: $position")
    }
}