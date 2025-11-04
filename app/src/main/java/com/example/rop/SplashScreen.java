package com.example.rop;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    // Deklarácie dátových položiek pre prvky v užívateľskom rozhraní
    private VideoView videoView;
    private ImageView placeholderImage;
    private FrameLayout videoFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Pripojenie prvkov z užívateľského rozhrania k datovym polozkam
        videoView = findViewById(R.id.videoView);
        placeholderImage = findViewById(R.id.placeholderImage);
        videoFrameLayout = findViewById(R.id.videoFrameLayout);

        // Získanie URI videa z resource súboru
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.background_video;
        Uri uri = Uri.parse(videoPath);

        // Nastavenie URI videa pre VideoView
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> {
            // Nastavenie mierky videa a čakanie na začiatok prehrávania pre skrytie placeholdera
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            mp.setOnInfoListener((mp1, what, extra) -> {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    // video sa začalo, skryje placeholder
                    placeholderImage.setVisibility(View.GONE);
                    videoFrameLayout.setVisibility(View.GONE);
                    return true;
                }
                return false;
            });
            mp.setOnCompletionListener(mediaPlayer -> {
                // Prehratie videa ukončené. presmerovanie na obrazovku Login
                NavigationUtils.openLogin(SplashScreen.this);
                finish();
            });

            // Spustenie prehrávania videa
            mp.start();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}