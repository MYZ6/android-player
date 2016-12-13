/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chenyi.langeasy.activity;

import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.util.LogHelper;


/**
 * Abstract activity with toolbar, navigation drawer and cast support. Needs to be extended by
 * any activity that wants to be shown as a top level activity.
 * <p>
 * The requirements for a subclass is to call {@link #initializeToolbar()} on onCreate, after
 * setContentView() is called and have three mandatory layout elements:
 * a {@link android.support.v7.widget.Toolbar} with id 'toolbar',
 * a {@link android.support.v4.widget.DrawerLayout} with id 'drawerLayout' and
 * a {@link android.widget.ListView} with id 'drawerList'.
 */
public abstract class ActionBarCastActivity extends AppCompatActivity {

    private static final String TAG = LogHelper.makeLogTag(ActionBarCastActivity.class);

    private static final int DELAY_MILLIS = 1000;

    private MenuItem mMediaRouteMenuItem;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private boolean mToolbarInitialized;

    private int mItemLastClicked = -1;
    private int mItemToOpenWhenDrawerCloses = -1;


    private final DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerClosed(View drawerView) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerClosed(drawerView);

            triggerNavigation();
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerStateChanged(newState);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerOpened(drawerView);
            if (getSupportActionBar() != null) getSupportActionBar()
                    .setTitle(R.string.app_name);
        }
    };

    protected abstract void toFragment(String type);

    private final FragmentManager.OnBackStackChangedListener mBackStackChangedListener =
            new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    updateDrawerToggle();
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.d(TAG, "Activity onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mToolbarInitialized) {
            throw new IllegalStateException("You must run super.initializeToolbar at " +
                    "the end of your onCreate method");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Whenever the fragment back stack changes, we may need to update the
        // action bar toggle: only top level screens show the hamburger-like icon, inner
        // screens - either Activities or fragments - show the "Up" icon instead.
        getFragmentManager().addOnBackStackChangedListener(mBackStackChangedListener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getFragmentManager().removeOnBackStackChangedListener(mBackStackChangedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // If not handled by drawerToggle, home needs to be handled by returning to previous
        if (item != null && item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the drawer is open, back will close it
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        // Otherwise, it may return to the previous fragment stack
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            // Lastly, it will rely on the system behavior for back
            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mToolbar.setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        mToolbar.setTitle(titleId);
    }

    protected void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar == null) {
            throw new IllegalStateException("Layout is required to include a Toolbar with id " +
                    "'toolbar'");
        }
//        mToolbar.inflateMenu(R.menu.drawer);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            if (navigationView == null) {
                throw new IllegalStateException("Layout requires a NavigationView " +
                        "with id 'nav_view'");
            }

            // Create an ActionBarDrawerToggle that will handle opening/closing of the drawer:
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    mToolbar, R.string.open_content_drawer, R.string.close_content_drawer);
            mDrawerLayout.setDrawerListener(mDrawerListener);
            populateDrawerItems(navigationView);
            setSupportActionBar(mToolbar);
            updateDrawerToggle();
        } else {
            setSupportActionBar(mToolbar);
        }

        mToolbarInitialized = true;
    }

    private void populateDrawerItems(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mItemToOpenWhenDrawerCloses = menuItem.getItemId();

                        if (mItemLastClicked == mItemToOpenWhenDrawerCloses) {
                            mItemToOpenWhenDrawerCloses = -1;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
        if (MusicPlayerOldActivity.class.isAssignableFrom(getClass())) {
            navigationView.setCheckedItem(R.id.navigation_listen);
        } else if (MusicPlayerOldActivity.class.isAssignableFrom(getClass())) {
            navigationView.setCheckedItem(R.id.navigation_learn);
        }
    }

    private void triggerNavigation(){
        if (mItemLastClicked == mItemToOpenWhenDrawerCloses) {
            return;
        }
        if (mItemToOpenWhenDrawerCloses >= 0) {
            Bundle extras = ActivityOptions.makeCustomAnimation(
                    ActionBarCastActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
            Class activityClass = null;
            String type = "activity";
            String title = "";
            LogHelper.i(TAG, "Fragment Type " + mItemToOpenWhenDrawerCloses);
            switch (mItemToOpenWhenDrawerCloses) {
                case R.id.navigation_learn:
//                        activityClass = MusicPlayerOldActivity.class;
                    type = "learn";
                    title = "Learn";
                    break;
                case R.id.navigation_listen:
                    type = "listen";
                    title = "Listen";
                    break;
                case R.id.navigation_booktype_list:
                    type = "booktype_list";
                    title = "Book Type";
                    break;
                case R.id.navigation_booklist:
                    type = "booklist";
                    title = "Books";
                    break;
                case R.id.navigation_playlist:
                    type = "playlist";
                    title = "Playlist";
                    break;
                case R.id.navigation_courselist:
                    type = "courselist";
                    title = "Courses";
                    break;
            }
            if ("activity".equals(type)) {
                if (activityClass != null) {
                    startActivity(new Intent(ActionBarCastActivity.this, activityClass), extras);
                    finish();
                }
            } else {
                toFragment(type);
            }
            mItemLastClicked = mItemToOpenWhenDrawerCloses;

            mToolbar.setTitle(title);
        }
    }

    protected void setNavigationStatus(String type) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        int itemId = -1;
        String title = "";
        if ("listen".equals(type)) {
            title = "Listen";
            itemId = R.id.navigation_listen;
        } else if ("learn".equals(type)) {
            title = "Learn";
            itemId = R.id.navigation_learn;
        } else if ("booklist".equals(type)) {
            title = "Books";
            itemId = R.id.navigation_booklist;
        } else if ("courselist".equals(type)) {
            title = "Courses";
            itemId = R.id.navigation_courselist;
        } else if ("playlist".equals(type)) {
            title = "Playlist";
            itemId = R.id.navigation_playlist;
        }
        navigationView.setCheckedItem(itemId);
        mItemToOpenWhenDrawerCloses = itemId;
        mItemLastClicked = mItemToOpenWhenDrawerCloses;

        mToolbar.setTitle(title);
    }

    protected void updateDrawerToggle() {
        if (mDrawerToggle == null) {
            return;
        }
        boolean isRoot = getFragmentManager().getBackStackEntryCount() == 0;
        mDrawerToggle.setDrawerIndicatorEnabled(isRoot);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(!isRoot);
            getSupportActionBar().setDisplayHomeAsUpEnabled(!isRoot);
            getSupportActionBar().setHomeButtonEnabled(!isRoot);
        }
        if (isRoot) {
            mDrawerToggle.syncState();
        }
    }

    /**
     * Shows the Cast First Time User experience to the user (an overlay that explains what is
     * the Cast icon)
     */
    private void showFtu() {
        Menu menu = mToolbar.getMenu();
    }
}
