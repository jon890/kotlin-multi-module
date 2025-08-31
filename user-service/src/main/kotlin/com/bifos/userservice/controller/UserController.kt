package com.bifos.userservice.controller

import com.bifos.userservice.dto.ApiResponse
import com.bifos.userservice.dto.CreateUserRequest
import com.bifos.userservice.dto.UserDto
import com.bifos.userservice.service.UserService
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import jakarta.servlet.http.HttpServletRequest
import java.util.*
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    @WithSpan("UserController.getAllUsers")
    fun getAllUsers(request: HttpServletRequest): ResponseEntity<ApiResponse<List<UserDto>>> {
        val requestId = UUID.randomUUID().toString()

        // MDC에 요청 컨텍스트 추가
        MDC.put("requestId", requestId)
        MDC.put("endpoint", "GET /api/v1/users")
        MDC.put("clientIp", getClientIpAddress(request))

        logger.info("사용자 목록 조회 API 호출: requestId={}", requestId)

        Span.current().apply {
            setAttribute("http.method", "GET")
            setAttribute("http.route", "/api/v1/users")
            setAttribute("request.id", requestId)
        }

        return try {
            val users = userService.getAllUsers()
            val response = ApiResponse(success = true, message = "사용자 목록 조회 성공", data = users)

            logger.info("사용자 목록 조회 API 성공: requestId={}, userCount={}", requestId, users.size)

            ResponseEntity.ok(response)
        } catch (e: Exception) {
            logger.error("사용자 목록 조회 API 실패: requestId={}", requestId, e)
            Span.current().recordException(e)

            val response =
                    ApiResponse<List<UserDto>>(
                            success = false,
                            message = "사용자 목록 조회 실패: ${e.message}"
                    )

            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        } finally {
            MDC.clear()
        }
    }

    @GetMapping("/{id}")
    @WithSpan("UserController.getUserById")
    fun getUserById(
            @PathVariable id: Long,
            request: HttpServletRequest
    ): ResponseEntity<ApiResponse<UserDto>> {
        val requestId = UUID.randomUUID().toString()

        MDC.put("requestId", requestId)
        MDC.put("endpoint", "GET /api/v1/users/{id}")
        MDC.put("userId", id.toString())
        MDC.put("clientIp", getClientIpAddress(request))

        logger.info("사용자 조회 API 호출: requestId={}, userId={}", requestId, id)

        Span.current().apply {
            setAttribute("http.method", "GET")
            setAttribute("http.route", "/api/v1/users/{id}")
            setAttribute("user.id", id)
            setAttribute("request.id", requestId)
        }

        return try {
            val user = userService.getUserById(id)

            if (user != null) {
                val response = ApiResponse(success = true, message = "사용자 조회 성공", data = user)

                logger.info(
                        "사용자 조회 API 성공: requestId={}, userId={}, username={}",
                        requestId,
                        id,
                        user.username
                )

                ResponseEntity.ok(response)
            } else {
                val response = ApiResponse<UserDto>(success = false, message = "사용자를 찾을 수 없습니다")

                logger.warn("사용자 조회 API - 사용자 없음: requestId={}, userId={}", requestId, id)

                ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
            }
        } catch (e: Exception) {
            logger.error("사용자 조회 API 실패: requestId={}, userId={}", requestId, id, e)
            Span.current().recordException(e)

            val response =
                    ApiResponse<UserDto>(success = false, message = "사용자 조회 실패: ${e.message}")

            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        } finally {
            MDC.clear()
        }
    }

    @PostMapping
    @WithSpan("UserController.createUser")
    fun createUser(
            @RequestBody request: CreateUserRequest,
            servletRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<UserDto>> {
        val requestId = UUID.randomUUID().toString()

        MDC.put("requestId", requestId)
        MDC.put("endpoint", "POST /api/v1/users")
        MDC.put("username", request.username)
        MDC.put("clientIp", getClientIpAddress(servletRequest))

        logger.info(
                "사용자 생성 API 호출: requestId={}, username={}, email={}",
                requestId,
                request.username,
                request.email
        )

        Span.current().apply {
            setAttribute("http.method", "POST")
            setAttribute("http.route", "/api/v1/users")
            setAttribute("user.username", request.username)
            setAttribute("request.id", requestId)
        }

        return try {
            val user = userService.createUser(request)
            val response = ApiResponse(success = true, message = "사용자 생성 성공", data = user)

            logger.info(
                    "사용자 생성 API 성공: requestId={}, userId={}, username={}",
                    requestId,
                    user.id,
                    user.username
            )

            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: Exception) {
            logger.error("사용자 생성 API 실패: requestId={}, username={}", requestId, request.username, e)
            Span.current().recordException(e)

            val response =
                    ApiResponse<UserDto>(success = false, message = "사용자 생성 실패: ${e.message}")

            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        } finally {
            MDC.clear()
        }
    }

    @DeleteMapping("/{id}")
    @WithSpan("UserController.deleteUser")
    fun deleteUser(
            @PathVariable id: Long,
            request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val requestId = UUID.randomUUID().toString()

        MDC.put("requestId", requestId)
        MDC.put("endpoint", "DELETE /api/v1/users/{id}")
        MDC.put("userId", id.toString())
        MDC.put("clientIp", getClientIpAddress(request))

        logger.info("사용자 삭제 API 호출: requestId={}, userId={}", requestId, id)

        Span.current().apply {
            setAttribute("http.method", "DELETE")
            setAttribute("http.route", "/api/v1/users/{id}")
            setAttribute("user.id", id)
            setAttribute("request.id", requestId)
        }

        return try {
            val deleted = userService.deleteUser(id)

            if (deleted) {
                val response = ApiResponse<Unit>(success = true, message = "사용자 삭제 성공")

                logger.info("사용자 삭제 API 성공: requestId={}, userId={}", requestId, id)

                ResponseEntity.ok(response)
            } else {
                val response = ApiResponse<Unit>(success = false, message = "삭제할 사용자를 찾을 수 없습니다")

                logger.warn("사용자 삭제 API - 사용자 없음: requestId={}, userId={}", requestId, id)

                ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
            }
        } catch (e: Exception) {
            logger.error("사용자 삭제 API 실패: requestId={}, userId={}", requestId, id, e)
            Span.current().recordException(e)

            val response = ApiResponse<Unit>(success = false, message = "사용자 삭제 실패: ${e.message}")

            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        } finally {
            MDC.clear()
        }
    }

    @GetMapping("/error")
    @WithSpan("UserController.simulateError")
    fun simulateError(request: HttpServletRequest): ResponseEntity<ApiResponse<Unit>> {
        val requestId = UUID.randomUUID().toString()

        MDC.put("requestId", requestId)
        MDC.put("endpoint", "GET /api/v1/users/error")
        MDC.put("clientIp", getClientIpAddress(request))

        logger.info("에러 시뮬레이션 API 호출: requestId={}", requestId)

        Span.current().apply {
            setAttribute("http.method", "GET")
            setAttribute("http.route", "/api/v1/users/error")
            setAttribute("request.id", requestId)
        }

        return try {
            userService.simulateError()
            // 이 코드는 실행되지 않음
            ResponseEntity.ok(ApiResponse(success = true, message = "성공"))
        } catch (e: Exception) {
            logger.error("에러 시뮬레이션 API 실패: requestId={}", requestId, e)
            Span.current().recordException(e)

            val response = ApiResponse<Unit>(success = false, message = "시뮬레이션된 에러: ${e.message}")

            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        } finally {
            MDC.clear()
        }
    }

    private fun getClientIpAddress(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        if (!xForwardedFor.isNullOrBlank()) {
            return xForwardedFor.split(",")[0].trim()
        }

        val xRealIp = request.getHeader("X-Real-IP")
        if (!xRealIp.isNullOrBlank()) {
            return xRealIp
        }

        return request.remoteAddr ?: "unknown"
    }
}
