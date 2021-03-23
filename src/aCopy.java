import cello.tablet.JTablet;
import cello.tablet.JTabletException;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;

/*
    date:2021-03-21
    author:王久铭
    purpose:为压力实例化界面提供复制和复制功能
 */
public class aCopy extends Frame implements ActionListener, MouseInputListener, KeyListener {
    private int time = 50; //更新时间为50毫秒
    private Timer timer = new Timer(time,this); //以每50毫秒触发一次actionPerformed触发器
    private aCopyJPanel aCopyJPanel = new aCopyJPanel(); //创建aCopyJPanel类
    private boolean ChooseFlag = false; //是否显示压力动态图像
    private int CurrentPress = -1; //获取当前的压力值
    private int TriggerPress = 876; //预设的压力值，当比这个值大的时候就会触发选择菜单
    //private int TriggerPress = -1; //预设的压力值，当比这个值大的时候就会触发选择菜单
    //下面这个两个boolean的值是用来检测复制和复制切换是否合法
    private boolean ColorChange = false; //在进入到复制的测试区域后，变为true
    private boolean PixelChange = false; //在进入到复制的测试区域后，变为true

    private boolean MenuFlag = false; //是否展开选择菜单
    private boolean MenuMove = true; //是否菜单的弹出位置随着鼠标位置改变
    private int NumberOfMenuItem = 2; //一个有2个可以选择的菜单栏,分别是复制和复制
    private int MenuX = 0;
    private int MenuY = 0;
    private int MenuWith = 50; //菜单的宽
    private int MenuHeight = 40; //菜单的高

    //压力实列化界面的定义
    private JFrame ActualPFrame = new JFrame("P-实列化界面");

    //设置画笔的初始复制
    private int SetColor = 0;
    //设置画笔的初始复制
    private int SetPixel = 0;

    private JTablet tablet = null;

    private PenValue penValue = new PenValue();

    private double x0,y0; //每一次笔尖开始的位置
    private double x1,y1; //每一次笔尖结束的位置

    /*
        将ActualPress分为两个区域
     */

