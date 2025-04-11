package com.example.flaggame.ui.game;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.flaggame.AnswerSet;
import com.example.flaggame.Flag;
import com.example.flaggame.FlagGame;
import com.example.flaggame.MainActivity;
import com.example.flaggame.R;
import com.example.flaggame.databinding.FragmentGameBinding;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class GameFragment extends Fragment implements View.OnClickListener {

    private FragmentGameBinding binding;
    private AnswerSet answerSet;
    private List<Button> selectButtons;
    private ViewGroup container;
    private TextView roundsPlayed;
    private TextView roundsWon;
    private TextView roundsLost;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        roundsPlayed.setText(String.valueOf(FlagGame.getRoundsPlayed()));
        roundsWon.setText(String.valueOf(FlagGame.getRoundsWon()));
        roundsLost.setText(String.valueOf(FlagGame.getRoundsLost()));
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GameViewModel galleryViewModel =
                new ViewModelProvider(this).get(GameViewModel.class);
        binding = FragmentGameBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        this.container = container;

        roundsPlayed = binding.statViewRoundNumber;
        roundsWon = binding.statViewWon;
        roundsLost = binding.statViewLost;

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MainActivity.setGameMenuActive();
        showQuestionOnGameView();
        selectButtons = new ArrayList<>();
        selectButtons.add(view.findViewById(R.id.button1));
        selectButtons.add(view.findViewById(R.id.button2));
        selectButtons.add(view.findViewById(R.id.button3));
        selectButtons.add(view.findViewById(R.id.button4));
        for (Button button : selectButtons) {
            button.setOnClickListener(this);
        }
        binding.gameScreen.setOnClickListener(this);
        initButtons();
        fillButtons();

    }


    private void fillButtons() {
        List<Flag> answerChoices = new ArrayList<>(FlagGame.getAnswerSet().getAnswersChoices());
        for (int i = 0; i < selectButtons.size(); i++) {
            selectButtons.get(i).setText(answerChoices.get(i).getCountryName());
        }
    }

    public void statRoundWin() {
        FlagGame.correctAnswer();
        binding.statViewWon.setText(String.valueOf(FlagGame.getRoundsWon()));
        binding.statViewRoundNumber.setText(String.valueOf(FlagGame.getRoundsPlayed()));
    }

    public void statRoundLost() {
        FlagGame.wrongAnswer();
        binding.statViewLost.setText(String.valueOf(FlagGame.getRoundsLost()));
        binding.statViewRoundNumber.setText(String.valueOf(FlagGame.getRoundsPlayed()));
    }

    public void initButtons() {
        for (Button button : selectButtons) {
            button.setEnabled(true);
            button.setTextColor(this.getResources().getColor(R.color.white));
            button.setBackgroundColor(Color.parseColor("#79242A2F"));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showQuestionOnGameView() {
        ImageView img = container.findViewById(R.id.current_flag);
        answerSet = FlagGame.getAnswerSet();
        Flag currentFlag = FlagGame.getCurrentFlag();
        int drawableResourceId = currentFlag.getDrawableResourceId();
        img.setImageResource(drawableResourceId);
    }

    @Override
    public void onClick(View view) {
        if (FlagGame.isShowResultIsActive()) {
            onClickSkip();
        }else if(view.getClass() == MaterialButton.class){

            Button chosen = (Button) view;
            boolean rightAnswer = false;

            // Right or Wrong Answer
            if (chosen.getText().equals(answerSet.getRightAnswer().getCountryName())) {
                chosen.setBackgroundColor(Color.rgb(0, 130, 0));
                statRoundWin();
                rightAnswer=true;
            } else {
                statRoundLost();
            }
            // Button Background Coloring
            for (Button button : selectButtons) {
                button.setEnabled(false);
                if (!rightAnswer) {
                    button.setBackgroundColor(Color.rgb(140, 0, 0));
                    if (button.getText().equals(answerSet.getRightAnswer().getCountryName())) {
                        button.setBackgroundColor(Color.rgb(0, 0, 140));
                    }
                }
            }

                FlagGame.pickRandomCurrentFlag();

        }
    }

    public void onClickSkip() {
        FlagGame.setShowResultIsActive(false);
        showQuestionOnGameView();

        initButtons();
        fillButtons();
    }
}