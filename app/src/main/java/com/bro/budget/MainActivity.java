package com.bro.budget;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bro.budget.dialog.AddAccountDialog;
import com.bro.budget.dialog.AddCategoryDialog;
import com.bro.budget.dialog.AddTransactionDialog;
import com.bro.budget.fragment.ActionFragment;
import com.bro.budget.fragment.HomeFragment;
import com.bro.budget.fragment.MyAccountsFragment;
import com.bro.budget.fragment.TransactionListFragment;
import com.bro.budget.realm.AccountRealmController;
import com.bro.budget.realm.CategoryRealmController;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FloatingActionMenu mFabMenu;
    private FloatingActionButton mAddTransaction;
    private FloatingActionButton mAddCategory;
    private FloatingActionButton mAddAccount;
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

        mFabMenu = findViewById(R.id.fabMenu);
        mFabMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFabMenu();
            }
        });

        mAddTransaction = findViewById(R.id.addTransaction);
        mAddTransaction.setOnClickListener(this);

        mAddCategory = findViewById(R.id.addCategory);
        mAddCategory.setOnClickListener(this);

        mAddAccount = findViewById(R.id.addAccount);
        mAddAccount.setOnClickListener(this);

        selectFragment(R.id.nav_home);
    }

    public void addAccount() {
        new AddAccountDialog(this).show();
    }

    public void addCategory() {
        new AddCategoryDialog(this).show();
    }

    public void addTransaction() {
        if (!AccountRealmController.getInstance().canAddTransaction()) {
            showAddAccountSnackbar();
        } else if (CategoryRealmController.getInstance().getNextId() == 1) {
            showAddCategorySnackbar();
        } else {
            new AddTransactionDialog(this).show();
        }
    }

    private void showAddCategorySnackbar() {
        Snackbar.make(findViewById(R.id.drawer_layout), "Add a category first!",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("Add Category", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addCategory();
                    }
                })
                .show();
    }

    private void showAddAccountSnackbar() {
        Snackbar.make(findViewById(R.id.drawer_layout), "You have no accounts!",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("Add Account", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addAccount();
                    }
                })
                .show();
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
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
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

    private void openFabMenu() {
        switch (mSelectedFragment.getFragmentName()) {
            case R.string.home:
                if (!mFabMenu.isOpened()) {
                    mFabMenu.open(true);
                } else {
                    mFabMenu.close(true);
                }
                break;
            case R.string.recurring_transactions:
            case R.string.posted_transactions:
                addTransaction();
                break;
            case R.string.my_accounts:
                addAccount();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addCategory:
                addCategory();
                mFabMenu.close(true);
                break;
            case R.id.addAccount:
                addAccount();
                mFabMenu.close(true);
                break;

            case R.id.addTransaction:
                addTransaction();
                mFabMenu.close(true);
                break;
        }
    }
}