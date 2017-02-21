package com.android.mobilesocietynetwork.client.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class FileHelper {
	public static String bytes2String(byte[] bt){
		BASE64Encoder encoder=new BASE64Encoder();
		String base64=encoder.encode(bt);
		return base64;
	}
	
	public static byte[] String2bytes(String s) throws IOException{
		BASE64Decoder decoder=new BASE64Decoder();
		byte[] bt=decoder.decodeBuffer(s);
		return bt;
	}
	
	public static void byte2File(byte[] bt,String filePath,String fileName){
		BufferedOutputStream bufferedOutputStream=null;
		FileOutputStream fileOutputStream=null;
		File file=null;
		try {
			File dir=new File(filePath);
			if(!dir.exists()&&dir.isDirectory()){
			//if(!dir.exists()){
				dir.mkdirs();
			}
			file=new File(filePath+File.separator+fileName);
			fileOutputStream=new FileOutputStream(file);
			bufferedOutputStream=new BufferedOutputStream(fileOutputStream);
			bufferedOutputStream.write(bt);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally {
			if(bufferedOutputStream!=null){
				try {
					bufferedOutputStream.close();
				} catch (IOException e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
			if(fileOutputStream!=null){
				try {
					fileOutputStream.close();
				} catch (IOException e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
		}
	}
	
	public static byte[] file2bytes(File file){
			byte[] buffer=null;
		try {
			FileInputStream fileInputStream=new FileInputStream(file);
			ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
			byte[] bs=new byte[1024];
			int n;
			while((n=fileInputStream.read(bs))!=-1){
				byteArrayOutputStream.write(bs, 0, n);
			}
			fileInputStream.close();
			byteArrayOutputStream.close();
			buffer=byteArrayOutputStream.toByteArray();
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return buffer;
	}
/*	public static String file2String(File file, String encoding) {
		InputStreamReader reader = null;
		StringWriter writer = new StringWriter();
		try {
			if (encoding == null || "".equals(encoding.trim())) {
				reader = new InputStreamReader(new FileInputStream(file), encoding);
			} else {
				reader = new InputStreamReader(new FileInputStream(file));
			}
			// ��������д�������
			char[] buffer = new char[1024];
			int n = 0;
			while (-1 != (n = reader.read(buffer))) {
				writer.write(buffer, 0, n);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		// ����ת�����
		if (writer != null)
			return writer.toString();
		else
			return null;
	}
	
    *//** 
     * ���ַ���д��ָ���ļ�(��ָ���ĸ�·�����ļ��в�����ʱ��������޶�ȥ�������Ա�֤����ɹ���) 
     * 
     * @param res            ԭ�ַ��� 
     * @param filePath �ļ�·�� 
     * @return �ɹ���� 
     *//* 
    public static boolean string2File(String res, String filePath) { 
            boolean flag = true; 
            BufferedReader bufferedReader = null; 
            BufferedWriter bufferedWriter = null; 
            try { 
                    File distFile = new File(filePath); 
                    if (!distFile.getParentFile().exists()) distFile.getParentFile().mkdirs(); 
                    bufferedReader = new BufferedReader(new StringReader(res)); 
                    bufferedWriter = new BufferedWriter(new FileWriter(distFile)); 
                    char buf[] = new char[1024];         //�ַ������� 
                    int len; 
                    while ((len = bufferedReader.read(buf)) != -1) { 
                            bufferedWriter.write(buf, 0, len); 
                    } 
                    bufferedWriter.flush(); 
                    bufferedReader.close(); 
                    bufferedWriter.close(); 
            } catch (IOException e) { 
                    e.printStackTrace(); 
                    flag = false; 
                    return flag; 
            } finally { 
                    if (bufferedReader != null) { 
                            try { 
                                    bufferedReader.close(); 
                            } catch (IOException e) { 
                                    e.printStackTrace(); 
                            } 
                    } 
            } 
            return flag; 
    }*/
}
