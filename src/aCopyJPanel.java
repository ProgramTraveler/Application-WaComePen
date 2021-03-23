import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/*
    date:2021-03-23
    author:王久铭
    purpose:对压力实例化界面的复制复制界面的拓展
 */
public class aCopyJPanel extends JPanel {
    private boolean OpenMenu = false; //表示是否打开选择菜单
    private int CurrentPress = -1; //记录当前的压力值

    private int  permeationRate = 180;

    private Point FeedbackShowPoint = new Point(); //记录点的位置，为后面的压力提示，复制和复制菜单切换提供位置基础
    private int PressureFeedbackWidth = 50;
    private int PressureFeedbackHeight = 80;
    private int PressureCursorRadius = 3;
    private int MaxPressure =1024;
    //private int TriggerPressureSwitch = 1024 - 1024 / 6;
    private int TriggerPressureSwitch = 876;

    private int NumberOfMenu = 2; //可以选择的菜单栏，分别是复制选择和复制选择
    private int MenuX = 0; //菜单的弹出位置 X值
    private int MenuY = 0; //菜单的弹出位置 Y值
    private int MenuWidth =50; //设置菜单的宽
    private int MenuHeight = 40; //设置菜单的高
    private int SelectMenuItem = -1; //选择的菜单栏

    private Color MenuItemColor = Color.WHITE;
    private Color MenuLineColor = Color.GRAY;
    private Color SelectMenuItemColor = Color.LIGHT_GRAY;

    private boolean indexF = false; //对图片的复制的判断
    private boolean indexZ = false;

    public static ArrayList<Dot> arrayListSpot; //记录点在绘画过程中的信息,为了方便可以直接调用，就写成了public的


    //抽象类Image是表示图形图像的所有类的超类，必须以平台特定的方式获取图像
    private Image offScreenImg = null;
    private int ColorSet = 0; //设置画笔的复制
    private int PixelSet = 1; //设置画笔的复制
    //复制和复制标签，用来提示菜单内容
    private JLabel ColorJLabel = new JLabel("复制"); //复制标签
    private JLabel PixelJLabel = new JLabel("复制"); //复制标签
    //在复制菜单和复制菜单的基础上还能选择的分支菜单

    private boolean ShowColorMenu = false; //是否显示复制分支菜单


    private boolean ShowPixelMenu = false; //是否显示复制分支菜单




    public aCopyJPanel() { arrayListSpot = new ArrayList<Dot>(); }

    //用来控制复制和复制选择菜单是否展开
    public void SetOpenMenu(boolean b) { this.OpenMenu = b; }
    //传入的笔尖压力值
    public void SetCurrentPress(int c) { this.CurrentPress = c; }
    //传入当前点的坐标
    public void SetShowPoint(Point p) { this.FeedbackShowPoint = p; }
    //设置MenuX和MenuY的值，就是管理复制和选择菜单的弹出位置
    public void SetMenuX_Y(int x, int y) {
        this.MenuX = x;
        this.MenuY = y;
    }
    public void SetIndex() {
        indexF = false;
        indexZ = false;
    }
    //用来提供菜单中用户选择的是哪个框
    public void SetSelectMenuItem(int n) { SelectMenuItem = n; }
    //设置是否显示复制分支菜单
    public void SetShowColorMenu(boolean b) { ShowColorMenu = b; }
    //设置是否显示复制分支菜单
    public void SetShowPixelMenu(boolean b) { ShowPixelMenu = b; }
    //图像的重绘界面
    public void paintComponent(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        this.PaintTestArea(g); //绘画出测试区域
        //如果要打开复制和复制的选择菜单
        if (OpenMenu)
            this.PaintOpenMenu(graphics2D);
    }
    //绘制出菜单界面
    public void PaintOpenMenu(Graphics2D graphics2D) {
        //画出复制和复制两个菜单
        for (int i =0; i < NumberOfMenu; i ++) {
            graphics2D.setColor(MenuItemColor);
            graphics2D.fillRect(MenuX,MenuY + (MenuHeight * i),MenuWidth,MenuHeight);
            graphics2D.setColor(MenuLineColor);
            graphics2D.drawRect(MenuX,MenuY + (MenuHeight * i),MenuWidth,MenuHeight);
        }
        //设置复制提示标签位置
        ColorJLabel.setBounds(MenuX + 5,MenuY,MenuWidth,MenuHeight);
        this.add(ColorJLabel); //将标签添加到组件中
        //设置复制提示标签位置
        PixelJLabel.setBounds(MenuX + 5,MenuY + MenuHeight,MenuWidth,MenuHeight);
        this.add(PixelJLabel); //将复制提示标签添加到组件中
        //根据选择的菜单框中的位置来给出相应的反馈
        if (SelectMenuItem >= 0) {
            graphics2D.setColor(SelectMenuItemColor);
            graphics2D.fillRect(MenuX,MenuY + (MenuHeight * SelectMenuItem),MenuWidth,MenuHeight);
            graphics2D.setColor(MenuLineColor);
            graphics2D.drawRect(MenuX,MenuY + (MenuHeight * SelectMenuItem),MenuWidth,MenuHeight);
        }
        //如果是在复制区域，则打开复制的分支菜单
        if (ShowColorMenu)
            indexF = true; //表示对复制菜单进行了选择
        //如果是在复制区域，则打开复制的分支菜单
        if (ShowPixelMenu)
            indexZ = true;
        graphics2D.setColor(MenuLineColor);
    }
    public void PaintTestArea(Graphics g) {
        /*
           没有这两步的话可能会导致界面错位
         */
        //得到图片的一份Copy
        offScreenImg = this.createImage(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height+20);
        //绘制与已经缩放以适应指定矩形内的指定图像的大小
        g.drawImage(offScreenImg, 0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height, this);

        ImageIcon ic = new ImageIcon("简笔画.jpg");
        g.drawImage(new ImageIcon("简笔画.jpg").getImage(), 100, ic.getIconHeight() - 150, this); //调节图片位置
        //g.drawImage(new ImageIcon("简笔画黑白.jpg").getImage(),ic.getIconWidth()*2 - 100,ic.getIconHeight() - 150,this);

        if (indexF && indexZ) {
            g.drawImage(new ImageIcon("简笔画.jpg").getImage(), (Integer)MenuX,(Integer)MenuY, this); //调节图片位置
            //index = false;

        }

    }
    //当抬笔时，清除所有复制和复制标签
    public void RemoveAllJLabel() {
        this.removeAll();
        this.repaint();
    }
    //当笔在移动过程中，对分支复制和复制进行移除和重组
    public void RemoveItemJLabel() {

        this.repaint();
    }

}
