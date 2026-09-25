package com.example.securescreen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_OPEN = 1;
    private ViewPager2 viewPager;
    private PdfPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Prevent screenshots and screen recording
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPager);
        Button btnOpen = findViewById(R.id.btnOpen);
        btnOpen.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Screenshots are disabled in this screen", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "application/pdf"});
            startActivityForResult(intent, REQUEST_OPEN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri == null) return;
            try {
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception ignored) {}
            displayUri(uri);
        }
    }

    private void displayUri(Uri uri) {
        try {
            String mime = getContentResolver().getType(uri);
            if (mime != null && mime.startsWith("image")) {
                try (InputStream is = getContentResolver().openInputStream(uri)) {
                    Bitmap bmp = android.graphics.BitmapFactory.decodeStream(is);
                    List<Bitmap> list = new ArrayList<>();
                    list.add(bmp);
                    replaceAdapter(new PdfPagerAdapter(list));
                }
            } else if ("application/pdf".equals(mime)) {
                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r");
                if (pfd == null) return;
                replaceAdapter(new PdfPagerAdapter(pfd));
            } else {
                Toast.makeText(this, "Unsupported file type", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to open: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void replaceAdapter(PdfPagerAdapter newAdapter) {
        // close existing
        if (adapter != null) {
            adapter.close();
        }
        adapter = newAdapter;
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) adapter.close();
    }
}
