package dev.agbaria.sharedshoppinglist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dev.agbaria.sharedshoppinglist.Fragments.FriendsFragment;
import dev.agbaria.sharedshoppinglist.Fragments.SharedListsFragment;
import dev.agbaria.sharedshoppinglist.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;
    private boolean isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isFirst = true;

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    isFirst = true;
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );
                    startActivity(intent);
                }
                else if (isFirst){
                    isFirst = false;
                    currentUser = mAuth.getCurrentUser();
                    assert currentUser != null;
                    Fragment shoppingLists = SharedListsFragment.getInstance(currentUser.getEmail());
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.content_main, shoppingLists).commit();
                }
            }
        };
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addList:
                return true;
            case R.id.action_savedLists:
                return true;
            case R.id.action_friends:
            Fragment fragment = FriendsFragment.getInstance(currentUser.getEmail());
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment)
                        .addToBackStack(null).commit();
            return true;
            case R.id.action_profile:
                return true;
            case R.id.action_signout:
                FirebaseAuth.getInstance().signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
