package com.dicoding.storyappdicoding

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.storyappdicoding.data.local.StoryDatabase
import com.dicoding.storyappdicoding.data.local.database.RemoteKeys
import com.dicoding.storyappdicoding.data.remote.response.ListStoryItem
import com.dicoding.storyappdicoding.data.remote.retrofit.ApiService
import com.dicoding.storyappdicoding.model.UserPreference

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreference
): RemoteMediator<Int, ListStoryItem>() {
    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, ListStoryItem>): RemoteKeys?{
        return state.pages.firstOrNull{it.data.isNotEmpty()}?.data?.firstOrNull()?.let { data ->
            storyDatabase.getRemoteKeysDao().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getLastRemoteKey(state: PagingState<Int, ListStoryItem>): RemoteKeys?{
        return state.pages.lastOrNull{it.data.isNotEmpty()}?.data?.lastOrNull()?.let { data ->
            storyDatabase.getRemoteKeysDao().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getClosestRemoteKey(state: PagingState<Int, ListStoryItem>): RemoteKeys?{
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDatabase.getRemoteKeysDao().getRemoteKeysId(id)
            }
        }
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page : Int = when(loadType){
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys  != null)
                prevKey
            }
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys  != null)
                nextKey
            }
        }

        try {
            val responseData = apiService
                .getAllStories("Bearer ${userPreference.getUser().token}", page, state.config.pageSize, 0)
                .listStory
            val endOfPaginationReached =responseData.isEmpty()

            storyDatabase.withTransaction {
                if(loadType == LoadType.REFRESH){
                    storyDatabase.getRemoteKeysDao().deleteRemoteKeys()
                    storyDatabase.getStoryDao().deleteAll()
                }

                val prevKey = if(page == 1) null else page - 1
                val nextKey = if(endOfPaginationReached) null else page + 1
                val keys = responseData.map{
                    RemoteKeys(id = it.id, prevKey = prevKey,nextKey = nextKey)
                }
                storyDatabase.getRemoteKeysDao().insertAll(keys)
                storyDatabase.getStoryDao().insertStory(responseData)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }catch (e: java.lang.Exception){
            return MediatorResult.Error(e)
        }
    }
}