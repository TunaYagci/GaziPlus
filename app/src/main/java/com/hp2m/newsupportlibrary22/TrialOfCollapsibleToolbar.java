package com.hp2m.newsupportlibrary22;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


/**
 * Created by Tuna on 7/31/2015.
 */
public class TrialOfCollapsibleToolbar extends AppCompatActivity {

    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collapsible_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Gazi+");
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Avengers: Age of Ultron");

        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addTab(tabLayout.newTab().setText("Duyuru"));
        tabLayout.addTab(tabLayout.newTab().setText("Notlar"));
        tabLayout.addTab(tabLayout.newTab().setText("Yemek Listesi"));
        tabLayout.addTab(tabLayout.newTab().setText("Ekstra"));

        tabLayout.setupWithViewPager(mPager);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter //implements PagerSlidingTabStrip.IconTabProvider
    {
        private final int NUM_PAGES = 4;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
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
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new Fragment1();
                case 1:
                    return new Fragment2();
                case 2:
                    return new Fragment3();
                case 3:
                    return new Fragment4();
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

