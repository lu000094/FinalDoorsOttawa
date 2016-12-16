package com.algonquinlive.lu000094.doorsopenottawa;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.algonquinlive.lu000094.doorsopenottawa.model.Building;
import com.algonquinlive.lu000094.doorsopenottawa.model.mUriProvider;

import java.io.InputStream;
import java.net.URL;
import java.util.List;


/**
 * Purpose: customize the Planet cell for each building displayed in the ListActivity (i.e. MainActivity).
 * Usage:
 *   1) extend from class ArrayAdapter<YourModelClass>
 *   2) @override getView( ) :: decorate the list cell
 *
 * Based on the Adapter OO Design Pattern.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 *
 * Reference: based on DisplayList in "Connecting Android Apps to RESTful Web Services" with David Gassner
 */
public class BuildingAdapter extends ArrayAdapter<Building> {

    private Context context;
    private List<Building> buildingList;

    // TODO: cache the binary image for each planet
    private LruCache<Integer, Bitmap> imageCache;

    //List<Building>

    public BuildingAdapter(Context context, int resource, List<Building> objects) {
        super(context, resource, objects);
        this.context = context;
        this.buildingList = objects;

        // TODO: instantiate the imageCache
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() /1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Building building = buildingList.get(position);
        ViewHolder holderView = null;

        if (convertView==null)
        {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_building, parent, false);
            holderView = new ViewHolder();
            holderView.tv = (TextView) convertView.findViewById(R.id.textView1);
            holderView.tv1 = (TextView) convertView.findViewById(R.id.textView2);
            holderView.image = (ImageView) convertView.findViewById(R.id.imageView1);
            holderView.imgButton = (ImageButton)convertView.findViewById(R.id.btnFav);
            holderView.imgButton.setFocusable(false);

            convertView.setTag(holderView);
        }else {
            holderView = (ViewHolder) convertView.getTag();
        }
        holderView.tv.setText(building.getName());
        holderView.tv1.setText(building.getAddress());

        // TODO: Display planet photo in ImageView widget
        Bitmap bitmap = imageCache.get(building.getBuildingId());
        if (bitmap != null) {
            Log.i("PLANETS", building.getName() + "\tbitmap in cache");
            holderView.image.setImageBitmap(building.getBitmap());
        } else {
            Log.i("PLANETS", building.getName() + "\tfetching bitmap using AsyncTask");
            BuildingAndView container = new BuildingAndView();
            container.building = building;
            container.view = holderView;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

        if (building.getIsFavarite()==0)
        {
            holderView.imgButton.setImageResource(R.drawable.unlike);
            holderView.imgButton.setBackgroundResource(R.drawable.unlike);
        }else{
            holderView.imgButton.setImageResource(R.drawable.like);
            holderView.imgButton.setBackgroundResource(R.drawable.like);
        }

        holderView.imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.doSelection(building);
            }
        });

        return convertView;
    }

    // container for AsyncTask params
    private class BuildingAndView {
        public Building building;
        public ViewHolder view;
        public Bitmap bitmap;
    }
    static class ViewHolder {
        TextView tv;
        TextView tv1;
        ImageView image;
        ImageButton imgButton;
    }

    private class ImageLoader extends AsyncTask<BuildingAndView, Void, BuildingAndView> {

        @Override
        protected BuildingAndView doInBackground(BuildingAndView... params) {
            BuildingAndView container = params[0];
            Building building = container.building;

            try {
                String imageUrl = mUriProvider.IMAGES_BASE_URL + building.getImage();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                building.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                System.err.println("IMAGE: " + building.getName() );
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(BuildingAndView result) {
            ImageView image = null;
            try {
                image = (ImageView) result.view.image;
                if (result.bitmap != null) {
                    image.setImageBitmap(result.bitmap);
//            result.building.setBitmap(result.bitmap);
                    imageCache.put(result.building.getBuildingId(), result.bitmap);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}