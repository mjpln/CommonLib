package com.knowology.ServiceHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import com.knowology.UtilityOperate.StringOper;

import sun.net.ftp.FtpClient;

/**
 * Java自带的API对FTP的操作
 * @Title:Ftp.java
 * @author: 钱磊
 */
public class FTPHelper {

	    /**
	     * 本地文件名
	     */
	    private String localfilename;
	    /**
	     * 远程文件名
	     */
	    private String remotefilename;
	    /**
	     * FTP客户端
	     */
	    private FtpClient ftpClient;

	    /**
	     * 服务器连接
	     * @param ip 服务器IP
	     * @param port 服务器端口
	     * @param user 用户名
	     * @param password 密码
	     * @param path 服务器路径
	     * @author 钱磊
	     * @date   2017年5月2日
	     */
	    public void connectServer(String ip, int port, String user,
	            char[] password, String path) {
	        try {
	            /* ******连接服务器的两种方法*******/
	            //第一种方法
//	            ftpClient = new FtpClient();
//	            ftpClient.openServer(ip, port);
	            //第二种方法
	            ftpClient = FtpClient.create(ip);
	            
	            ftpClient.login(user, password);
	            // 设置成2进制传输
	            ftpClient.setBinaryType();
	            System.out.println("login success!");
	            if (path.length() != 0){
	                //把远程系统上的目录切换到参数path所指定的目录
	                ftpClient.changeDirectory(path);
	            }
	            ftpClient.setBinaryType();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            throw new RuntimeException(ex);
	        }
	    }
	    /**
	     * 关闭连接
	     * @author 钱磊
	     * @date   2017年5月2日
	     */
	    public void closeConnect() {
	        try {
	            ftpClient.close();
	            System.out.println("disconnect success");
	        } catch (Exception ex) {
	            System.out.println("not disconnect");
	            ex.printStackTrace();
	            throw new RuntimeException(ex);
	        }
	    }
	    /**
	     * 上传文件，要求远程FTP上有此文件
	     * @param localFile 本地文件
	     * @param remoteFile 远程文件
	     * @author 钱磊
	     * @date   2017年5月2日
	     */
	    public void upload(String localFile, String remoteFile) {
	        this.localfilename = localFile;
	        this.remotefilename = remoteFile;
	        OutputStream os = null;
	        FileInputStream is = null;
	        try {
	            //将远程文件加入输出流中
	            os = ftpClient.putFileStream(this.remotefilename);
	            //获取本地文件的输入流
	            File file_in = new File(this.localfilename);
	            is = new FileInputStream(file_in);
	            //创建一个缓冲区
	            byte[] bytes = new byte[1024];
	            int c;
	            while ((c = is.read(bytes)) != -1) {
	                os.write(bytes, 0, c);
	            }
	            System.out.println("upload success");
	        } catch (Exception ex) {
	            System.out.println("not upload");
	            ex.printStackTrace();
	            throw new RuntimeException(ex);
	        } finally{
	            try {
	                if(is != null){
	                    is.close();
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                try {
	                    if(os != null){
	                        os.close();
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	    
	    /**
	     * 下载文件
	     * @param remoteFile 远程文件路径(服务器端)
	     * @param localFile 本地文件路径(客户端)
	     * @author 钱磊	
	     * @date   2017年5月2日
	     */
	    public void download(String remoteFile, String localFile) {
	        InputStream is = null;
	        FileOutputStream os = null;
	        try {
	            //获取远程机器上的文件filename，借助TelnetInputStream把该文件传送到本地。
	            is = ftpClient.getFileStream(remoteFile);
	            File file_in = new File(localFile);
	            os = new FileOutputStream(file_in);
	            byte[] bytes = new byte[1024];
	            int c;
	            while ((c = is.read(bytes)) != -1) {
	                os.write(bytes, 0, c);
	            }
	            System.out.println("download success");
	        } catch (Exception ex) {
	            System.out.println("not download");
	            ex.printStackTrace();
	            throw new RuntimeException(ex);
	        } finally{
	            try {
	                if(is != null){
	                    is.close();
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                try {
	                    if(os != null){
	                        os.close();
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }

	    public static void main(String agrs[]) {

	        String filepath[] = { "/ah_智送会话清单20170515-20170521.csv", "/ah20170328.txt"};
	        String localfilepath[] = { "C:\\Users\\Administrator\\Desktop\\ah_智送会话清单20170227-20170305.csv","C:\\Users\\Administrator\\Desktop\\ah20170328.txt"};

	        FTPHelper fu = new FTPHelper();
	        /*
	         * 使用默认的端口号、用户名、密码以及根目录连接FTP服务器
	         */
	        fu.connectServer("132.108.207.27", 21, "anhui30", "anhui_3400_30".toCharArray(), "/");
	        
	        //下载
	        for (int i = 0; i < filepath.length; i++) {
	            fu.download(filepath[i], localfilepath[i]);
	        }
	        
//	        String localfile = "C:\\Users\\Administrator\\Desktop\\测试.txt";
//	        String remotefile = "/测试.txt";
//	        //上传
//	        fu.upload(localfile, remotefile);
	        fu.closeConnect();
	    }
}
