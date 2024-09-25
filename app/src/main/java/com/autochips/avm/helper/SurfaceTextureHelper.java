package com.autochips.avm.helper;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;

import me.goldze.mvvmhabit.utils.KLog;

/**
 * 摄像机渲染
 */
public class SurfaceTextureHelper implements TextureView.SurfaceTextureListener {
    private Surface mSurface;

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width,
                                          int height) {
        KLog.d(width + " onSurfaceTextureAvailable  " + height);
        surfaceTexture.setDefaultBufferSize(width, height);
        mSurface = new Surface(surfaceTexture);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width,
                                            int height) {
        KLog.d(width + " onSurfaceTextureSizeChanged  " + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        KLog.d("onSurfaceTextureDestroyed");
        mSurface = null;
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        KLog.d("onSurfaceTextureUpdated");
    }
}
