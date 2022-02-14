package org.stormroboticsnj.scoutingradar2022;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String themeName = pref.getString(getString(R.string.pref_key_team), getString(R.string.pref_value_team_charge));
        if (themeName.equals(getString(R.string.pref_value_team_charge))) {
            setTheme(R.style.Theme_ScoutingRadar2022_Charge);
        } else {
            setTheme(R.style.Theme_ScoutingRadar2022_RedWatch);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(
                        R.id.nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment, "NO NAV HOST FRAGMENT").getNavController();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(
                item -> NavigationUI.onNavDestinationSelected(item, navController));

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);


    }
}