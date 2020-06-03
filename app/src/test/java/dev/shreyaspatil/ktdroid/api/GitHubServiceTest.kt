package dev.shreyaspatil.ktdroid.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@RunWith(JUnit4::class)
class GitHubServiceTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: GitHubService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .build()
            .create(GitHubService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun getUserByUsernameTest() = runBlocking {
        enqueueResponse("user.json")
        val username = "PatilShreyas"
        val user = service.getUser(username).body()

        assertThat(user?.username, equalTo(username))
    }

    @Test
    fun getRepositoriesByUserTest() = runBlocking {
        enqueueResponse("repositories.json")
        val repositories = service.getRepositoriesByUser("").body()

        assertThat(repositories?.size, equalTo(2))
        assertThat(
            repositories?.get(0)?.fullName,
            equalTo("PatilShreyas/android-dynamic-code-loading")
        )
        assertThat(
            repositories?.get(1)?.fullName,
            equalTo("PatilShreyas/android-testing")
        )
    }

    @Test
    fun getKtAndroidRepositoriesTest() = runBlocking {
        enqueueResponse("ktandroid_repositories.json")
        val repositories = service.getKtAndroidRepositories().body()?.items

        assertThat(repositories?.size, equalTo(2))
        assertThat(
            repositories?.get(0)?.fullName,
            equalTo("android/architecture-samples")
        )
        assertThat(
            repositories?.get(1)?.fullName,
            equalTo("shadowsocks/shadowsocks-android")
        )
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader!!
            .getResourceAsStream("api-response/$fileName")
        val source = inputStream.source().buffer()
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }
}