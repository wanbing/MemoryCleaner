package edu.wkd.towave.memorycleaner.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.john.waveview.WaveView;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.math.BigDecimal;

import javax.inject.Inject;

import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.ProcessListAdapter;
import edu.wkd.towave.memorycleaner.injector.component.DaggerActivityComponent;
import edu.wkd.towave.memorycleaner.injector.module.ActivityModule;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.activity.MemoryCleanPresenter;
import edu.wkd.towave.memorycleaner.mvp.views.impl.activity.MemoryCleanView;
import edu.wkd.towave.memorycleaner.tools.AppUtils;
import edu.wkd.towave.memorycleaner.tools.SnackbarUtils;
import edu.wkd.towave.memorycleaner.tools.TextFormater;
import edu.wkd.towave.memorycleaner.ui.activity.base.BaseActivity;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MemoryClean extends BaseActivity implements MemoryCleanView {
    Toolbar toolbar;
    RecyclerView recyclerView;
    MaterialProgressBar mProgressBar;
    TextView mTextView;
    WaveView mWaveView;
    RecyclerFastScroller mRecyclerFastScroller;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    FloatingActionButton mFloatingActionButton;
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Inject
    MemoryCleanPresenter mMemoryCleanPresenter;
    public static final int BASE_ID = 0;
    public static final int GROUP_ID = 100;
    MenuItem mMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePresenter();
        mMemoryCleanPresenter.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mProgressBar = (MaterialProgressBar) findViewById(R.id.scanProgress);
        mTextView = (TextView) findViewById(R.id.processName);
        mWaveView = (WaveView) findViewById(R.id.wave_view);
        mRecyclerFastScroller = (RecyclerFastScroller) findViewById(R.id.recyclerfastscroll);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.clean_memory);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresher);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cleanMemory();
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMemoryCleanPresenter.onDestroy();
    }


    private void initializePresenter() {
        mMemoryCleanPresenter.attachView(this);
    }


    @Override
    protected void initializeDependencyInjector() {
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


    @Override
    protected void initToolbar() {
        super.initToolbar(toolbar);
    }


    @Override
    protected int getLayoutView() {
        return R.layout.activity_memory_clean;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memory_clean, menu);
        SubMenu subMenu = menu.addSubMenu(GROUP_ID, BASE_ID, 0, "排序");
        subMenu.setIcon(R.drawable.ic_sort_white_24dp);
        subMenu.add(GROUP_ID + 1, BASE_ID + 1, 0, "应用名");
        subMenu.add(GROUP_ID + 1, BASE_ID + 2, 1, "大小");
        subMenu.add(GROUP_ID + 1, BASE_ID + 3, 2, "选中");
        subMenu.add(GROUP_ID + 2, BASE_ID + 4, 3, "降序")
                .setCheckable(true)
                .setChecked(true);
        subMenu.setGroupCheckable(GROUP_ID + 1, true, true);
        mMenuItem = menu.findItem(R.id.allcheck);
        ActionItemBadge.update(this, mMenuItem, FontAwesome.Icon.faw_check,
                ActionItemBadge.BadgeStyles.DARK_GREY, 0);
        return true;
    }


    @Override
    public void initViews(ProcessListAdapter recyclerAdapter, Context context, ItemTouchHelper itemTouchHelper) {
        recyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                        false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(mMemoryCleanPresenter);
        mSwipeRefreshLayout.setColorSchemeColors(getColorPrimary());
        mRecyclerFastScroller.attachRecyclerView(recyclerView);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @Override
    public void onScanStarted(Context context) {
        mFloatingActionButton.setVisibility(View.GONE);
        mCollapsingToolbarLayout.setTitle(
                "0M 0%-->" + AppUtils.getPercent(context) + "%");
        mWaveView.setProgress(0);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(0);
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setText("开始扫描");
    }


    @Override
    public void onScanProgressUpdated(Context context, int current, int max, long memory, String processName) {
        updateTitle(context, memory);
        updateBadge(current);
        mTextView.setText("正在扫描:" + current + "/" + max + " 进程名:" +
                processName);
        float percent = (int) (1.0 * current / max * 100);
        mProgressBar.setProgress((int) percent);
    }


    @Override
    public void onScanCompleted() {
        mFloatingActionButton.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);
    }


    @Override
    public RelativeLayout setDialogValues(String[] memory) {
        RelativeLayout dialog_process_detail
                = (RelativeLayout) getLayoutInflater().inflate(
                R.layout.dialog_process_detail, null);
        if (memory == null || memory.length == 0) return dialog_process_detail;
        TextView mTextView2 = (TextView) dialog_process_detail.findViewById(
                R.id.memory);
        TextView mTextView3 = (TextView) dialog_process_detail.findViewById(
                R.id.unit);
        mTextView2.setText(memory[0]);
        mTextView3.setText(memory[1]);
        return dialog_process_detail;
    }


    public void cleanMemory() {
        mMemoryCleanPresenter.cleanMemory();
    }


    @Override
    public void stopRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void startRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
    }


    @Override
    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }


    @Override
    public void enableSwipeRefreshLayout(boolean enable) {
        mSwipeRefreshLayout.setEnabled(enable);
    }


    @Override
    public void showSnackBar(String message) {
        SnackbarUtils.show(mFloatingActionButton, message);
    }


    @Override
    public void updateBadge(int count) {
        ActionItemBadge.update(mMenuItem, count);
    }


    @Override
    public void updateTitle(Context context, long memory) {
        float scanMemoryPercent = AppUtils.getPercent(memory);
        mCollapsingToolbarLayout.setTitle(
                TextFormater.dataSizeFormat(memory) + " " +
                        scanMemoryPercent + "%-->" +
                        new BigDecimal(AppUtils.getPercent(context) -
                                scanMemoryPercent).setScale(2,
                                BigDecimal.ROUND_HALF_UP).floatValue() +
                        "%");
        mWaveView.setProgress((int) scanMemoryPercent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMemoryCleanPresenter.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
