package com.hp2m.newsupportlibrary22;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;


public class MainActivity extends AppCompatActivity {

    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Gazi+");
        setSupportActionBar(toolbar);*/
        mPager = (ViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);

        //final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.mipmap.ic_launcher); adding logo
        //ab.setDisplayHomeAsUpEnabled(true); to go back, to the main activity

        ImageButton userButton = (ImageButton) findViewById(R.id.userButton);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUserButtonClick();
            }
        });


        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //tabLayout.setTabGravity(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayout.setupWithViewPager(mPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_public_white_48dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_school_darky_48dp);
        //tabLayout.addTab(tabLayout.newTab().setText("Yemek Listesi"));
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_cake_darky_48dp);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_public_white_48dp);
                        tabLayout.getTabAt(1).setIcon(R.drawable.school_darky_2);
                        tabLayout.getTabAt(2).setIcon(R.drawable.ic_cake_darky_48dp);

                        appBarLayout.setBackgroundColor(getResources().getColor(R.color.my_primary));
                        tabLayout.setBackgroundColor(getResources().getColor(R.color.my_primary));
                        mPager.setCurrentItem(0, true);
                        return;
                    case 1:
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_public_darky_48dp);
                        tabLayout.getTabAt(1).setIcon(R.drawable.ic_school_white_48dp);
                        tabLayout.getTabAt(2).setIcon(R.drawable.ic_cake_darky_48dp);

                        appBarLayout.setBackgroundColor(getResources().getColor(R.color.my_accent));
                        tabLayout.setBackgroundColor(getResources().getColor(R.color.my_accent));
                        mPager.setCurrentItem(1, true);
                        return;
                    case 2:
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_public_darky_48dp);
                        tabLayout.getTabAt(1).setIcon(R.drawable.school_darky_2);
                        tabLayout.getTabAt(2).setIcon(R.drawable.ic_cake_white_48dp);

                        appBarLayout.setBackgroundColor(getResources().getColor(R.color.fragment3_tabColor));
                        tabLayout.setBackgroundColor(getResources().getColor(R.color.fragment3_tabColor));
                        mPager.setCurrentItem(2, true);
                        return;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.detailed_duyuru_error)
                .showImageForEmptyUri(R.drawable.detailed_duyuru_error)
                .showImageOnLoading(R.drawable.detailed_duyuru_loading)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bmp) {
                        return Bitmap.createScaledBitmap(bmp, 1000, 1000, false);
                    }
                })
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .diskCacheExtraOptions(480, 320, null)
                .denyCacheImageMultipleSizesInMemory()
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

    }

    private void handleUserButtonClick() {

        Intent i = new Intent(this, UserActivity.class);
        startActivity(i);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_find_location) {
            return true;
        } else if (id == R.id.second_icon_on_supportactionbar)
            return true;

        return super.onOptionsItemSelected(item);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter //implements PagerSlidingTabStrip.IconTabProvider
    {
        private final int NUM_PAGES = 3;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /*@Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Duyuru";
                case 1:
                    return "Notlar";
                case 2:
                    return "Yemek Listesi";
                case 3:
                    return "Ekstra";
                default:
                    return null;
            }
        }*/

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new Fragment1();
                case 1:
                    return new Fragment2();
                case 2:
                    return new Fragment3();
                //case 3:
                // return new Fragment4();
                default:
                    return null;
            }


        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }


    }
}
