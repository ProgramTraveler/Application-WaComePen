import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/*
    date:2021-03-20
    author:王久铭
    purpose:该类的作用是将笔压力的动态信息进行图像的动态展示
 */
public class PAExperimentPanel extends JPanel {
    private boolean OpenMenu = false; //表示是否打开选择菜单
    private int CurrentPress = -1; //记录当前的压力值

    private int  permeationRate = 180;
    private Color ClearWhite = new Color( Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue(), permeationRate);
    private Color ClearLightGray = new Color( Color.lightGray.getRed(), Color.lightGray.getGreen(), Color.lightGray.getBlue(), permeationRate);
    private Color ClearBlack = new Color( Color.black.getRed(), Color.black.getGreen(), Color.black.getBlue(), permeationRate);
    private Color ClearRed = new Color( Color.red.getRed(), Color.red.getGreen(), Color.red.getBlue(), permeationRate);

    private Point FeedbackShowPoint = new Point(); //记录点的位置，为后面的压力提示，颜色和像素菜单切换提供位置基础
    private int PressureFeedbackWidth = 50;
    private int PressureFeedbackHeight = 80;
    private int PressureCursorRadius = 3;
    private int MaxPressure =1024;
    //private int TriggerPressureSwitch = 1024 - 1024 / 6;
    private int TriggerPressureSwitch = 876;

    private int NumberOfMenu = 2; //可以选择的菜单栏，分别是颜色选择和像素选择
    private int MenuX = 0; //菜单的弹出位置 X值
    private int MenuY = 0; //菜单的弹出位置 Y值
    private int MenuWidth =50; //设置菜单的宽
    private int MenuHeight = 40; //设置菜单的高
    private int SelectMenuItem = -1; //选择的菜单栏

    private Color MenuItemColor = Color.WHITE;
    private Color MenuLineColor = Color.GRAY;
    private Color SelectMenuItemColor = Color.LIGHT_GRAY;

    public static ArrayList<Dot> arrayListSpot; //记录点在绘画过程中的信息,为了方便可以直接调用，就写成了public的

    private Graphics2D Line; //设置线条的相关信息
    private Graphics2D offScreen; //显示测试区域
    //抽象类Image是表示图形图像的所有类的超类，必须以平台特定的方式获取图像
    private Image offScreenImg = null;
    private int ColorSet = 0; //设置画笔的颜色
    private int PixelSet = 1; //设置画笔的像素
    //颜色和像素标签，用来提示菜单内容
    private JLabel ColorJLabel = new JLabel("颜色"); //颜色标签
    private JLabel PixelJLabel = new JLabel("像素"); //像素标签
    //在颜色菜单和像素菜单的基础上还能选择的分支菜单
    private int NumberColor_Pixel = 3; //颜色和像素分别都可以选择3个
    private int MenuItemWidth = 50; //设置分支菜单的宽
    private int MenuItemHeight = 40; //设置分支菜单的高

    private boolean ShowBack = false; //用来控制是否显示压力的动态图像,默认为不打开

    private boolean ShowColorMenu = false; //是否显示颜色分支菜单
    private int SelectColorItem = -1; //颜色分支菜单中的具体颜色
    private JLabel ColorBlue = new JLabel("蓝色");
    private JLabel ColorRed = new JLabel("红色");
    private JLabel ColorYellow = new JLabel("黄色");

    private boolean ShowPixelMenu = false; //是否显示像素分支菜单
    private int SelectPixelItem = -1; //像素分支菜单中的具体像素
    private JLabel PixelTow = new JLabel("2.0");
    private JLabel PixelThree = new JLabel("3.0");
    private JLabel PixelFour = new JLabel("4.0");

    private int SetColor = 0; //记录被选择的颜色
    private int SetPixel = 1; //记录被选择的像素

    public PAExperimentPanel() { arrayListSpot = new ArrayList<Dot>(); }

