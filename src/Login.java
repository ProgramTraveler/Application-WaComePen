import javax.swing.*;

public class Login {
    public static void main(String[] arge) {

        SelectMode selectMode = new SelectMode();

        selectMode.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        selectMode.pack();
        selectMode.setLocationRelativeTo(selectMode);
        selectMode.setResizable(false);
        selectMode.setVisible(true);

    }
}
