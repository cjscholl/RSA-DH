package encryption;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.util.concurrent.TimeUnit;

public class Driver extends JFrame {
	Driver() {
		setTitle("Encryption");
		// set frame bounds
		setBounds(1000, 1000, 1000, 1000);
		// set frame layout
		setLayout(new FlowLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		// frame and panel creation
		JFrame frame = new Driver();
		JPanel panel1 = new JPanel();
		// buttons and text fields
		JButton login = new JButton("Login");
		JLabel userLabel = new JLabel("Username:");
		JLabel passLabel = new JLabel("Password:");
		JTextField userTextField = new JTextField(10);
		JPasswordField passTextField = new JPasswordField(10);

		// add features to screen
		panel1.add(userLabel);
		panel1.add(userTextField);
		panel1.add(passLabel);
		panel1.add(passTextField);
		panel1.add(login);
		frame.add(panel1);
		// second jpanel
		JPanel explanation = new JPanel();
		JLabel output = new JLabel("Output");
		explanation.add(output);
		JTextArea out = new JTextArea(30, 50);
		explanation.add(out);
		frame.add(explanation);
		// set to visible
		frame.setVisible(true);

		ActionListener key = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = "RSA & DH Encryption: \n\n";
				str += "Generate 2 random prime number (p*q): ";
				int p = getPrime(2);
				int q = getPrime(2);
				str += "p = " + p + " q = " + q + " ";
				int z = (p - 1) * (q - 1);
				str += "\n" + "\n" + " z = (p-1)*(q-1) = " + z;
				int k = 2;
				while (!isCoprime(k, z)) {
					k++;
				}
				str += "\n\n" + "Find a k coprime z. AKA gcd(z,k) = 1: " + k;
				int n = p * q;
				str += "\n\n" + "Give out public key. Public key: k&n = " + k + " & " + n;
				int j = j(k, z);
				str += "\n\n" + "Create private key. Private key: j where 1 = [(k*j)%z]. j = " + j;
				int c = getPrime(2);
				int g = g(c);
				str += "\n\n" + "Find primitive root g based off generated prime c: g = " + g;
				str += "\n\n" + "c and g are public information: c = " + c + " g = " + g;
				int a = (int) (Math.random() * 10 + 1);
				int b = (int) (Math.random() * 10 + 1);
				str += " a = " + a + " " + " b = " + b + "\n";
				int A = (int) (Math.pow(g, a) % c);
				int B = (int) (Math.pow(g, b) % c);
				str += "A = " + A + " " + " B = " + B;
				int s1 = (int) (Math.pow(B, a) % c);
				int s2 = (int) (Math.pow(A, b) % c);
				str += "\n\n";
				str += s1 + " = " + s2 + "; this number is the secret key between user and server.";
				char[] password = passTextField.getPassword();
				int[] userData = convertCharToInt(password);

				str += "\n" + "Encryption: ";
				// encryption
				for (int i = 0; i < userData.length; i++) {
					userData[i] = encryptRSA(userData[i], k, n);
					userData[i] = userData[i] * s1;
					str += userData[i] + " ";
				}
				str += "\n" + "Decryption: ";
				// decryption
				for (int i = 0; i < userData.length; i++) {
					userData[i] = (int) (userData[i] / s1);
					userData[i] = decryptRSA(userData[i], j, n);
					str += userData[i] + " ";
				}
				str += '\n' + convertIntToString(userData);
				out.setText(str);
			}
		};
		login.addActionListener(key);

	}

	// generates a prime number
	public static int getPrime(int n) {
		int prime = 0;
		String sprime = Long.toString(prime);
		char lastDigit = sprime.charAt(sprime.length() - 1);
		while (lastDigit == '0' || lastDigit == '2' || lastDigit == '4' || lastDigit == '5' || lastDigit == '6'
				|| lastDigit == '8') {
			prime = (int) (Math.random() * 9 * Math.pow(10, n - 1) + Math.pow(10, n - 1));
			sprime = Long.toString(prime);
			lastDigit = sprime.charAt(sprime.length() - 1);
		}
		while (!(isPrime(prime))) {
			prime = getPrime(n);
		}
		return prime;
	}

	// converts an array of chars to their ascii equivalent
	public static int[] convertCharToInt(char[] password) {
		int[] numPassword = new int[password.length];
		for (int cnt = 0; cnt < password.length; ++cnt) {
			char temp = password[cnt];
			int tempy = (int) temp;
			numPassword[cnt] = tempy;
		}
		return numPassword;
	}

	// converts an array of ascii valued chars into a single string
	public static String convertIntToString(int[] password) {
		char[] charPassword = new char[password.length];
		String passwordString = "";
		for (int cnt = 0; cnt < password.length; ++cnt) {
			int temp = password[cnt];
			charPassword[cnt] = (char) temp;
		}
		for (int cnt = 0; cnt < password.length; ++cnt) {
			passwordString += charPassword[cnt];
		}
		return passwordString;
	}

	// checks if a number is prime implementing sieve method
	public static boolean isPrime(int number) {
		if (number == 0)
			return false;
		for (int check = 2; check < number / 2; ++check) {
			if (number % check == 0) {
				return false;
			}
		}
		return true;
	}

	// determines if two numbers are coprime
	public static boolean isCoprime(int a, int b) {
		if (gcd(a, b) == 1)
			return true;
		return false;
	}

	// Implements the Euclidean algorithm to find the greatest common divisor
	// between two integers
	public static int gcd(int a, int b) {
		if (a == 0)
			return b;
		if (b == 0)
			return a;
		return gcd(b, a % b);
	}

	// determines j, the multiplicative inverse of k in mod z
	public static int j(int k, int z) {
		k %= z;
		for (int i = 1; i < z; i++) {
			if ((k * i) % z == 1)
				return i;
		}
		return 1;
	}

	// determines the primitive root of a prime number for DH encryption
	public static int g(int c) {
		int g = 2;
		int i;
		for (i = 1; i < c - 1; i++) {
			if (Math.pow(g, i) % c == 1) {
				g++; // increment g
			} else if (i == c - 2) {
				break;// if this is true, stop changing 'g'
			}
		}
		return g;
	}

	// encrypts data via RSA
	public static int encryptRSA(int a, int k, int n) {
		double E = Math.pow(a, k) % n;
		return (int) E;
	}

	// decrypts the RSA encrypted data
	public static int decryptRSA(int E, int j, int n) {
		int a = modPow(E, j, n);
		return a;
	}

	// exists due to the immense size of the values in the RSA Decryption
	public static int modPow(int base, int exp, int mod) {
		int result = 1;
		for (int i = 0; i < exp; i++) {
			result = ((result * base) % mod);
		}
		return result;
	}
}
