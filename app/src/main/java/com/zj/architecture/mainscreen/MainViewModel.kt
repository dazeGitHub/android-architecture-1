package com.zj.architecture.mainscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zj.architecture.repository.NewsItem
import com.zj.architecture.repository.NewsRepository
import com.zj.mvi.core.setEvent
import com.zj.mvi.core.setState
import com.zj.architecture.utils.FetchStatus
import com.zj.architecture.utils.PageState
import com.zj.architecture.utils.asLiveData
import com.zj.mvi.core.SingleLiveEvents
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {

    private var count: Int = 0
    private val repository: NewsRepository = NewsRepository.getInstance()

    //如果是网络请求返回的 状态 Status 和 数据 List, 使用 _viewStates
    private val _viewStates: MutableLiveData<MainViewState> = MutableLiveData(MainViewState())
    val viewStatesLiveData = _viewStates.asLiveData()

    //一些 UI 事件, 使用 _viewEvents
    private val _viewEvents: SingleLiveEvents<MainViewEvent> = SingleLiveEvents() //一次性的事件，与页面状态分开管理
    val viewEventsLiveData = _viewEvents.asLiveData()

    fun dispatch(viewAction: MainViewAction) {
        when (viewAction) {
            is MainViewAction.NewsItemClicked -> newsItemClicked(viewAction.newsItem)
            MainViewAction.FabClicked -> fabClicked()
            MainViewAction.OnSwipeRefresh -> fetchNews()
            MainViewAction.FetchNews -> fetchNews()
        }
    }

    private fun newsItemClicked(newsItem: NewsItem) {
        _viewEvents.setEvent(MainViewEvent.ShowSnackbar(newsItem.title))
    }

    private fun fabClicked() {
        count++
        _viewEvents.setEvent(MainViewEvent.ShowToast(message = "Fab clicked count $count"))
    }

    private fun fetchNews() {
        _viewStates.setState {
            copy(fetchStatus = FetchStatus.Fetching)
        }
        viewModelScope.launch {
            when (val result = repository.getMockApiResponse()) {
                is PageState.Error -> {
                    _viewStates.setState {
                        //_viewStates 的 value 值是 MainViewState 类型的,
                        //编译器会根据在 MainViewState 类的构造函数里声明的属性自动导出下列函数 :
                        //equals()/hashCode(), toString(), componentN(), copy()
                        //当要复制一个对象，只改变一些属性，但其余不变, 那么就使用 copy() 函数,
                        //例如 : 待copy对象.copy()
                        copy(fetchStatus = FetchStatus.Fetched)
                    }
                    _viewEvents.setEvent(MainViewEvent.ShowToast(message = result.message))
                }
                is PageState.Success -> {
                    _viewStates.setState {
                        copy(fetchStatus = FetchStatus.Fetched, newsList = result.data)
                    }
                }
            }
        }
    }
}