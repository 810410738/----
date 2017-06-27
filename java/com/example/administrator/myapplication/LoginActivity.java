package com.example.administrator.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button button;
    ImageView CodeImg ;
    EditText text1;
    EditText text2;
    EditText text3;
    TextView text;
    String stu_no= "";
    String passwd= "";
    String GetCode= "";
    String code= "";
    String cookie1 = "";
    String cookie2 = "";
    String htmlbuffer = "";
    String url = "http://192.168.240.168/xuanke";
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        button = (Button) findViewById(R.id.button);
        text1 = (EditText)findViewById(R.id.username);
        text1.setText("2015150285");
        text2 = (EditText)findViewById(R.id.passwd);
        text2.setText("030821");
        text3 = (EditText)findViewById(R.id.code);
        CodeImg = (ImageView)findViewById(R.id.image);
        text = (TextView)findViewById(R.id.text);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取cookies
                List<String> cookies = GetHttpResponseHeader();
                if(cookies!=null)
                {
                    cookie1=(cookies.get(0));
                    cookie2=(cookies.get(1));
                    cookie1 = cookie1.substring(0, cookie1.indexOf(';'));
                    cookie2 = cookie2.substring(0, cookie2.indexOf(';'));
                    cookie1 = cookie1+"; "+cookie2;
                    Log.d("1", "run: "+cookie1);
                    //获取验证码
                    getImg();
                }
                else
                {
                    //跳转到离线活动
                    Intent intent = new Intent(LoginActivity.this,OfflineActivity.class);
                    startActivity(intent);
                }

            }
        }).start();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //post登陆
                Post();
                //登陆成功后获取选课信息
            }
        });

    }




        public void Post() {
            // TODO Auto-generated method stub
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        String PostAdd =url+"/entrance1.asp";

                        URL u = new URL(PostAdd);
			/*
			 * stu_no:2015150285
			passwd:030821
			GetCode:758
			 */
                stu_no = text1.getText().toString();
                passwd = text2.getText().toString();
                GetCode = text3.getText().toString();


                        //post
                        String sendstr = "stu_no="+stu_no+"&passwd="+passwd+"&GetCode="+GetCode;
                        Log.d("1", "run: "+stu_no+" "+passwd+" "+GetCode);
//			System.out.println(sendstr);

                        HttpURLConnection connection =  (HttpURLConnection) u.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        connection.setInstanceFollowRedirects(false);//自动重定向
                        connection.setRequestProperty("Cookie", cookie1);
                        OutputStream out= connection.getOutputStream();
                        out.write(sendstr.getBytes());
                        out.flush();
                        out.close();
                        Reader r = new InputStreamReader(connection.getInputStream(),"GB2312");
                        int c;
                        htmlbuffer = "";
                        while((c=r.read())!=-1)
                        {
                            htmlbuffer+= (char)c;

                        }
                        r.close();
                        Looper.prepare();
                        if(htmlbuffer.contains("验证码输入错误"))
                        {
                            Toast toast=Toast.makeText(getBaseContext(), "验证码输入错误,请重新输入", Toast.LENGTH_SHORT);
                            toast.show();
                            //获取验证码
                            getImg();
                        }
                        else if(htmlbuffer.contains("密码错")){
                            Toast toast=Toast.makeText(getBaseContext(),"密码错，请重新输入", Toast.LENGTH_SHORT);
                            toast.show();
                            //获取验证码
                            getImg();
                        }
                        else if(htmlbuffer.contains("学号错")){
                            Toast toast=Toast.makeText(getBaseContext(),"学号错，请重新输入", Toast.LENGTH_SHORT);
                            toast.show();
                            //获取验证码
                            getImg();
                        }
                        else
                        {
                            //跳转到下个活动
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.putExtra("cookie",cookie1);
                            intent.putExtra("url",url);
                            startActivity(intent);
                        }
                        Looper.loop();
                        Log.d("1", "run: "+htmlbuffer);
//                        text.setText(htmlbuffer);

                        //接收响应

//                File file = new File("D:\\程序编程\\互联网编程\\大作业\\test.html");
//                PrintWriter output = new PrintWriter(file);

//
//                output.print(htmlbuffer);
//                output.flush();
//                output.close();
//			System.out.println(str);

//			redirect(htmlbuffer);
                    } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();

        }



        public  void getImg() {
            try
            {
                // TODO Auto-generated method stub
                double a = Math.random();
                String getImg = url+"/code.asp?id=!!!&random="+a;
                URL u = new URL(getImg);
                HttpURLConnection connection = (HttpURLConnection) u.openConnection();
                connection.setRequestMethod("GET");
//set header
                connection.setRequestProperty("Accept", "image/webp,image/*,*/*;q=0.8");
                connection.setRequestProperty("Cookie", cookie1);

                DataInputStream input = new DataInputStream(connection.getInputStream());

                 file =  getImgStorage("codeimg.jpg");
                FileOutputStream output = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int len;
                while((len=input.read(b))!=-1){
                    output.write(b, 0, len);
                }
                output.flush();
                output.close();
                input.close();
//                System.out.println("已经下载了验证码图片了！");
                CodeImg.post(new Runnable() {
                    @Override
                    public void run() {
                        CodeImg.setImageURI(Uri.fromFile(file) );
                    }
                });

            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        public  List<String>  GetHttpResponseHeader(){
            try {
                URL obj = new URL("http://192.168.240.168/xuanke/edu_login.asp");
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setReadTimeout(2000);
                Map<String, List<String>> map = conn.getHeaderFields();
                return map.get("Set-Cookie");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;


        }
        public File getImgStorage(String name){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),name);
        return file;
        }


    }

