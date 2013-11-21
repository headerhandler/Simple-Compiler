package com.compiler.main;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.compiler.parser.ConvertionToSML;
public class FileString extends JFrame
{
    private JMenuItem choose; // button to choose file
    private static String textAreaString;
    private JMenuBar bar;
    private JMenu file;
    private JMenuItem save;
    private JMenuItem exit, about, help;
    private Timer emptyCheck;
    private Font font = new Font("Serif", Font.BOLD, 15);
    private JPanel panelNorth;
    private Container southRegion;
    public static JTextArea textArea;
    private static TextArea outputArea;
    private JButton restart;
    private JMenu aboutMenu;
    private JButton start; // button to start compilation and execution
    public static File fileLoad; // file variable to store sml file being chosen
    public static PrintStream textAreaStream;
    private static FileInputStream inputStream;
    public FileString()
    {
        super("Simple Interpreter");
        try
        {
            for (UIManager.LookAndFeelInfo look : UIManager.getInstalledLookAndFeels())
            {
                if (look.getName().equals("Windows"))
                {
                    UIManager.setLookAndFeel(look.getClassName());
                    SwingUtilities.updateComponentTreeUI(this);
                    break;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        setLayout(new BorderLayout());
        bar = new JMenuBar();
        file = new JMenu("File");
        aboutMenu = new JMenu("About");
        help = new JMenuItem("Help");
        about = new JMenuItem("About");
        choose = new JMenuItem("Open File");
        start = new JButton("Compile and Run");
        save = new JMenuItem("Save File");
        panelNorth = new JPanel();
        panelNorth.setLayout(new FlowLayout());
        restart = new JButton("Restart application");
        exit = new JMenuItem("Exit");
        file.add(choose);
        file.add(save);
        file.add(exit);
        bar.add(file);
        bar.add(aboutMenu);
        aboutMenu.add(help);
        aboutMenu.add(about);
        outputArea = new TextArea(7, 60);
        outputArea.setFont(font);
        outputArea.setEditable(false);
        TextConsole console = new TextConsole(outputArea);
        textAreaStream = new PrintStream(console, true);
        southRegion = new Container();
        panelNorth.add(start);
        panelNorth.add(restart);
        southRegion.setLayout(new BorderLayout());
        southRegion.add(panelNorth, BorderLayout.NORTH);
        southRegion.add(outputArea, BorderLayout.CENTER);
        textArea = new JTextArea(12,60);
        textArea.setFont(font);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        add(bar, BorderLayout.NORTH);
        add(southRegion, BorderLayout.SOUTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        help.addActionListener(
        new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(null, "To learn the Simple language, use the text file that comes with this release");
            }
        });
        about.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(null, "For enquiries, source requests and general questions, mailT0: oduaht@gmail.com");
            }
        });
        restart.addActionListener(
        new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                restart();
            }
        });
        emptyCheck = new Timer(50, new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if(!textArea.getText().trim().equals(""))
                    start.setEnabled(true);
                else
                    start.setEnabled(false);
            }
        });
        emptyCheck.start();
        start.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent event)
            {
                //convertionToSML.firstPass(textArea.getText());
                try{
                ConvertionToSML.firstPass(textArea.getText());
                }
                catch(Exception e)
                {
                    emptyCheck.stop();
                    System.out.println("An unknown error occurred. Application will be restarted.");
                    restart();
                }
                finally 
                {
                    if (emptyCheck.isRunning())
                        emptyCheck.stop();
                    start.setEnabled(false); 
                }
            }
        });
        choose.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent event)
            {   // choose sml file
                JFileChooser fileChosen = new JFileChooser();
                fileChosen.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int value = fileChosen.showOpenDialog(FileString.this);
                if (value == JFileChooser.CANCEL_OPTION)
                    return;
                if (value == JFileChooser.APPROVE_OPTION)
                {
                    try
                    {
                        inputStream = new FileInputStream(fileChosen.getSelectedFile());
                        byte[] fileCont = new byte[(int)fileChosen.getSelectedFile().length()];
                        textAreaString = new String(fileCont);
                        textArea.setText(textAreaString);
                    }
                    catch(Exception er){}
                    
                }
            }
        });
        save.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    PrintWriter saveFile = new PrintWriter(new File(JOptionPane.showInputDialog(null, "File Name >>  ") + ".sim"));
                    saveFile.write(textArea.getText());
                    saveFile.close();
                }
                catch(FileNotFoundException err)
                {
                    JOptionPane.showMessageDialog(null, "Error while trying to save file");
                }
            }
        });
        exit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(1);
            }
        });
    }
    public static void main(String[] args) throws Exception
    {
        FileString newFile = new FileString();
        System.setOut(textAreaStream);
        System.setErr(textAreaStream);
        newFile.pack();
        newFile.setLocation(new Point(350,150));
        newFile.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFile.setVisible(true);
    }
    public static void restart()
    {
        try{
            Thread.sleep(600);
            Runtime.getRuntime().exec("java -jar SimpleCompiler.jar");
        }
        catch (Exception e) { e.printStackTrace();}
        System.exit(0);
    }
    private class TextConsole extends OutputStream
    {
        private TextArea output;
        private String readLineBuffer = new String();
        public TextConsole(TextArea param)
        {
            output = param;
        }
        public synchronized void write(int i) throws IOException
        {
            // make sure output is written line by line, and not by character/byte
            if ((char)i != '\n')
                readLineBuffer += String.valueOf((char)i);
            else
            {
                output.append(readLineBuffer + "\n");
                readLineBuffer = "";
            }
        }
    }
}