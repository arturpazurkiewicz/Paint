import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        //JFrame frame = new JFrame("Paint");
        Paint frame = new Paint();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,700);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}
