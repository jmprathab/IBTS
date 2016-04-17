package thin.blog.ibts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import datasets.User;

import static thin.blog.ibts.ApplicationHelper.readFromSharedPreferences;
import static thin.blog.ibts.ApplicationHelper.writeToSharedPreferences;

/**
 * Main Activity which contains Navigation Drawer and takes care of Replacing Fragments
 */
public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EditAccount.OnFragmentInteractionListener {
    @Bind(R.id.app_bar)
    Toolbar toolbar;
    @Bind(R.id.floating_action_button)
    FloatingActionButton floatingActionButton;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    private FragmentManager fragmentManager;
    private boolean backPressed = false;

    @OnClick(R.id.floating_action_button)
    public void floatingButtonPressed() {
        android.support.v4.app.Fragment current = fragmentManager.findFragmentById(R.id.activity_root_layout_linear);
        if (current instanceof MyAccount) {
            EditAccount editAccount = (EditAccount) fragmentManager.findFragmentByTag("EDIT_ACCOUNT");
            if (editAccount == null) {
                editAccount = EditAccount.newInstance();
            }
            replaceFragment(editAccount, "EDIT_ACCOUNT", "Edit Account");
            floatingActionButton.setVisibility(View.INVISIBLE);
            navigationView.setCheckedItem(R.id.menu_edit_details);
        }
        if (current instanceof Lister) {
            Lister lister = (Lister) current;
            lister.shareDataAsText();
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
        MyAccount myAccount = (MyAccount) getSupportFragmentManager().findFragmentByTag("MY_ACCOUNT");
        if (myAccount == null) {
            myAccount = MyAccount.newInstance();
        }
        replaceFragment(myAccount, "MY_ACCOUNT", "My Account");
        navigationView.setCheckedItem(R.id.menu_my_account);
        String userName = User.getUserObject(readFromSharedPreferences(Constants.USER_DATA_OBJECT, "")).getName();
        if (userName.contentEquals("-")) {
            getSupportActionBar().setSubtitle(null);
        } else {
            getSupportActionBar().setSubtitle(userName);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (backPressed) {
                super.onBackPressed();
                return;
            }
            this.backPressed = true;
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressed = false;
                }
            }, 2000);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        navigationView.setCheckedItem(id);
        if (id == R.id.menu_my_account) {
            MyAccount myAccount = (MyAccount) fragmentManager.findFragmentByTag("MY_ACCOUNT");
            if (myAccount == null) {
                myAccount = MyAccount.newInstance();
            }
            replaceFragment(myAccount, "MY_ACCOUNT", "My Account");
            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setImageResource(R.drawable.icon_edit);
        } else if (id == R.id.menu_edit_details) {
            EditAccount editAccount = (EditAccount) fragmentManager.findFragmentByTag("EDIT_ACCOUNT");
            if (editAccount == null) {
                editAccount = EditAccount.newInstance();
            }
            replaceFragment(editAccount, "EDIT_ACCOUNT", "Edit Account");
            floatingActionButton.setVisibility(View.INVISIBLE);
        } else if (id == R.id.menu_settings) {
            Settings settings = (Settings) fragmentManager.findFragmentByTag("SETTINGS");
            if (settings == null) {
                settings = Settings.newInstance();
            }
            replaceFragment(settings, "SETTINGS", "Settings");
            floatingActionButton.setVisibility(View.INVISIBLE);
        } else if (id == R.id.menu_logout) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this, R.style.AlertDialogDark);
            AlertDialog dialog;
            builder.setCancelable(false);
            builder.setTitle("Logout of Application?");
            builder.setMessage("Do you want to logout of the Application");
            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    writeToSharedPreferences(Constants.SUCCESSFUL_LOGIN_HISTORY, false);
                    startActivity(new Intent(Home.this, Login.class));
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Fragment fragment = fragmentManager.findFragmentById(R.id.activity_root_layout_linear);
                    if (fragment instanceof MyAccount) {
                        navigationView.setCheckedItem(R.id.menu_my_account);
                    } else if (fragment instanceof EditAccount) {
                        navigationView.setCheckedItem(R.id.menu_edit_details);
                    }
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();

        } else if (id == R.id.travel_history) {

            TravelHistory travelHistory = (TravelHistory) fragmentManager.findFragmentByTag("TRAVEL_HISTORY");
            if (travelHistory == null) {
                travelHistory = TravelHistory.newInstance();
            }
            replaceFragment(travelHistory, "TRAVEL_HISTORY", "Travel History");
            floatingActionButton.setVisibility(View.INVISIBLE);

        } else if (id == R.id.bus_tracker) {
            Lister lister = (Lister) fragmentManager.findFragmentByTag("BUS");
            if (lister == null) {
                lister = Lister.newInstance("BUS");
            }
            replaceFragment(lister, "BUS", "Bus Tracker");
            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setImageResource(R.drawable.icon_share);

        } else if (id == R.id.stop_details) {
            Lister lister = (Lister) fragmentManager.findFragmentByTag("STOP");
            if (lister == null) {
                lister = Lister.newInstance("STOP");
            }
            replaceFragment(lister, "STOP", "Stop Details");
            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setImageResource(R.drawable.icon_share);

        } else if (id == R.id.help) {
            Help help = (Help) fragmentManager.findFragmentByTag("HELP");
            if (help == null) {
                help = Help.newInstance();
            }
            replaceFragment(help, "HELP", "Help");
            floatingActionButton.setVisibility(View.INVISIBLE);

        } else if (id == R.id.about) {
            About about = (About) fragmentManager.findFragmentByTag("ABOUT");
            if (about == null) {
                about = About.newInstance();
            }
            replaceFragment(about, "ABOUT", "About");
            floatingActionButton.setVisibility(View.INVISIBLE);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction() {
        ApplicationHelper.writeToSharedPreferences(Constants.SUCCESSFUL_LOGIN_HISTORY, false);
        startActivity(new Intent(Home.this, Login.class));
        finish();
    }

    private void replaceFragment(Fragment fragment, String tag, String actionBarTitle) {
        fragmentManager.beginTransaction().replace(R.id.activity_root_layout_linear, fragment, tag).commit();
        getSupportActionBar().setTitle(actionBarTitle);
    }
}
