package com.circularchained.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.circularchained.android.viewpager.ProductPagerAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final Context mContext = MainActivity.this;
    public static String[] categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
        ExtendedFloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);
        categoryList = getResources().getStringArray(R.array.category);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        setUpViewPager(viewPager, tabLayout);
    }

    private void setUpViewPager(ViewPager2 viewPager, TabLayout tabLayout) {
        ProductPagerAdapter adapter = new ProductPagerAdapter(this, categoryList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(categoryList.length);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(categoryList[position])).attach();
        for (int i = 0; i < tabLayout.getTabCount(); i++){
            TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
            tabLayout.getTabAt(i).setCustomView(tv);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floatingActionButton) {
            startActivity(new Intent(mContext, CreateProduct.class));
        }else if (v.getId() == R.id.imageView){
            startActivity(new Intent(mContext, ScanQR.class));
        }
    }

}
