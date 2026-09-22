package com.example.registromultimedia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Pestaña «Equipo»: listado de integrantes.
 *
 * <p>Lee de {@link IntegranteRepository}, así que refleja lo que se agrega
 * desde el formulario en lugar de una lista de ejemplo propia.</p>
 */
public class IntegranteFragment extends Fragment {

    private RecyclerView recyclerIntegrantes;
    private TextView txtContador;
    private TextView txtVacio;
    private IntegranteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_integrante, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(vista, savedInstanceState);

        recyclerIntegrantes = vista.findViewById(R.id.recyclerIntegrantes);
        txtContador = vista.findViewById(R.id.txtContadorIntegrantes);
        txtVacio = vista.findViewById(R.id.txtEquipoVacio);

        adapter = new IntegranteAdapter(IntegranteRepository.getIntegrantes());

        recyclerIntegrantes.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerIntegrantes.setAdapter(adapter);
        // El alto de cada fila no depende del contenido: ahorra medidas.
        recyclerIntegrantes.setHasFixedSize(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        refrescar();
    }

    private void refrescar() {
        List<Integrante> integrantes = IntegranteRepository.getIntegrantes();
        adapter.actualizar(integrantes);

        boolean vacio = integrantes.isEmpty();
        txtVacio.setVisibility(vacio ? View.VISIBLE : View.GONE);
        recyclerIntegrantes.setVisibility(vacio ? View.GONE : View.VISIBLE);

        txtContador.setText(integrantes.size() == 1
                ? getString(R.string.equipo_contador_uno)
                : getString(R.string.equipo_contador, integrantes.size()));
    }
}
