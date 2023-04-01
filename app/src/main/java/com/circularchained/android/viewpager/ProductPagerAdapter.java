package com.circularchained.android.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.circularchained.android.fragments.SwipeFragment;

public class ProductPagerAdapter extends FragmentStateAdapter  {

    private final String[] categories;

    public ProductPagerAdapter(@NonNull FragmentActivity fragmentActivity, String[] categories) {
        super(fragmentActivity);
        this.categories = categories;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return SwipeFragment.getInstance(position, categories[position]);
    }

    @Override
    public int getItemCount() {
        return categories.length;
    }

}
