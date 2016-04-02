package thin.blog.ibts;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {
    @Bind(R.id.app_bar)
    Toolbar toolbar;
    @Bind(R.id.floating_action_button)
    FloatingActionButton floatingActionButton;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    FragmentManager fragmentManager;

    @OnClick(R.id.floating_action_button)
    public void floatingButtonPressed(View view) {
        android.support.v4.app.Fragment current = getSupportFragmentManager().findFragmentById(R.id.activity_root_layout_linear);
        if (current instanceof MyAccount) {
            EditAccount editAccount = (EditAccount) getSupportFragmentManager().findFragmentByTag("EDIT_ACCOUNT");
            if (editAccount == null) {
                editAccount = EditAccount.newInstance();
            }
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).replace(R.id.activity_root_layout_linear, editAccount, "EDIT_ACCOUNT").addToBackStack("EDIT_ACCOUNT").commit();
            floatingActionButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        MyAccount myAccount = (MyAccount) getSupportFragmentManager().findFragmentByTag("MY_ACCOUNT");
        if (myAccount == null) {
            myAccount = MyAccount.newInstance();
        }
        fragmentManager.beginTransaction().replace(R.id.activity_root_layout_linear, myAccount, "MY_ACCOUNT").commit();
        getSupportActionBar().setTitle("My Account");
        getSupportActionBar().setSubtitle("Prathab Murugan");
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (fragmentManager.getBackStackEntryCount() == 0) {
                finish();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_account) {

        } else if (id == R.id.travel_history) {

        } else if (id == R.id.bus_tracker) {

        } else if (id == R.id.stop_details) {

        } else if (id == R.id.help) {

        } else if (id == R.id.about) {

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackStackChanged() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.activity_root_layout_linear);
        if (fragment instanceof MyAccount) {
            floatingActionButton.setVisibility(View.VISIBLE);
        }
    }
}
