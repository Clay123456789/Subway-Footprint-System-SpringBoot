package com.subway_footprint_system.springboot_project.Utils;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.lang.*;
import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@Component
@Slf4j(topic="文件上传/写文件===ftp服务器:")
public class FtpUtil {

	@Autowired
	private StringEncryptor encryptor;
	/**
	 * 图片默认缩放比率
	 */
	public final double DEFAULT_SCALE = 0.8d;

	/**
	 * 缩略图后缀
	 */
	public final String SUFFIX = "-thumbnail";
	/**
	 * ftp服务器地址
	 */
	@Value("${ftp.host}")
	private String host;

	/**
	 * ftp服务器端口
	 */
	@Value("${ftp.port}")
	private int port;

	/**
	 * ftp服务器用户名
	 */
	@Value("${ftp.username}")
	private String username;

	/**
	 * ftp服务器密码
	 */
	@Value("${ftp.password}")
	private String password;

	/**
	 * ftp服务器存放文件的路径
	 */
	@Value("${ftp.remotePath}")
	private  String remotePath;

	/**
	 * ftp服务器访问文件的端口
	 */
	@Value("${ftp.accessPort}")
	private  String accessPort;

	private  FTPClient mFTPClient = new FTPClient();

	public FtpUtil() {
		// 在控制台打印操作过程
		//mFTPClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
	}


