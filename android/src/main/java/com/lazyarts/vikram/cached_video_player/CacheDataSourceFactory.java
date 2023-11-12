package com.lazyarts.vikram.cached_video_player;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.media3.datasource.DataSource;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;

import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.FileDataSource;
import androidx.media3.datasource.cache.CacheDataSink;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.SimpleCache;

import java.util.Map;

class CacheDataSourceFactory implements DataSource.Factory {
    private final Context context;
    private final long maxFileSize, maxCacheSize;

    private final DefaultHttpDataSource.Factory defaultHttpDataSourceFactory;

    CacheDataSourceFactory(Context context, long maxCacheSize, long maxFileSize) {
        super();
        this.context = context;
        this.maxCacheSize = maxCacheSize;
        this.maxFileSize = maxFileSize;

        defaultHttpDataSourceFactory = new DefaultHttpDataSource.Factory();
        defaultHttpDataSourceFactory.setUserAgent("ExoPlayer");
        defaultHttpDataSourceFactory.setAllowCrossProtocolRedirects(true);
    }

    void setHeaders(Map<String, String> httpHeaders) {
        defaultHttpDataSourceFactory.setDefaultRequestProperties(httpHeaders);
    }

    @NonNull
    @Override
    public DataSource createDataSource() {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(context).build();

        DefaultDataSource.Factory defaultDatasourceFactory = new DefaultDataSource.Factory(this.context, defaultHttpDataSourceFactory);
        defaultDatasourceFactory.setTransferListener(bandwidthMeter);

        SimpleCache simpleCache = SimpleCacheSingleton.getInstance(context, maxCacheSize).simpleCache;
        return new CacheDataSource(simpleCache, defaultDatasourceFactory.createDataSource(),
                new FileDataSource(), new CacheDataSink(simpleCache, maxFileSize),
                CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);
    }

}