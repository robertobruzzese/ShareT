package it.sapienza.macc.sharet.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "http://10.0.2.2:8080/shared-resources/"


/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object pointing to the desired URL
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

/**
 * A public interface that exposes the [getProperties] method
 */
interface SharedResourceApiService {
    /**
     * Returns a Retrofit callback that delivers a [List] of [MarsProperty]
     * The @GET annotation indicates that the "res_id" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("{res_id}?id=1")
    fun getProperties(): Deferred<SharedResourceProperty>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object SharedResourceApi {
    val retrofitService : SharedResourceApiService by lazy { retrofit.create(SharedResourceApiService::class.java) }
}