package com.example.administrator.myapplication;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class OfflineActivity extends AppCompatActivity {
    WebView web;
    Button button1;
    Button button2;
    Button button3;
    String xuanke="";
    String kechengbiao="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_layout);
        web = (WebView)findViewById(R.id.web);
        web.getSettings().setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
        String temp = "<h1 align=\"center\">当前为离线状态或不处于校内网！</h1>\n" +
                "<p style=\"color:red  \"align=\"center\">\n" +
                "\ttips:你可以点击选课结果或者课程表按钮来查看上次登陆缓存的数据。<br/>当然为了安全，你也可以点击清除缓存以清除保存在本地的个人数据。\n" +
                "</p>";
        web.getSettings().setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
        web.loadData(temp, "text/html; charset=UTF-8", null);//这种写法可以正确解码
        button1 = (Button)findViewById(R.id.xuanke);
        button2 = (Button)findViewById(R.id.kechengbiao);
        button3 = (Button)findViewById(R.id.clear);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //查看缓存的选课结果
                try {
                    String str = "";
                    File file = getHtmlStorage("xuanke.html");
                    if(file.exists()){
                        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        while((str=input.readLine())!=null){
                            xuanke+=str;
                        }
                        str = "";
                        input.close();
                        web.loadData(xuanke, "text/html; charset=UTF-8", null);//这种写法可以正确解码
                    }
                   else {
                        web.loadData("<h1 align=\"center\">系统没有选课结果的缓存！</h1>\n", "text/html; charset=UTF-8", null);
                    }

                    xuanke = "";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看缓存的选课结果
                try {
                    String str = "";
                    File file = getHtmlStorage("kechengbiao.html");
                    if(file.exists())
                    {
                        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        while((str=input.readLine())!=null){
                            kechengbiao+=str;
                        }
                        str = "";
                        input.close();
                        web.loadData(kechengbiao, "text/html; charset=UTF-8", null);//这种写法可以正确解码
                    }
                    else {
                        web.loadData("<h1 align=\"center\">系统没有课程表的缓存！</h1>\n", "text/html; charset=UTF-8", null);
                    }

                    kechengbiao = "";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                web.getSettings().setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
                if(deleteFile(getHtmlStorage("xuanke.html"))&&deleteFile(getHtmlStorage("kechengbiao.html")))
                {
                    web.loadData("<h1 align=\"center\">删除缓存成功！</h1>\n", "text/html; charset=UTF-8", null);
                }
                else{
                    web.loadData("<h1 align=\"center\">缓存已经被删除！</h1>\n", "text/html; charset=UTF-8", null);
                }

            }
        });
    }
    public File getHtmlStorage(String name){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),name);
        return file;
    }
    public boolean deleteFile(File file) {
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }
}
