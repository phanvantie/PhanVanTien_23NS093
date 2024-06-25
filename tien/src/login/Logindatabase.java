package login;

import javax.swing.*;

import java.awt.event.*;
import java.sql.*;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Logindatabase extends JFrame implements ActionListener {
    JTextField userField;
    JPasswordField passField;
    JButton loginButton, registerButton;

    public Logindatabase() {
        // Tạo các thành phần giao diện
        setLocationRelativeTo(null);
        userField = new JTextField(15);
        passField = new JPasswordField(15);
        loginButton = new JButton("Đăng Nhập");
        registerButton = new JButton("Đăng Ký");

        // Thêm ActionListener cho nút đăng nhập và đăng ký
        loginButton.addActionListener(this);
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RegisterForm();
                Logindatabase.this.dispose();
            }
        });

        // Sắp xếp giao diện
        JPanel panel = new JPanel();
        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(loginButton);
        panel.add(registerButton);

        // Thiết lập cửa sổ
        add(panel);
        setTitle("Đăng Nhập ");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent ae) {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        try {
            // Kiểm tra xem cả username và password có trống không
            if (username.isEmpty() && password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username và Password không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else if (checkLogin(username, password)) {
                // Chuyển sang form khác khi đăng nhập thành công
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                new Login();
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sai username hoặc password!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean checkLogin(String username, String password) {
        boolean isValid = false;

        try {
            // Mã hóa mật khẩu nhập vào để so sánh với mật khẩu đã mã hóa trong cơ sở dữ liệu
            String encryptedPassword = encrypt(password, "YourSecretKey1234"); 

            // Kết nối tới cơ sở dữ liệu
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/userdb", "root", "123456789"); 

            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, encryptedPassword);

            ResultSet rs = ps.executeQuery();
            isValid = rs.next();

            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    private String encrypt(String strToEncrypt, String secret)
    {
        try {
            byte[] key = Arrays.copyOf(secret.getBytes(), 16); // Đảm bảo khóa có độ dài 16 byte
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes()));
        } catch (Exception e) {
            System.out.println("Lỗi khi mã hóa: " + e.toString());
        }
        return null;
    }

    public static void main(String[] args) {
        new Logindatabase();
    }
}