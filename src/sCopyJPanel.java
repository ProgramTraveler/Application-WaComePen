import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/*
    date:2021-03-23
    author:王久铭
    purpose:对方位角离散化界面的复制和复制界面的拓展
 */
public class sCopyJPanel extends JPanel {
    private int CurrentAzimuth = -1; //当前方位角的值

    private int PartitionLineLength = 45; //设置圆形的覆盖区域（分隔线长度）

    private int  permeationRate = 180;
    private Color ClearWhite = new Color( Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue(), permeationRate);
    private Color ClearLightGray = new Color( Color.lightGray.getRed(), Color.lightGray.getGreen(), Color.lightGray.getBlue(), permeationRate);
    private Color ClearBlack = new Color( Color.black.getRed(), Color.black.getGreen(), Color.black.getBlue(), permeationRate);
    private Color ClearRed = new Color( Color.red.getRed(), Color.red.getGreen(), Color.red.getBlue(), permeationRate);
    private Color ClearPink = new Color( Color.pink.getRed(), Color.pink.getGreen(), Color.pink.getBlue(), permeationRate);
    private Color ClearGray = new Color( Color.gray.getRed(), Color.gray.getGreen(), Color.gray.getBlue(), permeationRate);

    private Point FeedbackShowPoint = new Point(); //记录点的位置，为后面的压力提示，复制和粘贴菜单切换提供位置基础
    public static ArrayList<Dot> arrayListSpot; //记录点在绘画过程中的信息,为了方便可以直接调用，就写成了public的
    private Graphics2D Line; //设置线条的相关信息
    private Graphics2D offScreen; //显示测试区域
    //抽象类Image是表示图形图像的所有类的超类，必须以平台特定的方式获取图像
    private Image offScreenImg = null;
    private int ColorSet = 0; //设置画笔的复制
    private int PixelSet = 1; //设置画笔的粘贴
    //复制和粘贴标签，用来提示菜单内容
    private JLabel ColorJLabel = new JLabel("复制"); //复制标签
    private JLabel PixelJLabel = new JLabel("粘贴"); //粘贴标签

    private boolean ShowBack = false; //用来控制是否显示压力的动态图像,默认为不打开

    private boolean ShowColorMenu = true; //是否显示复制菜单

    private boolean ShowPixelMenu = true; //是否显示像支菜单


    private int SetColor = 0; //记录被选择的复制
    private int SetPixel = 1; //记录被选择的粘贴