    public aCopy() {
        aCopyJPanel.setLayout(new BorderLayout());
        aCopyJPanel.addMouseListener(this);
        aCopyJPanel.addMouseMotionListener(this);
        aCopyJPanel.addKeyListener(this);


        //生成压力实列化测试界面
        this.CreateAPFrame();

        aCopyJPanel.requestFocusInWindow(); //让其获得焦点，这样才能是键盘监听能够正常使用

        try {
            tablet = new JTablet();
        } catch (JTabletException e) {
            e.printStackTrace();
        }
        timer.start();
        timer.stop();
    }
    public void CreateAPFrame() {
        ActualPFrame.add(aCopyJPanel);

        ImageIcon ic  = new ImageIcon("简笔画.jpg"); //方便后面为界面的大小和位置做参考
        ActualPFrame.setBounds(450,250, ic.getIconWidth()*3, ic.getIconHeight()*2); //设置界面的位置和大小

        ActualPFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ActualPFrame.setVisible(true);
    }
    //当压力到达时弹出的选择框
    public void ProcessTriggerSwitch() {
        MenuFlag = true; //展开选择菜单栏
        aCopyJPanel.SetOpenMenu(MenuFlag); //打开复制和复制的选择菜单
        aCopyJPanel.repaint(); //重绘
    }
    //重绘APInter界面
    public void RepaintAPInter() {

    }
    //根据用户的当前的鼠标位置来计算出用户选择的是菜单中的哪块区域
    public int CheckSelectMenuItem(int x,int y) {
        int MenuItem = -1;
        int tempY = MenuY;

        for (int i = 0; i < NumberOfMenuItem; i ++) {
            if ((MenuX + MenuWith) >=  x && (MenuX < x)) {
                if ((MenuY <= y) && (tempY + MenuHeight) >= y) {
                    MenuItem = i;
                    break;
                }
            }
            tempY += MenuHeight;
        }
        //如果选择的是复制菜单，打开复制的分支菜单显示，关闭复制分支菜单显示
        if (MenuItem == 0) {
            aCopyJPanel.SetShowColorMenu(true);
            aCopyJPanel.SetShowPixelMenu(false);
        }
        //如果选择的复制菜单，打开复制分支菜单显示，关闭复制分支菜单显示
        else if (MenuItem == 1) {
            aCopyJPanel.SetShowPixelMenu(true);
            aCopyJPanel.SetShowColorMenu(false);
        }else {
            //什么也不做
        }
        return MenuItem;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        CurrentPress = penValue.Pressure();
        //如果当前的压力值超过了预设的压力值
        if (TriggerPress <= CurrentPress) {
            timer.stop(); //停止触发actionPerformed
            MenuMove = false; //当压力到达到指定值后，菜单位置就固定了
            this.ProcessTriggerSwitch(); //当压力到达规定值时，弹出选择框

        }else {
            //没超过的话就展示压力的动态图像
            aCopyJPanel.SetCurrentPress(CurrentPress);
            aCopyJPanel.repaint();
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyPressed(KeyEvent e) {


    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }
    @Override
    public void mousePressed(MouseEvent e) {
        aCopyJPanel.SetIndex(); //对复制和复制初始化
        if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {

            timer.restart();
            //获得开始时鼠标的位置
            x0 = e.getX();
            y0 = e.getY();
            /*
            获得落笔的时间
             */
            //获得落笔的时间戳

            //如果菜单位置可以随着鼠标位置改变，那么就实时跟新菜单的出现位置
            if (MenuMove) {
                MenuX = e.getX();
                MenuY = e.getY();
                //记录菜单出现的位置
                aCopyJPanel.SetMenuX_Y(MenuX,MenuY);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        //当抬笔后说明已经选择完成
        MenuFlag = false; //此时关闭显示菜单
        aCopyJPanel.SetOpenMenu(false); //在显示界面关闭界面显示
        ChooseFlag = false; //不显示压力动态图像
        MenuMove = true; //菜单位置跟随鼠标变化
        aCopyJPanel.RemoveAllJLabel(); //清除复制和复制提示标签

        aCopyJPanel.SetShowColorMenu(false);
        aCopyJPanel.SetShowPixelMenu(false);
        //对压力值重新获取
        try {
            tablet.poll();
        } catch (JTabletException e1) {
            e1.printStackTrace();
        }
        timer.stop();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
       /*
        应该每次拖动时都会产生一个点对象
         */
        //获得笔在拖动时的坐标
        x1 = e.getX();
        y1 = e.getY();
        //点的位置，用来为压力的显示提供位置信息
        aCopyJPanel.SetShowPoint(new Point((int)x0,(int)y0));

        //点的位置，是用来为选择菜单的显示提供位置信息
        if (MenuMove) {
            MenuX = e.getX();
            MenuY = e.getY();
            //记录菜单出现的位置
            aCopyJPanel.SetMenuX_Y(MenuX,MenuY);
        }
        //如果要求打开选择菜单，此时不记录笔的轨迹信息
        if (MenuFlag) {

            //在每次移动的时候对复制分支和复制分支进行移除和重组
            aCopyJPanel.RemoveItemJLabel();

            //通过当前点的位置来计算用户选择的是菜单栏中的哪个区域
            aCopyJPanel.SetSelectMenuItem(this.CheckSelectMenuItem(e.getX(),e.getY()));

            aCopyJPanel.repaint();

        }else if (ChooseFlag == false && MenuFlag == false){
            //当不进行功能切换和菜单选择时，才会进行画线操作
            Dot dot = new Dot();
            dot.SetStarDot(x0,y0);
            dot.SetEndDot(x1,y1);
            dot.SetColor(SetColor); //点的复制
            dot.SetPixel(SetPixel); //点的复制

            double x = dot.DotStarX();
            double y = dot.DotStarY();


            //将点的信息记录在容器中
            aCopyJPanel.arrayListSpot.add(dot);
            aCopyJPanel.repaint();
            //更新点的起始坐标（下一个点的开始为上一个点的结束）
            x0 = x1;
            y0 = y1;

        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}



