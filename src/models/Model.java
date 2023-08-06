package models;

import models.datastructures.DataScores;

import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents the Model class for the Hangman game.
 * This class manages the game state, word retrieval from the database, and user input handling.
 */
public class Model {
    private final String databaseFile = "hangman_words_ee_test.db"; // Default database
    private Connection connection = null;
    private String dbUrl = "jdbc:sqlite:" + databaseFile;
    private final String imagesFolder = "images"; // Hangman game images location
    private List<String> imageFiles = new ArrayList<>(); // All images with full folder path
    private String[] cmbNames; // ComboBox categories names (contents)
    private final String chooseCategory = "All categories"; // Default first ComboBox choice
    private DefaultTableModel dtmScores; // Leaderboard DefaultTableModel
    private List<DataScores> dataScores = new ArrayList<>(); // The contents of the entire database table scores
    private int imageId = 0; // Current image id (0..11)
    private String selectedCategory = chooseCategory; // Default all categories as "All categories"
    private String newWord;  // Random word from database
    private List<Character> userWord;  // User found letters (use Character class instead of list of chars)
    private List<Character> allUserChars;  // Any letters user entered incorrectly (use Character class)
    private List<Character> correctChars; // Characters entered correctly
    private int counter;  // Error counter
    private String playerName = "UNKNOWN";
    private String leaderboardFile = "leaderboard.txt";
    private List<String> scoreData = new ArrayList<>();  // Leaderboard file contents
    private int gametime;
    private Connection dbConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
        connection = DriverManager.getConnection(dbUrl);
        return connection;
    }

    public Model() {
        new Database(this);
    }

    public void setCorrectCmbNames(List<String> unique) {
        cmbNames = new String[unique.size() + 1];
        cmbNames[0] = chooseCategory; // First choice before categories
        int x = 1;
        for (String category : unique) {
            cmbNames[x] = category;
            x++;
        }
    }

    public String[] getCmbNames() {
        return cmbNames;
    }

    public void setDtmScores(DefaultTableModel dtmScores) {
        this.dtmScores = dtmScores;
    }

    public List<DataScores> getDataScores() {
        return dataScores;
    }

    public void setDataScores(List<DataScores> dataScores) {
        this.dataScores = dataScores;
    }

    public String getDatabaseFile() {
        return databaseFile;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getGametime() {
        return gametime;
    }

    public void setGametime(int gametime) {
        this.gametime = gametime;
    }

    /**
     * Starts a new game by generating a random word from the database.
     * Resets userWord, allUserChars, correctChars, and the error counter.
     */
    public void startNewGame() {
        getRandomWord(); // Set new word (this.newWord)
        this.userWord = new ArrayList<>();
        this.allUserChars = new ArrayList<>();
        this.correctChars = new ArrayList<>();
        this.counter = 0;

        // Replace all letters with '_' and ' '

        for (int x = 0; x < this.newWord.length(); x++) {
            this.userWord.add('_');
            this.userWord.add(' ');
        }

    }

    /**
     * Updates the user input and replaces '_' with found letters in the userWord.
     *
     * @param userChar The character entered by the user.
     */
    public void changeUserInput(char userChar) {
        // Replace all '_' with found letters
        List<Character> currentWord = charsToList(newWord);
        int x = 0;
        for (char c : currentWord) {
            if (Character.toLowerCase(userChar) == Character.toLowerCase(c)) {
                userWord.set(x, Character.toUpperCase(userChar));
            }
            x = x + 2;
        }
    }

    /**
     * Converts a String to a List of Characters.
     *
     * @param word The String to convert.
     * @return The List of Characters.
     */
    private List<Character> charsToList(String word) {
        List<Character> charList = new ArrayList<>();
        for (char c : word.toCharArray()) {
            charList.add(c);
        }
        return charList;
    }

    /**
     * Retrieves the current user word as a String.
     *
     * @return The user word as a String.
     */
    public String getUserWord() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Character character : this.userWord) {
            stringBuilder.append(character);
        }
        return stringBuilder.toString();
    }

    /**
     * Get the list of wrong user characters
     * @return List<Character> list of all user characters
     */
    public List<Character> getAllUserChars() {
        return allUserChars;
    }

    /**
     * Get the list of correct user characters
     * @return List<Character> list of all user characters
     */
    public List<Character> getCorrectChars() {
        return correctChars;
    }

    /**
     * Get random word from database according to the chosen category
     * @return
     */
    public void getRandomWord() {
        try {
            // Database connection
            String connectionString = "jdbc:sqlite:" + getDatabaseFile();
            Connection connection = DriverManager.getConnection(connectionString);

            // SQL query to select a random word
            String query = "SELECT word FROM words WHERE category = ? OR ? = 'All categories' ORDER BY RANDOM() LIMIT 1";

            // Create a PreparedStatement to execute the query
            PreparedStatement statement = connection.prepareStatement(query);

            // Set the value of the category parameters
            statement.setString(1, selectedCategory);
            statement.setString(2, selectedCategory);

            // Execute the query and get the result set
            ResultSet resultSet = statement.executeQuery();

            // Fetch the word from the result set
            if (resultSet.next()) {
                this.newWord = resultSet.getString("word");
            }

            // Close the resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts the game results into the database as a new scoreboard entry.
     * The method retrieves the play time, player name, guessed word, wrong characters, and game time.
     * The entry is then committed to the database.
     * After the insertion, it also retrieves the updated dataScores.
     */
    public void insertToScoreboard() {
        String sql = "INSERT INTO scores (playertime, playername, guessword, wrongcharacters, gametime) VALUES (?, ?, ?, ?, ?)";
        String wrongLetters = listToString(getAllUserChars()); // Convert List<Character> to a string
        String playerTime = null;
        DataScores endTime = new DataScores(LocalDateTime.now(), getPlayerName(), getNewWord(), wrongLetters, getGametime());
        try {
            Connection conn = this.dbConnection();
            conn.setAutoCommit(false);

            PreparedStatement preparedStmt = conn.prepareStatement(sql);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            playerTime = LocalDateTime.now().format(formatter);
            preparedStmt.setString(1, playerTime);
            preparedStmt.setString(2, getPlayerName());
            preparedStmt.setString(3, getNewWord());
            preparedStmt.setString(4, wrongLetters);
            preparedStmt.setInt(5, getGametime());
            preparedStmt.executeUpdate();

            // Commit and close the resources
            conn.commit();
            preparedStmt.close();
            conn.close();

            getDataScores();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a List of Characters to a String.
     *
     * @param list The List of Characters to convert.
     * @return The String representation of the list.
     */
    private String listToString(List<Character> list) {
        StringBuilder sb = new StringBuilder();
        for (Character ch : list) {
            sb.append(ch);
        }
        return sb.toString();
    }

    public DefaultTableModel getDtmScores() {
        return dtmScores;
    }

    public String getImagesFolder() {
        return imagesFolder;
    }

    public void setImageFiles(List<String> imageFiles) {
        this.imageFiles = imageFiles;
    }

    public List<String> getImageFiles() {
        return imageFiles;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public String getNewWord() {
        return newWord;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
