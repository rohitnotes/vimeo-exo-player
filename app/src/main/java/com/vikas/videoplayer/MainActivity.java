package com.vikas.videoplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoFrameMetadataListener;
import com.vikas.videoplayer.vimeo.Files;
import com.vikas.videoplayer.vimeo.Progressive;
import com.vikas.videoplayer.vimeo.VideoInfo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    ImageButton playerSettings;
    ProgressBar progressBar;
    List<String> mediaList;
    SimpleExoPlayer simpleExoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        try {
            initPlayer();
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        if(getWindow() != null)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleExoPlayer.stop();
        simpleExoPlayer.release();
        simpleExoPlayer = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            simpleExoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            simpleExoPlayer.setPlayWhenReady(false);
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        simpleExoPlayer.setPlayWhenReady(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleExoPlayer.setPlayWhenReady(true);
    }

    private void initPlayer() {
        try {
            PlayerView videoPlayer = findViewById(R.id.player_view);
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);
            videoPlayer.setPlayer(simpleExoPlayer);
            progressBar = findViewById(R.id.loader);
            playerSettings = findViewById(R.id.player_settings);
            playerSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenu(v);
                }
            });
            simpleExoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onLoadingChanged(boolean isLoading) {
                    if(isLoading) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if(playbackState == SimpleExoPlayer.DISCONTINUITY_REASON_SEEK_ADJUSTMENT) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    if(playbackState == simpleExoPlayer.STATE_READY) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
            simpleExoPlayer.setVideoFrameMetadataListener(new VideoFrameMetadataListener() {
                @Override
                public void onVideoFrameAboutToBeRendered(
                    long presentationTimeUs,
                    long releaseTimeNs,
                    Format format,
                    @Nullable MediaFormat mediaFormat
                ) {
                    // comments section
                }

            });

            playVideo("263034305");

            simpleExoPlayer.setPlayWhenReady(true);
            simpleExoPlayer.addListener(new Player.EventListener() {

                @Override
                public int hashCode() {
                    return super.hashCode();
                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }
            });

        } catch (Exception e) {
            Log.d("error", "initPlayer: " + e.getLocalizedMessage());
        }
    }

    private void setMediaQuality(String videoQuality) {
        Uri uri;
        if(videoQuality.equals("LOW")) {
            uri = Uri.parse(mediaList.get(0));
        } else if(videoQuality.equals("HIGH")) {
            uri = Uri.parse(mediaList.get(mediaList.size() - 2));
        } else {
            uri = Uri.parse(mediaList.get(mediaList.size() - 1));
        }
        simpleExoPlayer.setPlayWhenReady(false);
        long position = simpleExoPlayer.getContentPosition();
        simpleExoPlayer.stop();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "vikas"));
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.seekTo(position);
        simpleExoPlayer.setPlayWhenReady(true);

    }

    private void playVideo(String id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://player.vimeo.com/video/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<VideoInfo> videoInfoCall = apiInterface.getVideoInfo(id);
        
        videoInfoCall.enqueue(new Callback<VideoInfo>() {
            @Override
            public void onResponse(Call<VideoInfo> call, Response<VideoInfo> response) {
                if(response.isSuccessful()) {
                    try {
                        Files files = response.body().getRequest().getFiles();
                        if(files.getProgressive() != null) {
                            List<Progressive> progressiveList = files.getProgressive();
                            List<String> url = new ArrayList<>();
                            for(int i=0; i<progressiveList.size(); i++) {
                                url.add(progressiveList.get(i).getUrl());
                            }
                            if(url.size() > 0) {
                                setMediaPlayback(url);
                            }
                            else
                                Toast.makeText(MainActivity.this, "Error: Unable to fetch video!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Unable to play video!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoInfo> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Oops! Something went wrong." + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setMediaPlayback(List<String> url) {
        mediaList = url;
        setMediaQuality("NORMAL");
    }

    public void showMenu(View v)
    {
        PopupMenu popup = new PopupMenu(this, v);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.video_quality_high:
                        setMediaQuality("HIGH");
                        break;
                    case R.id.video_quality_medium:
                        setMediaQuality("NORMAL");
                        break;
                    case R.id.video_quality_low:
                        setMediaQuality("LOW");
                        break;
                }
                return false;
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.video_settings, popup.getMenu());
        popup.show();
    }

}
