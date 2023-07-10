package com.bookmark.bookmark_oneday.domain.repository

import com.bookmark.bookmark_oneday.core.model.BaseResponse

interface DeleteBookRepository {
    suspend fun removeBook(bookId : String) : BaseResponse<Nothing>
    suspend fun removeAllBook() : BaseResponse<Nothing>
}