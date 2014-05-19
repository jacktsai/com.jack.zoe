package com.jack.zoe;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jack.zoe.util.J;

import java.util.ArrayList;

public class GalleryPicker extends Activity {
    private static final String TAG = GalleryPicker.class.getSimpleName();

    private GridView gridView;
    private ArrayList<Item> items = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.gallerypicker);

        this.gridView = (GridView)this.findViewById(R.id.albums);
        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = items.get(position);
                J.d(TAG, "selected %d %s", item.bucketId, item.bucketName);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.fillBuckets();
        this.gridView.setAdapter(new Adapter());
    }

    private void fillBuckets() {
        Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                .buildUpon()
                .appendQueryParameter("distinct", "true")
                .build();

        final String[] IMAGE_PROJECTION = new String[] {
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                "MIN(" + MediaStore.Images.ImageColumns._ID + ")"
        };

        Cursor cursor = this.getContentResolver().query(baseUri, IMAGE_PROJECTION, "1 = 1) GROUP BY (" + MediaStore.Images.ImageColumns.BUCKET_ID + "), (" + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, null, null);
        try {
            while (cursor.moveToNext()) {
                Item item = new Item();
                item.bucketId = cursor.getInt(0);
                item.bucketName = cursor.getString(1);
                item._id = cursor.getInt(2);
                items.add(item);
                J.d(TAG, "bucket ID %d, bucket name %s, added", item.bucketId, item.bucketName);
            }
        } finally {
            cursor.close();
        }
    }

    class Item {
        int bucketId;
        String bucketName;
        int _id;
    }

    class Adapter extends BaseAdapter {
        LayoutInflater inflater = GalleryPicker.this.getLayoutInflater();

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = inflater.inflate(R.layout.gallerypicker_item, null);
            } else {
                view = convertView;
            }

            Item item = items.get(position);

            ImageView imageView = (ImageView)view.findViewById(R.id.thumbnail);
            Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), item._id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            imageView.setImageBitmap(thumbnail);

            TextView title = (TextView)view.findViewById(R.id.title);
            title.setText(item.bucketName);

            return view;
        }
    }
}