    //用来控制像素和颜色选择菜单是否展开
    public void SetOpenMenu(boolean b) { this.OpenMenu = b; }
    //传入的笔尖压力值
    public void SetCurrentPress(int c) { this.CurrentPress = c; }
    //传入当前点的坐标
    public void SetShowPoint(Point p) { this.FeedbackShowPoint = p; }
    //设置MenuX和MenuY的值，就是管理颜色和选择菜单的弹出位置
    public void SetMenuX_Y(int x, int y) {
        this.MenuX = x;
        this.MenuY = y;
    }
    //用来提供菜单中用户选择的是哪个框
    public void SetSelectMenuItem(int n) { SelectMenuItem = n; }
    //用来提供颜色菜单中用户选择的具体颜色
    public void SetSelectColorItem(int n) { SelectColorItem = n; }
    public int GetSelectColorItem() { return SelectColorItem; }
    //用来提供像素菜单中用户选择的具体像素
    public void SetSelectPixelItem(int n) { SelectPixelItem = n; }
    public int GetSelectPixelItem() { return SelectPixelItem; }
    //用来选择是否显示压力的动态图像
    public void SetShowBack(boolean b) { ShowBack = b; }
    //设置是否显示颜色分支菜单
    public void SetShowColorMenu(boolean b) { ShowColorMenu = b; }
    public boolean GetShowColorMenu() { return ShowColorMenu; }
    //设置是否显示像素分支菜单
    public void SetShowPixelMenu(boolean b) { ShowPixelMenu = b; }
    public boolean GetShowPixelMenu() { return ShowPixelMenu; }
    //返回用户选择的颜色（是对应菜单中的颜色）
    public void DefineColor() { SetColor = 0; } //初始化颜色
    public int GetSetColor() { return SetColor; }
    //放回用户选择的像素（是对应菜单中的像素）
    public void DefinePixel() { SetPixel = 1; } //初始化像素
    public int GetSetPixel() { return SetPixel; }
    //图像的重绘界面
    public void paintComponent(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        this.PaintTestArea(g); //绘画出测试区域
        //如果要显示压力动态区域
        /*if (ShowBack)
            this.PaintPressFeedback(graphics2D);
         */
        //如果要打开颜色和像素的选择菜单
        if (OpenMenu)
            this.PaintOpenMenu(graphics2D);
    }
    //绘制出菜单界面
    public void PaintOpenMenu(Graphics2D graphics2D) {
        //画出颜色和像素两个菜单
        for (int i =0; i < NumberOfMenu; i ++) {
            graphics2D.setColor(MenuItemColor);
            graphics2D.fillRect(MenuX,MenuY + (MenuHeight * i),MenuWidth,MenuHeight);
            graphics2D.setColor(MenuLineColor);
            graphics2D.drawRect(MenuX,MenuY + (MenuHeight * i),MenuWidth,MenuHeight);
        }
        //设置颜色提示标签位置
        ColorJLabel.setBounds(MenuX + 5,MenuY,MenuWidth,MenuHeight);
        this.add(ColorJLabel); //将标签添加到组件中
        //设置像素提示标签位置
        PixelJLabel.setBounds(MenuX + 5,MenuY + MenuHeight,MenuWidth,MenuHeight);
        this.add(PixelJLabel); //将像素提示标签添加到组件中
        //根据选择的菜单框中的位置来给出相应的反馈
        if (SelectMenuItem >= 0) {
            graphics2D.setColor(SelectMenuItemColor);
            graphics2D.fillRect(MenuX,MenuY + (MenuHeight * SelectMenuItem),MenuWidth,MenuHeight);
            graphics2D.setColor(MenuLineColor);
            graphics2D.drawRect(MenuX,MenuY + (MenuHeight * SelectMenuItem),MenuWidth,MenuHeight);
        }
        //如果是在颜色区域，则打开颜色的分支菜单
        if (ShowColorMenu)
            this.PaintColorMenuItem(graphics2D);
        //如果是在像素区域，则打开像素的分支菜单
        if (ShowPixelMenu)
            this.PaintPixelMenuItem(graphics2D);
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
        g.drawImage(new ImageIcon("简笔画黑白.jpg").getImage(), 0, 0, this); //调节图片位置
        //转换
        Line = (Graphics2D) g;

        //设置写字板中的测试区域
        offScreen = (Graphics2D) g;
        //offScreen.setColor(Color.GREEN);
        //offScreen.fillRect(200,5,1150,100);
        //颜色和像素区域分隔线
        //offScreen.setColor(Color.white);
        //offScreen.drawLine(583,5,583,105);
        //offScreen.drawLine(966,5,966,105);

        //使用容器中点的信息来画线条
        for (int i = 0; i < arrayListSpot.size() ; i++) {

            double x0 = arrayListSpot.get(i).DotStarX();
            double y0 = arrayListSpot.get(i).DotStarY();
            double x1 = arrayListSpot.get(i).DotEndX();
            double y1 = arrayListSpot.get(i).DotEndY();

            //判断点的颜色
            ColorSet = arrayListSpot.get(i).DotColor();
            if (ColorSet == 0)
                Line.setColor(Color.BLACK);
            else if (ColorSet == 1)
                Line.setColor(Color.BLUE);
            else if (ColorSet == 2)
                Line.setColor(Color.RED);
            else if (ColorSet == 3)
                Line.setColor(Color.ORANGE);

            //判断点的像素
            PixelSet = arrayListSpot.get(i).DotPixel();
            if (PixelSet == 2)
                Line.setStroke(new BasicStroke(2));
            else if (PixelSet == 3)
                Line.setStroke(new BasicStroke(3));
            else if (PixelSet == 4)
                Line.setStroke(new BasicStroke(4));
            //画出线段
            Line2D line = new Line2D.Double(x0,y0,x1,y1);
            Line.draw(line);
            Line.setStroke(new BasicStroke(1));
        }
    }
    //当抬笔时，清除所有颜色和像素标签
    public void RemoveAllJLabel() {
        this.removeAll();
        this.repaint();
    }
    //当笔在移动过程中，对分支颜色和像素进行移除和重组
    public void RemoveItemJLabel() {
        //移除所有的颜色组件
        this.remove(ColorBlue);
        this.remove(ColorRed);
        this.remove(ColorYellow);
        //移除所有的像素组件
        this.remove(PixelTow);
        this.remove(PixelThree);
        this.remove(PixelFour);

        this.repaint();
    }
    //绘制出颜色的选择栏，分别为蓝色，红色和黄色
    public void PaintColorMenuItem(Graphics2D graphics2D) {
        //绘制出三个颜色区域，分别存放蓝色，红色和黄色
        for (int i = 0; i < NumberColor_Pixel; i ++) {
            graphics2D.setColor(MenuItemColor);
            graphics2D.fillRect(MenuX + MenuWidth,MenuY + (MenuItemHeight * i),MenuItemWidth,MenuItemHeight);
            graphics2D.setColor(MenuLineColor);
            graphics2D.drawRect(MenuX + MenuWidth,MenuY + (MenuItemHeight * i),MenuItemWidth,MenuItemHeight);
        }
        //设置红色颜色块的位置
        graphics2D.setColor(Color.BLUE);
        graphics2D.fillRect(MenuX + MenuWidth, MenuY, MenuItemWidth, MenuItemHeight);
        //设置黄色颜色块的位置
        graphics2D.setColor(Color.RED);
        graphics2D.fillRect(MenuX + MenuWidth, MenuY + MenuItemHeight, MenuItemWidth, MenuItemHeight);
        //设置蓝色颜色块的位置
        graphics2D.setColor(Color.ORANGE);
        graphics2D.fillRect(MenuX + MenuWidth, MenuY + MenuItemHeight * 2, MenuItemWidth, MenuItemHeight);
        //对设置的颜色块进行描边
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawRect(MenuX + MenuWidth, MenuY, MenuItemWidth, MenuItemHeight);
        graphics2D.drawRect(MenuX + MenuWidth, MenuY + MenuItemHeight, MenuItemWidth, MenuItemHeight);
        graphics2D.drawRect(MenuX + MenuWidth, MenuY + MenuItemHeight * 2, MenuItemWidth, MenuItemHeight);
        //如果有颜色被选择
        if (SelectColorItem >= 0) {
            graphics2D.setColor(SelectMenuItemColor);
            graphics2D.fillRect(MenuX + MenuWidth,MenuY + MenuItemHeight * SelectColorItem,MenuItemWidth,MenuItemHeight);
            graphics2D.setColor(MenuLineColor);
            graphics2D.drawRect(MenuX + MenuWidth,MenuY + MenuItemHeight * SelectColorItem,MenuItemWidth,MenuItemHeight);
            SetColor = SelectColorItem + 1; //只有进行有效的切换时才保留颜色值
        }
    }
    //绘制出像素选择栏，分别为2.0，3.0和4.0
    public void PaintPixelMenuItem(Graphics2D graphics2D) {
        //绘制出三个像素区域，分别存放2.0，3.0和4.0
        for (int i = 1; i < NumberColor_Pixel * 2 - 2; i ++) {
            graphics2D.setColor(MenuItemColor);
            graphics2D.fillRect(MenuX + MenuWidth,MenuY + (MenuItemHeight * i),MenuItemWidth,MenuItemHeight);
            graphics2D.setColor(MenuLineColor);
            graphics2D.drawRect(MenuX + MenuWidth,MenuY + (MenuItemHeight * i),MenuItemWidth,MenuItemHeight);
        }
        //设置所有像素标签的位置
        PixelTow.setBounds(MenuX + MenuWidth + 5,MenuY + MenuItemHeight,MenuItemWidth,MenuItemHeight);
        PixelThree.setBounds(MenuX + MenuWidth + 5,MenuY + MenuItemHeight * 2,MenuItemWidth,MenuItemHeight);
        PixelFour.setBounds(MenuX + MenuWidth + 5,MenuY + MenuItemHeight * 3,MenuItemWidth,MenuItemHeight);
        //将所有像素标签添加到组件中
        this.add(PixelTow);
        this.add(PixelThree);
        this.add(PixelFour);
        //如果有像素被选择
        if (SelectPixelItem >= 0) {
            graphics2D.setColor(SelectMenuItemColor);
            graphics2D.fillRect(MenuX + MenuWidth,MenuY + MenuItemHeight * (SelectPixelItem + 1),MenuItemWidth,MenuItemHeight);
            graphics2D.setColor(MenuLineColor);
            graphics2D.drawRect(MenuX + MenuWidth,MenuY + MenuItemHeight * (SelectPixelItem + 1),MenuItemWidth,MenuItemHeight);
            SetPixel = SelectPixelItem + 2; //只有进行有效的切换时，才保留像素值
        }
    }
}
