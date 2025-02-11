package com.zj.mvi.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import kotlin.reflect.KProperty1

//观察 LiveData<T> 中的 prop1 属性值变化, 如果变化了那么调用 action 回调函数
fun <T, A> LiveData<T>.observeState(
    lifecycleOwner: LifecycleOwner,
    prop1: KProperty1<T, A>,
    action: (A) -> Unit
) {
    //使用 LiveData 的扩展函数 .map{} 将一个类型的 LiveData 转换为 另一个类型的 LiveData
    this.map {
        //获取 LiveData 的 value 值的 prop1 属性的值, 然后创建 StateTuple1() 对象
        StateTuple1(prop1.get(it))
        //distinctUntilChanged() 防抖
    }.distinctUntilChanged().observe(lifecycleOwner) { (a) ->
        //当 prop1 属性发生改变时调用 action 回调方法
        action.invoke(a)
    }
}

fun <T, A, B> LiveData<T>.observeState(
    lifecycleOwner: LifecycleOwner,
    prop1: KProperty1<T, A>,
    prop2: KProperty1<T, B>,
    action: (A, B) -> Unit
) {
    this.map {
        StateTuple2(prop1.get(it), prop2.get(it))
    }.distinctUntilChanged().observe(lifecycleOwner) { (a, b) ->
        action.invoke(a, b)
    }
}

fun <T, A, B, C> LiveData<T>.observeState(
    lifecycleOwner: LifecycleOwner,
    prop1: KProperty1<T, A>,
    prop2: KProperty1<T, B>,
    prop3: KProperty1<T, C>,
    action: (A, B, C) -> Unit
) {
    this.map {
        StateTuple3(prop1.get(it), prop2.get(it), prop3.get(it))
    }.distinctUntilChanged().observe(lifecycleOwner) { (a, b, c) ->
        action.invoke(a, b, c)
    }
}

internal data class StateTuple1<A>(val a: A)
internal data class StateTuple2<A, B>(val a: A, val b: B)
internal data class StateTuple3<A, B, C>(val a: A, val b: B, val c: C)

fun <T> MutableLiveData<T>.setState(reducer: T.() -> T) {
    this.value = this.value?.reducer() //reducer : 缩减者
}

fun <T> SingleLiveEvents<T>.setEvent(vararg values: T) {
    this.value = values.toList()
}

fun <T> LiveEvents<T>.setEvent(vararg values: T) {
    this.value = values.toList()
}

fun <T> LiveData<List<T>>.observeEvent(lifecycleOwner: LifecycleOwner, action: (T) -> Unit) {
    this.observe(lifecycleOwner) {
        it.forEach { event ->
            action.invoke(event)
        }
    }
}

inline fun <T, R> withState(state: LiveData<T>, block: (T) -> R): R? {
    return state.value?.let(block)
}