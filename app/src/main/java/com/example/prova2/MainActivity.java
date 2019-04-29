package com.example.prova2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.prova2.database.NotebookContent;
import com.example.prova2.database.NotebooksViewModel;
import com.example.prova2.onboarding.OnboardingActivity;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity implements CameraFragment.OnFragmentInteractionListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private TabLayout mTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Shows walkthrough only the first time the app is opened
        SharedPreferences preferences =
                getSharedPreferences("my_preferences", MODE_PRIVATE);

        if (!preferences.getBoolean("onboarding_complete", false)) {

            Intent onboarding = new Intent(this, OnboardingActivity.class);
            startActivity(onboarding);

            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mTab = findViewById(R.id.tab);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                if(position == 0) {
                    showToolbar(true);
                    //    fab.show();

                } else {
                    showToolbar(false);
                    //    fab.hide();
                }
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

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
        if (id == R.id.action_help) {
            Intent onboarding = new Intent(this, OnboardingActivity.class);
            startActivity(onboarding);

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // hide/show toolbar
    public void showToolbar(boolean show) {
        if (show) {
            mTab.setVisibility(View.VISIBLE);
            mToolbar.setVisibility(View.VISIBLE);
        } else {
            mTab.setVisibility(View.GONE);
            mToolbar.setVisibility(View.GONE);
        }

    }


    //receive text read from CameraFragment
    @Override
    public void onFragmentInteraction(String parameter) {
        Log.i("MainActivity", "Received: " + parameter + " from CameraFragment");

        // show HomeFragment
        mViewPager.setCurrentItem(0);


        NotebooksViewModel viewModel = new NotebooksViewModel(getApplication());

        try {
            //Read just the number
            NotebookContent nc = viewModel.getFile(parseInt(parameter.replaceAll("[\\D]", "")));
            String fp = nc.getFilePath();

            Intent intent = new Intent(this, ImageDisplay.class);
            intent.putExtra("path", fp);
            startActivity(intent);
        } catch (NumberFormatException e) {
            Snackbar.make(findViewById(R.id.main_content),"Not a number", Snackbar.LENGTH_LONG)
                    .show();
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.main_content),"No files linked to that number", Snackbar.LENGTH_LONG)
                    .show();
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.i("MAIN", "position: " + position);

            if (position == 0)
                return HomeFragment.newInstance(position+1);
            else
                return CameraFragment.newInstance(position+1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "Notebooks";
            else {
                Drawable image = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_photo_camera_black_24dp);
                image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
                SpannableString sb = new SpannableString(" ");
                ImageSpan imageSpan = new ImageSpan(image);
                sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return sb;
            }
        }

    }
}
