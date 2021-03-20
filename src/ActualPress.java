import cello.tablet.JTablet;
import cello.tablet.JTabletException;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
    date:2021-03-20
    author:王久铭
    purpose:做为压力实例化界面的实际用途
 */
public class ActualPress extends JFrame implements ActionListener, MouseInputListener, KeyListener {
    private int time = 50; //更新时间为50毫秒
    private Timer timer = new Timer(time,this); //以每50毫秒触发一次actionPerformed触发器
    private PAExperimentPanel paExperimentPanel = new PAExperimentPanel(); //创建PAExperimentPanel类
    private boolean ChooseFlag = false; //是否显示压力动态图像
    private int CurrentPress = -1; //获取当前的压力值
    private int TriggerPress = 876; //预设的压力值，当比这个值大的时候就会触发选择菜单

    //下面这个两个boolean的值是用来检测颜色和像素切换是否合法
    private boolean ColorChange = false; //在进入到颜色的测试区域后，变为true
    private boolean PixelChange = false; //在进入到像素的测试区域后，变为true

    private boolean MenuFlag = false; //是否展开选择菜单
    private boolean MenuMove = true; //是否菜单的弹出位置随着鼠标位置改变
    private int NumberOfMenuItem = 2; //一个有2个可以选择的菜单栏,分别是颜色和像素
    private int MenuX = 0;
    private int MenuY = 0;
    private int MenuWith = 50; //菜单的宽
    private int MenuHeight = 40; //菜单的高

    //压力实列化界面的定义
    private JFrame ActualPFrame = new JFrame("P-实列化界面");

    //设置画笔的初始颜色
    private int SetColor = 0;
    //设置画笔的初始像素
    private int SetPixel = 0;

    private JTablet tablet = null;

    private PenValue penValue = new PenValue();

    private double x0,y0; //每一次笔尖开始的位置
    private double x1,y1; //每一次笔尖结束的位置

    /*
        将ActualPress分为两个区域
     */
    private JPanel APInter = new JPanel(); //提示信息区域
    private JPanel APDraw = new JPanel(); //画线区域


    public ActualPress() {
        paExperimentPanel.setLayout(new BorderLayout());
        paExperimentPanel.addMouseListener(this);
        paExperimentPanel.addMouseMotionListener(this);
        paExperimentPanel.addKeyListener(this);


        //生成压力实列化测试界面
        this.CreateAPFrame();

        paExperimentPanel.requestFocusInWindow(); //让其获得焦点，这样才能是键盘监听能够正常使用

        try {
            tablet = new JTablet();
        } catch (JTabletException e) {
            e.printStackTrace();
        }
        timer.start();
        timer.stop();
    }
    public void CreateAPFrame() {
        this.CreateAPInter();
        this.CreateAPDraw();
        /*
        将界面分割为两部分
         */
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,false,APInter,APDraw); //这里第一个参数是控制分割线竖直，第二个参数是当你拖曳切割面版的分隔线时，窗口内的组件是否会随着分隔线的拖曳而动态改变大小，最后两个参数就是我分割完成后分割线两边各添加哪个容器。
        jSplitPane.setDividerLocation(300); //分割线的位置  也就是初始位置
        jSplitPane.setOneTouchExpandable(false); //是否可展开或收起，在这里没用
        jSplitPane.setDividerSize(0);//设置分割线的宽度 像素为单位(这里设为0，择时不显示分割线)
        jSplitPane.setEnabled(false); //设置分割线不可拖动！！
        ActualPFrame.add(jSplitPane);  //加入到面板中就好了

