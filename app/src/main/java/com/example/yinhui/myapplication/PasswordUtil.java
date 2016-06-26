package com.example.yinhui.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

class PassPair {
    String account;
    String pass;
    public PassPair(String a, String p) {
        account = a;
        pass = p;
    }

    public String toString() {
        return account + " : " + pass;
    }
}
public class PasswordUtil {
    static SecretKey key = null;
    static MyDatabaseHelper dbHelper = null;
    static SQLiteDatabase db = null;

    public static PassPair[] getPassByProperty(String adminUser, String app) throws Exception {
        String sql = "select account, password from pass where adminuser = \'" + adminUser + "\' and website like \'%" + app + "%\'";
        Cursor cur = db.rawQuery(sql, null);
        PassPair[] result = null;
        if(cur.moveToFirst()){
            int count = cur.getCount();
            result = new PassPair[count];
            for(int i = 0; i < count; i++) {
                String account = cur.getString(0);
                String password = decryptByDES(Base64.decode(cur.getString(1), Base64.DEFAULT));
                result[i] = new PassPair(account, password);
                cur.moveToNext();
            }
        }

        return result;
    }

    public static void setPassByProperty(String adminUser, String account, String app, String pass)
            throws Exception {
        String sql = "insert into pass(adminuser, website, account, password) values(?,?,?,?)";
        db.execSQL(sql, new String[] {adminUser, app, account, encryptToStringDES(pass)});
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


    public static void init(Context context, String passAdmin) throws Exception{
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "PassManager.db", null, 1);
        db = dbHelper.getWritableDatabase();

        createSecretKey("DES", passAdmin);
    }

    public static String getUserPass(String user) {
        Cursor cursor = db.rawQuery("select password from user where adminuser = ?", new String[] {user});
        int count = cursor.getCount();
        if(count <= 0)
            return null;
        else {
            cursor.moveToFirst();
            return cursor.getString(0);
        }
    }

    public static void createUser(String user, String password) {
        String sql = "insert into user values(?, ?)";
        db.execSQL(sql, new String[] {user, encryptToStringDES(password)});
    }
}
