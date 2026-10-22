package com.genialeast.mviarchitecture.core.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI 基类：Intent 进入，State 单向流出，Effect 只承载一次性事件。
 *
 * 页面修改状态的唯一入口是 [onIntent]。外部拿到的是只读 [uiState]。
 * 导航和 Toast 走 [effect]，不写进状态。
 *
 * @param S 不可变 UI 状态
 * @param I 用户意图
 * @param E 一次性副作用
 *
 * @author GenialSir
 * @date 2026-10-22
 */
abstract class MviViewModel<S, I, E>(initialState: S) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)

    /** 只读状态流，Compose 用 collectAsStateWithLifecycle 收集。 */
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    /**
     * 缓冲通道保证每条导航或 Toast 被消费一次。
     * 配置变化后界面重新收集时，缓冲里还没被取走的事件仍然在。
     */
    private val _effect = Channel<E>(capacity = Channel.BUFFERED)
    val effect: Flow<E> = _effect.receiveAsFlow()

    /** 处理意图时读取的当前状态快照。 */
    protected val currentState: S
        get() = _uiState.value

    /**
     * UI 唯一入口。界面不调用 setState，只把意图交进来。
     */
    fun onIntent(intent: I) {
        handleIntent(intent)
    }

    protected abstract fun handleIntent(intent: I)

    /**
     * Reducer：基于上一份不可变状态算出下一份，并通知界面重组。
     */
    protected fun setState(reducer: S.() -> S) {
        _uiState.update { it.reducer() }
    }

    /**
     * 发送一次性副作用。放在 viewModelScope 里，页面销毁后不再往已取消的界面投递。
     */
    protected fun sendEffect(effect: E) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