	private boolean openConnection()throws SocketException, IOException {
		mFTPClient.setControlEncoding("UTF-8");
		mFTPClient.connect(host, port);

		if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
			mFTPClient.login(username, password);
			if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
				log.info(mFTPClient.getSystemType());
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
		log.info("logout");
		if (mFTPClient.isConnected()) {
			log.info("logout");
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
	public List<String> ftpUpload(List<File> files) {
		List<String> result = null;
		try {
			boolean isConnection = openConnection();
			if (isConnection) {
				result = uploadFile(remotePath, files);
				if (null==result) {
					log.info("文件上传失败！");
				} else if(result.size()<files.size()){
					log.info("部分文件上传失败！");
				}else{
					log.info("文件上传成功！");
				}
				logout();
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
						log.info("创建目录失败");
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
	 */
	public List<String> uploadFile(String remotePath, List<File> files) throws IOException {
		List<String> urllist=new ArrayList<>();
		// 进入被动模式
		mFTPClient.enterLocalPassiveMode();
		// 以二进制进行传输数据
		mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
		for (File localFile:files) {
			String fileName=localFile.getName();
			//如果是原图，则上传的文件名称需附带随机数
			if(!fileName.contains(SUFFIX)){
				fileName = appendRandomKey(fileName);
			}
			if (remotePath.contains("/")) {
				boolean isCreateOk = createDirectory(remotePath, mFTPClient);
				if (!isCreateOk) {
					log.info("文件夹创建失败");
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
							urllist.add(getFtpPath()+fileName);
						}
						log.info("文件"+ localFile.getName()+"已经上传成功");
						continue;
					}
					if (remoteSize > localFile.length()) {
						if (!mFTPClient.deleteFile(remoteFilePath)) {
							log.info("服务端文件操作失败");
							break;
						} else {
							boolean isUpload = uploadlocalFile(remoteFilePath, localFile, 0);
							if(isUpload&&urllist.indexOf(remoteFilePath)<0){
								deleteCompressImage(remotePath,fileName);
								urllist.add(getFtpPath()+fileName);
							}
							log.info( localFile.getName()+"是否上传成功：" + isUpload);
							continue;
						}
					}
					if (!uploadlocalFile(remoteFilePath, localFile, remoteSize)) {
						log.info("文件"+ localFile.getName()+"上传成功");
						if(urllist.indexOf(remoteFilePath)<0){
							deleteCompressImage(remotePath,fileName);
							urllist.add(getFtpPath()+fileName);
						}
						continue;
					} else {
						// 断点续传失败删除文件，重新上传
						if (!mFTPClient.deleteFile(remoteFilePath)) {
							log.info("服务端文件操作失败");
						} else {
							boolean isUpload = uploadlocalFile(remoteFilePath, localFile, 0);
							log.info("是否上传"+ localFile.getName()+"成功：" + isUpload);
							if(isUpload&&urllist.indexOf(remoteFilePath)<0){
								deleteCompressImage(remotePath,fileName);
								urllist.add(getFtpPath()+fileName);
							}
							continue;
						}
					}
				}

			}
			boolean isUpload = uploadlocalFile(remoteFilePath, localFile, remoteSize);
			log.info("是否上传"+ localFile.getName()+"成功：" + isUpload);
			if(isUpload&&urllist.indexOf(remoteFilePath)<0){
				deleteCompressImage(remotePath,fileName);
				urllist.add(getFtpPath()+fileName);
			}
		}

		return urllist;
	}

	/**
	 * 上传文件
	 *
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
				log.info("当前上传进度为：" + process);
			}
		}
		os.flush();
		randomAccessFile.close();
		os.close();
		boolean result = mFTPClient.completePendingCommand();
		return result;
	}
	/*
	*
	* 删除指定目录下原图指定比率的略缩图
	*
	* */
	private void deleteCompressImage(String remotePath,String fileName,double scale)throws IOException{
		if(!fileName.contains(SUFFIX)){
			// 列出ftp服务器上的文件
			FTPFile[] ftpFiles = mFTPClient.listFiles(remotePath);
			for (FTPFile ftpFile : ftpFiles) {
				String newFileName=appendSuffix(fileName,SUFFIX,scale);
				if (ftpFile.getName().contains(newFileName)) {
					if(mFTPClient.deleteFile(remotePath + "/" + newFileName)) {
						log.info("删除旧略缩图成功！");
					}else{
						log.info("删除旧略缩图失败！");
					}
					break;
				}
			}
		}
	}
	/*
	 *
	 * 删除指定目录下原图所有比率的略缩图
	 *
	 * */
	private void deleteCompressImage(String remotePath,String fileName)throws IOException{
		if(!fileName.contains(SUFFIX)){
			// 列出ftp服务器上的文件
			FTPFile[] ftpFiles = mFTPClient.listFiles(remotePath);
			for (FTPFile ftpFile : ftpFiles) {
				String newFileName=appendSuffix(fileName,SUFFIX);
				if (ftpFile.getName().endsWith(newFileName)) {
					if(!mFTPClient.deleteFile(remotePath + "/" + newFileName)) {
						log.info("删除旧略缩图失败！");
					}
				}
			}
		}

	}
	/*
	* 压缩ftp上的图片
	* */
    public List<String> ftpCompress(double scale,List<String> fileNames){
		List<String> result = null;
		try {
			boolean isConnection = openConnection();
			if (isConnection) {
				result = compressImages(scale,remotePath,fileNames);
				if (null==result) {
					log.info("文件压缩失败！");
				} else if(result.size()<fileNames.size()){
					log.info("部分文件压缩失败！");
				}else{
					log.info("文件压缩成功！");
				}
				logout();
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

	/*
	 * 以指定比率压缩ftp上的图片
	 * */
	public List<String> compressImages(double scale,String remotePath,List<String> fileNames)throws IOException{
		List<String> urllist=new ArrayList<>();
		// 进入被动模式
		mFTPClient.enterLocalPassiveMode();
		// 以二进制进行传输数据
		mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
		for (String fileName:fileNames) {
			String newFileName=appendSuffix(ReduceRandomKey(fileName),SUFFIX,scale);
			if (remotePath.contains("/")) {
				boolean isCreateOk = createDirectory(remotePath, mFTPClient);
				if (!isCreateOk) {
					log.info("文件夹创建失败");
					break;
				}
			}
			// 列出ftp服务器上的文件
			FTPFile[] ftpFiles = mFTPClient.listFiles(remotePath);
			if (ftpFiles.length > 0) {
				int i = 0;
				for (; i <ftpFiles.length ; i++) {
					if (ftpFiles[i].getName().endsWith(newFileName)) {//略缩图已存在
						if(urllist.indexOf(getFtpPath()+newFileName)<0){
							urllist.add(getFtpPath()+newFileName);
						}
						break;
					}
				}
				if(i==ftpFiles.length){//略缩图不存在
					for (FTPFile ftpFile:ftpFiles) {
						if (ftpFile.getName().endsWith(fileName)) {//找到原图
							if (!ftpFile.getName().contains(SUFFIX) && isImage(getFileExtention(ftpFile.getName()))) {
								String url=compressImage(scale,remotePath,ftpFile);//压缩
								if(urllist.indexOf(url)<0){
									urllist.add(url);
								}
								log.info("文件"+ ftpFile.getName()+"已经压缩成功");
							}
							break;
						}
					}

				}
			}

		}

		return urllist;
	}
	/**
	 * 按照指定缩放率生成缩略图并上传至指定的目录
	 *
	 */
	public String compressImage(double scale, String remotePath, FTPFile file) throws IOException {
		//先下载ftp文件到系统临时文件1
		String fileName=file.getName();
		System.out.println(fileName);
		String newFileName=appendSuffix(ReduceRandomKey(file.getName()),SUFFIX,scale);
		File tempFile1=new File(ReduceRandomKey(file.getName()));
		System.out.println(tempFile1.getName());
		FileOutputStream fos = new FileOutputStream(tempFile1);
		InputStream is = mFTPClient.retrieveFileStream(remotePath+"/"+fileName);
		byte[] buffers = new byte[1024];
		int len = -1;
		while ((len = is.read(buffers)) != -1) {
			fos.write(buffers, 0, len);
		}
		is.close();
		fos.close();
		boolean isDo = mFTPClient.completePendingCommand();
		if (isDo) {
			//对临时文件1进行压缩,保存至临时文件2
			File tempFile2=new File(newFileName);
			BufferedImage thumbnail =Thumbnails.of(tempFile1).scale(scale).asBufferedImage();
			ImageIO.write(thumbnail, getFileExtention(newFileName), tempFile2);
			System.out.println(tempFile1.getName());
			tempFile1.delete();
			System.out.println(tempFile2.getName());
			//将临时文件2上传至ftp服务器
			List<File>list=new ArrayList<>();
			list.add(tempFile2);
			List<String> result = null;
			result =uploadFile(remotePath,list);
			if (null==result) {
				log.info("文件上传失败！");
				return null;
			}else{
				log.info("文件上传成功！");
			}
			tempFile2.delete();
			return result.get(0);
		}
		//ftp文件下载失败
		log.info("文件压缩失败！");
		return null;

	}

	/*
	 * 获得指定位数的随机数
	 * */

	public String getRandom(int len) {
		String source = "0123456789abcdefghijklmnopqrstuvwxyz";
		Random r = new Random();
		StringBuilder rs = new StringBuilder();
		for (int j = 0; j < len; j++) {
			rs.append(source.charAt(r.nextInt(36)));
		}
		return rs.toString();
	}
	public String getRandomkey(int len){
		String ran=getRandom(len);
		String key=null;
		System.out.println("ran----------"+ran);
		do{
			//对随机数进行加密
			key=encryptor.encrypt(ran);
		}while (key.contains("/")||key.contains("\\")||key.contains(".")||key.contains("-"));
		return key;
	}
	public String getRandomBykey(String Key){
		int indexofkey=Key.lastIndexOf("-")+1;
		if(indexofkey>0){
			return Key.substring(0,indexofkey)+encryptor.decrypt(Key.substring(indexofkey));
		}
		return encryptor.decrypt(Key);
	}
	/**
	 * 文件名附加一个随机数
	 *
	 */
	public String appendRandomKey(String fileName) {
		String newFileName = "";

		int indexOfDot = fileName.lastIndexOf('.');

		if (indexOfDot != -1) {
			newFileName = fileName.substring(0, indexOfDot);
			newFileName += "-"+getRandomkey(20);
			newFileName += fileName.substring(indexOfDot);
		}

		return newFileName;
	}
	/**
	 * 文件名去掉附加的随机数
	 *
	 */
	public String ReduceRandomKey(String fileName) {
		String newFileName = "";

		int indexOfSUFFIX = fileName.lastIndexOf(SUFFIX);
		if (indexOfSUFFIX == -1) {//为原图
			int indexOfDot = fileName.lastIndexOf('.');
			if (indexOfDot != -1) {
				newFileName = fileName.substring(0, indexOfDot);
				newFileName = getRandomBykey(newFileName);
				newFileName += fileName.substring(indexOfDot);
			} else {//啥都不是，不处理，原路返回
				return newFileName;
			}
		}

		return newFileName;
	}
	/**
	 * 文件追加附带比率的后缀
	 *
	 */
	public String appendSuffix(String fileName, String suffix,double scale) {
		String newFileName = "";
		int indexOfDot = fileName.lastIndexOf('.');

		if (indexOfDot != -1) {
			newFileName = fileName.substring(0, indexOfDot);
			newFileName += suffix;
			newFileName += "-"+scale;
			newFileName += fileName.substring(indexOfDot);
		} else {
			newFileName = fileName + suffix;
		}

		return newFileName;
	}
	/**
	 * 文件追加不附带比率的后缀
	 *
	 */
	public String appendSuffix(String fileName, String suffix) {
		String newFileName = "";

		int indexOfDot = fileName.lastIndexOf('.');

		if (indexOfDot != -1) {
			newFileName = fileName.substring(0, indexOfDot);
			newFileName += suffix;
			newFileName += fileName.substring(indexOfDot);
		} else {
			newFileName = fileName + suffix;
		}

		return newFileName;
	}
	/**
	 * 根据文件扩展名判断文件是否图片格式
	 *
	 * @param extension 文件扩展名
	 * @return
	 */
	public boolean isImage(String extension) {
		String[] imageExtension = new String[]{"jpeg", "jpg", "gif", "bmp", "png"};

		for (String e : imageExtension) {
			if (extension.toLowerCase().equals(e)) {
				return true;
			}
		}
		return false;
	}

	public String getFileExtention(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}
	public String getOriginalImage(String path) throws IOException {
		if(path.contains(getFtpPath())){
			String fileName=path.replace(getFtpPath(),"");
			if(isImage(getFileExtention(fileName))&&fileName.contains(SUFFIX)){
				//验证是否为服务器上的略缩图
				boolean isConnection = openConnection();
				if (isConnection) {
					// 进入被动模式
					mFTPClient.enterLocalPassiveMode();
					// 以二进制进行传输数据
					mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
						if (remotePath.contains("/")) {
							boolean isCreateOk = createDirectory(remotePath, mFTPClient);
							if (!isCreateOk) {
								log.info("文件夹创建失败");
								logout();
								return null;
							}
						}

						// 列出ftp服务器上的文件
						FTPFile[] ftpFiles = mFTPClient.listFiles(remotePath);
						if (ftpFiles.length > 0) {
							FTPFile mFtpFile = null;
							for (FTPFile ftpFile : ftpFiles) {
								if (ftpFile.getName().endsWith(fileName)) {
									mFtpFile = ftpFile;
									break;
								}
							}
							if (mFtpFile != null) {
								//确认为当前ftp的略缩图，进行解析，得到原图
								int indexOfSUFFIX = fileName.lastIndexOf(SUFFIX);
								int indexOfs =fileName.substring(0,indexOfSUFFIX).lastIndexOf("-");
								String fileName1=fileName.substring(0,indexOfs);
								String random=fileName.substring(indexOfs+1,indexOfSUFFIX);
								for (FTPFile ftpFile : ftpFiles) {
									if (ftpFile.getName().contains(fileName1)&&!ftpFile.getName().contains(SUFFIX)) {
										int indexOfDot =ftpFile.getName().lastIndexOf(".");
										int indexOfjian =ftpFile.getName().lastIndexOf("-");
										String key=ftpFile.getName().substring(indexOfjian+1,indexOfDot);
										if(encryptor.decrypt(key).equals(random)){//找到原图
											logout();
											return getFtpPath()+ftpFile.getName();
										}
									}
								}
								return null;
							}

						}

					}
				logout();
			}
		}
		return null;
	}
	public String getFtpPath(){
		return "http://"+host+":"+accessPort+remotePath+"/";
	}

}