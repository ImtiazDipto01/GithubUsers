package com.imtiaz.githubuserstest.domain.usecase

import com.imtiaz.githubuserstest.core.extensions.State
import com.imtiaz.githubuserstest.data.local.dao.FakeUserDaoImp
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.repository.FakeUsersRepositoryImp
import com.imtiaz.githubuserstest.data.util.FETCH_AND_INSERT_USERS_SUCCESS
import com.imtiaz.githubuserstest.data.util.TestUtil.testTag
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * 1.Successful fetch user and Successful Insert
 *      - fetch and parse user response
 *      - insert note
 *      - confirm fetch user success
 *      - confirm insert user success
 *
 * 2. Bad Response and no data inserted
 *      - fetch and parse bad response
 *      - no insertion in db
 *      - confirm Bad response with error state
 *      - confirm no data inserted
 *
 *
 * @constructor Create empty Fetch users use case test
 */
@OptIn(InternalCoroutinesApi::class)
class FetchUsersUseCaseTest {

    // system in test
    private lateinit var fetchUsersUseCase: FetchUsersUseCase

    private lateinit var repository: UsersRepository
    private lateinit var dao: UserDao

    init {
        dao = FakeUserDaoImp()
        repository = FakeUsersRepositoryImp(dao)
        fetchUsersUseCase = FetchUsersUseCase(
            repository = repository
        )
    }

    @Test
    fun `fetch and insert users and confirm fetch and insert users is successful`() = runBlocking {
        testTag = FETCH_AND_INSERT_USERS_SUCCESS

        fetchUsersUseCase.execute(0).collect(object : FlowCollector<State<List<GithubUser>>>{
            override suspend fun emit(value: State<List<GithubUser>>) {
                assertTrue { (value as State.Success).data.isNotEmpty() }
                assertTrue { (value as State.Success).data.size == 3 }
            }
        })

        repository.getUsers().collect(object : FlowCollector<List<GithubUser>>{
            override suspend fun emit(value: List<GithubUser>) {
                assertTrue { value.size == 3 }
            }
        })
    }
}