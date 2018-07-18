package com.hotactress.hot.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arasthel.asyncjob.AsyncJob;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hotactress.hot.R;
import com.hotactress.hot.utils.Constants;
import com.hotactress.hot.utils.Gen;
import com.hotactress.hot.utils.PuzzleMatrixHelper;
import com.hotactress.hot.utils.SwipeDetector;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PuzzleSolvingActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int PUZZLE_SIZE = 3;
    public static final int PUZZLE_BLOCK_SIZE = 400;
    public final ImageView[][] imageViewMatrix = new ImageView[PUZZLE_SIZE][PUZZLE_SIZE];
    //    public int[][] initialMatrix = new int[PUZZLE_SIZE][PUZZLE_SIZE];  // initial distorted image indexes position
//    public int[][] solvedMatrix = new int[PUZZLE_SIZE][PUZZLE_SIZE];   // solved image index position
    public ImageView originalImageView, downloadImageView, shareWhatsappImageView, shareImageView, nextImageView;
    public Button resetButton, solveButton, resetInitialButton;
    public TextView totalMovesView, timeElapsedView;
    public List<Bitmap> bitmapList;
    public PuzzleMatrixHelper puzzleMatrixHelper;
    public MediaPlayer successMp;
    public MediaPlayer errorMp;
    public Vibrator vibe;
    public View shareLayout;
    public Bitmap mainImageBitmap;
    public String imageName = new Date().getTime() + ".jpeg"; //faker.name().fullName().replaceAll(" ", "") + ".jpeg";
    public String url ;
    public FirebaseAnalytics firebaseAnalytics ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_solving);
        for (int i = 0; i < PUZZLE_SIZE; i++) {
            for (int j = 0; j < PUZZLE_SIZE; j++) {
                int x = Gen.getResId(this, "puzzle_activity_image_" + i + "_" + j);
                imageViewMatrix[i][j] = findViewById(x);
            }
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Intent startingIntent = getIntent();
        url = startingIntent.getStringExtra("url");

        if(url == null)
            url = MessageFormat.format("http://104.236.43.23:3001/deepika/{0}.jpeg", new Random().nextInt(360));

        originalImageView = findViewById(R.id.puzzle_activity_original_image_id);
        resetButton = findViewById(R.id.puzzle_activity_reset_button_id);
        resetInitialButton = findViewById(R.id.puzzle_activity_reset_initial_id);
        solveButton = findViewById(R.id.puzzle_activity_solve_button_id);
//        totalMovesView = findViewById(R.id.puzzle_activity_total_moves_view_id);
//        timeElapsedView = findViewById(R.id.puzzle_activity_time_elapsed_view_id);
        shareLayout = findViewById(R.id.puzzle_activity_share_layout_id);
        downloadImageView = findViewById(R.id.puzzle_activity_download_button_id);
        shareWhatsappImageView = findViewById(R.id.puzzle_activity_whatsapp_button_id);
        shareImageView = findViewById(R.id.puzzle_activity_share_button_id);
        nextImageView = findViewById(R.id.puzzle_activity_next_button_id);

        loadImageIntoPuzzle(url);

        successMp = MediaPlayer.create(this, R.raw.success_sound);
        errorMp = MediaPlayer.create(this, R.raw.error_sound);
        vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        resetInitialButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        solveButton.setOnClickListener(this);
        downloadImageView.setOnClickListener(this);
        shareWhatsappImageView.setOnClickListener(this);
        shareImageView.setOnClickListener(this);
        nextImageView.setOnClickListener(this);

    }

    private void applyImagePiecesToMatrix() {

        Bitmap originalBitmap = Gen.mergeMultipleBitmaps(puzzleMatrixHelper.getBitmapList());
        originalImageView.setImageBitmap(originalBitmap);

        final Activity activity = this;

        for (int i = 0; i < PUZZLE_SIZE; i++) {
            for (int j = 0; j < PUZZLE_SIZE; j++) {

                imageViewMatrix[i][j].setImageBitmap(puzzleMatrixHelper.getBitmap(i, j));
                new SwipeDetector(imageViewMatrix[i][j]).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
                    @Override
                    public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum SwipeType) {
                        ImageView fromImageView = (ImageView) v;
                        String resName = getResources().getResourceEntryName(fromImageView.getId());
                        int fromX = Integer.parseInt(resName.split("_")[3]);
                        int fromY = Integer.parseInt(resName.split("_")[4]);
                        int toX = fromX, toY = fromY;
                        if (SwipeType == SwipeDetector.SwipeTypeEnum.BOTTOM_TO_TOP)
                            toX--;
                        else if (SwipeType == SwipeDetector.SwipeTypeEnum.TOP_TO_BOTTOM)
                            toX++;
                        else if (SwipeType == SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT)
                            toY++;
                        else if (SwipeType == SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT)
                            toY--;

                        Bundle b = new Bundle();

                        if (!puzzleMatrixHelper.isValidMove(fromX, fromY, toX, toY)) {
                            b.putString(FirebaseAnalytics.Param.ITEM_NAME, "error_move");
                            errorMp.start();
                            vibe.vibrate(100);
                            Toast.makeText(activity, "Sorry this move is not acceptable", Toast.LENGTH_SHORT).show();
                        } else {
                            b.putString(FirebaseAnalytics.Param.ITEM_NAME, "success_move");
                            successMp.start();
                            ImageView toImageView = imageViewMatrix[toX][toY];
                            Bitmap fromBm = puzzleMatrixHelper.getBitmap(fromX, fromY);
                            Bitmap toBm = puzzleMatrixHelper.getBitmap(toX, toY);
                            fromImageView.setImageBitmap(toBm);
                            toImageView.setImageBitmap(fromBm);
                            puzzleMatrixHelper.move(fromX, fromY, toX, toY);

                            if (puzzleMatrixHelper.isSolved()) {
                                Toast.makeText(activity, "Congrats, now you can share and download Me", Toast.LENGTH_SHORT).show();
                                shareLayout.setVisibility(View.VISIBLE);
                            }
                        }
                        firebaseAnalytics.logEvent(Constants.PUZZLE_SOLVING_ACTIVITY, b);
                    }
                });
            }
        }
    }


    private void randomizeImageMatrix(PuzzleMatrixHelper solver, List<Bitmap> originalImageBitmaps) {
        solver.randomizeOriginalMatrix();
        applyBitmapFromMatrix();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.puzzle_activity_reset_button_id) {
            puzzleMatrixHelper.randomizeOriginalMatrix();
            applyBitmapFromMatrix();
        }
        if (v.getId() == R.id.puzzle_activity_reset_initial_id) {
            puzzleMatrixHelper.resetToInitialMatrix();
            applyBitmapFromMatrix();
        }
        if (v.getId() == R.id.puzzle_activity_solve_button_id) {
            puzzleMatrixHelper.resetToSolveMatrix();
            applyBitmapFromMatrix();
        }
        if (v.getId() == R.id.puzzle_activity_download_button_id) {
            Gen.downloadImage(this, mainImageBitmap, imageName);
        }
        if (v.getId() == R.id.puzzle_activity_whatsapp_button_id){
            Gen.shareImage(this, mainImageBitmap, "com.whatsapp");
        }
        if (v.getId() == R.id.puzzle_activity_share_button_id){
            Gen.shareImage(this, mainImageBitmap, null);
        }
        if (v.getId() == R.id.puzzle_activity_next_button_id){
            String url = MessageFormat.format("http://104.236.43.23:3001/deepika/{0}.jpeg", new Random().nextInt(360));
            loadImageIntoPuzzle(url);
//            Gen.startActivity(this, true, PuzzleSolvingActivity.class, "url", url);
        }
    }

    private void loadImageIntoPuzzle(final String url) {
        final Activity activity = this;
        Gen.showLoader(activity);
        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {
                mainImageBitmap = Gen.getBitmapFromURL(url);
                AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                    @Override
                    public void doInUIThread() {
                        bitmapList = Gen.splitImage(mainImageBitmap, PUZZLE_SIZE, PUZZLE_BLOCK_SIZE);
                        puzzleMatrixHelper = new PuzzleMatrixHelper(PUZZLE_SIZE, bitmapList, totalMovesView);
                        applyImagePiecesToMatrix();
                        Gen.hideLoader(activity);
                    }
                });

            }
        });
    }

    private void loadImageIntoPuzzle(int drawableResource) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableResource);
        bitmap = Bitmap.createScaledBitmap(bitmap, PUZZLE_BLOCK_SIZE * PUZZLE_SIZE, PUZZLE_BLOCK_SIZE * PUZZLE_SIZE, true);
        bitmapList = Gen.splitImage(bitmap, PUZZLE_SIZE, PUZZLE_BLOCK_SIZE);
        puzzleMatrixHelper = new PuzzleMatrixHelper(PUZZLE_SIZE, bitmapList, totalMovesView);
        applyImagePiecesToMatrix();
    }

    private void applyBitmapFromMatrix() {
        for (int i = 0; i < PUZZLE_SIZE; i++)
            for (int j = 0; j < PUZZLE_SIZE; j++) {
                imageViewMatrix[i][j].setImageBitmap(puzzleMatrixHelper.getBitmap(i, j));
            }

    }
}
