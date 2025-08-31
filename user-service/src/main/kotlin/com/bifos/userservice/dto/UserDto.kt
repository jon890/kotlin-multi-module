package com.bifos.userservice.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class UserDto(
        val id: Long? = null,
        val username: String,
        val email: String,
        val fullName: String,
        val isActive: Boolean = true,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") val createdAt: LocalDateTime? = null,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") val updatedAt: LocalDateTime? = null
)

data class CreateUserRequest(val username: String, val email: String, val fullName: String)

data class ApiResponse<T>(
        val success: Boolean,
        val message: String,
        val data: T? = null,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        val timestamp: LocalDateTime = LocalDateTime.now()
)
