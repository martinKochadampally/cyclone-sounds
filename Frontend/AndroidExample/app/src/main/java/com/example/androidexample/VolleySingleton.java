package com.example.androidexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * A singleton class to manage Volley's RequestQueue and ImageLoader throughout the app.
 * This ensures that these objects are instantiated only once and are accessible from anywhere,
 * which is efficient and prevents issues with multiple queues or loaders.
 */
public class VolleySingleton {

    // The single instance of this class.
    private static VolleySingleton instance;
    // The queue for network requests.
    private RequestQueue requestQueue;
    // The loader for network images, with caching capabilities.
    private ImageLoader imageLoader;
    // The application context, used to prevent memory leaks.
    private static Context ctx;

    /**
     * Private constructor to prevent direct instantiation from other classes.
     * @param context The application context.
     */
    private VolleySingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        // Initialize the ImageLoader with the RequestQueue and an LruCache.
        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    // LruCache for image caching, holding up to 20 items.
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    /**
     * Provides a synchronized, global access point to the VolleySingleton instance.
     * @param context The application context.
     * @return The single instance of VolleySingleton.
     */
    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    /**
     * Returns the application's single RequestQueue instance, creating it if necessary.
     * @return The Volley RequestQueue.
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // Using getApplicationContext() is key. It prevents leaking the
            // Activity or BroadcastReceiver if one is passed as the context.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * Adds a generic Volley request to the RequestQueue.
     * @param req The request to be added.
     * @param <T> The type of the request's parsed response.
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Returns the application's single ImageLoader instance.
     * @return The Volley ImageLoader.
     */
    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
