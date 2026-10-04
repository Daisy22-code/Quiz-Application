package guinterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class QuizApplication extends JFrame implements ActionListener {

   
    JButton arraysButton, loopsButton, conditionalButton, methodsButton, oopButton, exitButton;
    JLabel title;
    JPanel menuPanel;

   
    ArrayList<String> questions = new ArrayList<>();
    ArrayList<String[]> options = new ArrayList<>();
    ArrayList<String> answers = new ArrayList<>();

    int currentQuestion = 0;
    int score = 0;
    int wrongAnswers = 0;

    
    StringBuilder reviewReport = new StringBuilder();

    
    JPanel quizPanel;
    JLabel questionLabel;
    JRadioButton optionA, optionB, optionC, optionD;
    ButtonGroup group;
    JButton nextButton;
    String currentTopic = "";

    public QuizApplication() {
        setTitle("Java Quiz Application");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

       
        menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(7, 1, 10, 10));

        title = new JLabel("Welcome to the Java Quiz Application");
        title.setHorizontalAlignment(SwingConstants.CENTER);

        arraysButton = new JButton("Arrays");
        loopsButton = new JButton("Loops");
        conditionalButton = new JButton("Conditional Statements");
        methodsButton = new JButton("Functions / Methods");
        oopButton = new JButton("Object Oriented Programming");
        exitButton = new JButton("Exit");

        arraysButton.addActionListener(this);
        loopsButton.addActionListener(this);
        conditionalButton.addActionListener(this);
        methodsButton.addActionListener(this);
        oopButton.addActionListener(this);
        exitButton.addActionListener(this);

        menuPanel.add(title);
        menuPanel.add(arraysButton);
        menuPanel.add(loopsButton);
        menuPanel.add(conditionalButton);
        menuPanel.add(methodsButton);
        menuPanel.add(oopButton);
        menuPanel.add(exitButton);

        
        quizPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        questionLabel = new JLabel("", SwingConstants.CENTER);
        optionA = new JRadioButton();
        optionB = new JRadioButton();
        optionC = new JRadioButton();
        optionD = new JRadioButton();
        nextButton = new JButton("Next Question");
        nextButton.addActionListener(this);

        group = new ButtonGroup();
        group.add(optionA);
        group.add(optionB);
        group.add(optionC);
        group.add(optionD);

        quizPanel.add(questionLabel);
        quizPanel.add(optionA);
        quizPanel.add(optionB);
        quizPanel.add(optionC);
        quizPanel.add(optionD);
        quizPanel.add(nextButton);

        add(menuPanel);
        setVisible(true);
    }

   
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == arraysButton) {
            currentTopic = "Arrays";
            loadQuizFromMaster("Section A: Arrays Quiz");
        } else if (e.getSource() == loopsButton) {
            currentTopic = "Section B: Loops Quiz";
            loadQuizFromMaster("Section B: Loops Quiz");
        } else if (e.getSource() == conditionalButton) {
            currentTopic = "Conditionals";
            loadQuizFromMaster("Section C: Conditional Statements Quiz");
        } else if (e.getSource() == methodsButton) {
            currentTopic = "Methods";
            loadQuizFromMaster("Section D: Functions / Methods Quiz");
        } else if (e.getSource() == oopButton) {
            currentTopic = "OOP";
            loadQuizFromMaster("Section E: Object-Oriented Programming Basics Quiz");
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        } else if (e.getSource() == nextButton) {
            checkAnswer();
        }
    }

    public void loadQuizFromMaster(String targetSection) {
        questions.clear();
        options.clear();
        answers.clear();
        reviewReport.setLength(0); 

        try {
            Scanner input = new Scanner(new File("Quiz file.txt"));
            boolean sectionFound = false;

            while (input.hasNextLine()) {
                String line = input.nextLine().trim();

                if (!sectionFound) {
                    if (line.startsWith(targetSection)) {
                        sectionFound = true;
                    }
                    continue; 
                }

                if (line.startsWith("Section ")) {
                    break;
                }

                if (line.startsWith("Question ")) {
                    questions.add(input.nextLine().trim()); 

                    String[] choice = new String[4];
                    choice[0] = input.nextLine().trim(); 
                    choice[1] = input.nextLine().trim(); 
                    choice[2] = input.nextLine().trim(); 
                    choice[3] = input.nextLine().trim(); 
                    options.add(choice);

                    String ansLine = input.nextLine().trim(); 
                    answers.add(ansLine.replace("Correct Answer: ", "").trim());
                }
            }
            input.close();

            currentQuestion = 0;
            score = 0;
            wrongAnswers = 0;

            remove(menuPanel);
            add(quizPanel);
            revalidate();
            repaint();

            showQuestion();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Could not locate 'Quiz file.txt' in your folder.");
        }
    }

    public void showQuestion() {
        if (currentQuestion >= questions.size()) {
            saveResultsToFile();

            
            String resultSummary = "Quiz Completed!\n\n" +
                    "Topic: " + currentTopic + "\n" +
                    "Total Questions: " + questions.size() + "\n" +
                    "Correct Answers: " + score + "\n" +
                    "Wrong Answers: " + wrongAnswers + "\n" +
                    "Final Score: " + score + "/" + questions.size() + "\n\n";

            if (wrongAnswers > 0) {
                resultSummary += "Review your mistakes below:";
                
            
                JTextArea textArea = new JTextArea(reviewReport.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(500, 250));

                JPanel customPanel = new JPanel(new BorderLayout(5, 5));
                customPanel.add(new JLabel(resultSummary), BorderLayout.NORTH);
                customPanel.add(scrollPane, BorderLayout.CENTER);

                JOptionPane.showMessageDialog(this, customPanel, "Quiz Summary Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                resultSummary += "Perfect job! You got every question right!";
                JOptionPane.showMessageDialog(this, resultSummary, "Quiz Summary Results", JOptionPane.INFORMATION_MESSAGE);
            }
            
            remove(quizPanel);
            add(menuPanel);
            revalidate();
            repaint();
            return;
        }

        questionLabel.setText((currentQuestion + 1) + ". " + questions.get(currentQuestion));
        optionA.setText(options.get(currentQuestion)[0]);
        optionB.setText(options.get(currentQuestion)[1]);
        optionC.setText(options.get(currentQuestion)[2]);
        optionD.setText(options.get(currentQuestion)[3]);

        group.clearSelection();
    }

    public void checkAnswer() {
        String selectedAnswer = "";

        if (optionA.isSelected()) selectedAnswer = optionA.getText();
        if (optionB.isSelected()) selectedAnswer = optionB.getText();
        if (optionC.isSelected()) selectedAnswer = optionC.getText();
        if (optionD.isSelected()) selectedAnswer = optionD.getText();

        if (selectedAnswer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an option before moving forward!");
            return;
        }

        String selectionLetter = selectedAnswer.substring(0, 1);
        String correctLetter = answers.get(currentQuestion);

        if (selectionLetter.equalsIgnoreCase(correctLetter)) {
            score++;
        } else {
            wrongAnswers++;
            
          
            reviewReport.append("incorrect Question ").append(currentQuestion + 1).append(": ")
                    .append(questions.get(currentQuestion)).append("\n")
                    .append("   Your Answer: ").append(selectedAnswer).append("\n");
            
            // Find the full textual line of the right answer option to display it nicely
            String explicitRightAnswerText = "";
            for (String choice : options.get(currentQuestion)) {
                if (choice.startsWith(correctLetter)) {
                    explicitRightAnswerText = choice;
                    break;
                }
            }
            reviewReport.append("   Correct Answer: ").append(explicitRightAnswerText).append("\n\n");
        }

        currentQuestion++;
        showQuestion();
    }

    private void saveResultsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("quiz_results.txt", true))) {
            writer.println("=====================================");
            writer.println("Quiz Completed!");
            writer.println("Topic: " + currentTopic);
            writer.println("Total Questions: " + questions.size());
            writer.println("Correct Answers: " + score);
            writer.println("Wrong Answers: " + wrongAnswers);
            writer.println("Final Score: " + score + "/" + questions.size());
            writer.println("=====================================\n");
        } catch (IOException e) {
            System.out.println("Error keeping track of text logs.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizApplication());
    }
}