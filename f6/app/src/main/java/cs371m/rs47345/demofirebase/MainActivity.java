package cs371m.rs47345.demofirebase;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FirebaseLoginFragment.FirebaseLoginInterface,
        FirebaseCreateAccountFragment.FirebaseCreateAccountInterface
{
    protected Menu drawerMenu;
    protected ActionBarDrawerToggle toggle;
    public static String TAG = "DemoFirebase";
    protected static String ANON_USER = "Anonymous User";
    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected String userName;
    protected PhotoFragment photoFragment;

    protected void firebaseInit() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    userName = user.getDisplayName();
                    if (userName == null) {
                        userName = ANON_USER; // Anonymous signin returns null
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    userName = null;
                }
                Log.d(TAG, "userName="+userName);
                if( photoFragment != null ) {
                    photoFragment.updateCurrentUserName(userName);
                }
                updateUserDisplay();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Putting it here means you can see it change
                updateUserDisplay();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerMenu = navigationView.getMenu();

        firebaseInit();
        // We might still be logged in
        updateUserDisplay();

        photoFragment = new PhotoFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.main_frame, photoFragment);
        // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }


    ///////////////////////////////////////////////////////////////////////
    // External Interfaces

    public void firebaseLoginFinish() {
        // Dismiss the Login fragment
        getFragmentManager().popBackStack();
        // Toggle back button to hamburger
        toggle.setDrawerIndicatorEnabled(true);
    }
    protected void toggleHamburgerToBack() {
        // Ideas on how to transition toggle from
        // https://stackoverflow.com/questions/28263643/tool-bar-setnavigationonclicklistener-breaks-actionbardrawertoggle-functionality/30951016#30951016
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(getDrawerToggleDelegate().getThemeUpIndicator());
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseLoginFinish();
            }
        });
        // Maybe this would be better, but above works.
        //http://stackoverflow.com/questions/27742074/up-arrow-does-not-show-after-calling-actionbardrawertoggle-setdrawerindicatorena
    }
    public void firebaseFromLoginToCreateAccount() {
        // Dismiss the Login fragment
        getFragmentManager().popBackStack();
        // Toggle back button to hamburger
        toggle.setDrawerIndicatorEnabled(true);
        toggleHamburgerToBack();

        // Replace main screen with the create account fragment
        FirebaseCreateAccountFragment fcaf = FirebaseCreateAccountFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.main_frame, fcaf);
        // Let us pop without explicit fragment remove
        ft.addToBackStack(null);
        // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    // If we return to MainActivity from fragment via Back button,
    // make sure drawer is closed
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        // Toggle back button to hamburger
        toggle.setDrawerIndicatorEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        if (id == R.id.exit) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // We have logged in or out, update all items that display user name
    protected void updateUserDisplay() {
        String loginString = "";
        String userString = userName;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            loginString = String.format("Log out as %s", userName);
        } else {
            userString = "Please log in";
            loginString = "Login";
        }
        TextView dit = (TextView) findViewById(R.id.drawerIDText);
        if (dit != null) {
            dit.setText(userString);
        }
        // findViewById does not work for menu items.
        MenuItem logMenu = (MenuItem) drawerMenu.findItem(R.id.nav_login);
        if (logMenu != null) {
            logMenu.setTitle(loginString);
            logMenu.setTitleCondensed(loginString);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.d("main", "menu option selected");
        if (id == R.id.nav_login)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                mAuth.signOut(); // Will call updateUserDisplay via callback
                return true;
            } else {
                toggleHamburgerToBack();
                FirebaseLoginFragment flf = FirebaseLoginFragment.newInstance();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // Replace any other Fragment with our new Details Fragment with the right data
                ft.add(R.id.main_frame, flf);
                // Let us come back
                ft.addToBackStack(null);
                // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }
        else if (id == R.id.nav_share)
        {

        } else if (id == R.id.nav_send)
        {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
