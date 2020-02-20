package com.vikas.videoplayer;

import android.content.pm.ActivityInfo;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
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

    static boolean fullscreen = false;
    ImageButton playerSettings, fullScreen;
    FrameLayout playerContainer;
    ProgressBar progressBar;
    List<VideoTrack> videoTracks;
    List<Song> songs;
    SimpleExoPlayer simpleExoPlayer;
    private PopupMenu popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // store songs
        songs = new ArrayList<>();
        songs.add(new Song("Sooraj Duba Hai Yaron", "Arijit Singh", "115637298"));
        songs.add(new Song("Duaa live", "Arijit Singh", "116975711"));
        songs.add(new Song("Blank Space", "Someone Fool", "115076220"));
        songs.add(new Song("Numb Encore", "Linkin Park", "391036452"));
        songs.add(new Song("Arijit Royal Stag Performance", "Arijit Singh", "95594048"));

        // main code here
        playerContainer = findViewById(R.id.player_container);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initPlayer();
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
            fullScreen = findViewById(R.id.fullscreen_btn);
            fullScreen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fullscreen) {
                        exitFullScreen();
                    } else {
                        setFullScreen();
                    }
                }
            });
            final PlayerView videoPlayer = findViewById(R.id.player_view);
            videoPlayer.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    Toast.makeText(MainActivity.this, "Drag" + event.toString(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(MainActivity.this, new ScaleGestureDetector.OnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    if(detector.getScaleFactor() > 1.2) {
                        videoPlayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                    }
                    else if(detector.getScaleFactor() < 0.9) {
                        videoPlayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    }
                    return false;
                }

                @Override
                public boolean onScaleBegin(ScaleGestureDetector detector) {
                    return true;
                }

                @Override
                public void onScaleEnd(ScaleGestureDetector detector) {
                }


            });

            videoPlayer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    scaleGestureDetector.onTouchEvent(event);
                    return false;
                }
            });

            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);
            videoPlayer.setPlayer(simpleExoPlayer);
            progressBar = findViewById(R.id.loader);
            playerSettings = findViewById(R.id.player_settings);
            popup = new PopupMenu(this, playerSettings);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.video_settings, popup.getMenu());
            playerSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenu(v);
                }
            });

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
                    item.setChecked(true);
                    return false;
                }
            });
            simpleExoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onLoadingChanged(boolean isLoading) {
                    if (isLoading) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == SimpleExoPlayer.DISCONTINUITY_REASON_SEEK_ADJUSTMENT) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    if (playbackState == simpleExoPlayer.STATE_READY) {
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

            simpleExoPlayer.setPlayWhenReady(true);

            playVideo(songs.get(0).getVimeoId());

        } catch (Exception e) {
            Log.d("error", "initPlayer: " + e.getLocalizedMessage());
        }
    }

    private void setMediaQuality(String videoQuality) {
        Uri uri;
        if (videoQuality.equals("HIGH") && videoTracks.size() - 1 > 0) {
            uri = Uri.parse(videoTracks.get(videoTracks.size() - 1).getUrl());
        } else if (videoQuality.equals("MEDIUM") && videoTracks.size() - 2 > 0) {
            uri = Uri.parse(videoTracks.get(videoTracks.size() - 2).getUrl());
        } else if (videoQuality.equals("LOW")) {
            uri = Uri.parse(videoTracks.get(0).getUrl());
        } else {
            uri = Uri.parse(videoTracks.get(0).getUrl());
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
                if (response.isSuccessful()) {
                    try {
                        Files files = response.body().getRequest().getFiles();
                        if (files.getProgressive() != null) {
                            List<Progressive> progressiveList = files.getProgressive();
                            List<VideoTrack> videoTracks = new ArrayList<>();
                            for (int i = 0; i < progressiveList.size(); i++) {
                                videoTracks.add(new VideoTrack(progressiveList.get(i).getUrl(), progressiveList.get(i).getQuality()));
                            }
                            if (videoTracks.size() > 0) {
                                setMediaPlayback(videoTracks);
                            } else
                                Toast.makeText(MainActivity.this, "Error: Unable to fetch video!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Unable to play video!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoInfo> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Oops! Something went wrong." + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setFullScreen() {
        playerContainer.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        fullScreen.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.fullscreen_exit));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        fullscreen = true;
    }

    private void exitFullScreen() {
        float dpCalculation = getResources().getDisplayMetrics().density;
        playerContainer.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                (int) (250 * dpCalculation)
        ));
        fullScreen.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.fullscreen_open));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fullscreen = false;
    }

    private void setMediaPlayback(List<VideoTrack> videoTracks) {
        this.videoTracks = videoTracks;
        setMediaQuality("MEDIUM");
    }

    public void showMenu(View v) {
        popup.show();
    }

    @Override
    public void onBackPressed() {
        if (!fullscreen) super.onBackPressed();
        else exitFullScreen();
    }

    private class Song {
        private String title, artist, vimeoId;

        public Song(String title, String artist, String vimeoId) {
            this.title = title;
            this.artist = artist;
            this.vimeoId = vimeoId;
        }

        public String getArtist() {
            return artist;
        }

        public String getTitle() {
            return title;
        }

        public String getVimeoId() {
            return vimeoId;
        }
    }

    private class VideoTrack {
        private String url, quality;

        public VideoTrack(String url, String quality) {
            this.url = url;
            this.quality = quality;
        }

        public String getUrl() {
            return url;
        }

        public String getQuality() {
            return quality;
        }
    }
}
