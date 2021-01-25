import java.awt.*;

import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.Vector;
import javax.swing.*;


class Paint extends JFrame {
    private boolean editable=true;
    private MyButton addCircle, addRectangle, changeColor, addPolygon,save,load,delete,info;
    private JLabel polozenie,rozmiar,kolor,numerFigury;
    private int figure; //0 = koło, 1 = prostokąt , 2 = wielkąt
    private int selectedFigure=-1,selectedPolygon=-1,selectedRegular=-1;
    private Vector<RectangularShape> figures = new Vector<>();
    private Vector<GeneralPath> polygon = new Vector<>();
    private Vector<Integer> order = new Vector<>(); // 0 = koło/prostokąt 1 = wielokąt
    private Vector<Double> sizePolygon = new Vector<>();
    private Vector<Color> colors = new Vector<>();
    private Vector<Position> positions = new Vector<>();
    private Vector<Vector<Position>> allPositions = new Vector<>();
    private Ellipse2D.Float auxiliaryEllipse = new Ellipse2D.Float();
    private int x,x1;
    private int y,y1;

    public Paint() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        addCircle = new MyButton(this,"dodaj koło");
        addRectangle = new MyButton(this,"dodaj prostokąt");
        addPolygon = new MyButton(this,"dodaj wielokąt");
        changeColor = new MyButton(this,"zmień kolor");
        save = new MyButton(this,"zapisz");
        load = new MyButton(this,"wczytaj");
        delete = new MyButton(this,"Usuń wszystko");
        info = new MyButton(this,"INFO");
        delete.setForeground(Color.red);
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Plik");
        final int space = 10;
        menuBar.setLayout(new FlowLayout(FlowLayout.LEFT,space,space));
        menuBar.add(menu);
        menu.add(delete);
        menu.add(info);
        menu.add(save);
        menu.add(load);
        menuBar.add(addCircle);
        menuBar.add(addRectangle);
        menuBar.add(addPolygon);
        menuBar.add(changeColor);

        changeColor.setEnabled(false);
        setJMenuBar(menuBar);

        polozenie = new JLabel();
        rozmiar = new JLabel();
        kolor = new JLabel();
        numerFigury = new JLabel();

        polozenie.setVisible(false);
        rozmiar.setVisible(false);
        kolor.setVisible(false);
        numerFigury.setVisible(false);

        menuBar.add(polozenie);
        menuBar.add(rozmiar);
        menuBar.add(kolor);
        menuBar.add(numerFigury);
        Surface surface = new Surface();

