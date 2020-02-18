package com.vikas.videoplayer;

import com.vikas.videoplayer.vimeo.VideoInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("{id}/config")
    Call <VideoInfo> getVideoInfo(@Path("id") String id);
}
