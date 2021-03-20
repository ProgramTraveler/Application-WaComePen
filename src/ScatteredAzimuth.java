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
    purpose:做为方位角离散化界面的实际运用
 */
public class ScatteredAzimuth extends JFrame implements MouseInputListener, KeyListener, ActionListener {
    private int time = 50; //更新时间为50毫秒一次
    private Timer timer = new Timer(time, this); //以每50毫秒一次触发actionPerformed触发器
    private ASExperimentJPanel asExperimentJPanel = new ASExperimentJPanel();
    private int CurrentAzimuth = -1; //获取当前方位角

    //下面这个两个boolean的值是用来检测颜色和像素切换是否合法
    private boolean ColorChange = false; //在进入到颜色的测试区域后，变为true
    private boolean PixelChange = false; //在进入到像素的测试区域后，变为true

    //方位角离散化界面定义
    private JFrame ScatteredAFrame= new JFrame("A-离散化");
    //设置画笔的初始颜色
    private int SetColor = 0;
    //设置画笔的初始像素
    private int SetPixel = 0;

    private JTablet tablet = null;

    //获取笔的实时数据
    private PenValue penValue = new PenValue();

    private double x0, y0; //每一次笔尖开始的位置
    private double x1, y1; //每一次笔尖结束的位置

    //用户是否第一次进入颜色测试区域，true表示未进入
    private boolean ColorFlag = true;
    //判断用户是否第一次进入像素测试区域，true表示未进入
    private boolean PixelFlag = true;

    public ScatteredAzimuth() {
        asExperimentJPanel.setLayout(new BorderLayout());
        asExperimentJPanel.addMouseListener(this);
        asExperimentJPanel.addMouseMotionListener(this);
        asExperimentJPanel.addKeyListener(this);

        this.CreateSAFrame(); //生成方位角离散化测试界面

        asExperimentJPanel.requestFocusInWindow(); //获得焦点，使键盘可以正常监听

        try {
            tablet = new JTablet();
        } catch (JTabletException e) {
            e.printStackTrace();
        }
        timer.start();
        timer.stop();
    }
    public void CreateSAFrame() {
        ScatteredAFrame.add(asExperimentJPanel);

        ImageIcon ic  = new ImageIcon("简笔画.jpg"); //方便后面为界面的大小和位置做参考
        ScatteredAFrame.setBounds(450,250, ic.getIconWidth()*3, ic.getIconHeight()*2); //设置界面的位置和大小
        ScatteredAFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ScatteredAFrame.setVisible(true);
    }
    public void CreateSAInter() {
    }
    public void CreateSADraw() {

    }
    //重绘SAInter界面(将提示的移除掉)
    public void RemoveRandom() {

    }

    //重绘SAInter界面
    public void RepaintSAInter() {

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        //System.out.println(CurrentAzimuth);
        CurrentAzimuth = penValue.Azimuth();
        asExperimentJPanel.SetCurrentAzimuth(CurrentAzimuth);
        asExperimentJPanel.repaint();
        asExperimentJPanel.RemoveAllJLabel();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (asExperimentJPanel.GetShowColorMenu() && asExperimentJPanel.GetShowPixelMenu()) {
                if (((CurrentAzimuth > 0 && CurrentAzimuth <=109) || (CurrentAzimuth >= 311 && CurrentAzimuth <= 360))) {

                    //展开二级菜单
                    asExperimentJPanel.SetShowColorMenu(false);
                }else if (CurrentAzimuth >= 154 && CurrentAzimuth < 311) {

                    //展开二级菜单
                    asExperimentJPanel.SetShowPixelMenu(false);
                }
            }else if (asExperimentJPanel.GetShowColorMenu() == false) {
                //如果二级菜单已经展开，那么就按照角度来进行颜色判定
                //对当前位置的角度进行颜色判定
                if (CurrentAzimuth > 57 && CurrentAzimuth <= 109) {
                    asExperimentJPanel.DefineColor(1);


                }
                if (CurrentAzimuth > 5 && CurrentAzimuth < 57) {
                    asExperimentJPanel.DefineColor(3);


                }
                if ((CurrentAzimuth > 0 && CurrentAzimuth <= 5) || (CurrentAzimuth >= 311 && CurrentAzimuth <= 360)) {
                    asExperimentJPanel.DefineColor(2);

                }
                //再一次打开一级颜色菜单
                asExperimentJPanel.SetShowColorMenu(true);
            } else if (asExperimentJPanel.GetShowPixelMenu() == false) {
                //如果二级菜单已经展开，那么就按照角度来进行像素判定
                //对当前位置角度进行像素判定
                if (CurrentAzimuth >= 154 && CurrentAzimuth < 206) {
                    asExperimentJPanel.DefinePixel(4);

                }
                if (CurrentAzimuth >= 206 && CurrentAzimuth < 258) {
                    asExperimentJPanel.DefinePixel(3);

                }
                if (CurrentAzimuth >= 258 && CurrentAzimuth < 311) {
                    asExperimentJPanel.DefinePixel(2);

                }

                //再一次打开像素一级菜单
                asExperimentJPanel.SetShowPixelMenu(true);
            }
        }
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
            asExperimentJPanel.SetShowBack(true);
            //获得开始时鼠标的位置
            x0 = e.getX();
            y0 = e.getY();

        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        asExperimentJPanel.repaint();
        //当抬笔后说明已经选择完成
        //ChooseFlag = false; //不显示压力动态图像
        asExperimentJPanel.SetShowBack(false); //不打开显示压力的动态显示
        asExperimentJPanel.RemoveAllJLabel(); //清除颜色和像素标签
        asExperimentJPanel.SetShowPixelMenu(true); //抬笔后改为一级菜单
        asExperimentJPanel.SetShowColorMenu(true); //抬笔后改为一级菜单
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
        //获得笔在拖动时的坐标
        x1 = e.getX();
        y1 = e.getY();
        //点的位置，用来为压力的显示提供位置信息
        asExperimentJPanel.SetShowPoint(new Point((int) x0, (int) y0));
        //获得颜色切换的颜色值
        SetColor = asExperimentJPanel.GetSetColor();
        //获得像素切换的像素值
        SetPixel = asExperimentJPanel.GetSetPixel();

        //当不进行功能切换和菜单选择时，才会进行画线操作
        Dot dot = new Dot();
        dot.SetStarDot(x0, y0);
        dot.SetEndDot(x1, y1);
        dot.SetColor(SetColor); //点的颜色
        dot.SetPixel(SetPixel); //点的像素

        double x = dot.DotStarX();
        double y = dot.DotStarY();

        //将点的信息记录在容器中
        asExperimentJPanel.arrayListSpot.add(dot);
        asExperimentJPanel.repaint();
        //更新点的起始坐标（下一个点的开始为上一个点的结束）
        x0 = x1;
        y0 = y1;
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
