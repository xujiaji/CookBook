package com.jiaji.cookbook.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jiaji.cookbook.info.ShowCookersInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiaJi on 2015/12/15.
 */
public class CooksDBManager {
    private SQLiteDatabase db;
    private ShowCookersInfo.Result.Data data;
    private static CooksDBManager cooksDBManager;

    private CooksDBManager(Context context) {
        CooksDBHelper helper = new CooksDBHelper(context);
        db = helper.getWritableDatabase();
    }

    public static CooksDBManager getCooksDBManager(Context context) {
        if (cooksDBManager == null) {
            cooksDBManager = new CooksDBManager(context);
        }
        return cooksDBManager;
    }

    /**
     * 增添数据
     */
    public void insertData(ShowCookersInfo.Result.Data data) {
        Cursor cursor = db.rawQuery("select * from " + CooksDBHelper.TABLE_TEXT + " where " + CooksDBHelper.ID + "=" + data.getId(),null);
        if (cursor.getCount() == 1) {
            cursor.close();
            db.execSQL("update " + CooksDBHelper.TABLE_TEXT + " set " + CooksDBHelper.HISTORY_LOOK + " = " + 1 + " where " + CooksDBHelper.ID + "=" + data.getId());
            return;
        }
        cursor.close();
        String textSql = "insert into " + CooksDBHelper.TABLE_TEXT + "(" + CooksDBHelper.ID + "," + CooksDBHelper.TITLE + "," +
                CooksDBHelper.TAGS + "," + CooksDBHelper.IMTRO + "," + CooksDBHelper.INGREDIENTS + "," + CooksDBHelper.BURDEN + "," +
                CooksDBHelper.ALBUMS + "," +
                CooksDBHelper.HISTORY_LOOK + ")" +
                " values('" + data.getId() + "','" + data.getTitle() + "','" + data.getTags() + "','" + data.getImtro() + "','" + data.getIngredients()
                + "','" + data.getBurden() + "','" + data.getAlbums().get(0) + "',1)";
        db.execSQL(textSql);
        //
        for (int i = 0, length = data.getSteps().size(); i < length; i++) {
            String imgsSql = "insert into " + CooksDBHelper.TABLE_IMGS + "(" + CooksDBHelper.ID + "," + CooksDBHelper.IMG + "," + CooksDBHelper.STEP + ")" + " values('" + data.getId() + "','" +
                    data.getSteps().get(i).getImg() + "','" + data.getSteps().get(i).getStep() + "')";
            db.execSQL(imgsSql);
        }
    }

