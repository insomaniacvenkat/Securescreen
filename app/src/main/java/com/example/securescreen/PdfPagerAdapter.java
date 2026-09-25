package com.example.securescreen;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;
import com.example.securescreen.ZoomableImageView;

import java.io.IOException;
import java.util.List;

public class PdfPagerAdapter extends RecyclerView.Adapter<PdfPagerAdapter.ViewHolder> {
    private PdfRenderer renderer;
    private ParcelFileDescriptor pfd;
    private List<Bitmap> images;
    private boolean isPdf = false;

    public PdfPagerAdapter(ParcelFileDescriptor pfd) throws IOException {
        this.pfd = pfd;
        this.renderer = new PdfRenderer(pfd);
        this.isPdf = true;
    }

    public PdfPagerAdapter(List<Bitmap> images) {
        this.images = images;
        this.isPdf = false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ZoomableImageView iv = new ZoomableImageView(parent.getContext());
        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new ViewHolder(iv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (isPdf) {
            PdfRenderer.Page page = null;
            try {
                page = renderer.openPage(position);
                int w = page.getWidth();
                int h = page.getHeight();
                Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                holder.photoView.setImageBitmap(bmp);
            } finally {
                if (page != null) page.close();
            }
        } else {
            Bitmap bmp = images.get(position);
            holder.photoView.setImageBitmap(bmp);
        }
    }

    @Override
    public int getItemCount() {
        if (isPdf) return renderer.getPageCount();
        return images == null ? 0 : images.size();
    }

    public void close() {
        try {
            if (renderer != null) renderer.close();
        } catch (Exception ignored) {}
        try {
            if (pfd != null) pfd.close();
        } catch (Exception ignored) {}
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ZoomableImageView photoView;

        ViewHolder(@NonNull ZoomableImageView pv) {
            super(pv);
            photoView = pv;
        }
    }
}
