package login;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class RegisterForm extends JFrame implements ActionListener {
    JTextField userField;
    JPasswordField passField;
    JButton registerButton;

    public RegisterForm() {
        // Tạo các thành phần giao diện
        userField = new JTextField(15);
        passField = new JPasswordField(15);
        registerButton = new JButton("Đăng ký");

        // Thêm ActionListener cho nút đăng ký
        registerButton.addActionListener(this);

        // Sắp xếp giao diện
        JPanel panel = new JPanel();
        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(registerButton);

        // Thiết lập cửa sổ
        add(panel);
        setTitle("Đăng ký");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
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
            } else if (registerUser(username, password)) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
                new Logindatabase();
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Đăng ký không thành công! Tên người dùng có thể đã được sử dụng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    private boolean registerUser(String username, String password) {
        boolean isRegistered = false;

        try {
            // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
            String encryptedPassword = encrypt(password, "YourSecretKey1234"); // Thay "YourSecretKey1234" bằng khóa thực sự của bạn

            // Kết nối tới cơ sở dữ liệu
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/userdb", "root", "123456789");

                String query = "INSERT INTO users (username, password) VALUES (?, ?)";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, username);
                ps.setString(2, encryptedPassword);

                int rowsAffected = ps.executeUpdate();
                isRegistered = rowsAffected > 0;

                ps.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return isRegistered;
        }

        private String encrypt(String strToEncrypt, String secret) {
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
            new RegisterForm();
        }
    }