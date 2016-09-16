package com.thomaskioko.podadddict.app.api.interfaces;

import com.thomaskioko.podadddict.app.api.model.ItunesLookUpResponse;
import com.thomaskioko.podadddict.app.api.model.PodCastPlaylistResponse;
import com.thomaskioko.podadddict.app.api.model.TopPodCastResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author Thomas Kioko
 */
public interface ItunesService {

    /**
     * Get Top rated PodCast from iTunes
     *
     * @return Json Response
     */
    @GET("us/rss/toppodcasts/limit=1/explicit=true/json")
    Call<TopPodCastResponse> getTopRatedPodCasts();

    /**
     * @param id This ID is required by iTunes when making a request. For more info
     *           {@see <href="https://affiliate.itunes.apple.com/resources/documentation/itunes-store-web-service-search-api/#lookup">}
     * @return Json Response
     */
    @GET("lookup/{id}")
    Call<ItunesLookUpResponse> getLookUpResponse(@Query("id") String id);

    /**
     * Endpoint to get PodCast playlist
     *
     * @param feedUrl PodCast playlist feed Url
     * @return Json Response
     */
    @FormUrlEncoded
    @POST("Pod-Adddict/")
    Call<PodCastPlaylistResponse> getPodCastPlaylistResponse(@Field("feedUrl") String feedUrl);
}
