package com.example.smixers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smixers.lessons.CategoryList;
import com.example.smixers.lessons.Mylessons;
import com.example.smixers.ui.Profile;
import com.example.smixers.utils.Tools;
import com.example.smixers.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;



public class MainPage extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG="MainPage";

    TextView users;

    private NavController navController;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private Menu menu_navigation;
    private DrawerLayout drawer;
    private View navigation_header;
    private boolean is_account_mode = false;

    private String userName,userMail,userImage;
    private FirebaseAuth mAuth;
    private AppBarConfiguration mAppBarConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent=getIntent();

        userName=intent.getStringExtra("accountName");
        userMail=intent.getStringExtra("userEmail");
        userImage=intent.getStringExtra("imageUrl");

        initToolbar();
        initNavigationMenu();


    }

    private void initNavigationMenu() {
        final NavigationView nav_view = findViewById(R.id.nav_view);

        drawer = findViewById(R.id.drawer_layout);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_inbox, R.id.nav_about,R.id.nav_profile,R.id.nav_lesson)
                .setOpenableLayout(drawer)
                .build();



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
               updateCounter(nav_view);
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                onItemNavigationClicked(item);
                return true;
            }
        });



        // open drawer at start
      //  drawer.openDrawer(GravityCompat.);
        updateCounter(nav_view);
        menu_navigation = nav_view.getMenu();

        // navigation header
        navigation_header = nav_view.getHeaderView(0);

        TextView name = navigation_header.findViewById(R.id.name);
        TextView email = navigation_header.findViewById(R.id.email);

        name.setText(userName);
        email.setText(userMail);



         navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(nav_view, navController);

        nav_view.setNavigationItemSelectedListener(this);
    }

    private void onItemNavigationClicked(MenuItem item) {
        int id = item.getItemId();


        if (!is_account_mode) {
//            Toast.makeText(getApplicationContext(), item.getTitle() + " Selected", Toast.LENGTH_SHORT).show();
            actionBar.setTitle(item.getTitle());
            drawer.closeDrawers();
        }
    }

    private void updateCounter(NavigationView nav) {
        if (is_account_mode) return;

        Menu m = nav.getMenu();
//
//        ((TextView) m.findItem(R.id.nav_all_inbox).getActionView().findViewById(R.id.text)).setText("75");
//        ((TextView) m.findItem(R.id.nav_inbox).getActionView().findViewById(R.id.text)).setText("68");
//
//        TextView badgePrioInbx = m.findItem(R.id.nav_priority_inbox).getActionView().findViewById(R.id.text);
//        badgePrioInbx.setText("3 new");
//        badgePrioInbx.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
//
//        TextView badgeSocial = m.findItem(R.id.nav_social).getActionView().findViewById(R.id.text);
//        badgeSocial.setText("51 new");
//        badgeSocial.setBackgroundColor(getResources().getColor(R.color.green_500));
//
//        ((TextView) m.findItem(R.id.nav_spam).getActionView().findViewById(R.id.text)).setText("13");
    }

    private void initToolbar() {

         toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Toolbar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Drawer News");

        Tools.setSystemBarColor(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_basic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        drawer.closeDrawers();
        Intent intent;

        return true;

    }


    // [START signOut]
    private void signOut() {
        mAuth.signOut();

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                      //  updateUI(null);

                        finish();
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });


    }



    private void updateUI(@Nullable GoogleSignInAccount account) {

        if (account != null) {
            users.setText(account.getDisplayName());

        } else {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

        }else{
            mAuth.signOut();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
Intent intent;

        Fragment fragment;

        drawer.closeDrawers();


        switch(item.getItemId()) {
            case R.id.nav_home:
                intent = new Intent(this, MainPage.class);
                this.startActivity(intent);
                break;
            case R.id.nav_lesson:
                Utils.navigateToFragment(navController, R.id.nav_priority_inbox, null, this);
                break;

            case R.id.nav_inbox:
//                intent = new Intent(this, Mylessons.class);
//                this.startActivity(intent);
                break;

            case R.id.nav_priority_inbox:

                break;

            case R.id.nav_membership:
               // Utils.navigateToFragment(navController, R.id.nav_membership, null, this);
                break;

            case R.id.nav_profile:
                Utils.navigateToFragment(navController, R.id.nav_profile, null, this);
                break;

            case R.id.nav_about:
                Utils.navigateToFragment(navController,R.id.nav_about,null,this);
                break;

            case R.id.nav_settings:

                break;

            case R.id.nav_help:

                break;

            case R.id.action_settings:
                signOut();
                break;
            default:
                return false;
        }


        return true;
    }
}