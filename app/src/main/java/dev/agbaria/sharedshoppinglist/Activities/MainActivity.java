package dev.agbaria.sharedshoppinglist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dev.agbaria.sharedshoppinglist.Fragments.FriendsFragment;
import dev.agbaria.sharedshoppinglist.Fragments.SavedListsFragment;
import dev.agbaria.sharedshoppinglist.Fragments.SharedListsFragment;
import dev.agbaria.sharedshoppinglist.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("agbaria", "auth state changed");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Log.d("agbaria", "user - null");
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );
                    startActivity(intent);
                }
            }
        };

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment shoppingLists = SharedListsFragment.getInstance(mAuth.getCurrentUser().getEmail());
        getSupportFragmentManager().beginTransaction().add(R.id.content_main, shoppingLists).commit();
        setTitle("Shared Lists");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
        if (id == R.id.action_signout) {
            FirebaseAuth.getInstance().signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.shopping_lists) {
            Fragment shoppingLists = new SharedListsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, shoppingLists).commit();
            setTitle("Shared Lists");
        } else if (id == R.id.saved_lists) {
            Fragment savedLists = new SavedListsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, savedLists).commit();
            setTitle("Saved Lists");
        } else if (id == R.id.friends) {
            Fragment friends = new FriendsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, friends).commit();
            setTitle("Friends");
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
