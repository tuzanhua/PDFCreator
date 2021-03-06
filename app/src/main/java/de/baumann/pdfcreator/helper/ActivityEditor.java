package de.baumann.pdfcreator.helper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

import java.io.File;

import de.baumann.pdfcreator.MainActivity;
import de.baumann.pdfcreator.R;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;


public class ActivityEditor extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        GPUImageView.OnPictureSavedListener {

    private GPUImageFilter mFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
    private GPUImageView mGPUImageView;

    private final GPUImage.ScaleType mScaleTyp = GPUImage.ScaleType.CENTER_INSIDE;

    private int h;
    private int w;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_title_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setImageResource(R.drawable.ic_check_white_48dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bitmap myBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/Pictures/.pdf_temp/pdf_temp.jpg");

        h = myBitmap.getHeight();
        w = myBitmap.getWidth();

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        assert seekBar != null;
        seekBar.setOnSeekBarChangeListener(this);


        File imgFile = new File(Environment.getExternalStorageDirectory() + "/Pictures/.pdf_temp/pdf_temp.jpg");
        mGPUImageView = (GPUImageView) findViewById(R.id.gpuimage);
        assert mGPUImageView != null;
        mGPUImageView.setScaleType(mScaleTyp);
        mGPUImageView.setImage(imgFile);
    }

    @Override
    public void onPictureSaved(final Uri uri) {
        Snackbar.make(mGPUImageView, getString(R.string.toast_savedImage), Snackbar.LENGTH_LONG)
                .setAction("Action", null);
    }

    private void saveImage() {

        Snackbar.make(mGPUImageView, getString(R.string.toast_savedImage), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        String folder = "/.pdf_temp/";
        String fileName = "pdf_temp.jpg";

        mGPUImageView.saveToPictures(folder, fileName, w, h, this);
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            mGPUImageView.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(progress);
        }
        mGPUImageView.requestRender();
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.filter) {

            File imgFile = new File(Environment.getExternalStorageDirectory() + "/Pictures/.pdf_temp/pdf_temp.jpg");
            mGPUImageView = (GPUImageView) findViewById(R.id.gpuimage);
            assert mGPUImageView != null;
            mGPUImageView.setImage(imgFile);

            GPUImageFilterTools.showDialog(this, new GPUImageFilterTools.OnGpuImageFilterChosenListener() {

                @Override
                public void onGpuImageFilterChosenListener(final GPUImageFilter filter) {
                    switchFilterTo(filter);
                    mGPUImageView.requestRender();
                }

            });
        }

        if (id == R.id.save) {

            saveImage();
        }

        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

}