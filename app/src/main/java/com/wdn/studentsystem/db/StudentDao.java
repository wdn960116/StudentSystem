package com.wdn.studentsystem.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wdn.studentsystem.domain.Student;

import java.security.spec.ECField;
import java.util.HashMap;
import java.util.Map;


public class StudentDao {
    private StudentDBOpenHelper helper;

    public StudentDao(Context context) {
        helper=new StudentDBOpenHelper(context);
    }

    /**
     * 添加一条记录
     * @param studentid
     * @param name
     * @param phone
     * @return 是否添加成功
     */
    public boolean add(String studentid,String name,String phone){
        SQLiteDatabase database=helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("studentid",studentid);
        values.put("name",name);
        values.put("phone",phone);
        long row=database.insert("info",null,values);
        database.close();
        if (row != -1)
            return true;
        else {
            return false;
        }
    }

    /**
     * 添加一条记录
     * @param student
     * @return 是否添加成功
     */
    public boolean addOne(Student student){
        SQLiteDatabase database=helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("studentid",String.valueOf(student.getId()));
        values.put("name",student.getName());
        values.put("phone",student.getPhone());
        long row=database.insert("info",null,values);
        database.close();
        if (row != -1)
            return true;
        else {
            return false;
        }
    }

    /**
     * 查询一条记录
     * @param position
     * 数据在数据库的位置
     * @return 一条记录
     */
    public Map<String,String> getStudentInfo(int position){
        SQLiteDatabase database=helper.getWritableDatabase();
        Cursor cursor=database.query("info",new String[]{
                "studentid","name","phone"},null,null,null,null,null);
        cursor.moveToPosition(position);
        String studentid = cursor.getString(0);
        String name = cursor.getString(1);
        String phone = cursor.getString(2);
        cursor.close();
        database.close();
        Map<String,String> map=new HashMap<>();
        map.put("studentid",studentid);
        map.put("name",name);
        map.put("phone",phone);
        return map;
    }

    /**
     * 查询数据库里面有多少条记录
     * @return 记录的条数
     */
    public int getTotalCount(){
        SQLiteDatabase database=helper.getWritableDatabase();
        Cursor cursor=database.query("info",null,null,null,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        database.close();
        return count;
    }
    /**
     * 删除一条记录
     * @param studentid
     * @return 是否删除成功
     */
    public boolean delete(String studentid){
        SQLiteDatabase database=helper.getWritableDatabase();
        long row=database.delete("info","studentid=?",new String[]{studentid});
        database.close();
        if (row != -1)
            return true;
        else {
            return false;
        }
    }

    /***
     * 删除所有数据
     */
    public void deleteAll(){
        SQLiteDatabase database=helper.getWritableDatabase();
        database.beginTransaction();
        try {
            Cursor info = database.query("info", new String[]{"studentid"}, null, null, null, null, null);
            while (info.moveToNext()){
                String studentid=info.getString(0);

                database.delete("info","studentid=?",new String[]{studentid});
            }
            info.close();
            database.setTransactionSuccessful();

        }catch (Exception e){
            e.printStackTrace();

        }finally {
            database.endTransaction();
            database.close();
        }

    }
}
