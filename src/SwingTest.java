import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingTest {
    public static void main(String[] args) {
        // Create a new JFrame (main window)
        JFrame frame = new JFrame("Swing Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        // Create a JPanel to hold components
        JPanel panel = new JPanel();
        frame.add(panel);

        // Add a label and a button to the panel
        JLabel label = new JLabel("Hello, Swing!");
        panel.add(label);

        JButton button = new JButton("Click Me");
        panel.add(button);

        // Add action listener to the button
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label.setText("Button clicked!");
            }
        });

        // Display the window
        frame.setVisible(true);
    }
}
