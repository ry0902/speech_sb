package com.bbnc.voice.recognize;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;


/**
 * java����http��get��post����
 */
public class HttpRequest {
	/**
	 * ��ָ��URL����GET��ʽ������
	 * @param url  ���������URL
	 * @param param �������   
	 * @return URL ����Զ����Դ����Ӧ
	 */
	public static String sendGet(String url, String param){
		String result = "";
		String urlName = url + "?" + param;
		try{
			URL realUrl = new URL(urlName);
			//�򿪺�URL֮�������
			URLConnection conn = realUrl.openConnection();
			//����ͨ�õ���������
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			//����ʵ�ʵ�����
			conn.connect();
			//��ȡ���е���Ӧͷ�ֶ�
//			Map<String, List<String>> map = conn.getHeaderFields();
			//�������е���Ӧͷ�ֶ�
//			for (String key : map.keySet()) {
//				System.out.println(key + "-->" + map.get(key));
//			}
			// ���� BufferedReader����������ȡURL����Ӧ
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
		} catch (Exception e) {
			System.out.println("����GET��������쳣" + e);
			e.printStackTrace();
		}
		return result;

	}
	
	/**
	 * ��ָ��URL����POST��ʽ������
	 * @param url  ���������URL
	 * @param param �������  
	 * @return URL ����Զ����Դ����Ӧ
	 */
	public static String sendPost(String url, String param){
		String result = "";
		try{
//            System.out.println("url = " + url + "\nparam = " + param);
			URL realUrl = new URL(url);
			//�򿪺�URL֮�������
			URLConnection conn =  realUrl.openConnection();
			//����ͨ�õ���������
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			//����POST�������������������
			conn.setDoOutput(true);
			conn.setDoInput(true);
			//��ȡURLConnection�����Ӧ�������
			PrintWriter out = new PrintWriter(conn.getOutputStream());
			//�����������
			out.print(param);
			//flush������Ļ���
			out.flush();
			// ���� BufferedReader����������ȡURL����Ӧ
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            if((line = in.readLine()) != null) {
            	result += line;
            }
            while ((line = in.readLine()) != null) {
                result += "\n" + line;
            }
		} catch (Exception e) {
//			System.out.println("����POST��������쳣" + e);
			e.printStackTrace();
		}
		return result;
	}
	
	//���Է���GET��POST����
	//public static void main(String[] args) throws Exception{
        //����GET����
    //    String s = HttpRequest.sendGet("http://127.0.0.1:8080/index",null);
    //    System.out.println(s);
        //����POST����
    //    String s1 = HttpRequest.sendPost("http://localhost:8080/addComment", "questionId=1&content=��������");
    //    System.out.println(s1);
    //}
}
