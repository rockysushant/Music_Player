package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {


    TextView tileTv,currentTimeTv,totalTimetv;
    SeekBar seekbar;
    ImageView pause,nextbtn,previous;

    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);


        tileTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimetv = findViewById(R.id.total_time);
        seekbar = findViewById(R.id.seekBar);
        pause = findViewById(R.id.play);
        nextbtn = findViewById(R.id.next);
        previous = findViewById(R.id.previous);

       tileTv.setSelected(true);


        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        try {
            setResourcesWithMusic();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if(mediaPlayer.setPreferredDevice(null));     // yha pe not null krna hai

                };{
                    seekbar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertTOMMS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        pause.setImageResource(R.drawable.baseline_pause_circle_24);
                    }else{
                        pause.setImageResource(R.drawable.baseline_play_circle_24);

                    }
                }
                new Handler().postDelayed(this,100);

            }
        });



        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

//                if(mediaPlayer!=null && b ){
//                    mediaPlayer.seekTo(onProgressChanged());   // progress
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    void setResourcesWithMusic() throws IOException {
        currentSong = songsList.get(MyMediaPlayer.currentIndex);
        tileTv.setText(currentSong.getTitle());

        totalTimetv.setText(convertTOMMS(currentSong.getDuration()));

        pause.setOnClickListener(v-> play());
        nextbtn.setOnClickListener(v-> {
            try {
                playNextSong();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        previous.setOnClickListener(v-> {
            try {
                playPreviousSong();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        playMusic();

    }

    private void playMusic() throws IOException {


        mediaPlayer.reset();
        mediaPlayer.setDataSource(currentSong.getPath());
        mediaPlayer.prepare();
        mediaPlayer.start();
        seekbar.setProgress(mediaPlayer.getDuration());
        seekbar.setMax(mediaPlayer.getDuration());

    }

    private void playNextSong() throws IOException {
       if(MyMediaPlayer.currentIndex == songsList.size()-1)
           return;

        MyMediaPlayer.currentIndex +=1;
        mediaPlayer.reset();
        setResourcesWithMusic();

    }

    private  void playPreviousSong() throws IOException {


        if(MyMediaPlayer.currentIndex == 0)
            return;

        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private  void play(){

        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();



    }
    public  static String convertTOMMS(String duration){
        long millis = Long.parseLong(duration);
         return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis)%TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis)%TimeUnit.MINUTES.toSeconds(1));
    }





}