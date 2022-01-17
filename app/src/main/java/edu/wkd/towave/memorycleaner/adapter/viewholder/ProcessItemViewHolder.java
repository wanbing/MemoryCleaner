package edu.wkd.towave.memorycleaner.adapter.viewholder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import edu.wkd.towave.memorycleaner.R;

/**
 * Created by towave on 2016/5/16.
 */
public class ProcessItemViewHolder extends RecyclerView.ViewHolder {
    ImageView mImageView;
    TextView mTextView;
    TextView mTextView2;
    CheckBox mCheckBox;

    public ProcessItemViewHolder(View parent) {
        super(parent);
        mImageView = (ImageView) parent.findViewById(R.id.icon);
        mTextView = (TextView) parent.findViewById(R.id.name);
        mTextView2 = (TextView) parent.findViewById(R.id.memory);
        mCheckBox = (CheckBox) parent.findViewById(R.id.is_clean);
    }


    public void setIcon(Drawable icon) {
        mImageView.setImageDrawable(icon);
    }


    public void setName(String name) {
        mTextView.setText(name);
    }


    public void setMemory(String memory) {
        mTextView2.setText(memory);
    }


    public void setChecked(boolean checked) {
        mCheckBox.setChecked(checked);
    }


    public void setCheckBoxVisible(boolean visible) {
        mCheckBox.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }


    public void setMemoryVisible(boolean visible) {
        mTextView2.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
}
