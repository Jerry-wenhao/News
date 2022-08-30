package com.app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.app.NewsApplication
import com.app.R
import com.app.ui.activity.SearchActivity
import com.app.util.showToast
import com.google.android.material.tabs.TabLayout
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class HomeFragment : Fragment() {

    private val newsTypeList = listOf("shehui", "guoji", "yule", "keji", "tiyu", "caijing")
    private val titleList = listOf("社会", "国际", "娱乐", "科技", "体育", "财经")

    private val fragmentList = ArrayList<NewsFragment>()

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var toolbar: Toolbar
    private lateinit var editText: EditText
    private lateinit var avatar: CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        tabLayout = view.findViewById(R.id.news_tab_layout)
        viewPager = view.findViewById(R.id.news_view_pager)
        toolbar = view.findViewById(R.id.home_tool_bar)
        editText = view.findViewById(R.id.home_edit_text)
        avatar = view.findViewById(R.id.avatar)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.inflateMenu(R.menu.home_tool_bar_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.email -> "你点击了邮箱按钮".showToast()
                R.id.full_screen -> "全屏".showToast()
            }
            false
        }

        editText.keyListener = null

        editText.setOnClickListener {
            val intent = Intent(NewsApplication.context, SearchActivity::class.java)
            startActivity(intent)
        }

        avatar.setOnClickListener {
            "你点击了头像".showToast()
        }

        for (i in newsTypeList.indices) {
            fragmentList.add(NewsFragment(newsTypeList[i], titleList[i]))
        }

        viewPager.offscreenPageLimit = titleList.size
        viewPager.adapter = Adapter(childFragmentManager)

        tabLayout.setupWithViewPager(viewPager)

    }

    inner class Adapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titleList[position]
        }

    }
}