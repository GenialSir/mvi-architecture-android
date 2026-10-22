package com.genialeast.mviarchitecture.core.data.repository

import com.genialeast.mviarchitecture.core.data.local.LocalNewsDataSource
import com.genialeast.mviarchitecture.core.data.model.NewsArticle
import com.genialeast.mviarchitecture.core.data.model.NewsSource
import com.genialeast.mviarchitecture.core.data.remote.RemoteNewsDataSource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

/**
 * 一次读取的结果。请求的来源和实际命中的来源可能不同：网络失败时会回退本地。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
data class NewsLoad(
    val articles: List<NewsArticle>,
    val requested: NewsSource,
    val loadedFrom: NewsSource,
    val message: String = "",
)

/**
 * 资讯仓库。页面对外只拿 [NewsArticle]，本地和网络两条数据源都藏在这里。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
interface NewsRepository {
    suspend fun load(source: NewsSource, force: Boolean = false): NewsLoad
    suspend fun article(id: String): NewsArticle?
    suspend fun articlesByIds(ids: Set<String>): List<NewsArticle>
}

/**
 * 资讯仓库实现。网络结果和本地结果分开缓存，收藏和详情可以按 id 找回任一条。
 *
 * 选择网络时先打测试接口；失败或空列表则改读本地目录，并在 [NewsLoad.message] 里说明。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val localNewsDataSource: LocalNewsDataSource,
    private val remoteNewsDataSource: RemoteNewsDataSource,
) : NewsRepository {

    private val mutex = Mutex()
    private var localCache: List<NewsArticle>? = null
    private var remoteCache: List<NewsArticle>? = null
    private val byId = linkedMapOf<String, NewsArticle>()

    override suspend fun load(source: NewsSource, force: Boolean): NewsLoad = mutex.withLock {
        when (source) {
            NewsSource.LOCAL -> {
                val articles = readLocal(force)
                NewsLoad(articles, NewsSource.LOCAL, NewsSource.LOCAL)
            }
            NewsSource.REMOTE -> loadRemote(force)
        }
    }

    override suspend fun article(id: String): NewsArticle? = mutex.withLock {
        byId[id]?.let { return@withLock it }
        readLocal(force = false).firstOrNull { it.id == id }?.let { return@withLock it }
        readRemoteOrNull(force = false)?.firstOrNull { it.id == id }
    }

    override suspend fun articlesByIds(ids: Set<String>): List<NewsArticle> = mutex.withLock {
        if (ids.isEmpty()) return@withLock emptyList()
        if (byId.isEmpty()) {
            readLocal(force = false)
        }
        val missing = ids.any { it !in byId }
        if (missing) {
            readRemoteOrNull(force = false)
        }
        ids.mapNotNull { byId[it] }
    }

    private suspend fun loadRemote(force: Boolean): NewsLoad {
        val remote = readRemoteOrNull(force)
        if (!remote.isNullOrEmpty()) {
            return NewsLoad(remote, NewsSource.REMOTE, NewsSource.REMOTE)
        }
        val local = readLocal(force = false)
        return NewsLoad(
            articles = local,
            requested = NewsSource.REMOTE,
            loadedFrom = NewsSource.LOCAL,
            message = "网络接口不可用，已改用本地目录",
        )
    }

    private fun readLocal(force: Boolean): List<NewsArticle> {
        if (!force) {
            localCache?.let { return it }
        }
        return localNewsDataSource.load().also { remember(it, local = true) }
    }

    private suspend fun readRemoteOrNull(force: Boolean): List<NewsArticle>? {
        if (!force) {
            remoteCache?.let { return it }
        }
        return try {
            val articles = remoteNewsDataSource.load()
            if (articles.isEmpty()) {
                if (force) remoteCache = null
                null
            } else {
                remember(articles, local = false)
                articles
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            if (force) remoteCache = null
            null
        }
    }

    private fun remember(articles: List<NewsArticle>, local: Boolean) {
        if (local) {
            localCache = articles
        } else {
            remoteCache = articles
        }
        articles.forEach { byId[it.id] = it }
    }
}