        //界面全屏设置
        ActualPFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ActualPFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ActualPFrame.setVisible(true);
    }
    public void CreateAPInter() {
        //上半部分的界面,背景颜色为默认颜色
        APInter.setLayout(null); //不采用布局管理器,由坐标定位


    }
    public void CreateAPDraw() {
        APDraw.setLayout(new BorderLayout());
        APDraw.setBackground(Color.WHITE);
        APDraw.add(paExperimentPanel,BorderLayout.CENTER);
    }
    //当压力到达时弹出的选择框
    public void ProcessTriggerSwitch() {
        MenuFlag = true; //展开选择菜单栏
        paExperimentPanel.SetOpenMenu(MenuFlag); //打开颜色和像素的选择菜单
        paExperimentPanel.repaint(); //重绘
    }
    //重绘APInter界面
    public void RepaintAPInter() {
        APInter.removeAll();
        APInter.repaint();

        APInter.revalidate();
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
        //如果选择的是颜色菜单，打开颜色的分支菜单显示，关闭像素分支菜单显示
        if (MenuItem == 0) {
            paExperimentPanel.SetShowColorMenu(true);
            paExperimentPanel.SetShowPixelMenu(false);
        }
        //如果选择的像素菜单，打开像素分支菜单显示，关闭颜色分支菜单显示
        else if (MenuItem == 1) {
            paExperimentPanel.SetShowPixelMenu(true);
            paExperimentPanel.SetShowColorMenu(false);
        }else {
            //什么也不做
        }
        return MenuItem;
    }
    //判断用户选择的是哪个颜色
    public int CheckColorItem(int x, int y) {
        int ColorItem = -1;
        int tempY = MenuY;
        for (int i = 0; i < 3; i ++) {
            if ((MenuX + MenuWith * 2) >= x && (MenuX + MenuWith <= x)) {
                if ((MenuY <= y) && (tempY + MenuHeight) >= y) {
                    ColorItem = i;
                    break;
                }
            }
            tempY += MenuHeight;
        }
        return ColorItem;
    }
    //判断用户选择的是哪个像素
    public int CheckPixelItem(int x, int y) {
        int PixelItem = -1;
        int tempY = MenuY;
        for (int i = 0; i < 3; i ++) {
            if ((MenuX + MenuWith * 2) >= x && (MenuX + MenuWith <= x)) {
                if ((MenuY + MenuHeight<= y) && (tempY + MenuHeight * 2) >= y) {
                    PixelItem = i;
                    break;
                }
            }
            tempY += MenuHeight;
        }
        return PixelItem;

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        CurrentPress = penValue.Pressure();
        //如果当前的压力值超过了预设的压力值
        if (TriggerPress <= CurrentPress) {
            timer.stop(); //停止触发actionPerformed
            paExperimentPanel.SetShowBack(false);
            MenuMove = false; //当压力到达到指定值后，菜单位置就固定了
            this.ProcessTriggerSwitch(); //当压力到达规定值时，弹出选择框

        }else {
            //没超过的话就展示压力的动态图像
            paExperimentPanel.SetCurrentPress(CurrentPress);
            paExperimentPanel.repaint();
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
        if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {

            timer.restart();
            paExperimentPanel.SetShowBack(true);

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
                paExperimentPanel.SetMenuX_Y(MenuX,MenuY);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        paExperimentPanel.repaint();
        //当抬笔后说明已经选择完成
        MenuFlag = false; //此时关闭显示菜单
        paExperimentPanel.SetOpenMenu(false); //在显示界面关闭界面显示
        ChooseFlag = false; //不显示压力动态图像
        paExperimentPanel.SetShowBack(false); //打开显示压力的动态显示
        MenuMove = true; //菜单位置跟随鼠标变化
        paExperimentPanel.RemoveAllJLabel(); //清除颜色和像素提示标签
        paExperimentPanel.SetSelectPixelItem(-1); //初始化像素分支选择
        paExperimentPanel.SetSelectColorItem(-1); //初始化颜色分支选择
        paExperimentPanel.SetShowColorMenu(false);
        paExperimentPanel.SetShowPixelMenu(false);
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
        paExperimentPanel.SetShowPoint(new Point((int)x0,(int)y0));
        //获得颜色切换的颜色值
        SetColor = paExperimentPanel.GetSetColor();
        //获得像素切换的像素值
        SetPixel = paExperimentPanel.GetSetPixel();
        //点的位置，是用来为选择菜单的显示提供位置信息
        if (MenuMove) {
            MenuX = e.getX();
            MenuY = e.getY();
            //记录菜单出现的位置
            paExperimentPanel.SetMenuX_Y(MenuX,MenuY);
        }
        //如果要求打开选择菜单，此时不记录笔的轨迹信息
        if (MenuFlag) {
            //在每次移动的时候对颜色分支和像素分支进行移除和重组
            paExperimentPanel.RemoveItemJLabel();
            //通过当前点的位置来计算用户选择的是菜单栏中的哪个区域
            paExperimentPanel.SetSelectMenuItem(this.CheckSelectMenuItem(e.getX(),e.getY()));
            //如果颜色的分支菜单被打开
            if (paExperimentPanel.GetShowColorMenu()) {
                //传入具体是哪个颜色被选择
                paExperimentPanel.SetSelectColorItem(this.CheckColorItem(e.getX(), e.getY()));
                this.RepaintAPInter();

            }
            //如果像素的分支菜单被打开
            if (paExperimentPanel.GetShowPixelMenu()) {
                //传入具体的哪个像素被选择
                paExperimentPanel.SetSelectPixelItem(this.CheckPixelItem(e.getX(), e.getY()));
                //如果像素提示还未出现就切换，像素误触发加一
                this.RepaintAPInter();
            }
            paExperimentPanel.repaint();

        }else if (ChooseFlag == false && MenuFlag == false){
            //当不进行功能切换和菜单选择时，才会进行画线操作
            Dot dot = new Dot();
            dot.SetStarDot(x0,y0);
            dot.SetEndDot(x1,y1);
            dot.SetColor(SetColor); //点的颜色
            dot.SetPixel(SetPixel); //点的像素

            double x = dot.DotStarX();
            double y = dot.DotStarY();


            //将点的信息记录在容器中
            paExperimentPanel.arrayListSpot.add(dot);
            paExperimentPanel.repaint();
            //更新点的起始坐标（下一个点的开始为上一个点的结束）
            x0 = x1;
            y0 = y1;

        }


    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}

