package com.example.etudiant.videoannottool;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.support.v4.content.ContextCompat;

import com.example.etudiant.videoannottool.adapter.AnnotationsAdapter;
import com.example.etudiant.videoannottool.adapter.VideosAdapter;
import com.example.etudiant.videoannottool.annotation.Annotation;
import com.example.etudiant.videoannottool.annotation.AudioAnnotation;
import com.example.etudiant.videoannottool.annotation.DrawAnnotation;
import com.example.etudiant.videoannottool.annotation.SlowMotionAnnotation;
import com.example.etudiant.videoannottool.annotation.TextAnnotation;
import com.example.etudiant.videoannottool.annotation.Video;
import com.example.etudiant.videoannottool.annotation.VideoAnnotation;
import com.example.etudiant.videoannottool.annotation.ZoomMotionAnnotation;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {
    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private MediaSource videoSource;
    private DefaultExtractorsFactory DataSourceFactory;

    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";

    private boolean ExoPlayerFullscreen = false;
    private FrameLayout FullScreenButton;
    private ImageView FullScreenIcon;
    private Dialog FullScreenDialog;

    private int ResumeWindow;
    private long ResumePosition;

    String videoName = "test"; // a modifié pour aller chercher le nom des video

    @Override
    protected void onCreate(Bundle savedInstanceState) {
   /*    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 2);
            }
        }
        else{


        }s


        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            ResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            ResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            ExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }

        ArrayList<Video> arrayOfVideos = new ArrayList<Video>();


        final VideoAnnotation videoAnnotations1;
        final VideoAnnotation videoAnnotations2;
        final VideoAnnotation videoAnnotations3;
        //ArrayList<Annotation> arrayOfAnnotations2 = new ArrayList<Annotation>();
        //ArrayList<Annotation> arrayOfAnnotations3 = new ArrayList<Annotation>();
        ArrayList<Annotation> arrayOfAnnotationsEmpty = new ArrayList<Annotation>();

        TextAnnotation annotation1 = new TextAnnotation("annotation1", null, 0, 0, null);
        TextAnnotation annotation2 = new TextAnnotation("annotation2", null, 0, 0, null);
        TextAnnotation annotation3 = new TextAnnotation("annotation3", null, 0, 0, null);

        ArrayList<TextAnnotation> arrayOfAnnotations1 = new ArrayList<>();

        arrayOfAnnotations1.add(annotation1);
        arrayOfAnnotations1.add(annotation2);
        arrayOfAnnotations1.add(annotation3);
        //arrayOfVideoAnnotations1.add(annotation2);
        //arrayOfVideoAnnotations1.add(annotation3);

        ArrayList<TextAnnotation> arrayOfAnnotations2 = new ArrayList<>();
        arrayOfAnnotations2.add(annotation2);
        arrayOfAnnotations2.add(annotation1);
        arrayOfAnnotations2.add(annotation3);

        ArrayList<TextAnnotation> arrayOfAnnotations3 = new ArrayList<>();
        arrayOfAnnotations3.add(annotation3);
        arrayOfAnnotations3.add(annotation2);
        arrayOfAnnotations3.add(annotation1);

        videoAnnotations1 = new VideoAnnotation(null, null, arrayOfAnnotations1, null, null, null, null);
        //videoAnnotations1.setTextAnnotationArrayList(arrayOfAnnotations1);

        videoAnnotations2 = new VideoAnnotation(null, null, arrayOfAnnotations2, null, null, null, null);
        //videoAnnotations2.setTextAnnotationArrayList(arrayOfAnnotations2);

        videoAnnotations3 = new VideoAnnotation(null, null, arrayOfAnnotations3, null, null, null, null);
        //videoAnnotations3.setTextAnnotationArrayList(arrayOfAnnotations3);

        List<Video> videoList = new ArrayList<>();
        Video video1 = new Video("test", null, videoAnnotations1);
        Video video2 = new Video("test2", null, videoAnnotations2);
        Video video3 = new Video("nom3", null, videoAnnotations3);
        videoList.add(video1);
        videoList.add(video2);
        videoList.add(video3);



        final VideosAdapter videosAdapter = new VideosAdapter(this, videoList);

        final AnnotationsAdapter annotationsAdapter = new AnnotationsAdapter(this, new ArrayList<>());
        final AnnotationsAdapter annotationsAdapter2 = new AnnotationsAdapter(this, video1.getAllAnnotationObject());

        final ListView listViewVideos = (ListView) findViewById(R.id.lv_videos);
        listViewVideos.setAdapter(videosAdapter);


        final ListView listViewAnnotations = (ListView) findViewById(R.id.lv_annotations);
        //listViewAnnotations.setAdapter(annotationsAdapter);

        //Spinner catégorie
        ArrayList<String> spinnerList = new ArrayList<String>();

        spinnerList.add("item1");
        spinnerList.add("item2");

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerAdapter);


        //Spinner sous-catégorie
        ArrayList<String> spinnerList2 = new ArrayList<String>();
        spinnerList2.add("item1");
        spinnerList2.add("item2");
        final Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);

        ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList2);
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner2.setAdapter(spinnerAdapter2);
        spinner2.setVisibility(View.GONE);
        /*
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                spinner2.setVisibility(View.VISIBLE);


            }
        });

        */



        listViewVideos.setClickable(true);

        listViewVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Video video = (Video) listViewVideos.getItemAtPosition(position);

                AnnotationsAdapter annotationsAdapter2 = new AnnotationsAdapter(listViewVideos.getContext(), video.getAllAnnotationObject());

                listViewAnnotations.setAdapter(annotationsAdapter2);

                videoName = video.getFileName();

                player.stop();

                initExoPlayer(); // crée des lecteurs en boucles
            }
        });


    }

    public void initExoPlayer() {
        String path = "android.resource://" + getPackageName() + "/" + R.raw.test;

        SimpleExoPlayerView exoPlayerView = findViewById(R.id.player_view);

        //1. creating an ExoPlayer with default parameters from Getting Started guide(https://google.github.io/ExoPlayer/guide.html)
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        DataSourceFactory = new DefaultExtractorsFactory();
        //2. prepare video source from url
        //        videoSource = new ExtractorMediaSource(Uri.parse(path), DataSourceFactory,
        //                new DefaultExtractorsFactory(), null, null);
        Uri uri = Uri.fromFile(new java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "Camera" + File.separator + videoName+".mp4"));
        //Uri uri = Uri.fromFile(new java.io.File("/sdcard/DCIM/Camera/" + videoName + ".mp4"));
        DataSpec dataSpec = new DataSpec(uri);
        FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }
        videoSource = new ExtractorMediaSource(
                uri,
                new DefaultDataSourceFactory(this, "ua"),
                new DefaultExtractorsFactory(), null, null);

        //2. create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, new DefaultLoadControl());
        exoPlayerView.setControllerShowTimeoutMs(0);
        exoPlayerView.setPlayer(player);
        player.setPlayWhenReady(false);
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.prepare(videoSource, false, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(STATE_RESUME_WINDOW, ResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, ResumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, ExoPlayerFullscreen);

        super.onSaveInstanceState(outState);
    }


    private void initFullscreenDialog() {

        FullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (ExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }


    private void openFullscreenDialog() {

        ((ViewGroup) playerView.getParent()).removeView(playerView);
        FullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        FullScreenIcon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_fullscreen_skrink));
        ExoPlayerFullscreen = true;
        FullScreenDialog.show();
    }


    private void closeFullscreenDialog() {

        ((ViewGroup) playerView.getParent()).removeView(playerView);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(playerView);
        ExoPlayerFullscreen = false;
        FullScreenDialog.dismiss();
        FullScreenIcon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_fullscreen_expand));
    }


    private void initFullscreenButton() {

        PlaybackControlView controlView = playerView.findViewById(R.id.exo_controller);
        FullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        FullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        FullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (playerView == null) {

            playerView = findViewById(R.id.player_view);
            initFullscreenDialog();
            initFullscreenButton();

        }

        initExoPlayer();

        if (ExoPlayerFullscreen) {
            ((ViewGroup) playerView.getParent()).removeView(playerView);
            FullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            FullScreenIcon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_fullscreen_skrink));
            FullScreenDialog.show();
        }
    }


    @Override
    protected void onPause() {

        super.onPause();

        if (playerView != null && playerView.getPlayer() != null) {
            ResumeWindow = playerView.getPlayer().getCurrentWindowIndex();
            ResumePosition = Math.max(0, playerView.getPlayer().getContentPosition());

            playerView.getPlayer().release();
        }

        if (FullScreenDialog != null)
            FullScreenDialog.dismiss();
    }

}
