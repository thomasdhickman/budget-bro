package com.thickman.aurora;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.thickman.aurora.dialog.AddCategoryDialog;
import com.thickman.aurora.fragment.ActionFragment;
import com.thickman.aurora.fragment.HomeFragment;
import com.thickman.aurora.fragment.MyAccountsFragment;
import com.thickman.aurora.fragment.TransactionListFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionMenu mFabMenu;
    private FloatingActionButton mAddTransaction;
    private FloatingActionButton mAddCategory;
    private ActionFragment mSelectedFragment;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFabMenu = findViewById(R.id.fab_add);
        mFabMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedFragment != null && !mSelectedFragment.getFragmentName().equals("Home")) {
                    mSelectedFragment.onAction();
                } else if (!mFabMenu.isOpened()) {
                    mFabMenu.open(true);
                } else if (mFabMenu.isOpened()) {
                    mFabMenu.close(true);
                }
            }
        });

        mAddTransaction = findViewById(R.id.addTransaction);
        mAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedFragment.onAction();
                mFabMenu.close(true);
            }
        });

        mAddCategory = findViewById(R.id.addCategory);
        mAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddCategoryDialog(MainActivity.this).show();
                mFabMenu.close(true);
            }
        });

        selectFragment(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectFragment(item.getItemId());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void selectFragment(int id) {
        switch (id) {
            case R.id.nav_recurring_transactions:
                mSelectedFragment = TransactionListFragment.newRecurringTransactionInstance();
                break;
            case R.id.nav_transactions:
                mSelectedFragment = TransactionListFragment.newPostedTransactionInstance();
                break;
            case R.id.nav_accounts:
                mSelectedFragment = MyAccountsFragment.newInstance();
                break;
            case R.id.nav_home:
            default:
                mSelectedFragment = new HomeFragment();
                break;
        }

        if (mSelectedFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_view, mSelectedFragment);
            fragmentTransaction.commit();
        }

        setToolbarTitle();
    }

    private void setToolbarTitle() {
        mToolbar.setTitle(mSelectedFragment.getFragmentName());
    }
}
