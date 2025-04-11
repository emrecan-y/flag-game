package com.example.flaggame.ui.wiki;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flaggame.Flag;
import com.example.flaggame.FlagGame;
import com.example.flaggame.MainActivity;
import com.example.flaggame.R;
import com.example.flaggame.databinding.FragmentWikiBinding;

import java.util.List;

public class WikiFragment extends Fragment {
    private RecyclerView recyclerView;
    private WikiAdapter wikiAdapter;
    private List<Flag> flags;
    private FragmentWikiBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        WikiViewModel slideshowViewModel =
                new ViewModelProvider(this).get(WikiViewModel.class);

        binding = FragmentWikiBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.wiki;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        flags = FlagGame.getCorrectlyGuessedFlagsCurrentGame();
        wikiAdapter = new WikiAdapter(flags);
        recyclerView.setAdapter(wikiAdapter);
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
}