        add(surface);
    }
    class Surface extends JPanel{
        Surface(){
            MovingAdapter ma = new MovingAdapter();

            addMouseMotionListener(ma);
            addMouseListener(ma);
            addMouseWheelListener(new ScaleHandler());

        }

        private void doDrawing(Graphics g) {
            if (!editable){
                selectedFigure=-1;
            }
            try {
                polozenie.setVisible(true);
                rozmiar.setVisible(true);
                kolor.setVisible(true);
                changeColor.setEnabled(true);
                numerFigury.setVisible(true);
                if (order.get(selectedFigure)==0){
                    polozenie.setText("Położenie: " + (int)figures.get(selectedRegular).getX() + " " + (int)figures.get(selectedRegular).getY());
                    rozmiar.setText("Wymiary: "+ figures.get(selectedRegular).getHeight()+" "+figures.get(selectedRegular).getWidth());
                    kolor.setText("Kolor: r:"+ colors.get(selectedFigure).getRed()+" g:"+colors.get(selectedFigure).getGreen()+" b:"+colors.get(selectedFigure).getBlue());
                }else {
                    rozmiar.setVisible(false);
                    polozenie.setText("Położenie: " + (int)polygon.get(selectedPolygon).getBounds2D().getX() + " " + (int)polygon.get(selectedPolygon).getBounds2D().getY());
                    kolor.setText("Kolor: r:"+ colors.get(selectedFigure).getRed()+" g:"+colors.get(selectedFigure).getGreen()+" b:"+colors.get(selectedFigure).getBlue());
                }
                numerFigury.setText("Warstwa: "+selectedFigure);

            }catch (ArrayIndexOutOfBoundsException e){
                polozenie.setVisible(false);
                rozmiar.setVisible(false);
                kolor.setVisible(false);
                numerFigury.setVisible(false);
                changeColor.setEnabled(false);
            }

            Graphics2D g2d = (Graphics2D) g;

            Font font = new Font("Serif", Font.BOLD, 40);
            g2d.setFont(font);
            g2d.setStroke(new BasicStroke(4.0f));

            int j=0;
            int k=0;
            for (int i=0;i<order.size();i++) {
                if (order.get(i) ==0){
                    g2d.setPaint(colors.get(i));
                    g2d.fill(figures.get(j));
                    j++;
                }else if (order.get(i)==1){
                    resizingPolygon(k);
                    g2d.setPaint(colors.get(i));
                    g2d.fill(polygon.get(k));
                    k++;
                }

                if(selectedFigure==i) {
                    g2d.setPaint(Color.red);
                    if (order.get(i)==0){
                        //      System.out.println(i+" "+j);
                        g2d.draw(figures.get(selectedRegular));
                    }else if (order.get(i)==1){
                        //    System.out.println(i);
                        g2d.draw(polygon.get(selectedPolygon));
                    }
                }
            }
            if (figure==2){
                try {
                    GeneralPath path = new GeneralPath(Path2D.WIND_NON_ZERO);
                    Position p = positions.get(0);
                    g2d.setPaint(Color.GREEN);
                    path.moveTo(positions.get(0).getX(), positions.get(0).getY());
                    for (int i = 1; i < positions.size(); i++) {
                        path.lineTo(positions.get(i).getX(), positions.get(i).getY());
                    }
                    g2d.draw(path);
                    auxiliaryEllipse.setFrameFromCenter(p.getX(), p.getY(), p.getX()+5, p.getY()+5);
                    g2d.setPaint(Color.red);
                    g2d.fill(auxiliaryEllipse);
                }catch (ArrayIndexOutOfBoundsException e){
                    // System.out.println("Rysuj");
                }
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            doDrawing(g);
        }
    }
    class MovingAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            x = x1 = e.getX();
            y = y1 = e.getY();
            if (editable) {
                selectedFigure=-1;
                selectedRegular=-1;
                selectedPolygon=-1;
                int j=figures.size()-1;
                int k=polygon.size()-1;
               // System.out.println("Polygon size: "+polygon.size());
                for (int i=order.size()-1;i>=0;i--) {
                    if (order.get(i) ==0){
                        if (figures.get(j).contains(x,y)) {
                            selectedFigure = i;
                            selectedPolygon = -1;
                            selectedRegular = j;
                           // System.out.println("f "+selectedFigure);
                            repaint();
                            break;
                        }
                        j--;
                    }else if (order.get(i)==1){
                        if (polygon.get(k).contains(x,y)){
                            selectedFigure = i;
                            selectedRegular = -1;
                            selectedPolygon = k;
                            repaint();
                            break;
                        }
                        k--;
                    }
                }
                repaint();
            }else if (figure == 2){
                positions.add(new Position(x,y));

                if (auxiliaryEllipse.getBounds2D().contains(x,y)){
                    order.add(1);
                    allPositions.add(positions);
                    GeneralPath path = new GeneralPath();
                    path.moveTo(positions.get(0).getX(), positions.get(0).getY());
                    for (int j = 1; j < positions.size() - 1; j++) {
                        path.lineTo(positions.get(j).getX(), positions.get(j).getY());
                    }
                    path.closePath();
                    polygon.add(path);
                    sizePolygon.add(1.0);
                    colors.add(new Color(randomColor(),randomColor(),randomColor()));
                    unblocking();
                }
                repaint();
               // System.out.println("Klik");
            }
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            if (!editable) {
                if (Math.abs(e.getX() - x1) > 5) {
                    switch (figure) {
                        case 0: {
                            figures.add(new Ellipse2D.Float(x1, y1, Math.abs(e.getX() - x1), Math.abs(e.getX() - x1)));
                            order.add(0);
                            unblocking();
                            break;
                        }
                        case 1: {
                            figures.add(new Rectangle2D.Float(x1, y1, Math.abs(e.getX() - x1), Math.abs(e.getY() - y1)));
                            order.add(0);
                            unblocking();
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (editable) {
                doMove(e);
            }
        }

        private void doMove(MouseEvent e) {
            int dx = e.getX() - x;
            int dy = e.getY() - y;
            if (selectedRegular!=-1) {
                figures.get(selectedRegular).setFrame(figures.get(selectedRegular).getX() + dx, figures.get(selectedRegular).getY() + dy, figures.get(selectedRegular).getWidth(), figures.get(selectedRegular).getHeight());
            }else if (selectedPolygon!=-1){
               // System.out.println(selectedPolygon);
                movingPolygon(dx,dy,selectedPolygon);
                reloadPolygon(selectedPolygon);
            }
            repaint();

            x += dx;
            y += dy;
        }
    }


    class ScaleHandler implements MouseWheelListener {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

            doScale(e);
        }

        private void doScale(MouseWheelEvent e) {
            if (editable) {
                if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                    float amount = e.getWheelRotation() * 5f;
                    if (selectedRegular!=-1){
                        figures.get(selectedRegular).setFrame(figures.get(selectedRegular).getX(),figures.get(selectedRegular).getY(),Math.abs(figures.get(selectedRegular).getWidth()+amount),Math.abs(figures.get(selectedRegular).getHeight()+amount));
                    }else if (selectedPolygon!=-1){
                        if (e.getWheelRotation()==-1){
                            //System.out.println(sizePolygon.get(selectedPolygon));
                            sizePolygon.set(selectedPolygon,Math.abs(sizePolygon.get(selectedPolygon)-0.01));
                            repaint();
                        }else if (e.getWheelRotation()==1){
                            //System.out.println(sizePolygon.get(selectedPolygon));
                            sizePolygon.set(selectedPolygon,sizePolygon.get(selectedPolygon)+0.01);
                            repaint();
                        }
                    }
                    repaint();
                }
            }
        }
    }

    public void addRectangle(){
        addRectangle.setBackground(Color.GREEN);
        editable=false;
        addCircle.setEnabled(false);
        addPolygon.setEnabled(false);
        figure = 1;
        repaint();
    }
    public void addEllipse(){
        addCircle.setBackground(Color.GREEN);
        editable=false;
        addRectangle.setEnabled(false);
        addPolygon.setEnabled(false);
        figure = 0;
        repaint();
    }
    public void addPolygon(){
        addPolygon.setBackground(Color.GREEN);
        editable=false;
        addCircle.setEnabled(false);
        addRectangle.setEnabled(false);
        figure = 2;
        positions = new Vector<>();
        repaint();
    }

    private int randomColor(){
        return (int)(Math.random()*256);
    }
    public void changeColor(){
        Color c = JColorChooser.showDialog(null,"Zmień kolor",colors.get(selectedFigure));
        if (c!=null){
            colors.set(selectedFigure,c);
        }
        repaint();
    }
    private void unblocking(){
        colors.add(new Color(randomColor(),randomColor(),randomColor()));
        addRectangle.setBackground(Color.LIGHT_GRAY);
        addPolygon.setBackground(Color.LIGHT_GRAY);
        addCircle.setBackground(Color.LIGHT_GRAY);
        repaint();
        editable = true;
        addCircle.setEnabled(true);
        addPolygon.setEnabled(true);
        addRectangle.setEnabled(true);
        figure = -1;

    }

    private void movingPolygon(int dx,int dy,int polygonNumber){
        //System.out.println(polygonNumber);
        Vector<Position> selected = allPositions.get(polygonNumber);
        for (Position position : selected) {
            position.setX(position.getX() + dx);
            position.setY(position.getY() + dy);
        }
    }
    private void resizingPolygon(int polygonNumber){
        Vector<Position> selected = allPositions.get(polygonNumber);
        GeneralPath path = new GeneralPath(Path2D.WIND_NON_ZERO);
        double a1 = selected.get(0).getX();
        double b1 = selected.get(0).getY();
        double amount =sizePolygon.get(polygonNumber);
        path.moveTo(a1, b1);
        for (int i=1;i<selected.size()-1;i++){
            double a = selected.get(i).getX();
            double b = selected.get(i).getY();
            a = a1+(a-a1)*amount;
            b = b1+(b-b1)*amount;
           // System.out.println(a+" "+b);
            path.lineTo(a,b);
        }
        path.closePath();
        try {
            polygon.set(polygonNumber, path);
        }catch (ArrayIndexOutOfBoundsException e){
            polygon.add(path);
        }
    }


    public void reloadPolygon(int polygonNumber){
        Vector<Position> p = new Vector<>();
        p = allPositions.get(polygonNumber);
        GeneralPath path = new GeneralPath(Path2D.WIND_NON_ZERO);
        path.moveTo(p.get(0).getX(), p.get(0).getY());
        for (int j = 1; j < p.size() - 1; j++) {
            path.lineTo(p.get(j).getX(), p.get(j).getY());
        }
        path.closePath();
        polygon.set(polygonNumber,path);
    }
    public void save(){
        FileDialog dialog = new FileDialog((Frame) null, "Wybierz plik", FileDialog.SAVE);
        dialog.setFile("*.paint");
        dialog.setVisible(true);
        String filename = dialog.getFile();
        if (filename != null) {
            if (!filename.toLowerCase().endsWith("*.paint")){
                filename+=".paint";
            }
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(dialog.getDirectory()+filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ObjectOutputStream p = null;
            try {
                p = new ObjectOutputStream(outputStream);
                p.writeObject(order);
                p.writeObject(colors);
                p.writeObject(allPositions);
                p.writeObject(sizePolygon);
                p.writeObject(figures);
                p.flush();
                outputStream.close();
                System.out.println("zapisano");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void load() {

        FileDialog dialog = new FileDialog((Frame) null, "Wybierz plik", FileDialog.LOAD);
        dialog.setFile("*.paint");
        dialog.setVisible(true);
        String filename = dialog.getFile();
        if (filename != null){
            System.out.println("You chose " + dialog.getDirectory());
            FileInputStream inputStream = null;
            delete();
            try {
                inputStream = new FileInputStream(dialog.getDirectory()+filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ObjectInputStream p;
            try {
                p = new ObjectInputStream(inputStream);
                try {
                    order = (Vector<Integer>) p.readObject();
                    colors = (Vector<Color>) p.readObject();
                    allPositions = (Vector<Vector<Position>>) p.readObject();
                    sizePolygon = (Vector<Double>) p.readObject();
                    figures = (Vector<RectangularShape>) p.readObject();
                    repaint();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void delete(){
        selectedPolygon=-1;
        selectedFigure=-1;
        selectedRegular=-1;
        order.clear();
        colors.clear();
        allPositions.clear();
        sizePolygon.clear();
        figures.clear();
        polygon.clear();
        positions.clear();
        System.out.println("usuwanie");
        repaint();
    }
}
class Position implements Serializable {
    private int x;
    private int y;
    Position(int x,int y){
        this.x=x;
        this.y=y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
