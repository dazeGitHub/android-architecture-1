package com.zj.architecture.mainscreen

import com.zj.architecture.repository.NewsItem
import com.zj.architecture.utils.FetchStatus

data class MainViewState(
    val fetchStatus: FetchStatus = FetchStatus.NotFetched,
    val newsList: List<NewsItem> = emptyList()
)

sealed class MainViewEvent {
    data class ShowSnackbar(val message: String) : MainViewEvent()
    data class ShowToast(val message: String) : MainViewEvent()
}

//Sealed class（密封类）和 枚举有点类似, 不同的是 枚举中每个类型只有一个对象, 而密封类中 每个类型可以拥有几个对象
//Sealed class（密封类）的所有子类都必须与密封类在同一文件中
sealed class MainViewAction {
    //创建名为 NewsItemClicked 的 data class 继承自 MainViewAction 类,
    //调用 MainViewAction 的无参构造函数, 调用 NewsItemClicked 类的带参构造函数
    data class NewsItemClicked(val newsItem: NewsItem) : MainViewAction()

    //object 的用法 :
    //对象声明 : 将类的声明和定义该类的单例对象结合在一起（即通过 object 就实现了单例模式）
    object FabClicked : MainViewAction()
    object OnSwipeRefresh : MainViewAction()
    object FetchNews : MainViewAction()
}