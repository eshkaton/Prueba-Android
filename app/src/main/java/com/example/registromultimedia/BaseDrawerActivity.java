package com.example.registromultimedia;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public abstract class BaseDrawerActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
      protected NavigationView navView;
      protected Toolbar toolbar;

    protected abstract int idPropio();

    protected void configurarMenuLateral() {
              drawerLayout = findViewById(R.id.drawer_layout);
              navView = findViewById(R.id.nav_view);
              toolbar = findViewById(R.id.toolbar);

          setSupportActionBar(toolbar);

          ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            this, drawerLayout, toolbar,
                            R.string.abrir_menu, R.string.cerrar_menu);
              drawerLayout.addDrawerListener(toggle);
              toggle.syncState();

          navView.setCheckedItem(idPropio());

          navView.setNavigationItemSelectedListener(item -> {
                        int id = item.getItemId();
                        drawerLayout.closeDrawer(GravityCompat.START);

                                                                if (id == idPropio()) {
                                                                                  return true;
                                                                } else if (id == R.id.menu_inicio) {
                                                                                  startActivity(new Intent(this, MainActivity.class));
                                                                                  finish();
                                                                } else if (id == R.id.menu_agregar_integrante) {
                                                                                  startActivity(new Intent(this, AgregarIntegranteActivity.class));
                                                                                  finish();
                                                                } else if (id == R.id.menu_lista_integrantes) {
                                                                                  startActivity(new Intent(this, ListaIntegrantesActivity.class));
                                                                                  finish();
                                                                } else if (id == R.id.menu_grabar_audio) {
                                                                                  startActivity(new Intent(this, activity_grabacion_audio.class));
                                                                } else if (id == R.id.menu_reproducir_audio) {
                                                                                  startActivity(new Intent(this, activity_reproductor_audio.class));
                                                                } else if (id == R.id.menu_cerrar_sesion) {
                                                                                  startActivity(new Intent(this, LoginActivity.class));
                                                                                  finishAffinity();
                                                                }
                        return true;
          });
    }

    @Override
      public void onBackPressed() {
                if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                              drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                              super.onBackPressed();
                }
      }
}
