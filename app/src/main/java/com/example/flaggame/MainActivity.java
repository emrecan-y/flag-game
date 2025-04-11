package com.example.flaggame;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flaggame.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private static String directory;
    private static String packageName;

    private AppBarConfiguration mAppBarConfiguration;
    private static ActivityMainBinding binding;

    private static Menu optionsMenu;

    public static String getDirectory() {
        return directory;
    }
    public static String getPackageNameStatic() {
        return packageName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        packageName = this.getPackageName();
        directory = getApplicationInfo().dataDir;
        FlagGame.init(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_startscreen,
                R.id.nav_game, R.id.nav_stats, R.id.nav_wiki)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        optionsMenu = menu;

        if(FlagGame.gameIsActive()){
            setGameMenuActive();
            setMenuDrawerGame();
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_game);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reset_game) {
            FlagGame.reset();
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_startscreen);
            setMenuDrawerStartScreen();

        } else if (item.getItemId() == R.id.reset_stats) {
           FlagGame.resetGlobalStats();
           Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_stats);
        }
        return super.onOptionsItemSelected(item);
    }


    public static void setGameMenuActive() {
        if (optionsMenu != null) {
            optionsMenu.setGroupVisible(R.id.menu_game, true);
            optionsMenu.setGroupVisible(R.id.menu_stats, false);
        }
    }

    public static void setStatsMenuActive() {
        if (optionsMenu != null) {
            optionsMenu.setGroupVisible(R.id.menu_game, false);
            optionsMenu.setGroupVisible(R.id.menu_stats, true);
        }
    }

    public static void setMenuDeactive() {
        if (optionsMenu != null) {
            optionsMenu.setGroupVisible(R.id.menu_game, false);
            optionsMenu.setGroupVisible(R.id.menu_stats, false);
        }
    }

    public static void setMenuDrawerGame() {
        binding.navView.getMenu().setGroupVisible(R.id.group_game, true);
        binding.navView.getMenu().setGroupVisible(R.id.group_startscreen, false);

    }

    public static void setMenuDrawerStartScreen() {
        binding.navView.getMenu().setGroupVisible(R.id.group_game, false);
        binding.navView.getMenu().setGroupVisible(R.id.group_startscreen, true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }


}