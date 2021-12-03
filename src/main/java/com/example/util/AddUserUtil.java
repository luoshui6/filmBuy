package com.example.util;

import com.example.entity.Account;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class AddUserUtil {
    private static void createUser(int count) throws Exception{
        List<Account> users=new ArrayList<Account>();
        //生成用户
        for(int i=0; i<count; i++) {
            Account user=new Account();
            user.setId(1000L + i);//注意，两数相加，不会超出数位数
            user.setName("user"+i);
            user.setSalt(Long.toString(System.currentTimeMillis()));
            user.setPassword(PasswordGenerateUtil.getPassword(user.getName(),"123",user.getSalt2(),2));
            user.setLevel(2);
            users.add(user);
            writerText("E:/filmBuy/", user.getId() + ","+ user.getName());
//            writerText("E:/filmBuy/", user.getId() + ","+ user.getName() + "," + "[\"4排7座\"]" );
        }
		System.out.println("craete users ----insert to db");
//        插入数据库
//		Connection conn=DBUtil.getConn();
//		String sql="insert into user_info(id, name, password, level, salt) values"
//				+ "(?,?,?,?,?)";
//		PreparedStatement pstmt=conn.prepareStatement(sql);
//		//生成用户
//		for(int i=0;i<users.size();i++) {
//			Account user=users.get(i);
//			pstmt.setLong(1, user.getId());
//			pstmt.setString(2, user.getName());
//			pstmt.setString(3, user.getPassword());
//			pstmt.setInt(4, user.getLevel());
//			pstmt.setString(5, user.getSalt());
//
//			pstmt.addBatch();
//		}
//		pstmt.executeBatch();
//		pstmt.close();
//		conn.close();
//        System.out.println("登录，生成token");
        //登录，使之生成一个token
//        String urlString="http://localhost:8888/login";
//		File file=new File("D:/tokens.txt");
//		if(file.exists()) {
//			file.delete();
//		}
//		RandomAccessFile raf=new RandomAccessFile(file,"rw");
//		file.createNewFile();
//		raf.seek(0);
//        for(int i=0; i<users.size();i++) {
//            Account user=users.get(i);
//            URL url=new URL(urlString);
//            HttpURLConnection co=(HttpURLConnection) url.openConnection();
//            co.setRequestMethod("POST");
//            co.setDoOutput(true);
//            OutputStream out=co.getOutputStream();
//            String params="name="+user.getName()+"&password="
//                    +"123"+"&level=" + 2;
//            out.write(params.getBytes());
//            out.flush();
//            InputStream inputStream=co.getInputStream();
//            ByteArrayOutputStream bout=new ByteArrayOutputStream();
//            byte buff[]=new byte[1024];
//            int len=0;
//            while((len=inputStream.read(buff))>=0) {
//                bout.write(buff,0,len);
//            }
//            inputStream.close();
//            bout.close();
//            String response=new String(bout.toByteArray());
//            System.out.println(response);
//
////			JSONObject jo=JSON.parseObject(response);
////			String token=jo.getString("data");
////			System.out.println("user:"+user.getId()+"	token:"+token);
////			String row=user.getId()+","+token;
////			raf.seek(raf.length());
////			raf.write(row.getBytes());
////			raf.write("\r\n".getBytes());
////			System.out.println("write to file : "+user.getId());
//        }
    }
    public static void main(String[] args) throws Exception {
        //createUser(5000);
        createUser(100);
    }

    public static void writerText(String path, String content) {

        File dirFile = new File(path);

        if (!dirFile.exists()) {//判断目录是否存在，不存在创建
            dirFile.mkdir();
        }

        try {
            //new FileWriter(path + "config.log", true)  设置true 在不覆盖以前文件的基础上继续写
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "userInfo.txt", true));
            writer.write(content+"\r\n");//写入文件
            writer.flush();//清空缓冲区数据
            writer.close();//关闭读写流
            System.out.println("写入成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
