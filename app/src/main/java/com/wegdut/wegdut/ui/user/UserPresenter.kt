package com.wegdut.wegdut.ui.user

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.data.user.UserRepository
import com.wegdut.wegdut.event.LoginEvent
import com.wegdut.wegdut.event.UserModificationEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class UserPresenter @Inject constructor() : UserContract.Presenter() {

    @Inject
    lateinit var userRepository: UserRepository
    private var firstSubscribe = true
    private var refreshing = false

    override fun refresh() {
        jobs.clear()
        refreshing = true
        userRepository.refresh()
        view?.setRefreshing(true)
        getExamPlanFromCache()
        getUser()
    }

    private fun getExamPlanFromCache() {
        view?.setExamPlanError(null)
        launch {
            tryIt({
                val plans = io { userRepository.getExamPlanFromCache() }
                view?.setExamPlan(plans)
            })
            getExamPlanFromApi()
        }
    }

    private fun getExamPlanFromApi() {
        launch {
            tryIt({
                val plans = io { userRepository.getExamPlanFromApi() }
                view?.setExamPlan(plans)
            }) {
                if (refreshing || firstSubscribe) {
                    firstSubscribe = false
                    val err = MyException.handle(it)
                    view?.setExamPlanError(err)
                }
            }
            if (refreshing) {
                view?.setRefreshing(false)
                refreshing = false
            }
        }
    }

    private fun getUser() {
        launch {
            tryIt({
                io { userRepository.getUser() }
            })
        }
    }

    override fun subscribe(view: UserContract.View) {
        super.subscribe(view)
        getExamPlanFromCache()
    }

    override fun start() {
        super.start()
        EventBus.getDefault().register(this)
    }

    override fun stop() {
        EventBus.getDefault().unregister(this)
        super.stop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(event: LoginEvent) {
        if (event.status == LoginEvent.Status.LOGIN)
            refresh()
        view?.updateUser()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserModificationEvent(event: UserModificationEvent) {
        view?.updateUser()
    }

}