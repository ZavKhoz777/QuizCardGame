import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class QuizCardPlayer implements Serializable{
    private JTextArea display;
    private JTextArea answer;
    private ArrayList<QuizCard> cardList;
    private QuizCard currentCard;
    private int currentCardIndex;
    private JFrame frame;
    private JButton nextButton;
    private boolean isShowAnswer;


    public static void main(String[] args) {
        QuizCardPlayer reader = new QuizCardPlayer();
        reader.go();
    }

    public void go() {
        frame = new JFrame("Quiz Card Player");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        Font bigFont = new Font("sanserif", Font.BOLD, 24);

        display = new JTextArea(10,20);
        display.setFont(bigFont);

        display.setLineWrap(true);
        display.setEditable(false);

        JScrollPane qScroller = new JScrollPane(display);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        nextButton = new JButton("Show question");
        mainPanel.add(qScroller);
        mainPanel.add(nextButton);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (isShowAnswer){
                    display.setText(currentCard.getAnswer());
                    isShowAnswer = false;
                }else {
                    if(currentCardIndex < cardList.size()){
                        showNextCard();
                    }else {
                        display.setText("Больше карточек нет!");
                        nextButton.setEnabled(false);
                    }
                }
            }
        });

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadMenuItem = new JMenuItem("Load card set");
        loadMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileOpen = new JFileChooser();
                fileOpen.showOpenDialog(frame);
                loadFile(fileOpen.getSelectedFile());
            }
        });
        JMenuItem createCardItem = new JMenuItem("Create new cards");
        createCardItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                QuizCardBuilder builder = new QuizCardBuilder();
                builder.go();
            }
        });
        fileMenu.add(loadMenuItem);
        fileMenu.add(createCardItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(640,500);
        frame.setVisible(true);
        openCards();
    }

    private void openCards(){
        while (true){
            JFileChooser fileOpen = new JFileChooser();
            fileOpen.showOpenDialog(frame);
            loadFile(fileOpen.getSelectedFile());
            if (cardList != null) break;
        }
    }


    private void loadFile(File file){
        cardList = new ArrayList<QuizCard>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null){
                makeCard(line);
            }
            reader.close();
        }catch (Exception ex){
            System.out.println("Couldn't read the card file");
            ex.printStackTrace();
        }
        showNextCard();
    }

    private void makeCard(String lineToParse){
        String[] result = lineToParse.split("/");
        QuizCard card = new QuizCard(result[0], result[1]);
        cardList.add(card);
        System.out.println("Made a card");
    }

    private void showNextCard(){
        currentCard = cardList.get(currentCardIndex);
        currentCardIndex++;
        display.setText(currentCard.getQuestion());
        nextButton.setText("Show answer");
        isShowAnswer = true;
    }
}
