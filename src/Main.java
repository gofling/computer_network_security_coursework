import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

@SuppressWarnings("Since15")
public class Main extends Applet {
    private JButton button;
    private JButton button2;
    private JButton confirmButton;
    private JLabel keyPasswordLabel;
    private JPasswordField keyPassword;
    private JLabel encodedFileNameLabel;
    private JTextField encodedFileName;
    private String fileToEncodePath;
    private String keyPath;

    private String selectFile(FileNameExtensionFilter filter) {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        if (filter != null) {
            jfc.setFileFilter(filter);
        }

        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            return selectedFile.getAbsolutePath();
        } else return null;
    }

    public void init() {
        confirmButton = new JButton("Encode the file");
        keyPassword = new JPasswordField("", 20);
        keyPasswordLabel = new JLabel("Password");
        encodedFileName = new JTextField("encoded", 20);
        encodedFileNameLabel = new JLabel("Encoded file name");

        button = new JButton("Select your key");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyPath = selectFile(new FileNameExtensionFilter("Secret key", "gpg"));
            }
        });
        button2 = new JButton("Choose file to encode");
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileToEncodePath = selectFile(null);
            }
        });

        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(new java.io.File("."));
                jfc.setDialogTitle("Where do you want to save the encoded file ?");
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jfc.getSelectedFile();
                    try {
                        Sign.sign(fileToEncodePath, keyPath, keyPassword.getText(), selectedFile.getAbsolutePath() + "\\" + encodedFileName.getText());
                    } catch (Exception ex) {
                        String message = getStackTrace(ex);
                        System.out.print(message);
                        JOptionPane.showMessageDialog(null,
                                message,
                                "Encryption error",
                                JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                }
            }
        });


    }

    public void start() {
        setSize(300, 350);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createRigidArea(new Dimension(0,20)));
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0,20)));
        panel.add(keyPasswordLabel);
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(keyPassword);
        panel.add(Box.createRigidArea(new Dimension(0,20)));
        panel.add(button2);
        panel.add(Box.createRigidArea(new Dimension(0,20)));
        panel.add(encodedFileNameLabel);
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(encodedFileName);
        panel.add(Box.createRigidArea(new Dimension(0,20)));
        panel.add(confirmButton);
        panel.add(Box.createRigidArea(new Dimension(0,20)));
        add(panel);
    }

    // debug
    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

}


