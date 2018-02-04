/**
 * @author jkrishnan
 *
 */
/**
 * Wrapper class to be used for FTP client
 * It is a self contained class with the configuration to connect to the FTP server
 * defined in the class itself
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.HashMap;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPConnector {

	/*
	 * Normally the following four properties should be read of a configuration
	 * file. But for simplicity and time constraints it has been "#defined" here
	 */

	String serverAddress = "ftp.clickfuel.com"; // ftp server address
	int port = 21; // ftp uses default port Number 21
	private static String username = "ftp_integration_test";// username of ftp
															// server
	private static String password = "6k0Sb#EXT6jw"; // password of ftp server

	FTPClient ftpClient = null;

	/*
	 * Constructor to create the FTP client
	 */
	public FTPConnector() {
		ftpClient = new FTPClient();
	}

	/*
	 * Method to establish a FTP connection to the server
	 */
	public void connectToServer() {
		try {
			ftpClient.connect(serverAddress, port);
			ftpClient.login(username, password);

			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE | FTP.ASCII_FILE_TYPE);
		} catch (IOException ex) {
			System.out.println("Error in downloading files from ftp Server : "
					+ ex.getMessage());
		}
	}

	/*
	 * Method to cleanup and close the FTP connection
	 */
	public void closeConnection() {
		try {
			if (ftpClient.isConnected()) {
				ftpClient.logout();
				ftpClient.disconnect();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * Method that provides a list of specific files in a given subdirectory on
	 * the FTP server. Similar to an "ls" command on Unix.
	 */
	public FTPFile[] getFileList(String sFilePattern, String sFilePath) {
		try {
			return ftpClient.listFiles(sFilePath + sFilePattern);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Given a filename and the path on the FTP server, this method downloads
	 * the file locally and returns success or failure
	 */
	public boolean downloadFile(String sFilePath, String sFileName) {
		String remoteFilePath = sFilePath + sFileName;
		File localfile = new File(sFileName);
		OutputStream outputStream;
		boolean bResult = false;

		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(
					localfile));
			bResult = ftpClient.retrieveFile(remoteFilePath, outputStream);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bResult;
	}
}
