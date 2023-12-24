package finaiProjectFile;

//final project code
// properly working with background colors

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateTable extends JFrame {
    private String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private String[] times = {"09:30-10:30 AM", "10:30-11:30 AM", "11:30-12:30 PM", "12:30-01:30 PM", "01:30-02:30 PM", "02:30-03:30 PM", "03:30-04:30 PM"};

    private JPanel timetablePanel;
    private JButton submitButton;
    private JPanel inputPanel;
    private List<JTextField> inputFields;

    private JPanel headerPanel; // Declare as an instance variable


    private static final String DB_URL = "jdbc:mysql://localhost:3306/timetable_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "T#9758@qlph";

    private String tableName;
    private int[] duplicates = {5, 5, 5, 4, 3, 1, 1, 1, 1}; // Number of duplicates for each row

    public CreateTable() {
        setTitle("Timetable");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 800));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //creating icon image
        ImageIcon image = new ImageIcon("picture/logo.jpg");
        setIconImage(image.getImage());

        // creating dialog box to take the database table name from the user
        String input = JOptionPane.showInputDialog("Enter Today's date (e.g.: 30-06-2023):");
        String[] dateParts = input.split("-");
        if (dateParts.length == 3) {
            tableName = dateParts[0] + "_" + dateParts[1] + "_" + dateParts[2];
            initializeDatabase(tableName);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid date format provided.", "Error", JOptionPane.ERROR_MESSAGE);
        }


        // Create a JPanel for the heading and paragraph
        headerPanel = new JPanel(); // Initialize here
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.PAGE_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create and customize the heading label
        JLabel headingLabel = new JLabel("Generate Timetable :");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 25));
        headingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(headingLabel);
        // Create and customize the paragraph label
        JLabel paragraphLabel = new JLabel("NOTE : Enter the Subject name according to the mentioned number of Classes needed in left table, and fill the row in the Time table for LUNCH time, and provide NAMAZ cell");
        paragraphLabel.setFont(new Font("Arial", Font.BOLD, 16));
        paragraphLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(paragraphLabel);

        JLabel paragraphLabel1 = new JLabel(" in time table(e.g. Friday 12:30-01:30 PM), then fill the cells in which you want labs, Now click on Submit :");
        paragraphLabel1.setFont(new Font("Arial", Font.BOLD, 16));
        paragraphLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(paragraphLabel1);

        JLabel paragraphLabel2 = new JLabel("( © Copyright 2023 | DEVELOPED BY : SYED NAVEED ULLAH HUSSAINI | CSE-2C )");
        paragraphLabel2.setFont(new Font("Arial", Font.PLAIN, 15));
        paragraphLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(paragraphLabel2);


        add(headerPanel, BorderLayout.NORTH);

        // Create input text fields with labels
        inputPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout for more control
        inputPanel.setBackground(Color.getHSBColor(105, 163, 222)); // Set the background color
        inputFields = new ArrayList<>();

        String[] fieldLabels = {
                "5 Classes:", "5 Classes:", "5 Classes:",
                "4 Classes:", "3 Classes:", "1 Extra curriculum:",
                "1 Extra curriculum:", "1 Extra curriculum:", "1 Extra curriculum:"
        };

        // Set custom UI style for input text fields
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));
            UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10); // Add left and right insets for spacing

        for (int i = 0; i < fieldLabels.length; i++) {
            JLabel label = new JLabel(fieldLabels[i]);
            label.setFont(label.getFont().deriveFont(16f)); // Increase font size to 16
            gbc.gridx = 0;
            gbc.gridy = i;
            inputPanel.add(label, gbc);

            JTextField inputField = new JTextField();
            inputField.setPreferredSize(new Dimension(200, inputField.getPreferredSize().height));
            inputFields.add(inputField);

            gbc.gridx = 1;
            gbc.gridy = i;
            inputPanel.add(inputField, gbc);
        }

        add(inputPanel, BorderLayout.WEST);

        // Create timetable panel
        timetablePanel = new JPanel(new GridLayout(times.length + 2, days.length + 1, 10, 10));
        timetablePanel.setPreferredSize(new Dimension(800, 500));
        timetablePanel.setBackground(Color.LIGHT_GRAY); // Set the background color
        add(timetablePanel, BorderLayout.CENTER);

        // Create timetable headers
        Font labelFont = new Font("Arial", Font.BOLD, 15);
        timetablePanel.add(createHeaderLabel("Time/Day", labelFont));
        for (String day : days) {
            timetablePanel.add(createHeaderLabel(day, labelFont));
        }

        // Create timetable cells
        Font cellFont = new Font("Arial", Font.BOLD, 15);
        for (String time : times) {
            timetablePanel.add(createCellLabel(time, cellFont));
            for (int i = 0; i < days.length; i++) {
                JTextField cell = new JTextField();
                timetablePanel.add(cell);
            }
        }

        // Create submit button
        submitButton = new JButton("Submit");
        submitButton.setFont(submitButton.getFont().deriveFont(18f)); // Increase font size to 16

        // Set button UI to BasicButtonUI for customizing appearance
        submitButton.setUI(new BasicButtonUI());
        submitButton.setBackground(new Color(51, 118, 183)); // Set background color (blue in this example)
        submitButton.setForeground(Color.WHITE); // Set text color

        add(submitButton, BorderLayout.SOUTH);

        // Add action listener to submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitTimetableData();
            }
        });
    }

    private JLabel createHeaderLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    private JLabel createCellLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private void submitTimetableData() {
        assignInputToTimetableCells();

        // Insert timetable data into the database
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            String insertQuery = "INSERT INTO " + tableName + " (day, time, event) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(insertQuery);

            int dayIndex = 0;
            int timeIndex = 0;

            for (Component component : timetablePanel.getComponents()) {
                if (component instanceof JTextField) {
                    JTextField cell = (JTextField) component;
                    String value = cell.getText();
                    if (dayIndex == days.length) {
                        dayIndex = 0;
                        timeIndex++;
                    }
                    pstmt.setString(1, days[dayIndex]);
                    pstmt.setString(2, times[timeIndex]);
                    pstmt.setString(3, value);
                    pstmt.executeUpdate();
                    dayIndex++;
                }
            }

            JOptionPane.showMessageDialog(null, "Timetable data saved successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving timetable data.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void assignInputToTimetableCells() {
        List<String> inputs = new ArrayList<>();
        for (JTextField inputField : inputFields) {
            String input = inputField.getText();
            if (!input.isEmpty()) {
                for (int i = 0; i < duplicates[inputFields.indexOf(inputField)]; i++) {
                    inputs.add(input);
                }
            }
        }

        Collections.shuffle(inputs); // Shuffle the input data

        // Assign input data to empty timetable cells randomly
        List<Component> cells = new ArrayList<>();
        for (Component component : timetablePanel.getComponents()) {
            if (component instanceof JTextField) {
                JTextField cell = (JTextField) component;
                if (cell.getText().isEmpty()) {
                    cells.add(component);
                }
            }
        }

        // Assign input data randomly to the empty timetable cells
        for (String input : inputs) {
            if (cells.isEmpty()) {
                break; // If all cells have been assigned, exit the loop
            }

            int randomIndex = (int) (Math.random() * cells.size()); // Get a random index
            JTextField cell = (JTextField) cells.get(randomIndex);
            cell.setText(input);
            cells.remove(randomIndex); // Remove the assigned cell from the list
        }
    }

    // Initializing the database
    private void initializeDatabase(String tableName) {
        this.tableName = tableName;
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.createStatement();

            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "day VARCHAR(20)," +
                    "time VARCHAR(20)," +
                    "event VARCHAR(255)" +
                    ")";
            stmt.executeUpdate(createTableQuery);

            // Optional: You can add additional initialization code here, such as inserting sample data into the table

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initializing database.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CreateTable timetable = new CreateTable();
            timetable.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the window
            timetable.setVisible(true);

            // Set the background color of the headerPanel (accessible here)
            timetable.headerPanel.setBackground(Color.GRAY);
        });
    }
}
