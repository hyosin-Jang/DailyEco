package com.example.dailyeco;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "dailyeco.db";

    public DBHelper(@Nullable Context context) {

        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if (!db.isReadOnly()) {
        // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }

        //데이터베이스 생성될 때 호출
        // 데이터베이스 -> 테이블 -> 컬럼 -> 값

        //daily 기록 테이블
        db.execSQL("CREATE TABLE IF NOT EXISTS tbl_dailynum (id_dailynum INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, doc_date TEXT, id_goal INTEGER, daily_count INTEGER(10), FOREIGN KEY(id_goal) REFERENCES tbl_goal(id_goal))");


        db.execSQL("INSERT into tbl_dailynum\n" +
                "(doc_date,id_goal,daily_count)\n" +
                "VALUES('2021-03-24',1,6);");

        //환경실천목표 테이블
        db.execSQL("CREATE TABLE IF NOT EXISTS tbl_goal (id_goal INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name_goal TEXT, icon_goal BLOB, is_active INTEGER)");

        Insert_tbl_goal("텀블러 사용하기",iconBitmap(R.drawable.ic_1),1);
        Insert_tbl_goal("장바구니 사용하기",iconBitmap(R.drawable.ic_2),1);
        Insert_tbl_goal("배달 시 일회용 수저, 포크 받지 않기",iconBitmap(R.drawable.ic_3),1);
        Insert_tbl_goal("재활용 용기 씻어서 버리기",iconBitmap(R.drawable.ic_4),1);
        Insert_tbl_goal("외식할 때 잔반 남기지 않기",iconBitmap(R.drawable.ic_5),1);
        Insert_tbl_goal("가까운 층은 걸어서 올라가기",iconBitmap(R.drawable.ic_6),1);
        Insert_tbl_goal("안쓰는 플러그 뽑기",iconBitmap(R.drawable.ic_7_7),0);
        Insert_tbl_goal("양치컵 사용하기",iconBitmap(R.drawable.ic_8_8),0);

/*
        db.execSQL("INSERT INTO tbl_goal (name_goal, icon_goal, is_active) VALUES" +
                "('장바구니 사용하기'+', '+'"+iconBitmap(R.drawable.ic_1)+"'+', '+'1'),"+
                "('배달 시 일회용 수저, 포크 받지 않기'+', '+'"+iconBitmap(R.drawable.ic_1)+"'+', '+'1')," +
                "('재활용 용기 씻어서 버리기'+', '+'"+iconBitmap(R.drawable.ic_1)+"'+', '+'1')," +
                "('외식할 때 잔반 남기지 않기'+', '+'"+iconBitmap(R.drawable.ic_1)+"'+', '+'1')," +
                "('가까운 층은 걸어서 올라가기'+', '+'"+iconBitmap(R.drawable.ic_1)+"'+', '+'1')," +
                "('안쓰는 플러그 뽑기'+', '+'"+iconBitmap(R.drawable.ic_1)+"'+', '+'1')," +
                "('양치컵 사용하기'+', '+'"+iconBitmap(R.drawable.ic_1)+"'+', '+'1')," +
                "('종이 영수증 받지 않기'+', '+'"+iconBitmap(R.drawable.ic_1)+"'+', '+'1')," +
                "('페트병 뚜껑, 링, 비닐 분리해서 버리기'+', '+'"+iconBitmap(R.drawable.ic_1)+"'+', '+'1')");

 */


        //목표횟수변경 테이블
        db.execSQL("CREATE TABLE IF NOT EXISTS tbl_goalcount (id_count INTEGER PRIMARY KEY AUTOINCREMENT, green_count INTEGER NOT NULL, yellow_count INTEGER NOT NULL)");

        db.execSQL("INSERT into tbl_goalcount(\n" +
                "green_count, yellow_count)\n" +
                "VALUES(5,3);");


    }

    public byte[] iconBitmap(int d){
        Drawable drawable = ResourcesCompat.getDrawable(Resources.getSystem(),d,null);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArray);
        byte[] img;
        img = byteArray.toByteArray();

        return img;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onCreate(db);
    }

    //daily 기록 select문
    public ArrayList<Dailylist> getDailylist(String date) {
        ArrayList<Dailylist> dailylists = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TBL_DAILYNUM "+
                "JOIN TBL_GOAL ON TBL_DAILYNUM.ID_GOAL = TBL_GOAL.ID_GOAL "+
                "WHERE DOC_DATE "+date,null);


        if(cursor.getCount()!=0){
            //조회된 데이터가 있을 때 내부수행

            int sum=0;
            while(cursor.moveToNext()){
                String name_goal=cursor.getString(cursor.getColumnIndex("name_goal"));
                int daily_count=cursor.getInt(cursor.getColumnIndex("daily_count"));

                Dailylist dailylist = new Dailylist();
                dailylist.setName_goal(name_goal);
                dailylist.setDaily_count(daily_count);

                sum+=daily_count;
                dailylist.setTotal_count(sum);
                dailylists.add(dailylist);

            }

        }
        cursor.close();

        return dailylists;
    }


    //daily 기록 insert문
    public void Insert_tbl_dailynum(int _id_dailynum, int _daily_num){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO tbl_dailynum (id_dailynum, daily_num) VALUES('"+_id_dailynum+"', '"+_daily_num+"')");

    }


    //목표횟수를 수정하는 update문
    public void Update_tbl_goalcount(int _green_count, int _yellow_count){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE tbl_goalcount SET green_count = '"+_green_count+"', yellow_count = '"+_yellow_count+"'" );

    }


    //환경실천목표 테이블에서 icon은 iconBitmap 함수를 이용해서 byte 형식으로 입력
    //환경실천목표를 추가하는 insert문
    public void Insert_tbl_goal(String _name_goal, byte[] _icon_goal, int _is_active){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO tbl_goal (name_goal, icon_goal, is_active) VALUES ('"+_name_goal+"', '"+_icon_goal+"','"+_is_active+"')");

    }

    //환경실천목표 추가화면의 6개 리스트를 수정하는 update문
    public void Update_tbl_goal(String _name_goal, byte[] _icon_goal, int _is_active){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("UPDATE tbl_goal SET is_active='"+_is_active+"', _icon_goal='"+_icon_goal+"' WHERE name_goal = '"+_name_goal+"'");
    }


}
