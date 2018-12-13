package com.wdn.studentsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wdn.loopj.android.http.AsyncHttpClient;
import com.wdn.loopj.android.http.AsyncHttpResponseHandler;
import com.wdn.loopj.android.http.RequestParams;
import com.wdn.loopj.android.http.ResponseHandlerInterface;
import com.wdn.studentsystem.db.StudentDao;
import com.wdn.studentsystem.domain.Student;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText et_id;
    private EditText et_name;
    private EditText et_phone;
    private ListView lv;
    private Button btn_add;
   /* private ArrayList<Student> list;*/
    private StudentDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //1.找到控件
        //2.模拟操作，添加假数据进行显示
        //3.去除假数据，添加真实数据
        //4.将真实数据写入数据库
        //5.将数据库上传到服务器
        initView();
    }
    /**
     * 初始化界面
     */
    private void initView() {
        setContentView(R.layout.activity_main);
        et_id = findViewById(R.id.et_id);
        et_name = findViewById(R.id.et_name);
        et_phone = findViewById(R.id.et_phone);
        lv = findViewById(R.id.lv);
        btn_add = findViewById(R.id.btn_add);
        dao = new StudentDao(this);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStudent(v);
            }
        });
      /*  list = new ArrayList<>();
        for (int i=0;i<10;i++){
            list.add(new Student(i,"name"+i,"123"+i));
        }
        lv.setAdapter(new MyAdapter());*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.activity_main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_delete:
                dao.deleteAll();
                Toast.makeText(this, "删除全部数据成功", Toast.LENGTH_SHORT).show();
                lv.setAdapter(new MyAdapter());
                break;
            case R.id.item_save:
                Toast.makeText(this, "上传数据到云服务器", Toast.LENGTH_SHORT).show();
                //上传数据到服务器
                uploadDBToServer();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadDBToServer() {
        File file=new File("/data/data/com.wdn.studentsystem/databases/student.db");
        if (file.exists()&&file.length()>0){
            try {
                AsyncHttpClient client=new AsyncHttpClient();
                RequestParams params=new RequestParams();
                params.put("file",file);
                client.post("http://192.168.1.6:8081/servlet/UploadServlet", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(MainActivity.this, "上传失败", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onProgress(int bytesWritten, int totalSize) {

                        System.out.println(bytesWritten+"/"+totalSize);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "上传失败", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "文件不存在或者内容为空", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * btn_add的点击事件，用来添加学生信息
     * @param view
     */
    private void addStudent(View view) {
        String id=et_id.getText().toString().trim();
        String name=et_name.getText().toString().trim();
        String phone=et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(id)||TextUtils.isEmpty(name)||TextUtils.isEmpty(phone)){
            Toast.makeText(this, "数据不能为空", Toast.LENGTH_SHORT).show();
        }else {
            //保存数据到数据库，并且同步显示到界面
            Student student=new Student();
            student.setId(Integer.parseInt(id));
            student.setName(name);
            student.setPhone(phone);
            boolean b = dao.addOne(student);
       if (b){
           Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
           lv.setAdapter(new MyAdapter());
        }
        }
    }

   private class MyAdapter extends BaseAdapter{

       private TextView tv_item_id;
       private TextView tv_item_name;
       private TextView tv_item_phone;

       @Override
       public int getCount() {
           return dao.getTotalCount();
       }

       @Override
       public Map<String, String> getItem(int position) {
           return dao.getStudentInfo(position);
       }

       @Override
       public long getItemId(int position) {
           return position;
       }

       @Override
       public View getView(final int position, View convertView, ViewGroup parent) {
           View view = View.inflate(MainActivity.this, R.layout.item, null);
           /*这里应该加个view.  very important
                      * */
           tv_item_id =view.findViewById(R.id.tv_item_id);
           tv_item_name = view.findViewById(R.id.tv_item_name);
           tv_item_phone = view.findViewById(R.id.tv_item_phone);
           //总是找不到textview
           tv_item_id.setText(getItem(position).get("studentid"));
           tv_item_name.setText(getItem(position).get("name"));
           tv_item_phone.setText(getItem(position).get("phone"));
           ImageView iv_delete = view.findViewById(R.id.iv_delete);
           iv_delete.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   delete(position);
               }
           });
           return view;
       }

       public void delete(int position){
           boolean result = dao.delete(getItem(position).get("studentid"));
           if (result){
               Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
               lv.setAdapter(new MyAdapter());
           }else {
               Toast.makeText(MainActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
           }

       }
    }

}