    /**
     * 删除数据
     *
     * @param data
     */
    public void delData(ShowCookersInfo.Result.Data data) {
        if (data == null) {
            Cursor cursor = db.rawQuery("select * from " + CooksDBHelper.TABLE_TEXT, null);
            while (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex(CooksDBHelper.HISTORY_LOOK)) == 1)//删除浏览历史的数据
                {
                    if (cursor.getInt(cursor.getColumnIndex(CooksDBHelper.MY_LIKE)) == 1){
                        db.execSQL(" update  "+CooksDBHelper.TABLE_TEXT+" set "+CooksDBHelper.HISTORY_LOOK+" =0"+" where "+CooksDBHelper.ID+" = "+cursor.getInt(cursor.getColumnIndex(CooksDBHelper.ID)));
                        continue;}
                    String textSql = "delete from " + CooksDBHelper.TABLE_TEXT + " where "
                            + CooksDBHelper.ID + "=" + cursor.getInt(cursor.getColumnIndex(CooksDBHelper.ID));
                    db.execSQL(textSql);
                    String imgsSql = "delete from " + CooksDBHelper.TABLE_IMGS + " where "
                            + CooksDBHelper.ID + "=" + cursor.getInt(cursor.getColumnIndex(CooksDBHelper.ID));
                    db.execSQL(imgsSql);
                }
            }
            cursor.close();
        } else {
            String textSql = "delete from " + CooksDBHelper.TABLE_TEXT + " where "
                    + CooksDBHelper.ID + "=" + data.getId();
            db.execSQL(textSql);
            String imgsSql = "delete from " + CooksDBHelper.TABLE_IMGS + " where "
                    + CooksDBHelper.ID + "=" + data.getId();
            db.execSQL(imgsSql);
        }
    }

    /**
     * 修改数据，default 0（不喜欢） default 1 （喜欢）
     *
     * @param data
     * @param isLike 是否收藏
     */
    public void updateData(ShowCookersInfo.Result.Data data, boolean isLike) {
        String textSql = "update " + CooksDBHelper.TABLE_TEXT + " set "
                + CooksDBHelper.MY_LIKE + "='" + (isLike ? 1 : 0) + "' where " + CooksDBHelper.ID + "=" + data.getId();
        db.execSQL(textSql);
    }

    public ShowCookersInfo getData(boolean isHistory, boolean isLike) {
        ShowCookersInfo info = new ShowCookersInfo();
        String textSql;
        if (isHistory) {
            textSql = "select * from " + CooksDBHelper.TABLE_TEXT + " where " + CooksDBHelper.HISTORY_LOOK + " = " + 1;

        } else {
            textSql = "select * from " + CooksDBHelper.TABLE_TEXT + " where " + CooksDBHelper.MY_LIKE + "=" + (isLike ? 1 : 0);
        }

        Cursor textCursor = db.rawQuery(textSql, null);
        ShowCookersInfo.Result result = new ShowCookersInfo.Result();
        info.setResult(result);
        List<ShowCookersInfo.Result.Data> datas = new ArrayList<>();
        result.setData(datas);
        ShowCookersInfo.Result.Data data;
        while (textCursor.moveToNext()) {
            data = new ShowCookersInfo.Result.Data();
            data.setId(textCursor.getString(textCursor.getColumnIndex(CooksDBHelper.ID)));
            data.setTitle(textCursor.getString(textCursor.getColumnIndex(CooksDBHelper.TITLE)));
            data.setTags(textCursor.getString(textCursor.getColumnIndex(CooksDBHelper.TAGS)));
            data.setImtro(textCursor.getString(textCursor.getColumnIndex(CooksDBHelper.IMTRO)));
            data.setIngredients(textCursor.getString(textCursor.getColumnIndex(CooksDBHelper.INGREDIENTS)));
            data.setBurden(textCursor.getString(textCursor.getColumnIndex(CooksDBHelper.BURDEN)));
            List<String> albums = new ArrayList<>();
            data.setAlbums(albums);
            albums.add(textCursor.getString(textCursor.getColumnIndex(CooksDBHelper.ALBUMS)));

            List<ShowCookersInfo.Result.Data.Steps> stepses = new ArrayList<>();
            data.setSteps(stepses);
            ShowCookersInfo.Result.Data.Steps steps;
            String imgsSql = "select * from " + CooksDBHelper.TABLE_IMGS + " where " + CooksDBHelper.ID + "=" + textCursor.getInt(textCursor.getColumnIndex(CooksDBHelper.ID));
            Cursor imgsCursor = db.rawQuery(imgsSql, null);
            while (imgsCursor.moveToNext()) {
                steps = new ShowCookersInfo.Result.Data.Steps();
                steps.setImg(imgsCursor.getString(imgsCursor.getColumnIndex(CooksDBHelper.IMG)));
                steps.setStep(imgsCursor.getString(imgsCursor.getColumnIndex(CooksDBHelper.STEP)));
                stepses.add(steps);
            }
            datas.add(data);
            imgsCursor.close();
        }
        textCursor.close();
        return info;
    }

    /**
     * 当前id的菜谱是否是添加了收藏
     *
     * @param id
     * @return
     */
    public boolean isLikeNowCook(String id) {
        boolean isLike;
        Cursor cursor = db.rawQuery("select " + CooksDBHelper.MY_LIKE + " from " + CooksDBHelper.TABLE_TEXT + " where " + CooksDBHelper.ID + "=" + id, null);
        cursor.moveToNext();
        isLike = cursor.getInt(0) == 1;
        cursor.close();
        return isLike;
    }

    public ShowCookersInfo.Result.Data getData() {
        return data;
    }

    public void setData(ShowCookersInfo.Result.Data data) {
        this.data = data;
    }
}
