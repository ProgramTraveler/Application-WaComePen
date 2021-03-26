import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/*
    date:2021-03-23
    author:王久铭
    purpose:对方位角离散化界面的复制和复制界面的拓展
 */
public class sCopyJPanel extends JPanel {
    private int CurrentTilt = -1; //记录当前的倾斜角的值

    private int PenHeight = 70; //提示笔的长度
    private int PenWidth = 1; //提示笔的宽度
    private int PenLineWidth = 2;
    private int PenTip = 3;
    private int AngleFeedBackRadius = PenHeight + 5;
    private int MinAngle = 22; //最小角度
    private int MaxAngle = 90; //最大角度

    private int TriggerAngleSwitch_1 = 54;
    private int TriggerAngleSwitch_2 = 71;

    private int  permeationRate = 180;
    private Color ClearWhite = new Color( Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue(), permeationRate);
    private Color ClearLightGray = new Color( Color.lightGray.getRed(), Color.lightGray.getGreen(), Color.lightGray.getBlue(), permeationRate);
    private Color ClearBlack = new Color( Color.black.getRed(), Color.black.getGreen(), Color.black.getBlue(), permeationRate);
    private Color ClearRed = new Color( Color.red.getRed(), Color.red.getGreen(), Color.red.getBlue(), permeationRate);
    private Color ClearPink = new Color( Color.pink.getRed(), Color.pink.getGreen(), Color.pink.getBlue(), permeationRate);
    private Color ClearGray = new Color( Color.gray.getRed(), Color.gray.getGreen(), Color.gray.getBlue(), permeationRate);


    private Point FeedbackShowPoint = new Point(); //记录点的位置，为后面的压力提示，颜色和像素菜单切换提供位置基础

    public static ArrayList<Dot> arrayListSpot; //记录点在绘画过程中的信息,为了方便可以直接调用，就写成了public的
    //抽象类Image是表示图形图像的所有类的超类，必须以平台特定的方式获取图像
    private Image offScreenImg = null;


    private JLabel ColorJLabel = new JLabel("复制"); //复制标签
    private JLabel PixelJLabel = new JLabel("粘贴"); //粘贴标签

    private boolean ShowBack = false; //用来控制是否显示压力的动态图像,默认为不打开

    private boolean ShowColorMenu = true; //是否显示复制分支菜单

    private boolean ShowPixelMenu = true; //是否显示粘贴分支菜单

    private boolean indexF = false;
    private boolean indexZ = false;

    public void SetIndexF(boolean b) {
        indexF = b;
    }

    public void SetIndexZ(boolean b) {
        indexZ = b;
    }
    public boolean GetIndexZ() {
        return indexZ;
    }
    public void SetIndex() {
        indexF = false;
        indexZ = false;
    }

