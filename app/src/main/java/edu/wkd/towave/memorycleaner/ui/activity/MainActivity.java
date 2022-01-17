package edu.wkd.towave.memorycleaner.ui.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import javax.inject.Inject;

import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.base.BaseFragmentPageAdapter;
import edu.wkd.towave.memorycleaner.injector.component.DaggerActivityComponent;
import edu.wkd.towave.memorycleaner.injector.module.ActivityModule;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity.MainPresenter;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.MainView;
import edu.wkd.towave.memorycleaner.tools.SnackbarUtils;
import edu.wkd.towave.memorycleaner.tools.ToolbarUtils;
import edu.wkd.towave.memorycleaner.ui.activity.base.BaseActivity;

public class MainActivity extends BaseActivity
        implements MainView, NavigationView.OnNavigationItemSelectedListener {

    ViewPager mViewPager;
    TabLayout mTabLayout;
    NavigationView navigationView;
    DrawerLayout drawer;
    Toolbar toolbar;
    @Inject MainPresenter mMainPresenter;

    ActionBarDrawerToggle toggle;
    BaseFragmentPageAdapter mCommonFragmentPageAdapter;


    @Override protected void onCreate(Bundle savedInstanceState) {
        launchWithNoAnim();
        super.onCreate(savedInstanceState);
        initializePresenter();
        mMainPresenter.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }


    private void initializePresenter() {
        mMainPresenter.attachView(this);
    }


    @Override protected void initializeDependencyInjector() {
        App app = (App) getApplication();
        mActivityComponent = DaggerActivityComponent.builder()
                                                    .activityModule(
                                                            new ActivityModule(
                                                                    this))
                                                    .appComponent(
                                                            app.getAppComponent())
                                                    .build();
        mActivityComponent.inject(this);
    }


    @Override public void onStart() {
        super.onStart();
        mMainPresenter.onStart();
    }


    @Override protected void onResume() {
        super.onResume();
        mMainPresenter.onResume();
    }


    @Override protected void onPause() {
        mMainPresenter.onPause();
        super.onPause();
    }


    @Override public void onStop() {
        mMainPresenter.onStop();
        super.onStop();
    }


    @Override public void showSnackbar() {
        SnackbarUtils.showAction(this, "你确定要退出吗", "退出", v -> {
            finish();
            //可能不能正常执行正常生命周期，即进程退出时不会去执行Activity的onPause、onStop和onDestroy方法，
            //System.exit(0);
        });
    }


    @Override public void reCreate() {
        super.recreate();
    }


    @Override public void onDestroy() {
        mMainPresenter.onDestroy();
        super.onDestroy();
    }


    @Override public void initToolbar() {
        ToolbarUtils.initToolbar(toolbar, this);
    }


    @Override public void initDrawerView() {
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getHeaderView(0).setBackgroundColor(getColorPrimary());
    }


    @Override public void initViewPager(ArrayList<Fragment> items) {
        //init viewpager
        mCommonFragmentPageAdapter = new BaseFragmentPageAdapter(
                getSupportFragmentManager(), items);
        mViewPager.setAdapter(mCommonFragmentPageAdapter);
        //mViewPager.setOffscreenPageLimit(1);

        for (int i = 0; i < mCommonFragmentPageAdapter.getCount(); i++) {
            mTabLayout.addTab(mTabLayout.newTab());
        }
        mTabLayout.setupWithViewPager(mViewPager);
    }


    @Override protected int getLayoutView() {
        return R.layout.activity_main;
    }


    @Override public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            showSnackbar();
        }
    }

    //@Override public boolean onCreateOptionsMenu(Menu menu) {
    //    // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.main, menu);
    //    return true;
    //}

    //@Override public boolean onOptionsItemSelected(MenuItem item) {
    //    if (mMainPresenter.onOptionsItemSelected(item)) {
    //        return true;
    //    }
    //    return super.onOptionsItemSelected(item);
    //}


    @SuppressWarnings("StatementWithEmptyBody") @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        mMainPresenter.onNavigationItemSelected(item.getItemId());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
