import com.sun.nio.sctp.MessageInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.MessageFormat;

public class MyButton extends JMenuItem {
    MyButton(Paint f, String a){
        super(a);
        this.setBackground(Color.LIGHT_GRAY);
        addActionListener(new MyButtonAdapter(f));
    }
}
class MyButtonAdapter implements ActionListener {
    Paint f;
    MyButtonAdapter(Paint f){
        this.f=f;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String b = e.getActionCommand();
        switch (b) {
            case "dodaj koło" -> {
                f.addEllipse();
                System.out.println("dodawanie koła");
            }
            case "dodaj prostokąt" -> {
                System.out.println("dodawanie prostokąta");
                f.addRectangle();
            }
            case "zmień kolor" -> {
                f.changeColor();
            }
            case "dodaj wielokąt" -> {
                f.addPolygon();
            }
            case "zapisz" -> {
                f.save();
            }
            case "wczytaj" -> {
                f.load();
            }
            case "Usuń wszystko" -> {
                f.delete();
            }
            case "INFO" -> {
                String message = "Nazwa programu: Paint\n" +
                        "Przeznaczenie: tworzenie rysunków\n" +
                        "Autor: Artur Pazurkiewicz";
                JOptionPane.showMessageDialog(f, message, "INFO", JOptionPane.PLAIN_MESSAGE);
            }
            default -> {
                System.out.println("Przycisk nie zdefiniowany");
            }
        }
    }
}
