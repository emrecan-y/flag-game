package com.example.flaggame.ui.stats;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.flaggame.FlagGame;
import com.example.flaggame.MainActivity;
import com.example.flaggame.databinding.FragmentStatsBinding;

public class StatsFragment extends Fragment {

    private FragmentStatsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StatsViewModel homeViewModel =
                new ViewModelProvider(this).get(StatsViewModel.class);

        binding = FragmentStatsBinding.inflate(inflater, container, false);

        return  binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.setStatsMenuActive();
        FlagGame instance = FlagGame.getInstance();
        binding.statisticRoundsPlayed.setText(String.valueOf(instance.getRoundsPlayedGlobal()));
        binding.statisticRoundsLost.setText(String.valueOf(instance.getRoundsLostGlobal()));
        binding.statisticRoundsWon.setText(String.valueOf(instance.getRoundsWonGlobal()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}