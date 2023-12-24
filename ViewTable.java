package finaiProjectFile;

// running successfully with good interface and Correct timing labels

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewTable extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/timetable_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "T#9758@qlph";

    private static JPanel headerPanel; // Declare as an instance variable

    private static JPanel headerPanel1; // Declare as an instance variable

    private String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private String[] times = {"09:30-10:30 AM", "10:30-11:30 AM", "11:30-12:30 PM", "12:30-01:30 PM", "01:30-02:30 PM", "02:30-03:30 PM", "03:30-04:30 PM"};

    public ViewTable(String tableName) {
        setTitle("Timetable Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 365);
        setLocationRelativeTo(null);

        //creating icon image
        ImageIcon image = new ImageIcon("picture/logo.jpg");
        setIconImage(image.getImage());

        // Create a JPanel for the heading
        headerPanel = new JPanel(); // Initialize here
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.PAGE_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create and customize the heading label
        JLabel headingLabel = new JLabel("Time Table Generated on :  " + tableName);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 25));
        headingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(headingLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Create a JPanel for the below paragraph
        headerPanel1 = new JPanel(); // Initialize here
        headerPanel1.setLayout(new BoxLayout(headerPanel1, BoxLayout.PAGE_AXIS));
        headerPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        // Create and customize the paragraph label
        JLabel paragraphLabel = new JLabel( "© Copyright 2023 | DEVELOPED BY : SYED NAVEED ULLAH HUSSAINI | CSE-2C");
        paragraphLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        paragraphLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel1.add(paragraphLabel);

        add(headerPanel1, BorderLayout.SOUTH);



        // Create timetable headers
        String[] columns = new String[days.length + 1];
        columns[0] = "Time/Day";
        System.arraycopy(days, 0, columns, 1, days.length);

        // Create timetable table model
        DefaultTableModel model = new DefaultTableModel(columns, times.length);

        // Populate time column
        for (int i = 0; i < times.length; i++) {
            model.setValueAt(times[i], i, 0);
        }

        // Retrieve timetable data from the database
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.createStatement();

            String selectQuery = "SELECT * FROM `" + tableName + "`";
            rs = stmt.executeQuery(selectQuery);

            // Populate (or) filling timetable cells
            while (rs.next()) {
                String time = rs.getString("time");
                String day = rs.getString("day");
                String event = rs.getString("event");

                int rowIndex = -1;
                for (int i = 0; i < times.length; i++) {
                    if (times[i].equalsIgnoreCase(time)) {
                        rowIndex = i;
                        break;
                    }
                }

                int columnIndex = -1;
                for (int i = 0; i < days.length; i++) {
                    if (days[i].equalsIgnoreCase(day)) {
                        columnIndex = i + 1;
                        break;
                    }
                }

                if (rowIndex >= 0 && columnIndex >= 0) {
                    model.setValueAt(event, rowIndex, columnIndex);
                }
            }

            // Create timetable table
            JTable timetableTable = new JTable(model);
            timetableTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            timetableTable.setRowHeight(30);
            timetableTable.setFont(timetableTable.getFont().deriveFont(Font.PLAIN, 13)); // Set cell font size
            timetableTable.getTableHeader().setFont(timetableTable.getTableHeader().getFont().deriveFont(Font.BOLD, 16)); // Increase header font size
            JScrollPane scrollPane = new JScrollPane(timetableTable);
            add(scrollPane, BorderLayout.CENTER);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving timetable data.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null)
                    rs.close();
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
            String input = JOptionPane.showInputDialog("Enter date: (e.g., 30-06-2023):");
            String[] dateParts = input.split("-");
            if (dateParts.length == 3) {
                String tableName = dateParts[0] + "_" + dateParts[1] + "_" + dateParts[2];
                ViewTable timetableViewer = new ViewTable(tableName);
                timetableViewer.setVisible(true);

                // Set the background color of the headerPanel (accessible here)
                headerPanel.setBackground(Color.getHSBColor(194, 37, 90));

                // Set the background color of the paragraph (accessible here)
                headerPanel1.setBackground(Color.getHSBColor(196, 34, 90));
            } else {
                JOptionPane.showMessageDialog(null, "Invalid date format provided.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

