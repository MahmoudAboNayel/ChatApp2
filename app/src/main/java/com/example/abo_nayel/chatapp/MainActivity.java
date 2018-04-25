package com.example.abo_nayel.chatapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    SectionsPageAdapter mSectionsPageAdapter;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar =(Toolbar)findViewById(R.id.main_page_toolbar);
        mAuth =FirebaseAuth.getInstance();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat App");

        mTabLayout = (TabLayout)findViewById(R.id.main_tablayout);
        mViewPager= (ViewPager)findViewById(R.id.main_ViewPager);
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser current_user = mAuth.getCurrentUser();

        if(current_user == null){
            goToStart();
        }
    }

    private void goToStart() {
        startActivity(new Intent(MainActivity.this , StartActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case  R.id.main_logout:
                FirebaseAuth.getInstance().signOut();
                goToStart();
                break;
            case R.id.main_acc_setting_btn:
                startActivity(new Intent(MainActivity.this , AcountSettingsActivity.class));
                break;
            case R.id.main_allusers_btn:
                startActivity(new Intent(MainActivity.this , UsersActivity.class));
                break;

        }
        return true;
    }
}
