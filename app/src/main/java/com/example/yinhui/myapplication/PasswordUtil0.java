package com.example.yinhui.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public class PasswordUtil0 {
    static SecretKey key = null;
    static Properties props = null;
    static String sDir = Environment.getExternalStorageDirectory().toString() + "/data/passmgr";
    static String passFile = sDir + "/pass.properties";

    public static String getPassByProperty(String adminUser, String app) throws Exception {
        InputStream in = new BufferedInputStream(new FileInputStream(passFile));
        if(props == null) {
            props = new Properties();
            props.load(in);
            in.close();
        }
        String password_byte = props.getProperty(app);

        if(password_byte == null)
            return null;
        String password = decryptByDES(Base64.decode(password_byte, Base64.DEFAULT));
        return password;
    }

    public static void setPassByProperty(String adminUser, String account, String app, String pass)
            throws Exception {
        InputStream fis = new FileInputStream(passFile);
        if(props == null) {
            props = new Properties();
            props.load(fis);
            fis.close();
        }
        OutputStream fos = new FileOutputStream(passFile);
        props.setProperty(app, Base64.encodeToString(encryptToDES(account + "++__** " + pass), Base64.DEFAULT));
        props.store(fos, "Update value");
        fos.close();


        String sql = "insert into pass(adminuser, website, account, password) values(?,?,?,?)";
        db.execSQL(sql, new String[] {adminUser, app, account, pass});
    }

    public static void createSecretKey(String algorithm, String pass) throws  Exception{
        DESKeySpec keySpec = new DESKeySpec(pass.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
        key = keyFactory.generateSecret(keySpec);
    }

    public static byte[] encryptToDES(String info)
            throws Exception {
        String Algorithm = "DES";
        SecureRandom sr = new SecureRandom();

        byte[] cipherByte = null;

        Cipher c1 = Cipher.getInstance(Algorithm);
        c1.init(Cipher.ENCRYPT_MODE, key);
        cipherByte = c1.doFinal(info.getBytes("UTF-8"));

        return cipherByte;
    }

    public static String encryptToStringDES( String info) {
        String result = null;
        try {
            result = Base64.encodeToString(encryptToDES(info), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    public static String decryptByDES(byte[] sInfo)
            throws Exception {
        String Algorithm = "DES";
        SecureRandom sr = new SecureRandom();
        byte[] cipherByte = null;
        Cipher c1 = Cipher.getInstance(Algorithm);
        c1.init(Cipher.DECRYPT_MODE, key);
        cipherByte = c1.doFinal(sInfo);
        return new String(cipherByte, "UTF-8");
    }

    private static MyDatabaseHelper dbHelper;
    private static SQLiteDatabase db;

    public static void init(Context context, String passAdmin) throws Exception{
        String status = Environment.getExternalStorageState();

        File destDir = new File(sDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        File file = new File(passFile);
        if(!file.exists())
            file.createNewFile();

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "PassManager.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        createSecretKey("DES", passAdmin);
    }

    public static String getUserPass(String user) {
        Cursor cursor = db.rawQuery("select password from user where name = ?", new String[] {user});
        int count = cursor.getCount();
        if(count <= 0)
            return null;
        else
            return cursor.getString(1);
    }

    public static void createUser(String user, String password) {
        String sql = "insert into user values(?, ?)";
        db.execSQL(sql, new String[] {user, password});
    }
}