    public sCopyJPanel() { arrayListSpot = new ArrayList<Dot>(); }
    //传入的笔尖倾斜角值
    public void SetCurrentTilt(int c) { this.CurrentTilt = c; }
    //传入当前点的坐标
    public void SetShowPoint(Point p) { this.FeedbackShowPoint = p; }
    //用来选择是否显示倾斜角的动态图像
    public void SetShowBack(boolean b) { ShowBack = b; }

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
        //显示动态倾斜角菜单
        if (ShowBack)
            this.PaintTiltFeedback(graphics2D);
    }
    //绘制倾斜角动态显示界面
    public void PaintTiltFeedback(Graphics2D graphics2D) {
        graphics2D.setColor(ClearWhite);
        graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius,(int)FeedbackShowPoint.getY() - AngleFeedBackRadius,AngleFeedBackRadius * 2,AngleFeedBackRadius * 2, MinAngle,MaxAngle - MinAngle);
        graphics2D.setColor(ClearBlack); //角度线和边界线的复制
        //整个角度的弧线
        graphics2D.drawArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius * 2, 22, 68);
        //上半部分边界线
        graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius * 2, 90,-1);
        //下半部分边界线
        graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius * 2, 22,1);
        //显示复制标签
        if (ShowColorMenu && ShowPixelMenu) {
            graphics2D.setColor(ClearBlack); //设置分隔线的复制
            //上半部分的扇形区域，由于没有直接画扇形的，所以就进行小角度的复制填充
            graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius * 2, 80,2);
            //下半部分的扇形区域
            graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius * 2, 54,2);
            if (CurrentTilt >=80 && CurrentTilt <= 90) {
                ColorJLabel.setBounds((int)FeedbackShowPoint.getX(),(int)FeedbackShowPoint.getY() - AngleFeedBackRadius - 50, AngleFeedBackRadius, AngleFeedBackRadius);
                this.add(ColorJLabel);
            }
            if (CurrentTilt <= 54 && CurrentTilt >= 38){
                ColorJLabel.setBounds((int)FeedbackShowPoint.getX() + AngleFeedBackRadius,(int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius, AngleFeedBackRadius);
                this.add(ColorJLabel);
            }
        }else if (ShowColorMenu == false) {
            remove(ColorJLabel);
            remove(PixelJLabel);

        }
        //显示粘贴标签
        if (ShowPixelMenu && ShowColorMenu) {
            graphics2D.setColor(ClearBlack); //设置分界线复制
            //上半部分的扇形区域
            graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius * 2, 71,2);
            //下半部分的复制区域
            graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius *2, 38, 2);
            if (CurrentTilt >= 71 && CurrentTilt < 80) {
                PixelJLabel.setBounds((int)FeedbackShowPoint.getX(),(int)FeedbackShowPoint.getY() - AngleFeedBackRadius - 50, AngleFeedBackRadius, AngleFeedBackRadius);
                this.add(PixelJLabel);
            }
            if (CurrentTilt >= 22 && CurrentTilt < 38) {
                PixelJLabel.setBounds((int)FeedbackShowPoint.getX() + AngleFeedBackRadius,(int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius, AngleFeedBackRadius);
                this.add(PixelJLabel);
            }

        }else if (ShowPixelMenu == false) {
            remove(ColorJLabel);
            remove(PixelJLabel);


        }
        //显示复制和粘贴菜单的选择框
        graphics2D.setColor(ClearLightGray);
        if (ShowPixelMenu == false) {
            if (CurrentTilt >= 66 && CurrentTilt <= 90) {
                graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius *2, 66, 24);
            }
            if (CurrentTilt >= 44 && CurrentTilt < 66) {
                graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius *2, 44, 22);
            }
            if (CurrentTilt >= 22 && CurrentTilt < 44) {
                graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius *2, 22, 22);
            }
        }else if (CurrentTilt >= 71 && CurrentTilt < 80 && ShowPixelMenu && ShowColorMenu) {
            graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius *2, 71, 9);
        }else if (CurrentTilt >=22 && CurrentTilt < 38 && ShowPixelMenu && ShowColorMenu) {
            graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius *2, 22, 16);
        }

        if (ShowColorMenu == false) {
            if (CurrentTilt >= 66 && CurrentTilt <= 90) {
                graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius *2, 66, 24);
            }
            if (CurrentTilt >= 44 && CurrentTilt < 66) {
                graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius *2, 44, 22);
            }
            if (CurrentTilt >= 22 && CurrentTilt < 44) {
                graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius *2, 22, 22);
            }
        }else if (CurrentTilt >= 80 && CurrentTilt <=90 && ShowColorMenu && ShowPixelMenu) {
            graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius *2, 80, 10);
        }else if (CurrentTilt >=38 && CurrentTilt <=54 && ShowColorMenu && ShowPixelMenu) {
            graphics2D.fillArc((int)FeedbackShowPoint.getX() - AngleFeedBackRadius, (int)FeedbackShowPoint.getY() - AngleFeedBackRadius, AngleFeedBackRadius * 2, AngleFeedBackRadius *2, 38, 16);
        }

        //当在菜单范围外，而且时一级菜单展示，那么就显示箭头
        if (CurrentTilt > 54 && CurrentTilt < 71 && ShowColorMenu && ShowPixelMenu) {
            AffineTransform affineTransform = new AffineTransform(); //构造一个新的 AffineTransform代表身份转换
            affineTransform.setToRotation(Math.toRadians(360 - CurrentTilt),FeedbackShowPoint.getX(),FeedbackShowPoint.getY());
            graphics2D.transform(affineTransform); //将此转换设置为转换的旋转变换

            BasicStroke basicStroke = new BasicStroke(PenLineWidth); //构造一个固定的 BasicStroke具有指定的线宽，并使用上限和连接样式的默认值
            graphics2D.setStroke(basicStroke);

            GeneralPath generalPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
            generalPath.moveTo( (int)FeedbackShowPoint.getX() + PenLineWidth, (int)FeedbackShowPoint.getY() );
            generalPath.lineTo( (int)FeedbackShowPoint.getX() + PenTip + PenLineWidth, (int)FeedbackShowPoint.getY() - PenWidth);
            generalPath.lineTo( (int)FeedbackShowPoint.getX() + PenHeight + PenLineWidth, (int)FeedbackShowPoint.getY() - PenWidth);
            generalPath.lineTo( (int)FeedbackShowPoint.getX() + PenLineWidth, (int)FeedbackShowPoint.getY());
            generalPath.closePath();

            graphics2D.setPaint(ClearPink);
            graphics2D.fill(generalPath);
            graphics2D.setPaint(ClearGray);
            graphics2D.draw(generalPath);
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
        g.drawImage(new ImageIcon("简笔画.jpg").getImage(), 100, ic.getIconHeight() - 150, this);

        if (indexF && indexZ) {
            g.drawImage(new ImageIcon("简笔画.jpg").getImage(), (int)FeedbackShowPoint.getX(),(int)FeedbackShowPoint.getY(), this); //调节图片位置
        }

    }
    //当抬笔时，清除所有复制和粘贴标签
    public void RemoveAllJLabel() {
        this.removeAll();
        this.repaint();
    }
    //当笔在移动过程中，对分支复制和粘贴进行移除和重组
    public void RemoveItemJLabel() {
        //移除所有的粘贴组件


        this.repaint();
    }

}
