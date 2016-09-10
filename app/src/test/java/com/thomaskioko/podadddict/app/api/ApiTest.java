package com.thomaskioko.podadddict.app.api;


import com.thomaskioko.podadddict.app.api.model.ItunesLookUpResponse;
import com.thomaskioko.podadddict.app.api.model.PodCastPlaylistResponse;
import com.thomaskioko.podadddict.app.api.model.TopPodCastResponse;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;

import org.junit.Test;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Class to test all endpoints.
 *
 * @author Thomas Kioko
 */
public class ApiTest extends BaseTestCase {


    @Test
    public void getPaymentResponse() throws IOException {

        getApiClient().setEndpointUrl(ApplicationConstants.ITUNES_END_POINT);
        Call<TopPodCastResponse> serverPaymentResponseCall = getApiClient().iTunesServices()
                .getTopRatedPodCasts();

        Response<TopPodCastResponse> request = serverPaymentResponseCall.execute();

        assertEquals(request.code(), 200);
        assertEquals(true, request.isSuccessful());
        assertNotNull(request.body().getFeed().getAuthor().getName());
    }

    @Test
    public void getPodCastLookupResponse() throws  IOException{
        getApiClient().setEndpointUrl(ApplicationConstants.ITUNES_END_POINT);
        Call<ItunesLookUpResponse> itunesLookUpResponseCall = getApiClient().iTunesServices()
                .getLookUpResponse(TestData.LOOK_UP_ID);

        Response<ItunesLookUpResponse> itunesLookUpResponseResponse = itunesLookUpResponseCall.execute();
        assertEquals(itunesLookUpResponseResponse.code(), 200);
        assertEquals(true, itunesLookUpResponseResponse.isSuccessful());
    }

    @Test
    public void getPodCastPlayListResponse() throws  IOException{
        getApiClient().setEndpointUrl(ApplicationConstants.LOCAL_SERVER_END_POINT);
        Call<PodCastPlaylistResponse> itunesLookUpResponseCall = getApiClient().iTunesServices()
                .getPodCastPlaylistResponse(TestData.FEED_URL);

        Response<PodCastPlaylistResponse> itunesLookUpResponseResponse = itunesLookUpResponseCall.execute();
        assertEquals(itunesLookUpResponseResponse.code(), 200);
        assertEquals(true, itunesLookUpResponseResponse.isSuccessful());
    }
}
