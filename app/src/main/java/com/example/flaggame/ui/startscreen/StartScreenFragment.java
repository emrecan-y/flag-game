package com.example.flaggame.ui.startscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.flaggame.FlagGame;
import com.example.flaggame.MainActivity;
import com.example.flaggame.R;
import com.example.flaggame.databinding.FragmentStartscreenBinding;

public class StartScreenFragment extends Fragment implements View.OnClickListener {

    private FragmentStartscreenBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StartScreenViewModel galleryViewModel =
                new ViewModelProvider(this).get(StartScreenViewModel.class);
        binding = FragmentStartscreenBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.buttonEasy.setOnClickListener(this);
        binding.buttonHard.setOnClickListener(this);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.setMenuDeactive();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        Button pressedButton = (Button) v;
        String difficulty = pressedButton.getText().toString().toUpperCase();
        FlagGame.start(difficulty);
        Navigation.findNavController(v).navigate(R.id.nav_game);
        MainActivity.setMenuDrawerGame();
        MainActivity.setGameMenuActive();
    }


}