    public sCopyJPanel() { arrayListSpot = new ArrayList<Dot>(); }
    //传入笔的方位角
    public void SetCurrentAzimuth(int c) { this.CurrentAzimuth = c; }
    //用来选择是否显示压力的动态图像
    public void SetShowBack(boolean b) { ShowBack = b; }
    //传入当前点的坐标
    public void SetShowPoint(Point p) { this.FeedbackShowPoint = p; }
    //返回用户选择的复制（是对应菜单中的复制）
    public void DefineColor(int i) { SetColor = i; } //初始化复制
    public int GetSetColor() { return SetColor; }
    //放回用户选择的粘贴（是对应菜单中的粘贴）
    public void DefinePixel(int i) { SetPixel = i; } //初始化粘贴
    public int GetSetPixel() { return SetPixel; }
    //控制是打开复制一级菜单还是二级菜单,true表示是一级菜单，false表示是二级菜单
    public void SetShowColorMenu(boolean b) { ShowColorMenu = b; }
    public boolean GetShowColorMenu() { return ShowColorMenu; } //返回复制菜单状态
    //控制是打开复制一级菜单还是二级菜单,true表示是一级菜单，false表示是二级菜单、
    public void SetShowPixelMenu(boolean b) { ShowPixelMenu = b; }
    public boolean GetShowPixelMenu() { return ShowPixelMenu; } //返回粘贴菜单状态
    //图像的重绘界面
    public void paintComponent(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        this.PaintTestArea(g); //绘制出测试区域
        //显示动态方位角菜单
        if (ShowBack)
            this.PaintAzimuthFeedback(graphics2D);
    }
    //绘制压力动态显示界面
    public void PaintAzimuthFeedback(Graphics2D graphics2D) {
        graphics2D.setColor(Color.WHITE); //设置测试背景为红色
        //设置圆形方位角展示区域出现的位置，红色覆盖的角度为0-360
        graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,0,360);
        //设置弧线复制
        graphics2D.setColor(Color.GRAY);
        //将弧线标黑(整个圆形区域的弧线)
        graphics2D.drawArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,0,360);
        //设置复制和粘贴一级菜单的分界线
        graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 154 + 90,4);
        graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 109 + 90,4);
        graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 311 + 90,4);
        graphics2D.setPaint(ClearWhite); //设置用户常用区域显示为白色
        //设置圆形方位角展示区域出现的位置，白色覆盖区域为109-154
        graphics2D.fillArc(FeedbackShowPoint.x - PartitionLineLength,FeedbackShowPoint.y - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 109 + 90,- 45); //88-176为常用的区域，所以用白色表示
        //显示复制一级菜单
        if (ShowColorMenu && ShowPixelMenu) {
            ColorJLabel.setBounds((int)FeedbackShowPoint.getX() - PartitionLineLength + 40,(int)FeedbackShowPoint.getY() - PartitionLineLength - 20,PartitionLineLength * 2,PartitionLineLength * 2);
            this.add(ColorJLabel);
        }else if (ShowColorMenu == false) {
            //展开复制二级菜单
            this.remove(ColorJLabel);
            this.remove(PixelJLabel);

            graphics2D.setColor(Color.BLUE);
            graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 109 + 90, 52);

            graphics2D.setColor(Color.ORANGE);
            graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 57 + 90,52);

            graphics2D.setColor(Color.RED);
            graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 5 + 90,54);
        }
        //显示粘贴一级菜单
        if (ShowColorMenu && ShowPixelMenu) {
            PixelJLabel.setBounds((int)FeedbackShowPoint.getX() - PartitionLineLength + 10,(int)FeedbackShowPoint.getY() - PartitionLineLength + 10,PartitionLineLength * 2,PartitionLineLength * 2);
            this.add(PixelJLabel);
        }else if (ShowPixelMenu == false) {
            //展开粘贴二级菜单
            remove(ColorJLabel);
            remove(PixelJLabel);

            graphics2D.setColor(ClearBlack); //设置分隔线颜色

        }
        //显示颜色的选择
        graphics2D.setColor(ClearLightGray);
        if (ShowColorMenu == false) {
            if (CurrentAzimuth <= 109 && CurrentAzimuth > 57) {
                graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 109 + 90, 52);
            }
            if (CurrentAzimuth <= 57 && CurrentAzimuth > 5) {
                graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 57 + 90,52);
            }
            if ((CurrentAzimuth <= 5 && CurrentAzimuth > 0) || (CurrentAzimuth >= 311 && CurrentAzimuth <= 360)) {
                graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 5 + 90,54);
            }
        }else if ((CurrentAzimuth >= 311 && CurrentAzimuth <= 360) || (CurrentAzimuth <= 109 && CurrentAzimuth >= 0)) {
            graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 109 + 90, 158);
        }
        //显示粘贴选择
        if (ShowPixelMenu == false) {
            if (CurrentAzimuth >= 154 && CurrentAzimuth < 206) {
                graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 154 + 90,-52);
            }
            if (CurrentAzimuth >= 206 && CurrentAzimuth < 258) {
                graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 206 + 90,-52);
            }
            if (CurrentAzimuth >= 258 && CurrentAzimuth < 311) {
                graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 258 + 90,-53);
            }
        }else if (CurrentAzimuth >= 154 && CurrentAzimuth < 311) {
            graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - 154 + 90, -157);
        }
        //箭头的显示控制
        if (CurrentAzimuth > 109 && CurrentAzimuth < 154 && ShowColorMenu && ShowPixelMenu) {
            graphics2D.setColor(Color.BLACK);
            graphics2D.fillArc((int)FeedbackShowPoint.getX() - PartitionLineLength,(int)FeedbackShowPoint.getY() - PartitionLineLength,PartitionLineLength * 2,PartitionLineLength * 2,360 - CurrentAzimuth + 90, 5);
        }

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


    }
    //当抬笔时，清除所有颜色和粘贴标签
    public void RemoveAllJLabel() {
        this.removeAll();
        this.repaint();
    }
    //当笔在移动过程中，对分支颜色和粘贴进行移除和重组
    public void RemoveItemJLabel() {
        //移除所有的粘贴组件

        this.repaint();
    }
}
