import javax.swing.SwingUtilities;

public class TodoListApp {
    public static void main(String[] args) {
        System.out.println("Starting application..."); // Add this line
        SwingUtilities.invokeLater(() -> {
            System.out.println("Creating frame..."); // Add this line
            new TodoListFrame().setVisible(true);
        });
    }
}