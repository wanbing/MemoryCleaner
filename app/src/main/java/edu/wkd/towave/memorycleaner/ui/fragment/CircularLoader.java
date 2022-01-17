package edu.wkd.towave.memorycleaner.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import javax.inject.Inject;

import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.adapter.MenuListAdapter;
import edu.wkd.towave.memorycleaner.mvp.presenters.Presenter;
import edu.wkd.towave.memorycleaner.mvp.presenters.impl.fragment.CircularLoaderPresenter;
import edu.wkd.towave.memorycleaner.mvp.views.impl.fragment.CircularLoaderView;
import edu.wkd.towave.memorycleaner.tools.TextFormater;
import edu.wkd.towave.memorycleaner.ui.fragment.base.BaseFragment;

/**
 * Created by Administrator on 2016/4/21.
 */
public class CircularLoader extends BaseFragment implements CircularLoaderView {

    CircularFillableLoaders
            mCircularFillableLoaders;
    TextView mTextView;
    TextView mTextView2;
    Button mButton;
    RecyclerView recyclerView;

    @Inject
    CircularLoaderPresenter mCircularLoaderPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void bindView(View view) {
        mCircularFillableLoaders = (CircularFillableLoaders) view.findViewById(R.id.circularFillableLoaders);
        mTextView = (TextView) view.findViewById(R.id.percent);
        mTextView2 = (TextView) view.findViewById(R.id.number);
        mButton = (Button) view.findViewById(R.id.onekeyclean);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cleanMemory();
            }
        });
    }


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_circular_loader;
    }


    @Override
    protected Presenter getPresenter() {
        return mCircularLoaderPresenter;
    }


    @Override
    protected void initializeDependencyInjector() {
        super.initializeDependencyInjector();
        mBuilder.inject(this);
    }


    @Override
    public void initViews(MenuListAdapter recyclerAdapter) {
        mButton.setBackgroundColor(getColorPrimary());
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                LinearLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
    }


    @Override
    public void updateViews(long sum, long available, float percent) {
        mTextView.setText(percent + "%");
        mTextView2.setText(
                "已用:" + TextFormater.dataSizeFormat(sum - available) + "/" +
                        TextFormater.dataSizeFormat(sum));
        mCircularFillableLoaders.setProgress((int) (100 - percent));
        mCircularFillableLoaders.setColor(getColorPrimary());
    }


    @Override
    public void onCleanStarted(Context context) {
        mButton.setClickable(false);
        mButton.setText("正在清理");
        mCircularFillableLoaders.setAmplitudeRatio(0.1f);
    }


    @Override
    public void onCleanCompleted(Context context, long memory) {
        mCircularFillableLoaders.setAmplitudeRatio(0.03f);
        mButton.setClickable(true);
        mButton.setText("一键清理");
    }


    @Override
    public void onDestroy() {
        mCircularLoaderPresenter.onDestroy();
        super.onDestroy();
    }


    public void cleanMemory() {
        mCircularLoaderPresenter.cleanMemory();
    }
}
