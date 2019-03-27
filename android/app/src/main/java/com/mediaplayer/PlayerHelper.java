package com.mediaplayer;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import static com.google.android.exoplayer2.Player.REPEAT_MODE_ALL;
import static com.google.android.exoplayer2.Player.STATE_READY;

public class PlayerHelper {
    public static final String TAG = "PlayerHelper";
    private Context mContext;
    private SimpleExoPlayer mSimpleExoPlayer;
    private MediaSource mediaSource;


    /**
     * 初始化player
     */
    public void initPlayer(Context context) {
        mContext = context;
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context);

        // Prepare the player with the source.
        mSimpleExoPlayer.setRepeatMode(REPEAT_MODE_ALL);
        mSimpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case STATE_READY:
                        Log.d(TAG, "onPlayerStateChanged: STATE_READY");
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }
        });
    }

    public void pause() {
        if (mSimpleExoPlayer == null) {
            Log.e(TAG, "pause: ", new Exception("需要先 init player"));
            return;
        }
        mSimpleExoPlayer.setPlayWhenReady(false);
    }

    public void play() {
        if (mSimpleExoPlayer == null) {
            Log.e(TAG, "play: ", new Exception("需要先 init player"));
            return;
        }
        mSimpleExoPlayer.setPlayWhenReady(true);
    }

    public void releasePlayer() {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.release();
        }
    }

    public void loadUri(Uri uri) {
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, mContext.getPackageName()));
        // This is the MediaSource representing the media to be played.
        mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        mSimpleExoPlayer.prepare(mediaSource);
    }

    public void loadAssets(String fileName) {
        DataSpec dataSpec = new DataSpec(Uri.parse("asset:///" + fileName));
        final AssetDataSource assetDataSource = new AssetDataSource(mContext);
        try {
            assetDataSource.open(dataSpec);
        } catch (AssetDataSource.AssetDataSourceException e) {
            e.printStackTrace();
        }
        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                //return rawResourceDataSource;
                return assetDataSource;
            }
        };
        mediaSource = new ExtractorMediaSource.Factory(factory).createMediaSource(assetDataSource.getUri());
        mSimpleExoPlayer.prepare(mediaSource);
    }
}
