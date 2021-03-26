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
    date:2021-03-21
    author:王久铭
    purpose:为方位角离散化界面提供复制复制功能
 */
public class sCopy extends JFrame implements MouseInputListener, KeyListener, ActionListener {
    private int time = 50; //更新时间为50毫秒
    private Timer timer = new Timer(time,this); //每50毫秒触发一次actionPerformed
    private sCopyJPanel sCopyJPanel = new sCopyJPanel();
    private int CurrentTilt = -1; //获取当前倾斜角



    //倾斜角离散化界面的定义
    private JFrame ScatteredTFrame = new JFrame("T-离散化界面");
    //设置画笔的初始颜色
    private int SetColor = 0;
    //设置画笔的初始像素
    private int SetPixel = 0;

    private JTablet tablet = null;

    //获取笔的实时数据
    private PenValue penValue = new PenValue();

    private double x0, y0; //每一次笔尖开始的位置
    private double x1, y1; //每一次笔尖结束的位置

    public sCopy() {
        sCopyJPanel.setLayout(new BorderLayout());
        sCopyJPanel.addMouseListener(this);
        sCopyJPanel.addMouseMotionListener(this);
        sCopyJPanel.addKeyListener(this);

        this.CreateSTFrame(); //生成倾斜角离散化界面

        sCopyJPanel.requestFocusInWindow(); //获得焦点

        try {
            tablet = new JTablet();
        } catch (JTabletException e) {
            e.printStackTrace();
        }

        timer.start();
        timer.stop();
    }

    public void CreateSTFrame() {
        ScatteredTFrame.add(sCopyJPanel);

        ImageIcon ic = new ImageIcon("简笔画.jpg");

        ScatteredTFrame.setBounds(450, 250, ic.getIconWidth() * 3, ic.getIconHeight() * 2);

        ScatteredTFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ScatteredTFrame.setVisible(true);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        CurrentTilt = penValue.Tilt();
        sCopyJPanel.SetCurrentTilt(CurrentTilt);
        sCopyJPanel.repaint();
        //对像素标签进行移除
        sCopyJPanel.RemoveAllJLabel();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //如果按下了空格键，说明确认选择
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (sCopyJPanel.GetShowColorMenu() && sCopyJPanel.GetShowPixelMenu()) {
                if (((CurrentTilt >=80 && CurrentTilt <=90) || (CurrentTilt >= 38 && CurrentTilt <= 54))) {
                    sCopyJPanel.SetIndexF(true);
                }else if ((CurrentTilt >= 71 && CurrentTilt < 80) || (CurrentTilt >= 22 && CurrentTilt < 38)) {
                    sCopyJPanel.SetIndexZ(true);
                    sCopyJPanel.SetShowBack(false);
                }
            }else if (sCopyJPanel.GetShowColorMenu() == false) {
                //再一次打开一级颜色菜单
                sCopyJPanel.SetShowColorMenu(true);
            } else if (sCopyJPanel.GetShowPixelMenu() == false) {
                //再一次打开像素一级菜单
                sCopyJPanel.SetShowPixelMenu(true);
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
        sCopyJPanel.SetIndex();
        if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
            timer.restart();
            sCopyJPanel.SetShowBack(true);
            //获得开始时鼠标的位置
            x0 = e.getX();
            y0 = e.getY();
            /*
            获得落笔的时间
             */
            //获得落笔的时间戳


        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //当颜色和像素都已经做过了，此时抬笔，说明已经是最后一次抬笔了
        sCopyJPanel.repaint();
        //当抬笔后说明已经选择完成
        sCopyJPanel.SetShowBack(false); //不打开显示压力的动态显示
        sCopyJPanel.RemoveAllJLabel(); //清除颜色和像素标签
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

        if (!sCopyJPanel.GetIndexZ()) {
            //点的位置，用来为压力的显示提供位置信息
            sCopyJPanel.SetShowPoint(new Point((int) x0, (int) y0));
        }

        //当不进行功能切换和菜单选择时，才会进行画线操作
        Dot dot = new Dot();
        dot.SetStarDot(x0, y0);
        dot.SetEndDot(x1, y1);
        dot.SetColor(SetColor); //点的颜色
        dot.SetPixel(SetPixel); //点的像素

       //将点的信息记录在容器中
       sCopyJPanel.arrayListSpot.add(dot);
       sCopyJPanel.repaint();
       //更新点的起始坐标（下一个点的开始为上一个点的结束）
       x0 = x1;
       y0 = y1;

    }
    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
