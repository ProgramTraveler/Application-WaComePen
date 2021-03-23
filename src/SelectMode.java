import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/*
    date:2021-03-21
    author:王久铭
    purpose:为用户提供选择模式的界面
 */
public class SelectMode extends JFrame implements ActionListener {
    private String TitleFrame = "登录界面";

    //保存模式按钮的轻量级容器
    private JPanel TechniquesPanel = new JPanel();

    //实际值模式下的选择按钮
    private JRadioButton ButtonActualP =  new JRadioButton("Painting");
    private JRadioButton ButtonActualT = new JRadioButton("Copy&Paste");
    private JRadioButton ButtonActualA = new JRadioButton("Enlarge&Lessen");
    //离散值模式下的选择按钮
    private JRadioButton ButtonScatteredP = new JRadioButton("painting");
    private JRadioButton ButtonScatteredT = new JRadioButton("copy&paste");
    private JRadioButton ButtonScatteredA = new JRadioButton("enlarge&lessen");

    private ButtonGroup ButtonFrame = new ButtonGroup(); //保存写字按钮组


    private JButton StartButton = new JButton("Start");
    private String StartCom = "Start";



    public SelectMode() {
        //创建一个登录界面
        this.CreateTestFrame();

        //添加开始按钮在当前也页面的监听
        StartButton.addActionListener(this);
        StartButton.setActionCommand(StartCom);


    }
    public void CreateTestFrame() {
        //界面的名称
        this.setTitle(TitleFrame);

        this.CreateTechniquesPanel(); //创建选择的模式的轻量级容器

        //将选择模式和选择练习加入到CentPanel的容器中
        JPanel CenterPanel = new JPanel();
        CenterPanel.setLayout(new BorderLayout());
        CenterPanel.add(TechniquesPanel,BorderLayout.CENTER);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(CenterPanel,BorderLayout.CENTER);
        this.getContentPane().add(StartButton,BorderLayout.SOUTH);

    }

    //添加模式的组件
    public void CreateTechniquesPanel() {
        //添加选择模式的按钮
        JPanel EastPanel = new JPanel();
        EastPanel.setLayout(new BoxLayout(EastPanel,BoxLayout.Y_AXIS));

        EastPanel.add(ButtonActualP);
        EastPanel.add(ButtonActualT);
        EastPanel.add(ButtonActualA);
        EastPanel.add(ButtonScatteredP);
        EastPanel.add(ButtonScatteredT);
        EastPanel.add(ButtonScatteredA);


        //划出一个区域，将这些模式圈起来
        TechniquesPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray),"Selection Techniques",TitledBorder.LEFT,TitledBorder.TOP));
        TechniquesPanel.setLayout(new BorderLayout());
        TechniquesPanel.add(EastPanel);
        /*
        将按钮添加到按钮组里
         */
        //实际值模式
        ButtonFrame.add(ButtonActualP);
        ButtonFrame.add(ButtonActualT);
        ButtonFrame.add(ButtonActualA);
        //离散值模式
        ButtonFrame.add(ButtonScatteredP);
        ButtonFrame.add(ButtonScatteredT);
        ButtonFrame.add(ButtonScatteredA);
        //获取选择的模式

    }

    //从按钮组中获得选择的按钮
    public static JRadioButton getSelection (ButtonGroup group) {
        for(Enumeration e = group.getElements(); e.hasMoreElements();) {
            JRadioButton b = (JRadioButton) e.nextElement();
            if (b.getModel() == group.getSelection())
                return b;
        }
        return null;

    }
    @Override
    public void actionPerformed(ActionEvent e) {

        //根据选择的按钮，进入到不同的模式中
        if(getSelection(ButtonFrame).getActionCommand() == "Painting") {
            ActualPress actualPress = new ActualPress();
        }else if (getSelection(ButtonFrame).getActionCommand() == "Copy&Paste") {
            aCopy aCopy = new aCopy();
        }else if (getSelection(ButtonFrame).getActionCommand() == "Enlarge&Lessen") {
            aEnlarge aEnlarge = new aEnlarge();
        }else if (getSelection(ButtonFrame).getActionCommand() == "painting") {
            ScatteredAzimuth scatteredAzimuth = new ScatteredAzimuth();
        }else if (getSelection(ButtonFrame).getActionCommand() == "copy&paste") {
            sCopy sCopy = new sCopy();
        }else if (getSelection(ButtonFrame).getActionCommand() == "enlarge&lessen") {
            sEnlarge sEnlarge = new sEnlarge();
        }




        this.dispose();
    }
}
