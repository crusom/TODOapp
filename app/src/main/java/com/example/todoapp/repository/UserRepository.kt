package com.example.todoapp.repository

import com.example.todoapp.data.UserDao
import com.example.todoapp.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    val readAllData: Flow<List<User>> = userDao.readAllData()

    suspend fun addUser(user: User){
        userDao.addUser(user)
    }

    suspend fun updateUser(user: User){
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User){
        userDao.deleteUser(user)
    }

    suspend fun deleteAllUsers(){
        userDao.deleteAllUsers()
    }

    suspend fun update(userList: List<User>){
        userDao.update(userList)
    }

    fun searchDatabase(searchQuery: String): Flow<List<User>>{
        return userDao.searchDatabase(searchQuery)
    }
}