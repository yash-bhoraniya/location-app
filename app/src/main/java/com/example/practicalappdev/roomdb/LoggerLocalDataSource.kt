/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.practicalappdev.roomdb

import android.os.Handler
import android.os.Looper
import com.example.practicalappdev.models.Address
import com.example.practicalappdev.roomdb.dao.LogDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * Data manager class that handles data manipulation between the database and the UI.
 */
class LoggerLocalDataSource @Inject constructor(private val logDao: LogDao) {

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val mainThreadHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    fun addLog(areas: Address) {
        executorService.execute {
            logDao.insert(areas)
        }
    }


    fun getAddressList(callback: (MutableList<Address>) -> Unit) {
        executorService.execute {
            val logs = logDao.getAddress()
            mainThreadHandler.post { callback(logs) }
        }
    }

    fun getAscAddressList(callback: (MutableList<Address>) -> Unit) {
        executorService.execute {
            val logs = logDao.getAscAddress()
            mainThreadHandler.post { callback(logs) }
        }
    }

    fun getDescAddressList(callback: (MutableList<Address>) -> Unit) {
        executorService.execute {
            val logs = logDao.getDescAddress()
            mainThreadHandler.post { callback(logs) }
        }
    }


    fun editAddress(areas: Address){
        executorService.execute {
            logDao.updateAddress(areas)
        }
    }
    fun updateAddressById(itemId: String, newDistance: Double){
        executorService.execute {
            logDao.updateAddressById(itemId , newDistance)
        }
    }

    fun deleteAddress(id : Int){
        executorService.execute {
            logDao.deleteAddress(id)
        }
    }


}
