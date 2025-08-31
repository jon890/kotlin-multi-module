package com.bifos.userservice.service

import com.bifos.userservice.dto.CreateUserRequest
import com.bifos.userservice.dto.UserDto
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service

@Service
class UserService {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val users = ConcurrentHashMap<Long, UserDto>()
    private var nextId = 1L

    init {
        // 샘플 데이터 초기화
        createInitialData()
    }

    @WithSpan("UserService.getAllUsers")
    fun getAllUsers(): List<UserDto> {
        logger.info("사용자 목록 조회 요청")

        // 현재 스팬에 속성 추가
        Span.current().apply {
            setAttribute("user.count", users.size.toLong())
            setAttribute("operation.type", "read")
        }

        // MDC에 컨텍스트 정보 추가
        MDC.put("operation", "getAllUsers")
        MDC.put("userCount", users.size.toString())

        val userList = users.values.toList()
        logger.info("사용자 목록 조회 완료: {} 명의 사용자", userList.size)

        // 구조화된 로그 예제
        logger.debug("사용자 목록 상세 정보: users={}", userList.map { it.id })

        MDC.clear()
        return userList
    }

    @WithSpan("UserService.getUserById")
    fun getUserById(id: Long): UserDto? {
        logger.info("사용자 조회 요청: userId={}", id)

        Span.current().apply {
            setAttribute("user.id", id)
            setAttribute("operation.type", "read")
        }

        MDC.put("operation", "getUserById")
        MDC.put("userId", id.toString())

        val user = users[id]

        if (user != null) {
            logger.info("사용자 조회 성공: userId={}, username={}", id, user.username)
            Span.current().setAttribute("user.found", true)
        } else {
            logger.warn("사용자를 찾을 수 없음: userId={}", id)
            Span.current().setAttribute("user.found", false)
        }

        MDC.clear()
        return user
    }

    @WithSpan("UserService.createUser")
    fun createUser(request: CreateUserRequest): UserDto {
        logger.info("사용자 생성 요청: username={}, email={}", request.username, request.email)

        Span.current().apply {
            setAttribute("user.username", request.username)
            setAttribute("user.email", request.email)
            setAttribute("operation.type", "create")
        }

        MDC.put("operation", "createUser")
        MDC.put("username", request.username)
        MDC.put("email", request.email)

        // 비즈니스 로직 처리 시뮬레이션
        processBusinessLogic()

        val now = LocalDateTime.now()
        val user =
                UserDto(
                        id = nextId++,
                        username = request.username,
                        email = request.email,
                        fullName = request.fullName,
                        createdAt = now,
                        updatedAt = now
                )

        users[user.id!!] = user

        logger.info("사용자 생성 완료: userId={}, username={}", user.id, user.username)

        // 메트릭 로그
        logger.info("metric:user_created counter=1 username={} email={}", user.username, user.email)

        MDC.clear()
        return user
    }

    @WithSpan("UserService.deleteUser")
    fun deleteUser(id: Long): Boolean {
        logger.info("사용자 삭제 요청: userId={}", id)

        Span.current().apply {
            setAttribute("user.id", id)
            setAttribute("operation.type", "delete")
        }

        MDC.put("operation", "deleteUser")
        MDC.put("userId", id.toString())

        val user = users[id]
        if (user != null) {
            users.remove(id)
            logger.info("사용자 삭제 완료: userId={}, username={}", id, user.username)
            Span.current().setAttribute("user.deleted", true)
            MDC.clear()
            return true
        } else {
            logger.warn("삭제할 사용자를 찾을 수 없음: userId={}", id)
            Span.current().setAttribute("user.deleted", false)
            MDC.clear()
            return false
        }
    }

    @WithSpan("UserService.simulateError")
    fun simulateError(): Nothing {
        logger.error("에러 시뮬레이션 시작")

        MDC.put("operation", "simulateError")

        try {
            // 의도적으로 예외 발생
            throw RuntimeException("시뮬레이션된 에러입니다")
        } catch (e: Exception) {
            logger.error("예외 발생: {}", e.message, e)
            Span.current().apply {
                recordException(e)
                setAttribute("error", true)
                setAttribute("error.type", e::class.java.simpleName)
            }
            MDC.clear()
            throw e
        }
    }

    private fun createInitialData() {
        logger.info("초기 데이터 생성 시작")

        val sampleUsers =
                listOf(
                        CreateUserRequest("john_doe", "john@example.com", "John Doe"),
                        CreateUserRequest("jane_smith", "jane@example.com", "Jane Smith"),
                        CreateUserRequest("bob_wilson", "bob@example.com", "Bob Wilson")
                )

        sampleUsers.forEach { request ->
            val user =
                    UserDto(
                            id = nextId++,
                            username = request.username,
                            email = request.email,
                            fullName = request.fullName,
                            createdAt = LocalDateTime.now(),
                            updatedAt = LocalDateTime.now()
                    )
            users[user.id!!] = user
            logger.debug("초기 사용자 생성: userId={}, username={}", user.id, user.username)
        }

        logger.info("초기 데이터 생성 완료: {} 명의 사용자", users.size)
    }

    private fun processBusinessLogic() {
        // 비즈니스 로직 처리 시뮬레이션
        val processingTime = Random.nextLong(50, 200)
        Thread.sleep(processingTime)

        logger.debug("비즈니스 로직 처리 완료: processingTime={}ms", processingTime)

        Span.current().setAttribute("processing.time.ms", processingTime)
    }
}
