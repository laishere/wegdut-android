package com.wegdut.wegdut.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wegdut.wegdut.MyApplication
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.user.LoginRepository
import com.wegdut.wegdut.event.UserModificationEvent
import com.wegdut.wegdut.ui.BaseTypeRVAdapter
import com.wegdut.wegdut.ui.ListRVAdapter
import com.wegdut.wegdut.ui.simple_html_page.SimpleHtmlPageActivity
import com.wegdut.wegdut.ui.student_verification.StudentVerificationCheckActivity
import com.wegdut.wegdut.ui.update.UpdateView
import com.wegdut.wegdut.ui.user_modification.UserModificationActivity
import com.wegdut.wegdut.utils.UIUtils
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class SettingActivity : DaggerAppCompatActivity() {
    private val adapter = SettingAdapter()
    private lateinit var recyclerView: RecyclerView
    private val updateView = UpdateView(this)

    @Inject
    lateinit var loginRepository: LoginRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        UIUtils.displayBackInToolbar(this, toolbar)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.items = makeItems()
        adapter.onItemClickListener = onItemClickListener
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        updateView.stop()
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserModificationEvent(event: UserModificationEvent) {
        adapter.items = makeItems()
        adapter.notifyDataSetChanged()
    }

    private val onItemClickListener = object : ListRVAdapter.OnItemClickListener {
        override fun onClick(view: View, pos: Int) {
            val item = adapter.items?.get(pos) ?: return
            if (item.data !is SettingItem) return
            when (item.data.type) {
                SettingType.ABOUT -> startSimpleHtml("关于", "about.html")
                SettingType.PRIVACY -> startSimpleHtml("隐私", "privacy.html")
                SettingType.LOGOUT -> logout()
                SettingType.STUDENT_VERIFICATION -> start(StudentVerificationCheckActivity::class.java)
                SettingType.USER -> start(UserModificationActivity::class.java)
                SettingType.UPDATE -> updateView.checkUpdate(quiet = false)
            }
        }
    }

    private fun logout() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                try {
                    loginRepository.logout()
                } catch (e: Throwable) {
                }
            }
            adapter.items = makeItems()
            adapter.notifyDataSetChanged()
            supportFinishAfterTransition()
        }
    }

    private fun startSimpleHtml(title: String, html: String) {
        val intent = Intent(this, SimpleHtmlPageActivity::class.java)
        intent.putExtra(SimpleHtmlPageActivity.TITLE, title)
        intent.putExtra(SimpleHtmlPageActivity.ASSET_HTML, html)
        startActivity(intent)
    }

    private fun start(clz: Class<*>) {
        val intent = Intent(this, clz)
        startActivity(intent)
    }

    private fun makeItems(): MutableList<BaseTypeRVAdapter.Item> {
        val list = mutableListOf<BaseTypeRVAdapter.Item>()
        val user = MyApplication.user
        if (user != null) {
            list.add(
                BaseTypeRVAdapter.Item(
                    0,
                    R.layout.item_setting_user,
                    SettingItem(user, SettingType.USER)
                )
            )
            list.add(BaseTypeRVAdapter.Item(0, R.layout.item_setting_divider))
            list.add(
                BaseTypeRVAdapter.Item(
                    0,
                    R.layout.item_setting_item,
                    SettingItem("学生认证", SettingType.STUDENT_VERIFICATION)
                )
            )
            list.add(BaseTypeRVAdapter.Item(0, R.layout.item_setting_divider))
        }
        list.addAll(
            listOf(
                BaseTypeRVAdapter.Item(
                    0,
                    R.layout.item_setting_item,
                    SettingItem("隐私相关", SettingType.PRIVACY)
                ),
                BaseTypeRVAdapter.Item(
                    0,
                    R.layout.item_setting_item,
                    SettingItem("关于APP", SettingType.ABOUT)
                ),
                BaseTypeRVAdapter.Item(
                    0,
                    R.layout.item_setting_item,
                    SettingItem("检查更新", SettingType.UPDATE)
                )
            )
        )
        if (user != null) {
            list.addAll(
                listOf(
                    BaseTypeRVAdapter.Item(0, R.layout.item_setting_divider),
                    BaseTypeRVAdapter.Item(
                        0,
                        R.layout.item_setting_logout,
                        SettingItem(null, SettingType.LOGOUT)
                    )
                )
            )
        }
        return list
    }

}