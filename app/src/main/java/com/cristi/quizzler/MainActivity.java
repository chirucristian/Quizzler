package com.cristi.quizzler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends Activity {

    Button mTrueButton;
    Button mFalseButton;
    TextView mQuestionTextView;
    Toast mToast;
    TextView mScoreTextView;
    ProgressBar mProgressBar;
    int mQuestionID;
    int mIndex;
    int mScore;

    private ArrayList<TrueFalse> mQuestionBank = new ArrayList<>(Arrays.asList(
            new TrueFalse(R.string.question_1, true),
            new TrueFalse(R.string.question_2, true),
            new TrueFalse(R.string.question_3, true),
            new TrueFalse(R.string.question_4, true),
            new TrueFalse(R.string.question_5, true),
            new TrueFalse(R.string.question_6, false),
            new TrueFalse(R.string.question_7, true),
            new TrueFalse(R.string.question_8, false),
            new TrueFalse(R.string.question_9, true),
            new TrueFalse(R.string.question_10, true),
            new TrueFalse(R.string.question_11, false),
            new TrueFalse(R.string.question_12, false),
            new TrueFalse(R.string.question_13,true)
    ));

    final int PROGRESS_BAR_INCREMENT = (int) Math.ceil(100.0 / mQuestionBank.size());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Collections.shuffle(mQuestionBank);

        // link components to layout
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mScoreTextView = (TextView) findViewById(R.id.score);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // retrieve saved state
        if (savedInstanceState != null) {
            mScore = savedInstanceState.getInt("ScoreKey");
            mIndex = savedInstanceState.getInt("IndexKey");
            mQuestionBank = (ArrayList<TrueFalse>) savedInstanceState.getSerializable("QuestionBank");
        } else {
            mScore = 0;
            mIndex = 0;
        }

        // show the first question from the bank
        mQuestionID = mQuestionBank.get(mIndex).getQuestionID();
        mQuestionTextView.setText(mQuestionID);

        // set score
        mScoreTextView.setText("Score " + mScore + "/" + mQuestionBank.size());

        // execute when pressing True or False
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
                updateQuestion();
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
                updateQuestion();
            }
        });
    }

    private void updateQuestion() {
        mIndex = (mIndex + 1) % mQuestionBank.size();

        if (mIndex == 0) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Game Over!");
            alert.setCancelable(false);
            alert.setMessage("You scored " + mScore + " points!");

            alert.setNegativeButton("Close Application", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            alert.setPositiveButton("Restart Quiz", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mScore = 0;
                    mScoreTextView.setText(R.string.initial_score);
                    mProgressBar.setProgress(0);
                    Collections.shuffle(mQuestionBank);
                    mQuestionTextView.setText(mQuestionBank.get(0).getQuestionID());
                }
            });

            alert.show();
        }

        Log.d("QuestionID", "" + mQuestionID);
        mQuestionID = mQuestionBank.get(mIndex).getQuestionID();
        mQuestionTextView.setText(mQuestionID);
        mProgressBar.incrementProgressBy(PROGRESS_BAR_INCREMENT);
        mScoreTextView.setText("Score " + mScore + "/" + mQuestionBank.size());
    }

    private void checkAnswer(boolean userSelection) {
        boolean correctAnswer = mQuestionBank.get(mIndex).isAnswer();

        if (userSelection == correctAnswer) {
            showToastMessage(R.string.correct_toast);
            ++mScore;
        } else {
            showToastMessage(R.string.incorrect_toast);
        }
    }

    private void showToastMessage(int toastTextResource) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getApplicationContext(), toastTextResource, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("ScoreKey", mScore);
        outState.putInt("IndexKey", mIndex);
        outState.putSerializable("QuestionBank", mQuestionBank);
    }
}
