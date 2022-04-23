package com.subway_footprint_system.springboot_project.Utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic="文件上传/写文件===ftp服务器:")
public class FtpUtil {
	private static FTPClient mFTPClient = new FTPClient();
	private static FtpUtil ftp = new FtpUtil();
	
	public FtpUtil() {
		// 在控制台打印操作过程
		// mFTPClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
	}

	/**
	 * 连接ftp服务器
	 * 
	 * @param host
	 *            ip地址
	 * @param port
	 *            端口号
	 * @param account
	 *            账号
	 * @param pwd
	 *            密码
	 * @return 是否连接成功
	 * @throws SocketException
	 * @throws IOException
	 */
	private boolean openConnection(String host, int port, String account, String pwd)
			throws SocketException, IOException {
		mFTPClient.setControlEncoding("UTF-8");
		mFTPClient.connect(host, port);

		if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
			mFTPClient.login(account, pwd);
			if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
				System.err.println(mFTPClient.getSystemType());
				FTPClientConfig config = new FTPClientConfig(mFTPClient.getSystemType().split(" ")[0]);
				config.setServerLanguageCode("zh");
				mFTPClient.configure(config);
				return true;
			}
		}
		disConnection();
		return false;
	}

	/**
	 * 登出并断开连接
	 */
	public void logout() {
		System.err.println("logout");
		if (mFTPClient.isConnected()) {
			System.err.println("logout");
			try {
				mFTPClient.logout();
				disConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 断开连接
	 */
	private void disConnection() {
		if (mFTPClient.isConnected()) {
			try {
				mFTPClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 上传文件到ftp服务器
	 */
	public static List<String> ftpUpload(List<File> files, String ftpUrl, int ftpPort,
										  String ftpUsername, String ftpPassword, String ftpRemotePath) {
		List<String> result = null;
		try {
			boolean isConnection = ftp.openConnection(ftpUrl, ftpPort, ftpUsername, ftpPassword);
			if (isConnection) {
				result = ftp.uploadFile(ftpRemotePath, files);
				if (result!=null) {
					log.info("文件上传成功！");
				} else {
					log.info("文件上传失败！");
				}
				ftp.logout();
			} else {
				log.info("链接ftp服务器失败，请检查配置信息是否正确！");
				result = null;
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 创建远程目录
	 * 
	 * @param remote
	 *            远程目录
	 * @param ftpClient
	 *            ftp客户端
	 * @return 是否创建成功
	 * @throws IOException
	 */
	public boolean createDirectory(String remote, FTPClient ftpClient) throws IOException {
		String dirctory = remote.substring(0, remote.lastIndexOf("/") + 1);
		if (!dirctory.equalsIgnoreCase("/") && !ftpClient.changeWorkingDirectory(dirctory)) {
			int start = 0;
			int end = 0;
			if (dirctory.startsWith("/")) {
				start = 1;
			}
			end = dirctory.indexOf("/", start);
			while (true) {
				String subDirctory = remote.substring(start, end);
				if (!ftpClient.changeWorkingDirectory(subDirctory)) {
					if (ftpClient.makeDirectory(subDirctory)) {
						ftpClient.changeWorkingDirectory(subDirctory);
					} else {
						System.err.println("创建目录失败");
						return false;
					}
				}
				start = end + 1;
				end = dirctory.indexOf("/", start);
				if (end <= start) {
					break;
				}
			}
		}
		return true;
	}

	/**
	 * 上传指定路径文件
	 *
	 * @param remotePath
	 *            上传文件的路径地址（文件夹地址）
	 * @param files
	 *            本地文件
	 * @throws IOException
	 *             异常
	 */
	public List<String> uploadFile(String remotePath, List<File> files) throws IOException {
		List<String> urllist=new ArrayList<>();
		// 进入被动模式
		mFTPClient.enterLocalPassiveMode();
		// 以二进制进行传输数据
		mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
		for (File localFile:files) {
			String fileName = localFile.getName();
			if (remotePath.contains("/")) {
				boolean isCreateOk = createDirectory(remotePath, mFTPClient);
				if (!isCreateOk) {
					System.err.println("文件夹创建失败");
					break;
				}
			}

			// 列出ftp服务器上的文件
			FTPFile[] ftpFiles = mFTPClient.listFiles(remotePath);
			long remoteSize = 0l;
			String remoteFilePath = remotePath + "/" + fileName;
			if (ftpFiles.length > 0) {
				FTPFile mFtpFile = null;
				for (FTPFile ftpFile : ftpFiles) {
					if (ftpFile.getName().endsWith(fileName)) {
						mFtpFile = ftpFile;
						break;
					}
				}
				if (mFtpFile != null) {
					remoteSize = mFtpFile.getSize();
					if (remoteSize == localFile.length()) {
						if(urllist.indexOf(remoteFilePath)<0){
							urllist.add(remoteFilePath);
						}
						System.err.println("文件"+ localFile.getName()+"已经上传成功");
						break;
					}
					if (remoteSize > localFile.length()) {
						if (!mFTPClient.deleteFile(remoteFilePath)) {
							System.err.println("服务端文件操作失败");
							break;
						} else {
							boolean isUpload = uploadlocalFile(remoteFilePath, localFile, 0);
							if(isUpload&&urllist.indexOf(remoteFilePath)<0){
								urllist.add(remoteFilePath);
							}
							System.err.println( localFile.getName()+"是否上传成功：" + isUpload);
							break;
						}
					}
					if (!uploadlocalFile(remoteFilePath, localFile, remoteSize)) {
						System.err.println("文件"+ localFile.getName()+"上传成功");
						if(urllist.indexOf(remoteFilePath)<0){
							urllist.add(remoteFilePath);
						}
						break;
					} else {
						// 断点续传失败删除文件，重新上传
						if (!mFTPClient.deleteFile(remoteFilePath)) {
							System.err.println("服务端文件操作失败");
						} else {
							boolean isUpload = uploadlocalFile(remoteFilePath, localFile, 0);
							System.err.println("是否上传"+ localFile.getName()+"成功：" + isUpload);
							if(isUpload&&urllist.indexOf(remoteFilePath)<0){
								urllist.add(remoteFilePath);
							}
							break;
						}
					}
				}

			}
			boolean isUpload = uploadlocalFile(remoteFilePath, localFile, remoteSize);
			System.err.println("是否上传"+ localFile.getName()+"成功：" + isUpload);
			if(isUpload&&urllist.indexOf(remoteFilePath)<0){
				urllist.add(remoteFilePath);
			}
		}

		return urllist;
	}

	/**
	 * 上传文件
	 *
	 * @param remoteFile
	 *            包含文件名的地址
	 * @param localFile
	 *            本地文件
	 * @param remoteSize
	 *            服务端已经存在的文件大小
	 * @return 是否上传成功
	 * @throws IOException
	 */
	private boolean uploadlocalFile(String remoteFile, File localFile, long remoteSize) throws IOException {
		long step = localFile.length() / 10;
		long process = 0;
		long readByteSize = 0;
		RandomAccessFile randomAccessFile = new RandomAccessFile(localFile, "r");
		OutputStream os = mFTPClient.appendFileStream(remoteFile);
		if (remoteSize > 0) {
			// 已经上传一部分的时候就要进行断点续传
			process = remoteSize / step;
			readByteSize = remoteSize;
			randomAccessFile.seek(remoteSize);
			mFTPClient.setRestartOffset(remoteSize);
		}
		byte[] buffers = new byte[1024];
		int len = -1;
		while ((len = randomAccessFile.read(buffers)) != -1) {
			os.write(buffers, 0, len);
			readByteSize += len;
			long newProcess = readByteSize / step;
			if (newProcess > process) {
				process = newProcess;
				System.err.println("当前上传进度为：" + process);
			}
		}
		os.flush();
		randomAccessFile.close();
		os.close();
		boolean result = mFTPClient.completePendingCommand();
		return result;
	}
	/**
	 * 向ftp写数据
	 */
//	public void writeFile() {
//
//		// 要写入的文件内容
//		String fileContent = "hello world，你好世界";
//		// ftp登录用户名
//		String userName = "admin";
//		// ftp登录密码
//		String userPassword = "xxxx";
//		// ftp地址
//		String server = "127.0.0.1";//直接ip地址
//		// 创建的文件
//		String fileName = "ftp.txt";
//		// 指定写入的目录
//		String path = "wd";
//
//		FTPClient ftpClient = new FTPClient();
//		try {
//			InputStream is = null;
//			// 1.输入流
//			is = new ByteArrayInputStream(fileContent.getBytes());
//			// 2.连接服务器
//			ftpClient.connect(server);
//			// 3.登录ftp
//			ftpClient.login(userName, userPassword);
//			// 4.指定写入的目录
//			ftpClient.changeWorkingDirectory(path);
//			// 5.写操作
//			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
//			ftpClient.storeFile(new String(fileName.getBytes("utf-8"),
//					"iso-8859-1"), is);
//			is.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (ftpClient.isConnected()) {
//				try {
//					ftpClient.disconnect();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}

}