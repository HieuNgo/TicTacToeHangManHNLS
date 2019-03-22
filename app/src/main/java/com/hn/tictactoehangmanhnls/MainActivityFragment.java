// MainActivityFragment.java
// Contains the Flag Quiz logic
package com.hn.tictactoehangmanhnls;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MainActivityFragment extends Fragment {
   // String used when logging error messages
   private static final String TAG = "FlagQuiz Activity";

   private static final int WORDS_IN_QUIZ = 10;

   private List<String> fileNameList; // hangman file names
   private List<String> wordsList; // words in current quiz
   private String correctAnswer; // correct country for the current word
   private int totalGuesses; // number of guesses made
   private int correctAnswers; // number of correct guesses
   private int guessRows; // number of rows displaying guess Buttons
   private SecureRandom random; // used to randomize the quiz
   private Handler handler; // used to delay loading next images
   private Animation shakeAnimation; // animation for incorrect guess

   private LinearLayout quizLinearLayout; // layout that contains the quiz
   private TextView guessWordTextView; // shows current question #
   private ImageView manImageView; // displays a man
   private LinearLayout row1LinearLayouts; // rows of word textviews
   private LinearLayout row2LinearLayouts; // rows of answer textview and Buttons

   private TextView letter1;
   private TextView letter2;
   private TextView letter3;
   private TextView letter4;
   private TextView letter5;
   private EditText guessLetter;
   private Button answer;  // guess a letter
   private Button changeGame; // change the game

   // configures the MainActivityFragment when its View is created
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);
      View view =
         inflater.inflate(R.layout.fragment_main, container, false);

      fileNameList = new ArrayList<>();
      wordsList = new ArrayList<>();
      random = new SecureRandom();
      handler = new Handler();

      // load the shake animation that's used for incorrect answers
      shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
         R.anim.incorrect_shake);
      shakeAnimation.setRepeatCount(3); // animation repeats 3 times

      // get references to GUI components
      quizLinearLayout =
         (LinearLayout) view.findViewById(R.id.quizLinearLayout);
      guessWordTextView =
         (TextView) view.findViewById(R.id.guessWordTextView);
      manImageView = (ImageView) view.findViewById(R.id.manImageView);
      row1LinearLayouts =
         (LinearLayout) view.findViewById(R.id.row1LinearLayout);
      row2LinearLayouts =
         (LinearLayout) view.findViewById(R.id.row2LinearLayout);
      letter1 = (TextView) view.findViewById(R.id.letter1);
      letter2 = (TextView) view.findViewById(R.id.letter2);
      letter3 = (TextView) view.findViewById(R.id.letter3);
      letter4 = (TextView) view.findViewById(R.id.letter4);
      letter5 = (TextView) view.findViewById(R.id.letter5);
      guessLetter = (EditText) view.findViewById(R.id.guessLetter);
      changeGame = (Button) view.findViewById(R.id.changeGame);
      answer = (Button) view.findViewById(R.id.answer);

      // set questionNumberTextView's text
      guessWordTextView.setText(
         getString(R.string.question, 1, WORDS_IN_QUIZ));
      return view; // return the fragment's view for display
   }


   // set up and start the next quiz
   public void resetQuiz() {
      // use AssetManager to get image file names for enabled regions
      AssetManager assets = getActivity().getAssets();
      wordsList.clear(); // empty list of image file names

      try {
         String[] paths = assets.list("Hangman");

         for (String path : paths)
            fileNameList.add(path.replace(".png", ""));
      }
      catch (IOException exception) {
         Log.e(TAG, "Error loading image file names", exception);
      }

      correctAnswers = 0; // reset the number of correct answers made
      totalGuesses = 0; // reset the total number of guesses the user made
      wordsList.clear(); // clear prior list of quiz countries

      int wordCounter = 1;
      int numberOfWords = fileNameList.size();

      // add FLAGS_IN_QUIZ random file names to the quizCountriesList
      while (wordCounter <= WORDS_IN_QUIZ) {
         int randomIndex = random.nextInt(numberOfWords);

         // get the random file name
         String filename = fileNameList.get(randomIndex);

         // if the region is enabled and it hasn't already been chosen
         if (!wordsList.contains(filename)) {
            wordsList.add(filename); // add the file to the list
            ++wordCounter;
         }
      }

      loadNextFlag(); // start the quiz by loading the first flag
   }

   // after the user guesses a correct flag, load the next flag
   private void loadNextWord() {
      // get file name of the next flag and remove it from the list
      String nextImage = wordsList.remove(0);
      correctAnswer = nextImage; // update the correct answer
      letter1.setText("");// clear answer
       letter2.setText("");// clear answer
       letter3.setText("");// clear answer
       letter4.setText("");// clear answer
       letter5.setText("");// clear answer
      // display current question number
      guessWordTextView.setText(getString(
         R.string.question, (correctAnswers + 1), WORDS_IN_QUIZ));

      // extract the region from the next image's name
      String region = nextImage.substring(0, nextImage.indexOf('-'));

      // use AssetManager to load next image from assets folder
      AssetManager assets = getActivity().getAssets();

      // get an InputStream to the asset representing the next flag
      // and try to use the InputStream
      try (InputStream stream =
              assets.open(region + "/" + nextImage + ".png")) {
         // load the asset as a Drawable and display on the flagImageView
         Drawable flag = Drawable.createFromStream(stream, nextImage);
         flagImageView.setImageDrawable(flag);

         animate(false); // animate the flag onto the screen
      }
      catch (IOException exception) {
         Log.e(TAG, "Error loading " + nextImage, exception);
      }

      Collections.shuffle(fileNameList); // shuffle file names

      // put the correct answer at the end of fileNameList
      int correct = fileNameList.indexOf(correctAnswer);
      fileNameList.add(fileNameList.remove(correct));

      // add 2, 4, 6 or 8 guess Buttons based on the value of guessRows
      for (int row = 0; row < guessRows; row++) {
         // place Buttons in currentTableRow
         for (int column = 0;
              column < guessLinearLayouts[row].getChildCount();
              column++) {
            // get reference to Button to configure
            Button newGuessButton =
               (Button) guessLinearLayouts[row].getChildAt(column);
            newGuessButton.setEnabled(true);

            // get country name and set it as newGuessButton's text
            String filename = fileNameList.get((row * 2) + column);
            newGuessButton.setText(getCountryName(filename));
         }
      }

      // randomly replace one Button with the correct answer
      int row = random.nextInt(guessRows); // pick random row
      int column = random.nextInt(2); // pick random column
      LinearLayout randomRow = guessLinearLayouts[row]; // get the row
      String countryName = getCountryName(correctAnswer);
      ((Button) randomRow.getChildAt(column)).setText(countryName);
   }

   // parses the country flag file name and returns the country name
   private String getCountryName(String name) {
      return name.substring(name.indexOf('-') + 1).replace('_', ' ');
   }

   // animates the entire quizLinearLayout on or off screen
   private void animate(boolean animateOut) {
      // prevent animation into the the UI for the first flag
      if (correctAnswers == 0)
         return;

      // calculate center x and center y
      int centerX = (quizLinearLayout.getLeft() +
         quizLinearLayout.getRight()) / 2; // calculate center x
      int centerY = (quizLinearLayout.getTop() +
         quizLinearLayout.getBottom()) / 2; // calculate center y

      // calculate animation radius
      int radius = Math.max(quizLinearLayout.getWidth(),
         quizLinearLayout.getHeight());

      Animator animator;

      // if the quizLinearLayout should animate out rather than in
      if (animateOut) {
         // create circular reveal animation
         animator = ViewAnimationUtils.createCircularReveal(
            quizLinearLayout, centerX, centerY, radius, 0);
         animator.addListener(
            new AnimatorListenerAdapter() {
               // called when the animation finishes
               @Override
               public void onAnimationEnd(Animator animation) {
                  loadNextFlag();
               }
            }
         );
      }
      else { // if the quizLinearLayout should animate in
         animator = ViewAnimationUtils.createCircularReveal(
            quizLinearLayout, centerX, centerY, 0, radius);
      }

      animator.setDuration(500); // set animation duration to 500 ms
      animator.start(); // start the animation
   }

   // called when a guess Button is touched
   private OnClickListener guessButtonListener = new OnClickListener() {
      @Override
      public void onClick(View v) {
         Button guessButton = ((Button) v);
         String guess = guessButton.getText().toString();
         String answer = getCountryName(correctAnswer);
         ++totalGuesses; // increment number of guesses the user has made

         if (guess.equals(answer)) { // if the guess is correct
            ++correctAnswers; // increment the number of correct answers

            // display correct answer in green text
            answerTextView.setText(answer + "!");
            answerTextView.setTextColor(
               getResources().getColor(R.color.correct_answer,
                  getContext().getTheme()));

            disableButtons(); // disable all guess Buttons

            // if the user has correctly identified FLAGS_IN_QUIZ flags
            if (correctAnswers == FLAGS_IN_QUIZ) {
               // DialogFragment to display quiz stats and start new quiz
               DialogFragment quizResults =
                  new DialogFragment() {
                     // create an AlertDialog and return it
                     @Override
                     public Dialog onCreateDialog(Bundle bundle) {
                        AlertDialog.Builder builder =
                           new AlertDialog.Builder(getActivity());
                        builder.setMessage(
                           getString(R.string.results,
                              totalGuesses,
                              (1000 / (double) totalGuesses)));

                        // "Reset Quiz" Button
                        builder.setPositiveButton(R.string.reset_quiz,
                           new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog,
                                                  int id) {
                                 resetQuiz();
                              }
                           }
                        );

                        return builder.create(); // return the AlertDialog
                     }
                  };

               // use FragmentManager to display the DialogFragment
               quizResults.setCancelable(false);
               quizResults.show(getFragmentManager(), "quiz results");
            }
            else { // answer is correct but quiz is not over
               // load the next flag after a 2-second delay
               handler.postDelayed(
                  new Runnable() {
                     @Override
                     public void run() {
                        animate(true); // animate the flag off the screen
                     }
                  }, 2000); // 2000 milliseconds for 2-second delay
            }
         }
         else { // answer was incorrect
            flagImageView.startAnimation(shakeAnimation); // play shake

            // display "Incorrect!" in red
            answerTextView.setText(R.string.incorrect_answer);
            answerTextView.setTextColor(getResources().getColor(
               R.color.incorrect_answer, getContext().getTheme()));
            guessButton.setEnabled(false); // disable incorrect answer
         }
      }
   };

   // utility method that disables all answer Buttons
   private void disableButtons() {
      for (int row = 0; row < guessRows; row++) {
         LinearLayout guessRow = guessLinearLayouts[row];
         for (int i = 0; i < guessRow.getChildCount(); i++)
            guessRow.getChildAt(i).setEnabled(false);
      }
   }
}


/*************************************************************************
 * (C) Copyright 1992-2016 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/
