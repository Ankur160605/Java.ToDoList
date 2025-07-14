import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class TodoListFrame extends JFrame {
    private DefaultListModel<Task> todoListModel;
    private JList<Task> todoList;
    private JTextField taskInput;
    private JComboBox<String> priorityComboBox;
    private JCheckBox completedCheckBox;
    private Preferences prefs;
    private static final String TASKS_KEY = "todo_tasks";

    public TodoListFrame() {
        setTitle("To-Do List App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setBackground(Color.YELLOW);
        getContentPane().setBackground(new Color(13,43,161));
        prefs = Preferences.userNodeForPackage(TodoListFrame.class);

        todoListModel = new DefaultListModel<>();
        todoList = new JList<>(todoListModel);
        todoList.setCellRenderer(new TaskListRenderer());
        taskInput = new JTextField();
        taskInput.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 102)),BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        taskInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskInput.setBackground(new Color(240,247,245));
        taskInput.setForeground(new Color(60, 60, 60));
        priorityComboBox = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        priorityComboBox.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
            int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return this;
            }
        });
        priorityComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priorityComboBox.setBackground(Color.BLUE);
        priorityComboBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 210, 220)),
    BorderFactory.createEmptyBorder(0, 0, 0, 0)));
    completedCheckBox = new JCheckBox("Completed");

        JButton addButton = new JButton("Add Task");
        JButton removeButton = new JButton("Remove Selected");
        JButton clearButton = new JButton("Clear All");
        JButton saveButton = new JButton("Save Tasks");
        JButton loadButton = new JButton("Load Tasks");

        
JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
inputPanel.setBackground(new Color(245,248,252));

JPanel inputContainer = new JPanel(new BorderLayout()) {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(0, 0, 0, 20));
        g2d.fillRoundRect(2, 3, getWidth()-4, getHeight()-4, 15, 15);
    }
};

inputContainer.setOpaque(false);
inputContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
inputContainer.add(taskInput, BorderLayout.CENTER);
priorityComboBox.setBackground(Color.WHITE);
priorityComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
priorityComboBox.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
JPanel inputRow = new JPanel(new BorderLayout(10, 0));
inputRow.setOpaque(false);
inputRow.add(inputContainer, BorderLayout.CENTER);
inputRow.add(priorityComboBox, BorderLayout.EAST);

inputPanel.add(inputRow, BorderLayout.NORTH);
inputPanel.add(completedCheckBox, BorderLayout.CENTER);
        

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 1,1));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        setLayout(new BorderLayout(5, 5));
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(todoList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addTask());
        addButton.setBackground(new Color(0,153,0));
        addButton.setForeground(Color.WHITE);
        removeButton.addActionListener(e -> removeSelectedTask());
        removeButton.setBackground(new Color(153,0,0));
        removeButton.setForeground(Color.WHITE);
        clearButton.addActionListener(e -> clearAllTasks());
        clearButton.setBackground(new Color(51,0,102));
        clearButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveTasks());
        saveButton.setBackground(new Color(153,0,0));
        saveButton.setForeground(Color.WHITE);
        loadButton.addActionListener(e -> loadTasks());
        loadButton.setBackground(new Color(0,153,0));
        loadButton.setForeground(Color.WHITE);
        taskInput.addActionListener(e -> addTask());


        // Load saved tasks on startup
        loadTasks();

        // Set up window listener to save on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveTasks();
            }
        });
    }

    private void addTask() {
        String description = taskInput.getText().trim();
        if (!description.isEmpty()) {
            String priority = (String) priorityComboBox.getSelectedItem();
            boolean completed = completedCheckBox.isSelected();
            Task newTask = new Task(description, priority, completed);
            todoListModel.addElement(newTask);
            taskInput.setText("");
            completedCheckBox.setSelected(false);
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a task description", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedTask() {
        int selectedIndex = todoList.getSelectedIndex();
        if (selectedIndex != -1) {
            todoListModel.remove(selectedIndex);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to remove", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearAllTasks() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear all tasks?", 
            "Confirm Clear", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            todoListModel.clear();
        }
    }

    private void saveTasks() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            
            ArrayList<Task> tasks = new ArrayList<>();
            for (int i = 0; i < todoListModel.size(); i++) {
                tasks.add(todoListModel.getElementAt(i));
            }
            
            oos.writeObject(tasks);
            prefs.putByteArray(TASKS_KEY, baos.toByteArray());
            JOptionPane.showMessageDialog(this, "Tasks saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving tasks: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTasks() {
        try {
            byte[] taskBytes = prefs.getByteArray(TASKS_KEY, null);
            if (taskBytes != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(taskBytes);
                ObjectInputStream ois = new ObjectInputStream(bais);
                
                @SuppressWarnings("unchecked")
                ArrayList<Task> tasks = (ArrayList<Task>) ois.readObject();
                
                todoListModel.clear();
                for (Task task : tasks) {
                    todoListModel.addElement(task);
                }
                JOptionPane.showMessageDialog(this, "Tasks loaded successfully!");
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading tasks: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    class TaskListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    


            Task task = (Task) value;

            setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(128,128,128)),
            BorderFactory.createEmptyBorder(7, 7, 7, 7)
        ));

         if (!isSelected) {
            setBackground(index % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
        }
        else {
            setBackground(new Color(207,244,252));
        }

        setText("<html><div style='padding: 3px'>" + 
               task.getDescription() + 
               "<br><small><font color='#666666'>" + 
               task.getPriority() + 
               (task.isCompleted() ? " • Completed" : "") + 
               "</font></small></div></html>");
        
        
            
            
            
            if (task.isCompleted()) {
                setForeground(Color.GRAY);
                setFont(getFont().deriveFont(Font.ITALIC));
            } else {
                switch (task.getPriority()) {
                    case "High":
                        setForeground(new Color (200,0,0));
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    case "Medium":
                        setForeground(new Color(210,105,0));
                        break;
                    case "Low":
                        setForeground(new Color(0,128,0));
                        break;
                }
            }
            
            return this;
        }
    }
}

class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private String description;
    private String priority;
    private boolean completed;

    public Task(String description, String priority, boolean completed) {
        this.description = description;
        this.priority = priority;
        this.completed = completed;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return description + " (" + priority + ")" + (completed ? " - DONE" : "");
    }
}