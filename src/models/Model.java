package models;

import models.datastructures.DataScores;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A model where the entire logic of the game must take place
 */
public class Model {
    /*
    hangman_words_ee.db - Estonian words, the leaderboard table is empty
    hangman_words_en.db - English words, the leaderboard table is empty
    hangman_words_ee_test.db - Estonian words, the leaderboard table is NOT empty
     */
    private final String databaseFile = "hangman_words_ee_test.db"; // Default database
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
    private int counter;  // Error counter
    private String playerName = "UNKNOWN";
    private String leaderboardFile = "leaderboard.txt";
    private List<String> scoreData = new ArrayList<>();  // Leaderboard file contents

    /**
     * During the creation of the model, the names of the categories to be shown in the combobox are known
     */
    public Model() {
        new Database(this);
    }

    /**
     * Sets the content to match the ComboBox. Adds "All categories" and unique categories obtained from the database.
     * @param unique all unique categories from database
     */
    public void setCorrectCmbNames(List<String> unique) {
        cmbNames = new String[unique.size()+1];
        cmbNames[0] = chooseCategory; // First choice before categories
        int x = 1;
        for(String category : unique) {
            cmbNames[x] = category;
            x++;
        }
    }

    /**
     * All ComboBox contents
     * @return ComboBox contents
     */
    public String[] getCmbNames() {
        return cmbNames;
    }

    /**
     * Sets a new DefaultTableModel
     * @param dtmScores DefaultTableModel
     */
    public void setDtmScores(DefaultTableModel dtmScores) {
        this.dtmScores = dtmScores;
    }

    /**
     * ALl leaderbaord content
     * @return List<DataScores>
     */
    public List<DataScores> getDataScores() {
        return dataScores;
    }

    /**
     * Sets the new content of the leaderboard
     * @param dataScores List<DataScores>
     */
    public void setDataScores(List<DataScores> dataScores) {
        this.dataScores = dataScores;
    }

    /**
     * Returns the configured database file
     * @return databaseFile
     */
    public String getDatabaseFile() {
        return databaseFile;
    }

    public void startNewGame() {
        getRandomWord(); // Set new word (this.newWord)
        System.out.println(this.newWord);  // For testing
        System.out.println(this.selectedCategory);
        this.userWord = new ArrayList<>();
        this.allUserChars = new ArrayList<>();
        this.counter = 0;
        // Replace all letters with '_' and ' '
        for (int x = 0; x < this.newWord.length(); x++) {
            this.userWord.add('_');
            this.userWord.add(' ');
        }
        this.userWord.remove(this.userWord.size()-1);
    }
    public void sendLetter() {
    }
    /**
     * Returns the user_word as a String
     * @return String representation of the user_word
     */
    public String getUserWord() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Character character : this.userWord) {
            stringBuilder.append(character);
        }
        return stringBuilder.toString();
    }
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
     * Returns the default table model (DefaultTableModel)
     * @return DefaultTableModel
     */
    public DefaultTableModel getDtmScores() {
        return dtmScores;
    }

    /**
     * Returns the images folder
     * @return String
     */
    public String getImagesFolder() {
        return imagesFolder;
    }

    /**
     * Sets up an array of new images
     * @param imageFiles List<String>
     */
    public void setImageFiles(List<String> imageFiles) {
        this.imageFiles = imageFiles;
    }

    /**
     * An array of images
     * @return List<String>
     */
    public List<String> getImageFiles() {
        return imageFiles;
    }

    /**
     * The id of the current image
     * @return id
     */
    public int getImageId() {
        return imageId;
    }

    /**
     * Sets the new image id
     * @param imageId id
     */
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    /**
     * Returns the selected category
     * @return selected category
     */
    public String getSelectedCategory() {
        return selectedCategory;
    }

    /**
     * Sets a new selected category
     * @param selectedCategory new category
     */
    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }
}
